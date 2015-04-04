# 問題1

```scala
def sum(l: List[Int]): Int = l match {
  case Nil => 0
  case x :: xs => x + sum(xs)
}
```



# 問題2

```scala
def sum(l: List[Int]): Int = {
  def loop(l: List[Int], acc: Int): Int = l match {
    case Nil => acc
    case x :: xs => loop(xs, acc + x)
  }
  loop(l, 0)
}
```



# 問題3

```scala
def binarySearch(list: List[Int], a: Int): Boolean = list match {
  case Nil      => false
  case x :: Nil => x == a
  case _ => {
    val p = list.size / 2
    val m = list(p)
    if (a == m)     true
    else if (a < m) binarySearch(list.take(p), a)
    else            binarySearch(list.drop(p), a)
  }
}
```



# 問題4

```scala
sealed abstract class Tree {
  def insert(x: Int): Tree
}
case class Empty() extends Tree {
  override def insert(x: Int): Tree = Node(x, Empty(), Empty())
}
case class Node(a: Int, left: Tree, right: Tree) extends Tree {
  override def insert(x: Int): Tree =
    if (x <= a) Node(a, left.insert(x), right)
    else        Node(a, left, right.insert(x))
}
```



# 問題5

```scala
sealed abstract class Tree {
  def insert(x: Int): Tree
  def contains(x: Int): Boolean
}
case class Empty() extends Tree {
  override def insert(x: Int): Tree = Node(x, Empty(), Empty())
  override def contains(x: Int): Boolean = false
}
case class Node(a: Int, left: Tree, right: Tree) extends Tree {
  override def insert(x: Int): Tree =
    if (x <= a) Node(a, left.insert(x), right)
    else        Node(a, left, right.insert(x))

  override def contains(x: Int): Boolean =
    if (x == a)      true
    else if (x <= a) left.contains(x)
    else             right.contains(x)
}
```



# 問題6

```scala
sealed abstract class Tree {
  def insert(x: Int): Tree
  def contains(x: Int): Boolean
  def remove(x: Int): Tree
}
case class Empty() extends Tree {
  override def insert(x: Int): Tree = Node(x, Empty(), Empty())
  override def contains(x: Int): Boolean = false
  override def remove(x: Int): Tree = Empty()
}
case class Node(a: Int, left: Tree, right: Tree) extends Tree {
  override def insert(x: Int): Tree =
    if (x <= a) Node(a, left.insert(x), right)
    else        Node(a, left, right.insert(x))

  override def contains(x: Int): Boolean =
    if (x == a)      true
    else if (x <= a) left.contains(x)
    else             right.contains(x)

  override def remove(x: Int): Tree = (left, right) match {
    case (_: Empty, _: Empty) =>
      if (x == a) Empty()
      else        Node(a, Empty(), Empty())
    case (l: Node, _: Empty) =>
      if (x == a) l
      else        Node(a, l.remove(x), Empty())
    case (_: Empty, r: Node) =>
      if (x == a) r
      else        Node(a, Empty(), r.remove(x))
    case (l: Node, r: Node) => {
      val maxFromRight = maxValue(r)
      if (x == a)     Node(maxFromRight, l, r.remove(maxFromRight))
      else if (x < a) Node(a, l.remove(x), r)
      else            Node(a, l, r.remove(x))
    }
  }

  private def maxValue(node: Node): Int = node match {
    case Node(v, _: Empty, _: Empty) => v
    case Node(v, l: Node, _: Empty)  => List(v, maxValue(l)).max
    case Node(v, _: Empty, r: Node)  => List(v, maxValue(r)).max
    case Node(v, l: Node, r: Node)   => List(v, maxValue(l), maxValue(r)).max
  }
}
```

