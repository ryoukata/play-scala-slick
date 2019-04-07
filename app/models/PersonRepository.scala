package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class PersonRepository @Inject()
      (dbConfigProvider: DatabaseConfigProvider)
      (implicit ec: ExecutionContext){

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def mail = column[String]("mail")
    def tel = column[String]("tel")

    def * = (id, name, mail, tel) <> ((Person.apply _).tupled, Person.unapply)
  }

  private val people = TableQuery[PeopleTable]

  def list(): Future[Seq[Person]] = db.run {
    people.sortBy(_.name.asc).result
  }

  // レコードの追加
  def create(name: String, mail: String, tel: String): Future[Int] =
    db.run(
      people += Person(0, name, mail, tel)
    )

  // 特定のIDのレコードを検索して返す
  def get(id: Int): Future[Person] = db.run {
    people.filter(_.id === id).result.head
  }

  // レコード更新処理（レコードの追加も可能。IDを見て存在しなければ追加、存在すれば更新）
  def update(id: Int, name: String, mail: String, tel: String): Future[Int] = {
    db.run(
      people.insertOrUpdate(Person(id, name, mail, tel))
    )
  }

  // レコードの削除
  def delete(id: Int): Future[Int] = {
    db.run(
      people.filter(_.id === id).delete
    )
  }

  // 検索機能（あいまい検索）
  def find(s: String): Future[Seq[Person]] = db.run {
    // people.filter(_.name like "%" + s + "%").result
    // 複数条件
    people.filter(p => (p.name like "%" + s + "%") || (p.mail like "%" + s + "%")).result
  }

}
