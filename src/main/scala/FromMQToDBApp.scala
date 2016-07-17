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

  val ds = new BasicDataSource
  ds.setDriverClassName("org.h2.Driver")
  ds.setUrl("jdbc:h2:./h2db")
  //ds.setUsername("")
  //ds.setPassword("")
  val mainApp = new Main
  mainApp.bind("h2db",ds)
  val connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.3.38:61616")
  mainApp.bind("amq-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory))
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

class FromMQToDBAppRoute(context: CamelContext) extends ScalaRouteBuilder(context) {

  errorHandler(deadLetterChannel("file:error"))
//"""amq-jms:queue:TESTQ"""
  """timer:name?period=5000""" ==> {
    setBody("123")
    to("log:123")
    process((exchange: Exchange) => {
      val uuid = UUID.randomUUID
      val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
      val messageBody = exchange.getIn.getBody(classOf[String])
      exchange.getOut.setBody(s"INSERT INTO PUBLIC.MESSAGETABLE (ID, DATETIME, BODY) VALUES('$uuid', '$time', '$messageBody')")
    })
    //to ("log:123")
    to("jdbc:h2db")
   // to("log:123")
  }
}
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