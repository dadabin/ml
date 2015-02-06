
object Hello{
  def main(args:Array[String]): Unit ={
     val str="1 2 4"
    str.flatMap( f = (x) => {
      println(x)
        x.toString})
  }
}