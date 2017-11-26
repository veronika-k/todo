package main.scala

/**
  * Created by inoquea on 26.11.17.
  */
import model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._

object Main {

  val db = slickProfile.api.Database.forURL(
    "jdbc:postgresql://ec2-174-129-37-15.compute-1.amazonaws.com:5432/dbio76lfn642mk?sslmode=require&user=ihlufrlpffkzps&password=b7541238210dd2e41867d591b503a1ca4fbac843eec43897101e622583d8b1f6")

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

  def printGeneralMenu() = {
    println("1. Login" + "\n0. Exit")
  }

  def printUserMenu() = {
    println(
      "1. Add item" + "\n2. Delete item" + "\n3. Deactivate item" + "\n4. Activate item" +
        "\n5. Show All items" + "\n6. Show Active items" + "\n7. Show  inactive items" + "\n0. Log out")
  }

  def login(): Future[Option[Int]] = {
    println("Input login: ")
    val login = StdIn.readLine()
    println("Input password: ")
    val password = StdIn.readLine()
    userRepo.authentication(login, password)
  }

  def addItem(userId: Int) = {
    println("Input new item")
    itemRepo.createItem(StdIn.readLine(), userId)
  }

  def deleteItem(id: Int, userId: Int) = {
    itemRepo.deleteById(id, userId)
  }

  def activateItem(id: Int, userId: Int) = {
    itemRepo.activateById(id, userId)
  }

  def deactivateItem(id: Int, userId: Int) = {
    itemRepo.deactivateById(id, userId)
  }

  def userLoop(userId: Int): Unit = {
    printUserMenu()
    try {
      val cmd = StdIn.readInt()
      cmd match {
        case 0 =>
          println("Logging out!")
          generalLoop()
        case 1 =>
          addItem(userId)
          userLoop(userId)
        case 2 =>
          println("Input number of item to delete")
          deleteItem(StdIn.readInt(), userId).onComplete {
            case Success(x) =>
              x match {
                case 0 =>
                  println("Wrong number")
                  userLoop(userId)
                case _ =>
                  println("Item was deleted")

                  userLoop(userId)
              }
          }
        case 3 =>
          println("Input number of item to deactivate")
          deactivateItem(StdIn.readInt(), userId).onComplete {
            case Success(x) =>
              x match {
                case 0 =>
                  println("Wrong number")
                  userLoop(userId)
                case _ =>
                  println("Item was deactivated")
                  println("---------------------------------------")
                  userLoop(userId)
              }
          }
        case 4 =>
          activateItem(StdIn.readInt(), userId).onComplete {
            case Success(x) =>
              x match {
                case 0 =>
                  println("Wrong number")
                  userLoop(userId)
                case _ =>
                  println("Item was activated")
                  println("---------------------------------------")
                  userLoop(userId)
              }
          }
        case 5 =>
          println("All items")
          itemRepo.readAllItems(userId).onComplete {
            case Success(items) =>
              items.foreach { case (id, str) => println(id + ". " + str) }
              println()
              println("---------------------------------------")
              userLoop(userId)
            case Failure(ex) =>
              println("Error")
              userLoop(userId)
          }
        case 6 =>
          println("Active items")
          itemRepo.readActiveItems(userId).onComplete {
            case Success(items) =>
              items.foreach { case (id, str) => println(id + ". " + str) }
              println()
              println("---------------------------------------")
              userLoop(userId)
            case Failure(ex) =>
              println("Error")
              userLoop(userId)
          }
        case 7 =>
          println("Inactive items")
          itemRepo.readNotActiveItems(userId).onComplete {
            case Success(items) =>
              items.foreach { case (id, str) => println(id + ". " + str) }
              println()
              println("---------------------------------------")
              userLoop(userId)
            case Failure(ex) =>
              println("Error")
              userLoop(userId)
          }
        case _ => userLoop(userId)
      }
    } catch {
      case _ =>
        println("Something wrong. Please, write number you want to choose")
        userLoop(userId)
    }
  }

  def generalLoop(): Unit = {
    printGeneralMenu()
    val cmd = StdIn.readInt()
    cmd match {
      case 0 => sys.exit(0)
      case 1 =>
        login().onComplete {
          case Success(userIdOption) =>
            userIdOption match {
              case Some(userId) => userLoop(userId)
              case None =>
                println("Wrong login or password")
                generalLoop()
            }
          case Failure(_) => println("Error")
        }
      case _ => generalLoop()
    }
  }
  def main(args: Array[String]): Unit = {
    generalLoop()
    //init()
    //fullAll()
    while (true) Thread.sleep(Long.MaxValue)
  }
}
