/*
В данном примере будет рассмотрена маршрутизация в зависимости от содержимого сообщения.
Мы предполагаем, что на входе будет xml сообщение, которое брабатывается по-разному в зависимости от значения элемента "То"
К примеру, нам известно, что сообщение, в котором  <To>ActiveMQ</To> - нужно отправить в очередь, а <To>H2</To> - обработать каким-то образом и отправить в БД, <To>someAdress</To> - обработать еще каким-то образом.
В сообщение будет добавлен заголовок "Destination" с именем endpoint, в который надо будет отправить сообщение дальше.
Если возникнет ошибка при обработке сообщения или в таблице маршрутизации не будет соответствующего значения, то отправляем сообщение в "direct:trash"
??? - конструкция скала, которая позволяет заменить блок кода для компиляции. Предполагается замена на рабочий код.
*/


import org.apache.camel.{Exchange, CamelContext}
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import scala.xml.XML

/**
  * Created by smakhetov on 11.07.2016.
  */
class ContentOrientedRouting(context: CamelContext) extends ScalaRouteBuilder(context) {

  // При ошибках обработки сообщения отправляем сообщения в "direct:trash"
  errorHandler(deadLetterChannel("direct:trash"))

  // Опишем таблицу маршрутизации в виде Map
  val destMap = Map(
    "ActiveMQ"    -> "jms-amq:queue:inbox",
    "H2"          -> "direct:h2db",
    "someAdress"  -> "direct:outbox")
  // Вынесем обработку в отдельную функцию
  val addRoutingAction = (exchange: Exchange) => {
    // Получим значение тега "To" из XML, который пришла на вход
    val body = exchange.getIn.getBody(classOf[String])
    val xmlBody = XML.loadString(body)
    val toValue = (xmlBody \\ "To").text
    // Получим имя endpoint, если такого значения нет - отправляем в "direct:trash"
    val dest = destMap.getOrElse(toValue,"direct:trash")
    // Устанавливаем значение заголовка
    exchange.getOut.setHeader("Destination", dest)
  }

  """direct:inbox1""" ==> {
    process(addRoutingAction)
    // извлекаем из заголовка "Destination" endpoint и отправляем туда сообщение
    recipients(_.in("Destination"))
  }
  // Описываем логику для разных endpoint
  """jms-amq:queue:inbox""" ==> {???}

  """direct:h2db""" ==> {
    process((exchange: Exchange) => {???})
    to ("jdbc:h2db")
  }
  """direct:outbox""" ==> {???}

  """direct:trash""" ==> {???}

}
