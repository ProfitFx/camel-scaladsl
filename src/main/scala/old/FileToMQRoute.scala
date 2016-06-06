package old

import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.languages.Languages

/**
  * Created by Enot on 05.06.2016.
  */
class FileToMQRoute extends RouteBuilder with Languages{


  """quartz://groupName/timerName?trigger.repeatInterval=2000&trigger.repeatCount=-1""" ==>
    {
      setBody("1234")
      to("ActiveMQ:queue:OrdersQueue")
      log("456")
    }
//  "ActiveMQ:queue:OrdersQueue" ==> {
//    to("log:123")
//  }

}
