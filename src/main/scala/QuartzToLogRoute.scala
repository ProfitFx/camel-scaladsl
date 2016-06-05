/**
  * Created by Enot on 05.06.2016.
  */

import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.scala.dsl.languages.Languages

class QuartzToLogRoute extends RouteBuilder with Languages {

  """quartz://groupName/timerName?cron=0/2+*+*+*+*+?""" ==>
  //"""quartz://groupName/timerName?trigger.repeatInterval=246&trigger.repeatCount=10000""" ==>
  {
  //  to("log:123")
    log("456")
  }

//  """timer://foo?period=600""" ==> {
//    to("log:123")
//  }
}
