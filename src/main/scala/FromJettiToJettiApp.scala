import org.apache.camel.{Exchange, CamelContext}
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by smakhetov on 16.06.2016.
  */

object JettyApp extends App with RouteBuilderSupport{
  val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new JettyRoute(context))
  mainApp.run
}


class JettyRoute(override val context: CamelContext) extends ScalaRouteBuilder(context) {

  """jetty:http://0.0.0.0:1234/myapp/myservice""" ==> {
    process((exchange: Exchange) => {
      val fooParam = exchange.getIn.getHeader("foo")
      def responseText = fooParam match {
        case null => "Foo parameter not found"
        case _ => s"Foo parameter = $fooParam"
      }
      exchange.getOut().setBody(s"<html><body>$responseText</body></html>")
    })
  }
}

object testpattern extends App {
  val st = "2004"
  val date = """(\d+)""".r
  def s = st match {
    case date(year) => s"$year was a good year for PLs."
    case _ => "KO"
  }
  println(s)
}


object testpattern1 extends App {
  val st = "2134"
  val date = """21534"""
  def s = st match {
    case zz => s"$zz OK"
    case _ => "KO"
  }
  println(s)
}