import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.languages.Languages

/**
  * Created by smakhetov on 06.06.2016.
  */

class FromFileToFileRoute extends RouteBuilder with Languages {
  """file:inbox?charset=utf-8""" ==> {
    to ("file:outbox?charset=Windows-1251")
  }
}

