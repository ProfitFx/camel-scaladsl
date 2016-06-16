import org.apache.camel.CamelContext
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}

/**
  * Created by smakhetov on 06.06.2016.
  */



/**
  * Создадим простое приложение, которое будет использовать компонент http://camel.apache.org/file2.html из пакета camel-core
  * Приложение будет состоять из класса, в котором описаны маршруты и объекта, в котором создается Camel контекст и в котором происходит привязка к классу с маршрутами.
  * extends App позволяет производить запуск командой "sbt run"
  */

object FromFileToFileApp extends RouteBuilderSupport { //App with
//Создаем Camel Main класс и контекст в нем
val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  // Привязываем классы с маршрутами
  mainApp.addRouteBuilder(new FromFileToFileRoute(context))
  // Запускаем
  mainApp.run
}


class FromFileToFileRoute(override val context: CamelContext) extends ScalaRouteBuilder(context) {
  // Читаем содержимое файла в одной кодировке из папки "inbox"
  """file:inbox?charset=utf-8""" ==> {
    // Пишем в другой кодировке в директорию "outbox"
    to ("file:outbox?charset=Windows-1251")
  }
}

/**
в данном маршруте не происходит никаких преобразований с содержимым сообщения, отсутствует маршрутизация.
после запуска приложеия командой "sbt run" в папке проекта будут автоматически созданы папки "inbox", "outbox"
  При попадании в директорию "inbox" файл автоматически считывается - исчезает из папки, и появляется вдиректории "outbox" в другой кодировке.

  */