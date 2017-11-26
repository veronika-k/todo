package model

import scala.util.hashing.MurmurHash3

/**
  * Created by inoquea on 25.11.17.
  */
object DataList {

  val userList = List((Some(1), "data", MurmurHash3.stringHash("data")),
                      (Some(2), "root", MurmurHash3.stringHash("root")))
  val itemsList = for (i <- 1 to 10) yield {
    (Some(i),
     s"Item #$i",
     i % 3 == 0,
     false,
     if (i % 3 == 0) 1
     else i % 3)
  }
}
