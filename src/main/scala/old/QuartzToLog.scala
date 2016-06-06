package old

import org.apache.camel.impl.DefaultCamelContext

/**
  * Created by Enot on 05.06.2016.
  */
object QuartzToLog {

//  val logger: Logger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
//  logger.setLevel(Level.INFO);

  val context = new DefaultCamelContext
  context.addRoutes(new QuartzToLogRoute)
  context.start
  Thread.currentThread.join
}
