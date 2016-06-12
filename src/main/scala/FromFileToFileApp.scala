import org.apache.camel.CamelContext
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by smakhetov on 06.06.2016.
  */

object FromFileToFileApp extends RouteBuilderSupport { //App with
  //Создаем класс и контекст в нем
  val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  //Привязываем классы с маршрутами
  mainApp.addRouteBuilder(new FromFileToFileRoute(context))
  //Запускаем приложение
  mainApp.run
}

class FromFileToFileRoute(override val context: CamelContext) extends ScalaRouteBuilder(context) {
  //Читаем содержимое файла в одной кодировке из папки "inbox"
  """file:inbox?charset=utf-8""" ==> {
    //Пишем в другой кодировке в директорию "outbox"
    to ("file:outbox?charset=Windows-1251")
  }
}


//object FromFileToFileApp {//extends App {
//  val context = new DefaultCamelContext
//  context.addRoutes(FromFileToFileRoute)
// // FromFileToFileRoute
//
//
//    object FromFileToFileRoute extends ScalaRouteBuilder(context) {
//      """file:inbox?charset=utf-8""" ==> {
//        to ("file:outbox?charset=Windows-1251")
//      }
//    }
//  context.start
//  Thread.currentThread.join
//
//
//  //addRoutesToCamelContext(context)
//}

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