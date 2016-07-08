import org.apache.camel.{CamelContext, Exchange}
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

import scala.xml.XML

/**
  * Created by Enot on 13.06.2016.
  */
object FromMQToDBApp extends App with RouteBuilderSupport {//
val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

class FromMQToDBAppRoute(context: CamelContext) extends ScalaRouteBuilder(context) {

  errorHandler(deadLetterChannel("file:error"))

  //  """quartz://groupName/timerName?cron=0/5+*+*+*+*+?""" ==>  {
  //    to ("log:123")
  //  }

  """file:inbox""" ==> {

    val setRoute = (exchange: Exchange) => {
      println("1234354")
      val body = exchange.getIn.getBody(classOf[String])
      val elem = XML.loadString(body)

      println("1234354" + (elem \\"root").text + "1234354")

      // exchange.getOut.setBody((body \\ "root").text)
      exchange.getOut.setBody("123")
    }
    process(setRoute) to """file:outbox"""
  }

}