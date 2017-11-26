package model

import slick.dbio.Effect.Read
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import slick.sql.SqlAction

import scala.concurrent.Future

/**
  * Created by inoquea on 25.11.17.
  */
case class Item(id: Option[Int],
                str: String,
                isActive: Boolean,
                isDeleted: Boolean,
                userId: Int)

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


}
