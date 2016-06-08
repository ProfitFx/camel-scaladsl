import org.apache.camel.Exchange
import org.apache.camel.impl.{DefaultCamelContext, SimpleRegistry}
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.camel.scala.dsl.languages.Languages
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
  * Created by smakhetov on 07.06.2016.
  */
object FromHTTPToRedis{// extends App{


  val registry = new SimpleRegistry
  registry.put("stringSerializer",new StringRedisSerializer())


  val context = new DefaultCamelContext(registry)
  context.addRoutes(FromHTTPToRedisRoute)
  context.start
  Thread.currentThread.join


  object FromHTTPToRedisRoute extends ScalaRouteBuilder(context) with Languages{

    """quartz://groupName/timerName?cron=0+0/1+*+*+*+?""" ==> {

      //val addr = "http:www.google.com/finance/info?q=NASDAQ%3aGOOG"
      to("http://www.google.com/finance/info?q=CURRENCY%3aUSDRUB")
      process((exchange: Exchange) => {
        exchange.getOut.setHeader("CamelRedis.Key",System.currentTimeMillis())
        exchange.getOut.setHeader("CamelRedis.Value",exchange.getIn.getBody(classOf[String]))
      })
      to ("""spring-redis://172.16.7.58:6379?serializer=#stringSerializer""")
    }
  }
}

//      val myProcessor = (exchange: Exchange) => {
//        exchange.getOut.setHeader("qwer","retr")
//      }
//to("""https://query.yahooapis.com/v1/public/yql?q=select+*+from+yahoo.finance.xchange+where+pair+=+%22USDRUB%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=""")
//to("http:camel.apache.org/http4.html")
//setHeader("CamelRedis.Value","Rghty")
//setHeader("CamelRedis.Key",System.currentTimeMillis())