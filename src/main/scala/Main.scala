/**
  * Created by inoquea on 25.11.17.
  */
import queries._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks.{break, _}
import scala.util.{Failure, Success}
import scala.io.StdIn

object Main extends App {
  println("HELLO V!")
  val db = slickProfile.api.Database.forURL(
    "jdbc:postgresql://localhost/airport?user=inoquea&password=11111111")
  val queries = new dbQueries(db)

  def printGeneralMenu() = {
    println("1. Login" + "\n0. Exit")
  }

  def printUserMenu() = {
    println(
      "1. Add item" + "\n 2. Delete item" + "\n 3.Deactivate item" + "\n 4. Activate item" +
        "\n 5. Show All items" + "\n 6. Show Active items" + "\n 7. Show not active items" + "\n0. Log out")
  }

  def login(): Future[Option[Int]] = {
    println("Input login: ")
    val login = StdIn.readLine()
    println("Input password: ")
    val password = StdIn.readLine()
    queries.authentication(login, password)
  }

  def addItem(userId: Int) = {
    println("Input new item")
    queries.createItem(StdIn.readLine(), userId)
  }

  def deleteItem(userId: Int) = {
    println("Input number of item to delete")
    queries.deleteById(StdIn.readInt(), userId)
  }
  def activateItem(userId: Int) = {
    println("Input number of item to delete")
    queries.activateById(StdIn.readInt(), userId)
  }
  def deactivateItem(userId: Int) = {
    println("Input number of item to delete")
    queries.deactivateById(StdIn.readInt(), userId)
  }
  def showAllItem(userId: Int) = {
    println("All items")
    queries.printItemsQueryResult(queries.readAllItems,userId, ". ")
  }

  def showActiveItem(userId: Int) = {
    println("All items")
    //Await.result(queries.readActiveItems(userId), Duration.Inf).foreach(println)
    queries.printItemsQueryResult(queries.readActiveItems,userId, ". ")
  }

  def showNotActiveItem(userId: Int) = {
    println("All items")
    //Await.result(queries.readNotActiveItems(userId), Duration.Inf).foreach(println)
    queries.printItemsQueryResult(queries.readNotActiveItems,userId, ". ")
  }

  def userLoop(userId: Int): Unit = {
    breakable {
      while (true) {
        printUserMenu()
        val cmd = StdIn.readInt()
        cmd match {
          case 0 => break
          case 1 => addItem(userId)
          case 2 => deleteItem(userId)
          case 3 => deactivateItem(userId)
          case 4 => activateItem(userId)
          case 5 => showAllItem(userId)
          case 6 => showActiveItem(userId)
          case 7 => showNotActiveItem(userId)
        }
      }
    }
  }

  breakable {
    while (true) {
      printGeneralMenu()
      val command = StdIn.readInt()
      if (command == 0) break
      if (command == 1) {
        val userIdQuery = Await.result(login(), Duration.Inf)
//        userIdQuery.onComplete {
//          case Success(userIdOption) =>
//            userIdOption match {
//              case Some(userId) => userLoop(userId)
//              case None => println("Wrong login or password")
//            }
//          case Failure(ex) => println("Error")
//        }
        userIdQuery match {
          case Some(userId) => userLoop(userId)
          case None         => println("Wrong login or password")
        }
      }
    }

  }
}

//def userLoop(userId: Int): Unit = {
//  breakable {
//    while (true) {
//      printUserMenu()
//      val cmd = StdIn.readInt()
//      cmd match {
//        case 0 => break
//        case 1 => addItem(userId)
//        case 2 => deleteItem(userId)
//        case 3 => deactivateItem(userId)
//        case 4 => activateItem(userId)
//        case 5 => showAllItem(userId)
//        case 6 => showActiveItem(userId)
//        case 7 => showNotActiveItem(userId)
//      }
//    }
//  }
//}