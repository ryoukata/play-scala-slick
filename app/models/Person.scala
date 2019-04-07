package models

import play.api.data.Form
import play.api.data.Forms._

object Person {
  val personForm: Form[PersonForm] = Form {
    // バリデーションチェックが可能(verifyingを使って細かな設定をしている)
    mapping(
      "name" -> nonEmptyText
          .verifying(error="3文字以上に。", constraint=_.length >= 3)
          .verifying(error="10文字以内に。", constraint=_.length <= 10),
      "mail" -> text
          .verifying(error="メールアドレスを入力。",
            constraint=_.matches("""([a-zA-Z0-9\.\_-]+)@([a-zA-Z0-9\.\_-]+)""")
          ),
      "tel" -> nonEmptyText
          .verifying(error="半角の数値とハイフンのみ入力可。", constraint=_.matches("""[1-9-]+"""))
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