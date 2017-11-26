package main.scala

import model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

/**
  * Created by inoquea on 26.11.17.
  */
class ItemTable(tag: Tag) extends Table[Item](tag, "items") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val str = column[String]("login")
  val isActive = column[Boolean]("active")
  val isDeleted = column[Boolean]("deleted")
  val userId = column[Int]("userId")
  val userIdFK = foreignKey("userIdFK", userId, TableQuery[UserTable])(_.id)

  def * =
    (id.?, str, isActive, isDeleted, userId) <> (Item.apply _ tupled, Item.unapply)
}

object ItemTable {
  val table = TableQuery[ItemTable]
}

class ItemRepository(db: Database) {
  val table = TableQuery[ItemTable]

  def create(item: Item): Future[Item] =
    db.run(ItemTable.table returning ItemTable.table += item)

  def update(item: Item): Future[Int] =
    db.run(ItemTable.table.filter(_.id === item.id).update(item))


  def getById(itemId: Int): Future[Option[Item]] =
    db.run(ItemTable.table.filter(_.id === itemId).result.headOption)

  def createItem(str: String, userId: Int): Unit = {
    create(new Item(None, str, true, false, userId))
  }

  def readAllItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      ItemTable.table
        .filter(row => row.userId === userId && row.isDeleted === false)
        .map(row => (row.id, row.str)).sorted
        .result)
  }

  def readActiveItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      ItemTable.table
        .filter(row =>
          row.userId === userId && row.isDeleted === false && row.isActive === true)
        .map(row => (row.id, row.str)).sorted
        .result)
  }

  def readNotActiveItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      ItemTable.table
        .filter(row =>
          row.userId === userId && row.isDeleted === false && row.isActive === false)
        .map(row => (row.id, row.str)).sorted
        .result)
  }

  def userItems(userId: Int) = {
    ItemTable.table
      .filter(_.userId === userId)
  }

  def deactivateById(itemId: Int, userId: Int): Future[Int] = {
    db.run(
      userItems(userId)
        .filter(_.id === itemId)
        .map(_.isActive)
        .update(false))
  }

  def activateById(itemId: Int, userId: Int): Future[Int] = {
    db.run(
      userItems(userId)
        .filter(_.id === itemId)
        .map(_.isActive)
        .update(true))
  }

  def deleteById(itemId: Int, userId: Int): Future[Int] = {
    db.run(
      userItems(userId)
        .filter(_.id === itemId)
        .map(_.isDeleted)
        .update(true))
  }
}