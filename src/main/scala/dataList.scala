/**
  * Created by inoquea on 25.11.17.
  */
object dataList {

  val userList = List((Some(1), "data", "data"), (Some(2), "root", "root"))
  val itemsList = for (i <- 1 to 10) yield {
    (Some(i),
     s"Item #$i",
     i % 3 == 0,
     false,
     if (i % 3 == 0) 1
     else i % 3)
  }

}
