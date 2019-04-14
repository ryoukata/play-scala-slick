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
                               repository2: MessageRepository,
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
    // foldメソッドのエラー処理でバリデーションチェックが可能
    Person.personForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.add("error.", errorForm)))
      },
      person => {
        repository.create(person.name, person.mail, person.tel).map { _ =>
          // flashingでフラッシュメッセージの表示（キーと値を設定し、キーを指定して表示する）
          Redirect(routes.HomeController.index).flashing("success"->"エンティティを作成しました！")
        }
      }
    )
  }

  // 特定のIDのレコードだけを表示
  def show(id: Int) = Action.async { implicit request =>
    repository.get(id).map { person =>
      Ok(views.html.show(
        "People Data.", person
      ))
    }
  }

  // レコードの編集
  def edit(id: Int) = Action.async { implicit request =>
    repository.get(id).map { person =>
      val fdata: Form[PersonForm] = Person.personForm.fill(PersonForm(person.name, person.mail, person.tel))
      Ok(views.html.edit(
        "Edit Person.", fdata, id
      ))
    }
  }

  // レコードの更新
  def update(id: Int) = Action.async { implicit request =>
    Person.personForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.edit("error.", errorForm, id)))
      },
      person => {
        repository.update(id, person.name, person.mail, person.tel).map { _ =>
          Redirect(routes.HomeController.index)
        }
      }
    )
  }

  // レコードの削除画面表示
  def delete(id: Int) = Action.async { implicit request =>
    repository.get(id).map { person =>
      Ok(views.html.delete(
        "Delete Person.", person, id
      ))
    }
  }

  // レコードの削除
  def remove(id: Int) = Action.async { implicit request =>
    repository.delete(id).map { _ =>
      Redirect(routes.HomeController.index)
    }
  }

  // レコードの検索画面の表示
  def find() = Action { implicit request =>
      Ok(views.html.find(
        "Find Data.", Person.personFind, Seq[Person]()
      ))
  }

  // レコードの検索
  def search() = Action.async { implicit request =>
    Person.personFind.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.find("error.", errorForm, Seq[Person]())))
      },
      find => {
        repository.find(find.find).map { result =>
            Ok(views.html.find(
              "Find: " + find.find, Person.personFind, result
            ))
        }
      }
    )
  }

  // メッセージ画面の表示
  def message() = Action.async { implicit request =>
    repository2.listMsgWithP().map { messages =>
      Ok(views.html.message(
        "Message List.",
        Message.messageForm, messages
      ))
    }
  }

  // メッセージの作成
  def addmessage() = Action.async { implicit request =>
    Message.messageForm.bindFromRequest.fold(
      errorForm => {
        repository2.listMsgWithP().map { messages =>
          Ok(views.html.message(
            "ERROR.",
            errorForm, messages
          ))
        }
      },
      message => {
        repository2.createMsg(message.personId, message.message).map { _ =>
          Redirect(routes.HomeController.message).flashing("success"->"エンティティを作成しました！")
        }
      }
    )
  }

}
