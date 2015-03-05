# 問題1

```scala
scala> for(x <- List(1,2,3);
     |     y <- List(10,100,1000))
     |   yield x * y
res0: List[Int] = List(10, 100, 1000, 20, 200, 2000, 30, 300, 3000)
```



# 問題2

```scala
scala> for(x <- List(1,2,3);
     |     y <- List(10, 100, 1000)
     |     if x * y > 50)
     |   yield x * y
res1: List[Int] = List(100, 1000, 200, 2000, 300, 3000)
```



# 問題3

```scala
scala> for(x <- (2 to 100 by 2) if x % 7 == 3) yield x
res2: scala.collection.immutable.IndexedSeq[Int] = Vector(10, 24, 38, 52, 66, 80, 94)
```



# 問題4

```scala
scala> for(s <- List("Apple", "Banana", "Pineapple", "Lemon")) yield s.substring(0, 1)
res3: List[String] = List(A, B, P, L)
```



# 問題5

```scala
scala> for(x <- 1 to 100) yield {
     |   if (x % 15 == 0) "FizzBuzz"
     |   else if (x % 5 == 0) "Buzz"
     |   else if (x % 3 == 0) "Fizz"
     |   else x.toString
     | }
res4: scala.collection.immutable.IndexedSeq[String] = Vector(1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz, 16, 17, Fizz, 19, Buzz, Fizz, 22, 23, Fizz, Buzz, 26, Fizz, 28, 29, FizzBuzz, 31, 32, Fizz, 34, Buzz, Fizz, 37, 38, Fizz, Buzz, 41, Fizz, 43, 44, FizzBuzz, 46, 47, Fizz, 49, Buzz, Fizz, 52, 53, Fizz, Buzz, 56, Fizz, 58, 59, FizzBuzz, 61, 62, Fizz, 64, Buzz, Fizz, 67, 68, Fizz, Buzz, 71, Fizz, 73, 74, FizzBuzz, 76, 77, Fizz, 79, Buzz, Fizz, 82, 83, Fizz, Buzz, 86, Fizz, 88, 89, FizzBuzz, 91, 92, Fizz, 94, Buzz, Fizz, 97, 98, Fizz, Buzz)
```



# 問題6

```scala
scala> List("a", "b", "c", "d", "e", "f", "g") zip List(1, 2, 3)
res5: List[(String, Int)] = List((a,1), (b,2), (c,3))
```



# 問題7

直角三角形の斜辺をc、残りの2辺のうち長い方をa、短い方をbとする。

まずは三角形を3要素のタプルで表して、全ての組み合わせをつくってみる。

```scala
scala> for(c <- 1 to 10; a <- 1 to 10; b <- 1 to 10) yield (a, b, c)
res6: scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vector((1,1,1), (1,2,1), (1,3,1), (1,4,1), (1,5,1), (1,6,1), (1,7,1), (1,8,1), (1,9,1), (1,10,1), (2,1,1), (2,2,1), (2,3,1), (2,4,1), (2,5,1), (2,6,1), (2,7,1), (2,8,1), (2,9,1), (2,10,1), (3,1,1), (3,2,1), (3,3,1), (3,4,1), (3,5,1), (3,6,1), (3,7,1), (3,8,1), (3,9,1), (3,10,1), (4,1,1), (4,2,1), (4,3,1), (4,4,1), (4,5,1), (4,6,1), (4,7,1), (4,8,1), (4,9,1), (4,10,1), (5,1,1), (5,2,1), (5,3,1), (5,4,1), (5,5,1), (5,6,1), (5,7,1), (5,8,1), (5,9,1), (5,10,1), (6,1,1), (6,2,1), (6,3,1), (6,4,1), (6,5,1), (6,6,1), (6,7,1), (6,8,1), (6,9,1), (6,10,1), (7,1,1), (7,2,1), (7,3,1), (7,4,1), (7,5,1), (7,6,1), (7,7,1), (7,8,1), (7,9,1), (7,10,1), (8,1,1), (8,2,1), (8,3,1), (8,4,1), (8,5,1), (8,6,1), (8,7,1), (8,8,1), (8,9,1), (8,10,1),...
```

ピタゴラスの定理が成り立つタプルだけを抽出するためフィルタを追加。さらに、aはcより短く、bはaよりも短くなることを考慮する。

```scala
scala> for(c <- 1 to 10;
     |     a <- 1 to c;
     |     b <- 1 to a;
     |     if a*a + b*b == c*c)
     |   yield (a, b, c)
res7: scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vector((4,3,5), (8,6,10))
```

周囲の長さが24になる条件を追加。

```scala
scala> for(c <- 1 to 10;
     |     a <- 1 to c;
     |     b <- 1 to a;
     |     if a*a + b*b == c*c && a+b+c == 24)
     |   yield (a, b, c)
res8: scala.collection.immutable.IndexedSeq[(Int, Int, Int)] = Vector((8,6,10))
```

