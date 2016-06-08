import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder

/**
  * Created by smakhetov on 06.06.2016.
  */



object FromFileToFileApp {//extends App {
  val context = new DefaultCamelContext
  context.addRoutes(FromFileToFileRoute)
 // FromFileToFileRoute


    object FromFileToFileRoute extends ScalaRouteBuilder(context) {
      """file:inbox?charset=utf-8""" ==> {
        to ("file:outbox?charset=Windows-1251")
      }
    }
  context.start
  Thread.currentThread.join


  //addRoutesToCamelContext(context)
}

//object FromFileToFileApp extends App {
//  val context = new DefaultCamelContext
//  // FromFileToFileRoute
//  context.start
//  Thread.currentThread.join
//}
//
//
//object FromFileToFileRoute extends ScalaRouteBuilder(FromFileToFileApp.context) {
//  """file:inbox?charset=utf-8""" ==> {
//    to ("file:outbox?charset=Windows-1251")
//  }
//  addRoutesToCamelContext(context)
//}

// context.addRoutes(new FromFileToFileRoute)