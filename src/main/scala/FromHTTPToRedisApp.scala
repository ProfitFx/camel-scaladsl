import org.apache.camel.{Exchange, CamelContext}
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{ScalaRouteBuilder, RouteBuilderSupport}
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
  * Created by smakhetov on 07.06.2016.
  */

object FromHTTPToRedisApp extends App with RouteBuilderSupport{
  val mainApp = new Main
  //Прописываем вместо стандартного кастомный stringSerializer для redis
  mainApp.bind("stringSerializer",new StringRedisSerializer)
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromHTTPToRedisRoute(context))
  mainApp.run
}

class FromHTTPToRedisRoute  (override val context: CamelContext) extends ScalaRouteBuilder(context) {
  //По таймеру, раз в минуту обращаемся к HTTP сервису
  """quartz://groupName/timerName?cron=0+0/1+*+*+*+?""" ==> {
    to("http://www.google.com/finance/info?q=CURRENCY%3aUSDRUB")
    // создаем пару ключ-значение для redis, записываем в хедер
    process((exchange: Exchange) => {
      exchange.getOut.setHeader("CamelRedis.Key",System.currentTimeMillis())
      exchange.getOut.setHeader("CamelRedis.Value",exchange.getIn.getBody(classOf[String]))
    })
    // Отправляем данные в Redis
   // to ("""spring-redis://172.16.7.58:6379?serializer=#stringSerializer""")
    to ("""spring-redis://192.168.3.45:6379?serializer=#stringSerializer""")
  }
}

/*
1) Just disable protected mode sending the command 'CONFIG SET protected-mode no' from the loopback interface by connecting to Redis from the sam
 */

//val addr = "http:www.google.com/finance/info?q=NASDAQ%3aGOOG"
//object FromHTTPToRedis{// extends App{
//
//
//  val registry = new SimpleRegistry
//  registry.put("stringSerializer",new StringRedisSerializer())
//  val context = new DefaultCamelContext(registry)
//  context.addRoutes(FromHTTPToRedisRoute)
//  context.start
//  Thread.currentThread.join
//
//
//  object FromHTTPToRedisRoute extends ScalaRouteBuilder(context) with Languages{
//
//    """quartz://groupName/timerName?cron=0+0/1+*+*+*+?""" ==> {
//
//      //val addr = "http:www.google.com/finance/info?q=NASDAQ%3aGOOG"
//      to("http://www.google.com/finance/info?q=CURRENCY%3aUSDRUB")
//      process((exchange: Exchange) => {
//        exchange.getOut.setHeader("CamelRedis.Key",System.currentTimeMillis())
//        exchange.getOut.setHeader("CamelRedis.Value",exchange.getIn.getBody(classOf[String]))
//      })
//      to ("""spring-redis://172.16.7.58:6379?serializer=#stringSerializer""")
//    }
//  }
//}

//      val myProcessor = (exchange: Exchange) => {
//        exchange.getOut.setHeader("qwer","retr")
//      }
//to("""https://query.yahooapis.com/v1/public/yql?q=select+*+from+yahoo.finance.xchange+where+pair+=+%22USDRUB%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=""")
//to("http:camel.apache.org/http4.html")
//setHeader("CamelRedis.Value","Rghty")
//setHeader("CamelRedis.Key",System.currentTimeMillis())