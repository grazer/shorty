package com.kenrailey.shorty

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.scalatra.test.scalatest._
import org.scalatest.FunSuiteLike
import slick.jdbc.MySQLProfile.api._

/**
 * run these with "sbt test"
 */
class ShortyServletTests extends ScalatraSuite with FunSuiteLike {

  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds, None)
  addServlet(new ShortyServlet(db), "/*")

  /**
   * check that form looks OK
   */
  test("GET / should return status 200"){
    get("/"){
      status should equal (200)
      body should include ("Shorten It!")
    }
  }

  /**
   * check that form looks OK
   */
  test("GET /:id with bad data should display error"){
    get("/xxxxxxxxxxx"){
      status should equal (200)
      body should include ("Link invalid")
    }
  }

  /**
   * check that POST catches bad links
   */
  test("POST / with bad link"){
    post("/", Map("URL" -> "bad link")){
      status should equal (200)
      body should include ("Link invalid")
    }
  }

  /**
   * check that POST works with correct links
   */
  test("POST / with good link"){
    post("/", Map("URL" -> "http://google.com")){
      status should equal (200)
      body should not include ("Link invalid")
    }
  }
}
