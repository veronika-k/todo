package queries

/**
  * Created by inoquea on 26.11.17.
  */
import model._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class dbQueries(val db: Database) {

  val userRepo = new UserRepository(db)
  val itemRepo = new ItemRepository(db)

  def init(): Unit = {
    val f = db.run(userRepo.table.schema.create)
    f.onComplete { _ =>
      db.run(itemRepo.table.schema.create)
    }
  }

  def fullAll(): Unit = {
    for (i <- DataList.userList) {
      userRepo.create((User.apply _).tupled(i))
    }

    for (i <- DataList.itemsList) {
      itemRepo.create((Item.apply _).tupled(i))
    }
  }

  def createItem(str: String, userId: Int): Unit = {
    itemRepo.create(new Item(None, str, true, false, userId))
  }

  def readAllItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      itemRepo.table
        .filter(row => row.userId === userId && row.isDeleted === false)
        .map(row => (row.id, row.str))
        .result)
  }

  def readActiveItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      itemRepo.table
        .filter(row =>
          row.userId === userId && row.isDeleted === false && row.isActive === true)
        .map(row => (row.id, row.str))
        .result)
  }

  def readNotActiveItems(userId: Int): Future[Seq[(Int, String)]] = {
    db.run(
      itemRepo.table
        .filter(row =>
          row.userId === userId && row.isDeleted === false && row.isActive === false)
        .map(row => (row.id, row.str))
        .result)
  }

  def printItemsQueryResult[T](query: Int => Future[Seq[T]],
                               userId: Int,
                               separator: String): Unit = {
    val res = query(userId)
    res.onComplete { _ =>
      res.foreach { seq =>
        seq.map { case (id, str) => println(id + separator + str) }
      }
    }
  }

  def authentication(login: String, password: String) = {
    db.run(
      userRepo.table
        .filter(row => row.login === login && row.password === password)
        .map(_.id)
        .result
        .headOption)
  }

  def userItems(userId: Int) = {
    itemRepo.table
      .filter(_.userId === userId)
  }

  def deactivateById(itemId: Int, userId: Int): Future[Int] = {
    db.run(
      userItems(userId)
        .filter(_.id === itemId)
        .map(_.isActive)
        .update(true))
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
