import org.apache.camel.{CamelContext, Exchange}
import org.apache.camel.main.Main
import org.apache.camel.scala.dsl.builder.{RouteBuilderSupport, ScalaRouteBuilder}
import org.apache.commons.dbcp2.BasicDataSource

import scala.xml.XML

/**
  * Created by Enot on 13.06.2016.
  */
object FromMQToDBApp extends App with RouteBuilderSupport {//

  val ds = new BasicDataSource
  ds.setDriverClassName("org.h2.Driver")
  ds.setUrl("jdbc:h2:./h2db")
  //ds.setUsername("")
  //ds.setPassword("")
  val mainApp = new Main
  mainApp.bind("h2db",ds)
  val context = mainApp.getOrCreateCamelContext
  mainApp.addRouteBuilder(new FromMQToDBAppRoute(context))
  mainApp.run
}

class FromMQToDBAppRoute(context: CamelContext) extends ScalaRouteBuilder(context) {

 // errorHandler(deadLetterChannel("file:error"))

  val dbInsert = (exchange: Exchange) => {
    val sqlStatement = "INSERT  TESTTABLE"
    exchange.getOut.setBody(sqlStatement)
  }

    """quartz://groupName/timerName?cron=0/5+*+*+*+*+?""" ==>  {
      to("log:123")
      setBody("select count(*) from MESSAGETABLE")
      to("jdbc:h2db")
      to("log:124")
    }
}


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