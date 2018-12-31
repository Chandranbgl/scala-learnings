abstract class CompareT[T]{
  def isSmaller(x: T, y: T): Boolean
  def isLarger(x: T, y: T): Boolean
}


def insertSort[T](item: T, list: List[T])(implicit cmp: CompareT[T]): List[T] = {
  list match {
    case Nil => List(item)
    case head :: _ if cmp.isSmaller(item, head) => item :: list
    case head :: tail => head :: insertSort(item, tail)
  }
}

def genInsert[T: CompareT](item: T, list: List[T]): List[T] = {
  val cmp = implicitly[CompareT[T]]
  list match {
    case Nil => List(item)
    case head :: _ if cmp.isSmaller(item, head) => item :: list
    case head :: tail => head :: insertSort(item, tail)
  }
}

def genSort[T: CompareT](item: List[T]): List[T] = {
    item match {
      case Nil => Nil
      case head :: tail => insertSort(head, genSort(tail))
    }
}

val nums = List(1,4,3,2,6,5)

implicit val cmp = new CompareT[Int] {
  override def isSmaller(x: Int, y: Int) = x < y

  override def isLarger(x: Int, y: Int) = x > y
}

genSort(nums)

nums.mkString("{",",","}")


val IntCus = implicitly[Numeric[Int]]
IntCus.times(3,4)

