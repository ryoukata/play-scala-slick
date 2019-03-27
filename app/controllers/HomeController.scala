package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class HomeController @Inject()(repository: PersonRepository,
                               cc: MessagesControllerComponents)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def index() = Action.async { implicit request =>
    repository.list().map { people =>
      Ok(views.html.index(
        "People Data.", people
      ))
    }
  }

  // レコード追加画面の表示
  def add() = Action { implicit request =>
    Ok(views.html.add(
      "フォームを入力してください。",
      Person.personForm
    ))
  }

  // レコード追加処理
  def create() = Action.async { implicit request =>
    Person.personForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.add("error.", errorForm)))
      },
      person => {
        repository.create(person.name, person.mail, person.tel).map { _ =>
          Redirect(routes.HomeController.index)
        }
      }
    )
  }

}
