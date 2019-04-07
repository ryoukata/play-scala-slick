package models

import play.api.data.Form
import play.api.data.Forms._

object Person {
  val personForm: Form[PersonForm] = Form {
    mapping(
      "name" -> text,
      "mail" -> text,
      "tel" -> text
    )(PersonForm.apply)(PersonForm.unapply)
  }

  // 検索用のフォーム
  val personFind: Form[PersonFind] = Form {
    mapping(
      "find" -> text
    )(PersonFind.apply)(PersonFind.unapply)
  }

}

case class Person(id: Int, name: String, mail: String, tel: String)
case class PersonForm(name: String, mail: String, tel: String)
// 検索用のオブジェクト
case class PersonFind(find: String)