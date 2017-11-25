package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

/**
  * Created by inoquea on 25.11.17.
  */
case class User(id: Option[Int], login: String, password: String)

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val login = column[String]("login")
  val password = column[String]("password")

  def * = (id.?, login, password) <> (User.apply _ tupled, User.unapply)
}

object UserTable {
  val table = TableQuery[UserTable]
}

class UserRepository(db:Database){
  val table =TableQuery[UserTable]
  def create(user: User): Future[User] =
    db.run(UserTable.table returning UserTable.table += user)

  def update (user: User): Future[Int] =
    db.run(UserTable.table.filter(_.id === user.id).update(user))

  def delete (userId: Int): Future[Int] =
    db.run(UserTable.table.filter(_.id === userId).delete)

  def getById(userId: Int): Future[Option[User]] =
    db.run(UserTable.table.filter(_.id === userId).result.headOption)
}