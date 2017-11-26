/**
  * Created by inoquea on 26.11.17.
  */

package object model {
  case class Item(id: Option[Int],
                  str: String,
                  isActive: Boolean,
                  isDeleted: Boolean,
                  userId: Int)
  case class User(id: Option[Int], login: String, password: Int)
}