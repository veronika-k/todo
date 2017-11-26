package main.scala

import model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.hashing.MurmurHash3

/**
  * Created by inoquea on 26.11.17.
  */
class UserTable(tag: Tag) extends Table[User](tag, "users") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val login = column[String]("login")
  val password = column[Int]("password")

  def * = (id.?, login, password) <> (User.apply _ tupled, User.unapply)
}

object UserTable {
  val table = TableQuery[UserTable]
}

class UserRepository(db: Database) {
  val table = TableQuery[UserTable]
  def create(user: User): Future[User] =
    db.run(UserTable.table returning UserTable.table += user)

  def update(user: User): Future[Int] =
    db.run(UserTable.table.filter(_.id === user.id).update(user))

  def delete(userId: Int): Future[Int] =
    db.run(UserTable.table.filter(_.id === userId).delete)

  def getById(userId: Int): Future[Option[User]] =
    db.run(UserTable.table.filter(_.id === userId).result.headOption)

  def authentication(login: String, password: String) = {
    db.run(
      table
        .filter(row =>
          row.login === login && row.password === MurmurHash3.stringHash(
            password))
        .map(_.id)
        .result
        .headOption)
  }
}
