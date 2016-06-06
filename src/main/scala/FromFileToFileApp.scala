import org.apache.camel.impl.DefaultCamelContext

/**
  * Created by smakhetov on 06.06.2016.
  */



object FromFileToFileApp extends App{
  val context = new DefaultCamelContext
  context.addRoutes(new FromFileToFileRoute)
  context.start
  Thread.currentThread.join
}