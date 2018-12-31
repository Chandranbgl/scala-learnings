
class Foo(x: Int, z: Int){
  val y = x
}

val foo = new Foo(10,20)

println(foo.y)
