import org.apache.camel.impl.DefaultCamelContext
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.component.jms.JmsComponent

/**
  * Created by Enot on 05.06.2016.
  */
object FileToMQ extends App{
//  val connectionFactory = new MQQueueConnectionFactory {
//    setHostName(conf.getString("wmq.host"))
//    setPort(conf.getInt("wmq.port"))
//    setTransportType(1)//(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP)
//    setQueueManager(conf.getString("wmq.qmname"))
//    setChannel(conf.getString("wmq.channel"))
//  }



  val cf = new ActiveMQConnectionFactory("tcp://localhost:61616")




  val context = new DefaultCamelContext
  context.addComponent("ActiveMQ", JmsComponent.jmsComponentAutoAcknowledge(cf))
  context.addRoutes(new FileToMQRoute)
  context.start
  Thread.currentThread.join
}
