package com.kenrailey.shorty

import org.scalatra._
import slick.jdbc.MySQLProfile.api._


/**
 * We only have a single table, named "links"
 * define the schema here
 */
object Tables {

  // Definition of the Links table
  class Links(tag: Tag) extends Table[(Option[Int], String)](tag, "links") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def url = column[String]("url")
    def * = (id.?, url)
  }

  // Table query for the Links table, represents all tuples
  val links = TableQuery[Links]
}


/**
 * servlet definition
 */
class ShortyServlet(db:Database) extends ScalatraServlet with FutureSupport {

  // converting to base 36 makes the url slightly shorter
  val ConvertBase = 36
   
  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  /**
   * GET index page just shows a form
   */
  get("/") {
    views.html.index()
  }

  /**
   * GET with a path attempts to redirect
   */
  get("/:id") {
    try {
      val id = Integer.parseInt(params("id"), ConvertBase)
      db.run(Tables.links.filter(_.id === id).result) map { link =>
        if (link.isEmpty) {
          // we can't find the id sent in
          views.html.index("","Unable to find link "+id)
        } else {
          // link is legit, so redirect (302)
          redirect(link.head._2)
        }
      }
    } catch {
      // if we can't parse the id, the return to form
      case e:Exception => views.html.index("","Link invalid") 
    }
  }

  /**
   * POST to index page generates a new short URL
   */
  post("/") {

    // this will be the base of our shortened URL
    val localUrl = request.getRequestURL()

    // URL to shorten
    val url = params("URL")

    // only accept http or https links
    if (url.indexOf("http") < 0) {
      views.html.index("", "Link invalid")
    } else {

      // store the URL and fetch new id
      db.run((Tables.links returning Tables.links.map(_.id)) += (None, url)) map { linkId =>
        views.html.index(localUrl + Integer.toString(linkId, ConvertBase))
      }
    }
  }

  /**
   * this route just creates our db table
   * obviously a production site wouldn't have this
   */
  get("/create-db") {
     db.run(Tables.links.schema.create)
  }
}
