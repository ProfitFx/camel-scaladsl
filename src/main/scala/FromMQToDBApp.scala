/*
В отдельный пример выделил работу с очередью и БД.
Проблема в том, что нужно как то настроить их
Если в предыдущих примерах настрока проводилась с помощью параметров в строке  endpoint, то здесь нужно заранее создать объект,
сделать на его основе компонент и использовать далее.
Различие очереди и БД в том, что для БД используем BasicDataSource, и создаем dataSourceName, который является частью URI camel-jdbc
а для очереди используем JmsComponent и создаем на его основе новый компонент, с кастомным названием.
 */



import java.text.SimpleDateFormat
import java.util.{UUID, Date}
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}
import org.apache.camel.{CamelContext, Exchange}
// Для создания подключения к БД необходим BasicDataSource
import org.apache.commons.dbcp2.BasicDataSource
// Для работы с месседж-брокером нужно импортировать соответствующий ConnectionFactory класс
import org.apache.activemq.ActiveMQConnectionFactory



/**
  * Created by Enot on 13.06.2016.
  */

object FromMQToDBApp extends App with RouteBuilderSupport {
  val mainApp = new Main
  // Для работы с БД необходимо создать объект и передать ему свойства соединения
  val ds = new BasicDataSource
  ds.setDriverClassName("org.h2.Driver")
  ds.setUrl("jdbc:h2:./h2db")
  // Добавим бд в приложение, далее в названии получателя будем использовать "h2db"
  mainApp.bind("h2db",ds)
  //Для работы с очередью создадим MQConnectionFactory
  val cf= new ActiveMQConnectionFactory("tcp://192.168.3.38:61616")
  // создадим компонент для работы с очередью
  mainApp.bind("amq-jms", JmsComponent.jmsComponentAutoAcknowledge(cf))
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

// Данный класс реализует чтение сообщения из очереди и запись его в БД
class FromMQToDBAppRoute(context: CamelContext) extends ScalaRouteBuilder(context) {
  // Читаем сообщение из очереди. Компонент называется также, как мы его назвали ранее - "amq-jms", имя очереди передается как параметр
  // Для каждого брокера необходимо создавать свой компонент
  """amq-jms:queue:TESTQ""" ==> {
    process((exchange: Exchange) => {
      // Генерим uuid, дату/время и извлекаем тело сообщения
      val uuid = UUID.randomUUID
      val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
      val messageBody = exchange.getIn.getBody(classOf[String])
      // формируем запрос с параметрами
      exchange.getOut.setBody(s"INSERT INTO PUBLIC.MESSAGETABLE (ID, DATETIME, BODY) VALUES('$uuid', '$time', '$messageBody')")
    })
    // Отправляем подготовленный запрос в бд
    // Компонент называется jdbc, далее указывается конкретный DataSource
    to("jdbc:h2db")

  }
}

/*
Для других БД нужно, соответственно, добавить библиотеку в build.sbt,
определить имя класса драйвера, url, возможно понадобятся другие свойства подключения, к примеру,
Имя пользователя и пароль.
Для Postgres

    val ds = new BasicDataSource {
      setDriverClassName("org.postgresql.Driver")
      setUrl(conf.getString("jdbc:postgresql://myhost:5432/mydb"))
      setUsername(conf.getString("myusername"))
      setPassword(conf.getString("mypassword"))
      }

      build.sbt
      libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1207"

С очередями несколько сложнее.
Для некоторых библиотеки не открыты для доступа в репозиториях и используются *.jar файлы, подкладываемыее в папку lib проекта.
В любом случае, нужно получить connection factory
К примеру, для IBM Websphere MQ
    val cf = new MQQueueConnectionFactory {
      setHostName("myhost")
      setPort(1414)
      setTransportType(1)
      setQueueManager("myqmname")
      setChannel("mychannel")
    }

    Для oracle weblogic jms еще интереснее
    Если взять за основу инструкцию https://blogs.oracle.com/soaproactive/entry/how_to_create_a_simple
то объявление компонента будет такое:

  val env = new util.Hashtable[String, String]
  env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory")
  env.put(javax.naming.Context.PROVIDER_URL, "t3://myhost:7001")
  val ic: InitialContext = new InitialContext(env)
  val connectionFactory = ic.lookup("jms/TestConnectionFactory").asInstanceOf[QueueConnectionFactory]
  // где jms/TestConnectionFactory - jndi для ConnectionFactory"

  mainApp.bind("ora-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))

    а endpoint URI удет такого формата: """ora-jms:queue:./TestJMSModule!TestJMSQueue"""
    где ./ обозначает текущий сервер, "TestJMSModule" JNDI имя модуля "TestJMSQueue" - JNDI имя очереди

 */




//errorHandler(deadLetterChannel("file:error"))
//to ("log:123")
// to("log:123")
//"""timer:name?period=5000""" ==> {
//    setBody("123")
//    to("log:123")
//ds.setUsername("")
//ds.setPassword("")
//new java.util.Date()
//1970-01-01 05:00:00
//  val ds = new BasicDataSource
//  ds.setDriverClassName("org.h2.Driver")
//  ds.setUrl("jdbc:h2:./h2db")
//  //ds.setUsername("")
//  //ds.setPassword("")
//  val mainApp = new Main
//  mainApp.bind("h2db",ds)
//  val context = mainApp.getOrCreateCamelContext
//  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
//  mainApp.run
//}
//
//class FromMQToDBAppRoute(context: CamelContext) extends ScalaRouteBuilder(context) {
//
// // errorHandler(deadLetterChannel("file:error"))
//
//  val dbInsert = (exchange: Exchange) => {
//    val sqlStatement = "INSERT  TESTTABLE"
//    exchange.getOut.setBody(sqlStatement)
//  }
//
//    """quartz://groupName/timerName?cron=0/5+*+*+*+*+?""" ==>  {
//      to("log:123")
//      setBody("select count(*) from MESSAGETABLE")
//      to("jdbc:h2db")
//      to("log:124")
//    }
//}

//import com.ibm.mq.jms.MQQueueConnectionFactory
//import org.apache.camel.component.jms.JmsComponent
//import org.apache.camel.main.Main
//import org.apache.camel.scala.dsl.builder.RouteBuilderSupport
//import org.apache.commons.dbcp2.BasicDataSource
//
///**
//  * Created by smakhetov on 15.07.2016.
//  */
//object MainApp extends App with RouteBuilderSupport {//
//
//  val ds = new BasicDataSource
//  ds.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver")
//  ds.setUrl("jdbc:ucanaccess://TestDB.mdb")
//  //ds.setUsername("")
//  //ds.setPassword("")
//  val mainApp = new Main
//  mainApp.bind("accdb",ds)
//
//  val connectionFactory = new MQQueueConnectionFactory {
//    setHostName("impop-ig-stand")
//    setPort(1414)
//    setTransportType(1)//(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP)
//    setQueueManager("SYNC.IISVVT.QM")
//    setChannel("APP.SVRCONN")
//  }
//
//  mainApp.bind("wmq-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
//  val context = mainApp.getOrCreateCamelContext
//  mainApp.addRouteBuilder(new FromFileToMQ(context))
//  mainApp.addRouteBuilder(new FromMQToFile(context))
//  mainApp.run
//}



//  """file:inbox""" ==> {
//
//    val setRoute = (exchange: Exchange) => {
//      println("1234354")
//      val body = exchange.getIn.getBody(classOf[String])
//      val elem = XML.loadString(body)
//
//      println("1234354" + (elem \\"root").text + "1234354")
//
//      // exchange.getOut.setBody((body \\ "root").text)
//      exchange.getOut.setBody("123")
//    }
//    process(setRoute) to """file:outbox"""
//  }



//  """file:inbox""" ==> {
//
//    val setDestHeader = (exchange: Exchange) => {
//      val routes = Map("mq send" -> "amq:queue:in","db insert" -> "jdbc:h2")
//
//      val body = exchange.getIn.getBody(classOf[String])
//      val xmlBody = XML.loadString(body)
//      val action = (xmlBody \\ "action").text
//      val to = routes.getOrElse(action,"file:uncnownAction")
//      exchange.getOut.setBody(to)
//      val content = (xmlBody \\ "content").text
//      exchange.getOut.setBody(content)
//    }
//
//    to ("log:123")
//    process(setDestHeader) to "log:124"
//    // setHeader("dest","file:dest")
//    //  recipients(_.in("dest"))
//  }
//}

/*
CREATE TABLE MESSAGETABLE(
	ID UUID NOT NULL PRIMARY KEY,
	DATETIME TIMESTAMP,
	BODY VARCHAR(65536)
);
 */