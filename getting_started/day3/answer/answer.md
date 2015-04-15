# 問題1

```scala
def product(xs: List[Int]) = xs.foldLeft(1)(_ * _)
```



# 問題2

```scala
def length[A](xs: List[A]): Int = xs.foldLeft(0)((acc, _) => acc + 1)
```



# 問題3

```scala
def reverse[A](xs: List[A]): List[A] = xs.foldLeft(Nil: List[A])((acc, x) => x :: acc)
```



# 問題4

```scala
def append[A](as: List[A], bs: List[A]): List[A] = as.foldRight(bs)(_ :: _)
```



# 問題5
```scala
case class User(id: Int, age: Int)

def findById(id: Int): Option[User] = {
  val db = Map(
    1 -> User(1, 19),
    2 -> User(2, 25),
    3 -> User(3, 30)
  )
  db.get(id)
}

def addAges(userIdParam1: Option[Int], userIdParam2: Option[Int], minAgeParam: Option[Int]): Int = {
  val result = for {
    minAge  <- minAgeParam.orElse(Some(20));
    userId1 <- userIdParam1;
    userId2 <- userIdParam2;
    user1   <- findById(userId1);
    user2   <- findById(userId2)
  } yield {
    val age1 = if (user1.age > minAge) user1.age else 0
    val age2 = if (user2.age > minAge) user2.age else 0
    age1 + age2
  }
  result.getOrElse(0)
}
```

