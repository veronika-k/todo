/**
  * Created by inoquea on 25.11.17.
  */

import model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.util.{Failure, Success}

object Main extends App {
  println("HELLO V!")
  val db = slickProfile.api.Database.forURL(
    "jdbc:postgresql://localhost/airport?user=inoquea&password=11111111")
  val userRepo = new UserRepository(db)
  val itemRepo = new ItemRepository(db)

  //init()
  def init(): Unit = {
    val f = db.run(userRepo.table.schema.create)
    f.onComplete {_ =>
      db.run(itemRepo.table.schema.create)
    }
  }

  //fullAll()
  def fullAll(): Unit = {
    for (i <- dataList.userList) {
      userRepo.create((User.apply _).tupled(i))
    }

    for (i <- dataList.itemsList) {
      itemRepo.create((Item.apply _).tupled(i))
    }
  }

  def createItem(str: String, userId: Int): Unit = {
    itemRepo.create(new Item(None, str, true, false, userId))
  }


  //Await.result(itemRepo.updateActiveById(3), Duration.Inf)
  itemRepo.deleteById(7)

  Thread.sleep(2000)
}
