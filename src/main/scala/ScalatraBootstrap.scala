import com.kenrailey.shorty._
import com.mchange.v2.c3p0.ComboPooledDataSource
import javax.servlet.ServletContext
import org.scalatra._
import org.slf4j.LoggerFactory
import slick.jdbc.H2Profile.api._

/**
 * Provide basic DB connection and setup
 */
class ScalatraBootstrap extends LifeCycle {

  val logger = LoggerFactory.getLogger(getClass)

  val cpds = new ComboPooledDataSource
  
  logger.info("Created c3p0 connection pool")

  /**
   * initialize servlet with db reference
   */
  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds, None)
    context.mount(new ShortyServlet(db), "/*")
  }

  /**
   * tear down connection pool
   */
  override def destroy(context: ServletContext) {
    super.destroy(context)
    logger.info("Closing c3po connection pool")
    cpds.close
  }
}
