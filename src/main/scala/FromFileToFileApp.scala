import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder

/**
  * Created by smakhetov on 06.06.2016.
  */



object FromFileToFileApp{

  val context = new DefaultCamelContext
  context.addRoutes(FromFileToFileRoute)
  context.start
  Thread.currentThread.join

  object FromFileToFileRoute extends ScalaRouteBuilder(context){
    """file:inbox?charset=utf-8""" ==> {
      to ("file:outbox?charset=Windows-1251")
    }
  }
}

