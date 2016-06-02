/**
  * Created by smakhetov on 02.06.2016.
  */
/**
  * Created by smakhetov on 25.02.2016.
  */

//

import com.typesafe.config.ConfigFactory
import org.apache.camel.Exchange
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.languages.Languages


class JMSRead extends RouteBuilder with Languages{

  val conf = ConfigFactory.load()
  val targettable = conf.getString("db.targettable")
  val targettable1 = conf.getString("db1.targettable")

  """test-jms:queue:PUOP.EVENTS.IN""" ==> {
    //id("jms_message")
    log("Message processing started")
    //to("file:target/out")
    to("direct:parse","direct:parse1")
  }

  """direct:parse""" ==> {

    val myProcessor = (exchange: Exchange) => {
      val body = xml.XML.loadString(exchange.getIn.getBody(classOf[String]))
      val id = java.util.UUID.randomUUID()
      val idref =         (body \ "Body" \\ "IDRef").text
      val segmentref =    (body \ "Body" \\ "SourceSegment").text
      val operationdt =   (body \ "Body" \\ "OperationDt").text
      val source =        (body \ "Body" \\ "Source").text
      val receiver =      (body \ "Body" \\ "Receiver").text
      val msg =           (body \ "Body" \\ "Msg").text
      val status =        (body \ "Body" \\ "Status").text
      val trackid =       (body \ "Body" \\ "TrackID").text
      val accepttime =    (body \ "Body" \\ "AcceptTime").text
      val From =          (body \ "Body" \\ "From").text
      val fromsegment =   (body \ "Body" \\ "FromSegment").text
      val To =            (body \ "Body" \\ "To").text
      val tosegment =     (body \ "Body" \\ "To" \ "@Segment").text
      val errorcode =     (body \ "Body" \\ "ErrorCode").text
      val errortxt =      (body \ "Body" \\ "ErrorTxt").text
      val messageid =     (body \ "Body" \\ "MessageID").text
      val relatesto =     (body \ "Body" \\ "RelatesTo").text
      val code =          (body \ "Body" \\ "Code").text
      val version =       (body \ "Body" \\ "Version").text
      val procedurecode = (body \ "Body" \\ "ProcedureCode").text
      val transactioncode=(body \ "Body" \\ "TransactionCode").text
      val messagetype =   (body \ "Body" \\ "MessageType").text
      val messagecode =   (body \ "Body" \\ "MessageCode").text
      val replyto =       (body \ "Body" \\ "ReplyTo").text
      val action =        (body \ "Body" \\ "Action").text
      val relatesaction = (body \ "Body" \\ "RelatesTo" \ "@RelatesAction").text
      val procedureid =   (body \ "Body" \\ "ProcedureID").text
      val conversationid= (body \ "Body" \\ "ConversationID").text


      // val sql = s"INSERT INTO public.camel_test (id, text) VALUES('${uuid}', '${messageid}');"
      val sql = s"""INSERT INTO ${targettable}
        (id, idref, segmentref, operationdt, source,
         receiver, msg, status, trackid, accepttime,
         "From", fromsegment, "To", tosegment, errorcode,
         errortxt, messageid, relatesto, code, "version",
         procedurecode, transactioncode, messagetype, messagecode, replyto,
         "action", relatesaction, procedureid, conversationid)
         VALUES('${id}', '${idref}', '${segmentref}', '${operationdt}', '${source}',
         '${receiver}', '${msg}', '${status}', '${trackid}', '${accepttime}',
         '${From}', '${fromsegment}', '${To}', '${tosegment}', '${errorcode}',
         '${errortxt}', '${messageid}', '${relatesto}', '${code}', '${version}',
         '${procedurecode}', '${transactioncode}', '${messagetype}', '${messagecode}', '${replyto}',
         '${action}', '${relatesaction}', '${procedureid}', '${conversationid}');"""

      //log("insert")
      exchange.getOut.setBody(sql)
    }

    // log("Insert to DB")
    process(myProcessor) to ("jdbc:postgresRoute")
    log("Message processing db finished")
  }

  """direct:parse1""" ==> {

    val myProcessor = (exchange: Exchange) => {
      val body = xml.XML.loadString(exchange.getIn.getBody(classOf[String]))
      val id = java.util.UUID.randomUUID()
      val idref =         (body \ "Body" \\ "IDRef").text
      val segmentref =    (body \ "Body" \\ "SourceSegment").text
      val operationdt =   (body \ "Body" \\ "OperationDt").text
      val source =        (body \ "Body" \\ "Source").text
      val receiver =      (body \ "Body" \\ "Receiver").text
      val msg =           (body \ "Body" \\ "Msg").text
      val status =        (body \ "Body" \\ "Status").text
      val trackid =       (body \ "Body" \\ "TrackID").text
      val accepttime =    (body \ "Body" \\ "AcceptTime").text
      val From =          (body \ "Body" \\ "From").text
      val fromsegment =   (body \ "Body" \\ "FromSegment").text
      val To =            (body \ "Body" \\ "To").text
      val tosegment =     (body \ "Body" \\ "To" \ "@Segment").text
      val errorcode =     (body \ "Body" \\ "ErrorCode").text
      val errortxt =      (body \ "Body" \\ "ErrorTxt").text
      val messageid =     (body \ "Body" \\ "MessageID").text
      val relatesto =     (body \ "Body" \\ "RelatesTo").text
      val code =          (body \ "Body" \\ "Code").text
      val version =       (body \ "Body" \\ "Version").text
      val procedurecode = (body \ "Body" \\ "ProcedureCode").text
      val transactioncode=(body \ "Body" \\ "TransactionCode").text
      val messagetype =   (body \ "Body" \\ "MessageType").text
      val messagecode =   (body \ "Body" \\ "MessageCode").text
      val replyto =       (body \ "Body" \\ "ReplyTo").text
      val action =        (body \ "Body" \\ "Action").text
      val relatesaction = (body \ "Body" \\ "RelatesTo" \ "@RelatesAction").text
      val procedureid =   (body \ "Body" \\ "ProcedureID").text
      val conversationid= (body \ "Body" \\ "ConversationID").text


      // val sql = s"INSERT INTO public.camel_test (id, text) VALUES('${uuid}', '${messageid}');"
      val sql = s"""INSERT INTO ${targettable1}
        (id, idref, segmentref, operationdt, source,
         receiver, msg, status, trackid, accepttime,
         "From", fromsegment, "To", tosegment, errorcode,
         errortxt, messageid, relatesto, code, "version",
         procedurecode, transactioncode, messagetype, messagecode, replyto,
         "action", relatesaction, procedureid, conversationid)
         VALUES('${id}', '${idref}', '${segmentref}', '${operationdt}', '${source}',
         '${receiver}', '${msg}', '${status}', '${trackid}', '${accepttime}',
         '${From}', '${fromsegment}', '${To}', '${tosegment}', '${errorcode}',
         '${errortxt}', '${messageid}', '${relatesto}', '${code}', '${version}',
         '${procedurecode}', '${transactioncode}', '${messagetype}', '${messagecode}', '${replyto}',
         '${action}', '${relatesaction}', '${procedureid}', '${conversationid}');"""

      //log("insert")
      exchange.getOut.setBody(sql)
    }

    // log("Insert to DB")
    process(myProcessor) to ("jdbc:postgresRoute1")
    log("Message processing db1 finished")
  }

}


