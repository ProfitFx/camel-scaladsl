

/**
  * Created by smakhetov on 06.06.2016.
  */



/*
   Создадим простое приложение, которое будет использовать компонент http://camel.apache.org/file2.html из пакета camel-core.
   Приложение будет состоять из объекта, запускающего приложение FromFileToFileApp, и класса FromFileToFileRoute, в котором описаны маршруты.
   Класс с маршрутами можно вынести в отдельный файл.
   файл src/main/scala/FromFileToFileApp.scala

  */

import org.apache.camel.CamelContext
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{ScalaRouteBuilder, RouteBuilderSupport}

//extends App определяет запускаемый объект для приложения и позволяет производить запуск командой "sbt run"
object FromFileToFileApp extends App with RouteBuilderSupport {
//Создаем Camel Main класс и контекст в нем
val mainApp = new Main
  val context = mainApp.getOrCreateCamelContext
  // Привязываем классы с маршрутами
  mainApp.addRouteBuilder(new FromFileToFileRoute(context))
  // Запускаем
  mainApp.run
}

class FromFileToFileRoute(context: CamelContext) extends ScalaRouteBuilder(context) {
  // Читаем содержимое файла в одной кодировке из папки "inbox"
  """file:inbox?charset=utf-8""" ==> {
    // Пишем в другой кодировке в директорию "outbox"
    to ("file:outbox?charset=Windows-1251")
  }
}

class FromFileToFileRoute1(context: CamelContext) extends ScalaRouteBuilder(context) {
  // Читаем содержимое файла в одной кодировке из папки "inbox"
  """file:inbox?charset=utf-8""" ==> {
    // Пишем в другой кодировке в директорию "outbox"
    to ("file:outbox?charset=Windows-1251")
  }
}

/*
   в данном маршруте не происходит никаких преобразований с содержимым сообщения, отсутствует маршрутизация.
   после запуска приложеия командой "sbt run" в папке проекта будут автоматически созданы папки "inbox", "outbox"
   При попадании в директорию "inbox" файл автоматически считывается - исчезает из папки, и появляется вдиректории "outbox" в другой кодировке.

  */