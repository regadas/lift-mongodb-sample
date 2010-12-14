package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._
import net.liftweb.mongodb._

import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    MongoDB.defineDb(
      DefaultMongoIdentifier,
      MongoAddress(MongoHost("127.0.0.1"), "lift_app")
    ) 
    
    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

  }
}
