import org.apache.camel.CamelContext
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by Enot on 13.06.2016.
  */
object FromMQToDBApp extends App with RouteBuilderSupport {
val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

class FromMQToDBAppRoute(override val context: CamelContext) extends ScalaRouteBuilder(context) {
  //Читаем содержимое файла в одной кодировке из папки "inbox"
  """quartz://groupName/timerName?cron=0/5+*+*+*+*+?""" ==>  {
    //Пишем в другой кодировке в директорию "outbox"
    to ("log:123")
  }
}