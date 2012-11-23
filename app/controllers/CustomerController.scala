package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json

import models.{AppDB, Customer}

object CustomerController extends Controller {

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "address" -> optional(text)
    )(Customer.apply)(Customer.unapply)
  )

  def index = Action {
    Ok(views.html.customer(Customer.findAll(), form))
  }

  def persist = Action { implicit request =>
    form.bindFromRequest.value map { customer =>
      inTransaction(AppDB.customerTable insert customer)
      Redirect(routes.CustomerController.index)
    } getOrElse BadRequest
  }

  def remove(id: Long) = Action {
    Customer.remove(id)
    Redirect(routes.CustomerController.index)
  }

}