import org.apache.camel.{Exchange, CamelContext}
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by smakhetov on 16.06.2016.
  */

/**
  * Cв данном примере с помощь. компонента jetty реализуем простой http сервер,
  * который получает get запрос с параметром и отдает значение параметра.
  * Добавим немного патерн-матчинга, чтобы отдавать значение в зависимости от наличия и формата передаваемого параметра
  * Предполагается, что параметр, uuid, должен передаваться в соответствующем формате.
  *
  */

object JettyApp extends  RouteBuilderSupport{ //App with
  val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new JettyRoute(context))
  mainApp.run
}

class JettyRoute(context: CamelContext) extends ScalaRouteBuilder(context) {
  // Определяем порт и адрес сервиса
  """jetty:http://0.0.0.0:1234/myapp/myservice""" ==> {
    process((exchange: Exchange) => {
      // Извлекаем значение параметра uuid из get запроса к сервису
      val uuidParam = exchange.getIn.getHeader("uuid")
      // Определяем паттерн для параметра
      val pattern = """[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}""".r
      // Возвращаем ответ в зависимости от извлеченного значения
      def responseText = uuidParam match {
        case null => "Uuid parameter not found"
        case pattern() => s"$uuidParam"
        case _ => s"Uuid parameter format is not valid"
      }
      // Определяем тип возвращаемого контента как xml
      exchange.getOut().setHeader(Exchange.CONTENT_TYPE,"text/xml; charset=utf-8")
      // Возвращаем xml с ответом.
      exchange.getOut().setBody(<uuid>{responseText}</uuid>)
      //вариант отправки параметра как строки s"<uuid>$responseText</uuid>" тоже рабочий.
    })
  }
}
//Примеры запросов для проверки
//http://localhost:1234/myapp/myservice?uuid=2a577d52-e5a1-4da5-96e5-bdba1f68e6f1
//http://localhost:1234/myapp/myservice?uuid=123
//http://localhost:1234/myapp/myservice
//http://localhost:1234/myapp/myservice?guid=2a577d52-e5a1-4da5-96e5-bdba1f68e6f
// соответственно, формирование ответа можно реализовать в соответствии с пожеланиями.
