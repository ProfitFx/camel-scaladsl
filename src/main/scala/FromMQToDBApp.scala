import org.apache.camel.CamelContext
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by Enot on 13.06.2016.
  */
object FromMQToDBApp extends  RouteBuilderSupport {//App with
  val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

class FromMQToDBAppRoute(override val context: CamelContext) extends ScalaRouteBuilder(context) {

 errorHandler(deadLetterChannel("mock:error"))

  """quartz://groupName/timerName?cron=0/5+*+*+*+*+?""" ==>  {
    to ("log:123")
  }
}