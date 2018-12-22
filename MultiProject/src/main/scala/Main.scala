

class Animal (val name : String)

object Animal{

  def apply(name: String): Animal = {
    new Animal(name)
  }
}

object Main extends App{

//  val animal = new Animal("dog")
  val animal = Animal("Cat")

  println(animal.name)

  def time() = System.nanoTime

  def callByName(x: => Long): Unit = {
    println(x)
    println(x)
  }

  def callByValue(x: Long) = {
    println(x)
    println(x)
  }

  callByName(time)

  callByValue(time)
}


