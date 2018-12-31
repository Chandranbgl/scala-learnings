abstract class Food { val name: String }

abstract class Fruit extends Food
case class Banana(name: String) extends Fruit
case class Apple(name: String) extends Fruit

abstract class Cereal extends Food
case class Granola(name: String) extends Cereal
case class Muesli(name: String) extends Cereal

class Red() extends Apple("kasmir")

class Round extends Red

def eat(food: Food): String = s"${food.name} eaten"

val a = Apple("Kashmir")
val b = Muesli("maggi")
val ba = Banana("Rasthali")
eat(a)
eat(b)

case class Bowl(food: Food){
  override def toString = s"A bowl of yummy ${food.name}s"
  def contents = food
}

val b1 = Bowl(a)
b1.contents
val b2 = Bowl(b)
b2.contents

case class Bowl1[T](food: T){
  override def toString = s"A bowl of yummy ${food}s"
}

Bowl1(a)
Bowl1(b)
//covariance with upper bound
case class Bowl2[+T <: Fruit](food: T){
  override def toString = s"A bowl of yummy ${food}s"
}

val b3 = Bowl2[Apple](a)

def serveToFruitEater(bowl2: Bowl2[Apple]) =
  s"mmmm, those ${bowl2} were very good"

serveToFruitEater(b3)

//covariance with lower bound
case class Bowl3[+T >: Fruit](food: T){
  override def toString = s"A bowl of yummy ${food}s"
}

val b4 = Bowl3[Food](a)

def serveToFruitEater3(bowl3: Bowl3[Food]) =
  s"mmmm, those ${bowl3} were very good"

serveToFruitEater3(b4)

abstract class Bowl4 {
  type FOOD <: Food
  val food: FOOD
  def eat = s"Yummy ${food.name}"
}

class AppleBowl(val f: Apple) extends Bowl4 {
  type FOOD = Apple
  override val food = f
}

