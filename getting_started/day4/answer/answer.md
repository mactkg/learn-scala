# 問題1

```scala
def quickSort(list: Seq[Int]): Seq[Int] = list match {
  case Nil      => Nil
  case x :: Nil => Seq(x)
  case x :: xs  => {
    val smallerOrEqual = for (y <- xs; if y <= x) yield y
    val larger         = for (y <- xs; if y > x ) yield y
    quickSort(smallerOrEqual) ++ Seq(x) ++ quickSort(larger)
  }
}
```



# 問題2

```scala
def quickSort[A <% Ordered[A]](list: Seq[A]): Seq[A] = list match {
  case Nil      => Nil
  case x :: Nil => Seq(x)
  case x :: xs  => {
    val smallerOrEqual = for (y <- xs; if y <= x) yield y
    val larger         = for (y <- xs; if y > x ) yield y
    quickSort(smallerOrEqual) ++ Seq(x) ++ quickSort(larger)
  }
}
```



# 問題3

```scala
sealed abstract class Tree[A] {
  def insert(x: A): Tree[A]
  def contains(x: A): Boolean
}
case class Empty[A <% Ordered[A]]() extends Tree[A] {
  override def insert(x: A): Tree[A] = Node(x, Empty(), Empty())
  override def contains(x: A): Boolean = false
}
case class Node[A <% Ordered[A]](a: A, left: Tree[A], right: Tree[A]) extends Tree[A] {
  override def insert(x: A): Tree[A] =
    if (x <= a) Node(a, left.insert(x), right)
    else        Node(a, left, right.insert(x))

  override def contains(x: A): Boolean = 
    if (x == a)      true
    else if (x <= a) left.contains(x)
    else             right.contains(x)
}
```

