# Day3 書きかけ

## 型パラメータを使ったメソッド

前回リストの先頭要素を返すheadメソッドを自分で作ってみました。

```scala
def head(xs: List[Int]): Int = xs match {
  case Nil    => throw new IllegalArgumentException
  case x :: _ => x
}
```

このメソッドはList[Int]型にしか対応していませんが、リストの先頭要素を返すだけなので、List[String]やList[Double]など、他の型のリストにも対応できるはずです。

`xs: List[Int]`というのを`xs: List[A]`としてAの部分にIntやStringなど、色々な型をうけとる可能性がありますよ、という風にしたいです。これには型パラメータを使います。型パラメータはメソッド名のすぐ後ろ、引数の前に書きます。

```scala
def head[A](xs: List[A]): A = xs match {
  case Nil    => throw new IllegalArgumentException
  case x :: _ => x
}
```

`def head[A](xs: List[A]): A` というのは、headメソッドは型パラメータAを持ちますよ、メソッドの引数はList[A]型ですよ、返り値はA型ですよ、という意味になります。

型パラメータを受け取るメソッドを使うときは型パラメータに何かしらの型を指定します。型パラメータは、引数の型から推論可能なので省略できます。例えば、`List(1,2,3)`が指定された場合は、型が`List[A]`である引数`xs`に、`List[Int]`型の値を渡しているので、型パラメータ`A`が`Int`であると推論できます。

```scala
scala> head[Int](List(1,2,3))
res21: Int = 1

scala> head[String](List("aaa", "bbb", "ccc"))
res22: String = aaa

scala> head(List(1,2,3))
res23: Int = 1

scala> head(List("aaa", "bbb", "ccc"))
res24: String = aaa
```


これでheadメソッドは色々な型に対応できるようになりました。このように色々な型を受け取れるメソッドは、多相である、と言います。

多相には2種類あります。1つめは今回のように型パラメータを使った多相です。2つめは、Day2でShapeを継承した2種類のRectangleとCircle、そしてShape型を受け取れるsumAreaメソッドをつくったことを思い出してください。このsumAreaメソッドもShapeのサブ型であれば受け取れるため多相であると言えます。



## 型コンストラクタ

型パラメータはメソッドだけでなく、クラスなどでも使えます。

これまでList[Int]型のリストをたくさん使ってきました。List型というのは存在せず、存在するのはList[Int]型です。Listは型ではありません。Listは型コンストラクタと呼ばれるものです。

型コンストラクタに型パラメータを与えると型になります。コンストラクタにパラメータを渡すとインスタンスをつくることができるのと同じようなイメージです。Listという型コンストラクタにIntという型パラメータを渡すと、List[Int]型という型ができあがります。

型コンスタクタを自分でつくってみましょう。型パラメータの定義では`[]`を使います。例えばケースクラスで型パラメータを使い、Capsuleという型コンストラクタをつくってみましょう。インスタンス化すると、型が`Capsule[Int]`、`Capsule[String]`、`Capsule[List[Int]]`となり、型コンストラクタから複数の型をつくれることが分かります。

```scala
scala> case class Capsule[A](x: A)
defined class Capsule

scala> Capsule(2)
res25: Capsule[Int] = Capsule(2)

scala> Capsule("ABC")
res26: Capsule[String] = Capsule(ABC)

scala> Capsule(List(1,2,3))
res27: Capsule[List[Int]] = Capsule(List(1, 2, 3))
```



## 関数

これまでに色々なメソッドを使ってきました。メソッドは何かしらのオブジェクトに属するもので単独では存在することができません（REPL上は例外です）。

Scalaではメソッドと似ていますが、オブジェクトに属さずに単独で存在できる「関数」があります。Scalaにおいて関数はファーストクラスオブジェクトです。

ファーストクラスオブジェクトであるとは、なんらかのオブジェクトに属さずにそれ単独で値として扱えるというようなことです。値として扱えるので、変数に代入したり、他のメソッドの引数に関数を渡したり、リストに複数の関数を格納したり、ということができます。

では、関数をつくってみましょう。関数をつくるための関数リテラルが用意されています。引数として1つのIntを受け取り、2倍にして返す関数を変数`f`に代入してみます。

```scala
scala> val f = (x: Int) => x * 2
f: Int => Int = <function1>
```

関数リテラルは、`(引数リスト) => { 関数本体 }`というようなつくりです。関数本体が1つの式である場合は`{}`を省略できます。

REPLの出力を見てみましょう。`f: Int => Int = <function1>`というのは、「`f`という変数は `Int => Int` 型で、値は `<function1>` です」という意味になります。変数`f`には、`Int => Int`型のオブジェクトが代入されているということになります。

`Int => Int` 型というのは、引数として1つのInt型をとり、返り値がInt型である関数を表します。値の `<function1>` というのは関数本体を書きたいところだけど書くと長くなっちゃうので<function1>と書いて値が関数であることを示してるんだと解釈すればいいかと思います。

では、`f`に代入した関数を使ってみましょう。関数にある適当な数値を渡してみましょう。関数`f`に数値10を適用する、と言うこともできます。関数を表すオブジェクトのapplyメソッドを呼び出すことで関数を実行できます。Scalaでは、applyメソッドは省略できることを思い出しましょう。

```scala
scala> f.apply(10)
res1: Int = 20

scala> f(10)
res2: Int = 20
```

関数を定義し、関数で定義した処理を実行することができました。

関数をつくる方法は関数リテラルの他に、メソッドに部分適用をする方法があります。例えば、`2 + 3`というのはInt型の+メソッドを呼び出していました。この+メソッドから、「2を加算する関数」をつくってみます。メソッドを呼び出すときに具体的な値を指定する代わりに、`_`を指定することで部分適用することができます。

```scala
scala> val f = 2.+(_: Int)
scala> f(3)

scala> val f = 2 + (_: Int)
scala> f(3)

scala> val f: (Int) => Int = 2 + _
scala> f(3)
```

部分適用するときは、型を明示的に指定する必要があります。型を明示的に指定するのは、`_`の型でも良いし、`f`の型でも良いです。

メソッドのレシーバーも`_`で置き換えることができます（これを部分適用と呼ぶかどうか分からないです、ごめんなさい・・・）。この場合は2つの引数を受け取ることになります。1つめの引数がレシーバーで、2つめの引数が+メソッドの引数になります。

```scala
scala> val f: (Int, Int) => Int = _ + _
```

3つの引数を受け取るsumメソッドをつくってみましょう。部分適用もしてみます。

```scala
scala> def sum(a: Int, b: Int, c: Int) = a + b + c
scala> val f: (Int) => Int = sum(_, 1, 2)
scala> val f: (Int, Int) => Int = sum(_, _, 2)
scala> val f: (Int, Int, Int) => Int = sum(_, _, _)
```

全ての引数を`_`で指定する場合は、`()`ごと`_`で置き換えることで短く書けます。

```scala
scala> val f: (Int, Int, Int) => Int = sum _
```



## コレクションの高階メソッド
関数は、`Int => Int` 型というような型を持つオブジェクトであることは分かりました。関数がファーストクラスオブジェクトだと何に役立つのでしょうか？

役立つシーンの1つに関数を受け取るメソッドがあります。関数を受け取るメソッドを高階メソッドと呼びます。高階メソッドの中でもコレクションが持つ高階メソッドはよく使うことになるでしょう。

コレクションの高階メソッドの1つ、foreachメソッドを使ってみます。foreachメソッドに渡せる関数の型は、`A => Unit` 型です。foreachメソッドは、受け取った関数にコレクションの要素をそれぞれ順番に適用します。

```scala
scala> val cs = ('A' to 'Z')
scala> cs.foreach((c: Char) => println(c))
```

csがCharのコレクションなので、関数の引数であるcがCharであることはScalaが推論できます。なので、cの型は省略できます。

```scala
scala> cs.foreach((c) => println(c))
```

関数の引数が1つの場合は`()`も省略できます。

```scala
scala> cs.foreach(c => println(c))
```

さらに、printlnに部分適用することで、`A => Unit` 型の関数をつくれるため、関数リテラルを使う必要もないです。

```scala
scala> cs.foreach(println(_))
```

全ての引数を部分適用することになるので、`()`ごと`_`で置き換えても同じです。

```scala
scala> cs.foreach(println _)
```

さらに、`()`ごと`_`で置き換えてつくった関数を別のメソッドに渡すとき、`_`自体を省略することが可能です。

```scala
scala> cs.foreach(println)
```

だいぶすっきりしますね。省略ルールは以前も紹介したこのページが参考になります -> [Scala の省略ルール早覚え](https://gist.github.com/gakuzzzz/10104162)

次は、mapメソッドを使ってみます。mapメソッドは、引数で受け取った関数にコレクションそれぞれの要素を適用した結果からなる新たなコレクションを作ります。mapに渡せる関数の型は `A => B` 型となります。

```scala
scala> val xs = (1 to 10)
scala> xs.map(x => x * 2)
scala> xs.map(_ * 2)
```

このmapメソッド、あるコレクションを別のコレクションに変換しています。これってどこかで聞き覚えないですか？そうです、Day1でやったfor式です。上のmapを使った例はfor式でも書けますね。

```scala
scala> for(x <- xs) yield x * 2
```

どちらを使っても同じことができます。どちらを使うべきかというのは特にないので読みやすいを使えばいいかと思います。

foreach、 mapの他に、filterという高階メソッドがあります。filterにはBooleanを返す関数を渡すことができます。Booleanを返す関数のことを「述語」と呼んだりします。述語関数に各要素を適用した結果がtrueになる要素のみからなるコレクションを取得できます。filterに渡せる関数の型は `A => Boolean` 型です。

```scala
scala> xs.filter(x => x > 6)
scala> xs.filter(_ > 6)
```

mapとfilterを組み合わせてみます。filterの後ろに続くのがmapかforeachの場合はfilterより高速に動作するwithFilterが使えます。

```scala
scala> xs.filter(_ > 6).map(_ * 2)
scala> xs.withFilter(_ > 6).map(_ * 2)
```

このfilter、withFilterもfor式で同じことができますね。for式ではifを使ってフィルタできるのでした。

```scala
scala> for(x <- xs; if x > 6) yield x * 2
```

もう1つ、flatMapメソッドを見てみましょう。flatMapはmapと同じように値変換するような感じなのですが、ちょっと違います。flatMapに渡す変換のための関数では、コレクションを返す必要があるのです。flatMapに渡せる関数の型は `A => List[B]` 型となります。

```scala
scala> List(1,2,3,4).flatMap(a => 1 to a)
scala> List(1,2,3,4).flatMap(1 to _)
```

仮にコレクションを返す関数をmapに渡すと入れ子のコレクションが出来上がるでしょう。入れ子のリストを解消するにはflattenメソッドを使います。flatMapは、map+flattenを行った結果と同じになります。

```scala
scala> List(1,2,3,4).map(1 to _)
scala> List(1,2,3,4).map(1 to _).flatten
scala> List(1,2,3,4).flatMap(1 to _)
```

このflatMapもfor式で書けます。ジェネレータを複数回続けた場合と同じことになります。2つのコレクションをfor式の2つのジェネレータに書くと、各要素を組み合わせた処理を書けました。

例えば前回の練習問題で三角形を3要素のタプルで表現するのがありました。for式、flatMap、両方を使った場合はこうなります。

```scala
scala> for (c <- 1 to 10;
     |      a <- 1 to c;
     |      b <- 1 to a)
     |   yield (a,b,c)

scala> (1 to 10).flatMap {
     |   c => (1 to c).flatMap {
     |     a => (1 to a).map(b => (a,b,c))
     |   }
     | }
```

直角三角形だけを抽出したい場合はこうなります。

```scala
scala> for (c <- 1 to 10;
     |      a <- 1 to c;
     |      b <- 1 to a;
     |      if c*c == a*a + b*b)
     |   yield (a,b,c)

scala> (1 to 10).flatMap {
     |   c => (1 to c).flatMap {
     |     a => (1 to a).withFilter(b => c*c == a*a + b*b).map(b => (a,b,c))
     |   }
     | }
```

うーん、これはfor式を使った方が見やすい気がしますね。

その他の高階メソッドをいくつか見てみましょう。

```scala
scala> List(8,2,6,9,8,3,5).partition(_ % 2 == 0)
res70: (List[Int], List[Int]) = (List(8, 2, 6, 8),List(9, 3, 5))

scala> List(8,2,6,9,8,3,5).find(_ % 2 != 0)
res71: Option[Int] = Some(9)

scala> List(8,2,6,9,8,3,5).takeWhile(_ % 2 == 0)
res72: List[Int] = List(8, 2, 6)

scala> List(8,2,6,9,8,3,5).dropWhile(_ % 2 == 0)
res73: List[Int] = List(9, 8, 3, 5)

scala> List(8,2,6,9,8,3,5).span(_ % 2 == 0)
res74: (List[Int], List[Int]) = (List(8, 2, 6),List(9, 8, 3, 5))

scala> List(8,2,6,9,8,3,5).forall(_ % 2 == 0)
res75: Boolean = false

scala> List(8,2,6,9,8,3,5).forall(_ > 0)
res76: Boolean = true

scala> List(8,2,6,9,8,3,5).exists(_ % 2 == 0)
res77: Boolean = true

scala> List(8,2,6,9,8,3,5).exists(_ < 0)
res78: Boolean = false

scala> List(8,2,6,9,8,3,5).sortWith((a, b) => a > b)
res79: List[Int] = List(9, 8, 8, 6, 5, 3, 2)

scala> List(8,2,6,9,8,3,5).sortWith(_ < _)
res80: List[Int] = List(2, 3, 5, 6, 8, 8, 9)
```



## ローンパターン

関数は、高階メソッド以外でも色々なところで役に立ちます。例えば、ファイルをつくるメソッドを書いてみましょう。

```scala
import java.nio.file._

def writeTo(fileName: String, body: String): Unit = {
  val writer = Files.newBufferedWriter(Paths.get(fileName))
  try {
    writer.write(body)
  } finally {
    writer.close
  }
}
```

- - -

Scalaの標準ライブラリにはファイル書き込みを行うクラスが用意されておらず、ライブラリを使用しない場合はJavaのjava.nio.file.Filesクラスなどを使います。クラス名より前の部分"java.nio.file"の部分をパッケージと言います。これは名前空間で、同じクラス名でも別パッケージであれば定義できるようになっています（例えば、hoge.FilesクラスをつくってもOK）。上の例だとFilesとPathsがjava.nio.fileパッケージなのですが、パッケージ名から書くのは大変です。こういうときは`import`でパッケージを指定すると、指定したパッケージ内のクラスはクラス名だけで使うことができるようになります。

- - -

注目したいのは`try { } finally { }`の部分です。`try { }`で囲まれたコードでエラーが発生しても`finally { }`で囲まれたコードが実行されます。エラーが発生しなくても`finally { }`で囲まれた部分は実行されます。ファイルのクローズのようにエラーが発生しようがしまいが必ず実行したいコードは`finally { }`で囲みます。

ファイルの書き込み処理を行う箇所では、この`try { } finally { }`を書く必要があります。都度書いているとDRYではなくなります。こういう場合に便利なのがローンパターンと呼ばれる方法です。`try { } finally { }`という構造を別メソッドで定義し、`try { }`で囲みたい処理は、`java.io.Writer => Any` 型の関数で渡してあげるようにします。

```scala
import java.nio.file._

def using(fileName: String, f: java.io.Writer => Any): Unit = {
  val writer = Files.newBufferedWriter(Paths.get(fileName))
  try {
    f(writer)
  } finally {
    writer.close
  }
}

def writeTo(fileName: String, body: String): Unit = {
  using(fileName, (writer) => {
    writer.write(body)
  })
}
```

これでファイルを書き込むときはusingメソッドを使えばよく、`try { } finally { }`をいろんなところに書く必要はなくなりました。リソースのオープン・クローズはusingメソッド内で行うことで、usingメソッドを使う人はリソースのオープン・クローズを気にしなくてよくなります。このようにオープンしたリソースを関数に貸し出すような動きをするのでローンパターンと呼ばれているようです。ローンパターンの実装についてこちらに色々載ってます -> [Scala using(Hishidama's Scala loan-pattern Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/sample/using.html)



## 複数の引数リスト

usingメソッドを使うことでファイルのオープン・クローズをDRYにできました。usingメソッドですが、言語の組み込み機能であってもよさそうですよね。残念ながらScalaには最初からusingは用意されていません。しかし、usingをあたかも組み込みの構文のように見せることができます。usingを定義するときの引数リスト、メソッド名の右にある`()`の部分を2つに分けてみましょう。

```scala
def using(fileName: String)(f: java.io.Writer => Any): Unit = {
  ・・・
```

この例のように、Scalaでは複数引数がある場合に、引数リストを複数個に分けることができます。このようなメソッドを呼び出す方も2つの引数リストを指定します。

```scala
def writeTo(fileName: String, body: String): Unit = {
  using(fileName)((writer) => {
    writer.write(body)
  })
}
```

さらに、Scalaでは引数が1つの引数リストの`()`は`{}`と書くことができます。これを使うとusingが組み込みの構文であるかのように使えます。

```scala
def writeTo(fileName: String, body: String): Unit = {
  using(fileName) { writer =>
    writer.write(body)
  }
}
```

ここでは深く追いませんが、このように複数の引数リストを持つメソッドをカリー化されたメソッド、と呼んだりします。



## 畳み込み

コレクションの高階メソッドに戻ります。ここで紹介するのはfoldLeftとfoldRightです。これらの高階メソッドは畳み込みと呼ばれます。

Day2で再帰をやりましたが、再帰でよく出てきたパターンがありました。リストに対して先頭要素と先頭要素以外のリストに分割し、先頭要素以外のリストを部分問題として扱う、というパターンです。よく出るパターンなので抽象化して扱えると楽です。畳み込みとはこのパターンのためにあります。

Intのリストの各要素の合計を計算するsumメソッドを思い出しましょう。

```scala
def sum(xs: List[Int]): Int = xs match {
  case Nil       => 0
  case x :: tail => x + sum(tail)
}
```

末尾再帰で書き換えたのがこちらです。

```scala
def sum(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum(tail, x + acc)
}
```

これをfoldLeftメソッドで書き換えたのがこちらです。foldLeftは2つの引数リストを持ちます。1つ目の引数リストにはアキュムレータの初期値を、2つ目の引数リストにはアキュムレータとリストの要素を使う計算を行う関数を指定します。

```scala
def sum(xs: List[Int]): Int = xs match {
  xs.foldLeft(0)((acc, x) => x + acc
}
```

なんと、sumメソッドは、リストに用意されているfoldLeftメソッドを使うだけで実現できてしまいます。わざわざsumメソッドを定義するまでもないかもしれません。REPLで試してみましょう。List[A]型のfoldLeftの引数は (B)((B, A) => B) という感じになります。アキュムレータの型がBで、関数の型がアキュムレータとリストの要素受け取るので、`(B, A) => B` 型になります。

```scala
scala> val sum = List(1,2,3,4,5).foldLeft(0)((acc, x) => acc + x)
sum: Int = 15
```

関数内で、引数が1度しか使われていないのであれば、`_`で置き換えることができるのでした。これは引数が複数個でも同様です。つまり、こう書けます。

```scala
scala> val sum = List(1,2,3,4,5).foldLeft(0)(_ + _)
sum: Int = 15
```

このようにリストを先頭とそれ以外のリストに分割するような再帰を畳み込みで実現できました。この式がどのように評価されるのかイメージしてみましょう。

```scala
List(1,2,3,4,5).foldLeft(0)((acc, x) => acc + x)
(0 + 1) + List(2,3,4,5).foldLeft(0)((acc, x) => acc + x)
((0 + 1) + 2) + List(3,4,5).foldLeft(0)((acc, x) => acc + x)
(((0 + 1) + 2) + 3) + List(4,5).foldLeft(0)((acc, x) => acc + x)
((((0 + 1) + 2) + 3) + 4) + List(5).foldLeft(0)((acc, x) => acc + x)
(((((0 + 1) + 2) + 3) + 4) + 5) + List().foldLeft(0)((acc, x) => acc + x)
(((((0 + 1) + 2) + 3) + 4) + 5)
15
```

アキュムレータを1番左端として左側に畳込むように評価するのでfoldLeftを使った処理は左畳み込みと呼ばれます。

逆にfoldRightを使った場合は右畳み込みとなります。

```scala
scala> val sum = List(1,2,3,4,5).foldLeft(0)(_ + _)
sum: Int = 15
```

右畳み込みをイメージしてみましょう。


```scala
List(1,2,3,4,5).foldRight(0)((acc, x) => acc + x)
1 + List(2,3,4,5).foldRight(0)((acc, x) => acc + x)
1 + (2 + List(3,4,5).foldRight(0)((acc, x) => acc + x))
1 + (2 + (3 + List(4,5).foldRight(0)((acc, x) => acc + x)))
1 + (2 + (3 + (4 + List(5).foldRight(0)((acc, x) => acc + x))))
1 + (2 + (3 + (4 + (5 + List().foldRight(0)((acc, x) => acc + x)))))
1 + (2 + (3 + (4 + (5 + 0))))
```

先ほど出てきたmapメソッドと同じことを畳み込みを使って実装してみましょう。アキュムレータは空のリストになります。

```scala
scala> val xs = List(1,2,3,4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
<console>:13: error: type mismatch;
 found   : List[Int]
  required: scala.collection.immutable.Nil.type
         val xs = List(1,2,3,4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
```

アキュムレータの型をうまく推論してくれないので、明示的にList[Int]と指定してあげましょう。

```scala
scala> val xs = List(1,2,3,4,5).foldLeft(Nil: List[Int])((acc, x) => acc ++ List(x * 2))
xs: List[Int] = List(2, 4, 6, 8, 10)
```

mapと同じ動きができました。リストの++メソッドによる連結は問題があります。計算速度が、++メソッドのレシーバであるリストの長さに依存しており、長いリストに対してこの処理を行うと遅くなります。

上のfoldLeftを使った式がどのように評価されるのかイメージしてみましょう。

```scala
List(1,2,3,4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
(Nil ++ List(1 * 2)) ++ List(2,3,4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
((Nil ++ List(1 * 2)) ++ List(2 * 2)) ++ List(3,4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
(((Nil ++ List(1 * 2)) ++ List(2 * 2)) ++ List(3 * 2)) ++ List(4,5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
((((Nil ++ List(1 * 2)) ++ List(2 * 2)) ++ List(3 * 2)) ++ List(4 * 2)) ++ List(5).foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
(((((Nil ++ List(1 * 2)) ++ List(2 * 2)) ++ List(3 * 2)) ++ List(4 * 2)) ++ List(5 * 2)) ++ List().foldLeft(Nil)((acc, x) => acc ++ List(x * 2))
Nil ++ List(2) ++ List(4) ++ List(6) ++ List(8) ++ List(10)
List(2) ++ List(4) ++ List(6) ++ List(8) ++ List(10)
List(2, 4) ++ List(6) ++ List(8) ++ List(10)
List(2, 4, 6) ++ List(8) ++ List(10)
List(2, 4, 6, 8) ++ List(10)
List(2,4,6,8,10)
```

++メソッドのレシーバーがどんどん大きくなっていくのが分かりますね。

こういう場合はfoldRightによる右畳み込みが使えます。右畳み込みはレシーバであるリストの右から順番に処理します。そのため、++メソッドよりも効率よく処理できる::メソッドを使うことができます。foldRightの引数は (B)((A, B) => B) という感じです。アキュムレータの型がBで、関数がリストの要素とアキュムレータを受け取るため `(A, B) => B` 型となります。

```scala
scala> val xs = List(1,2,3,4,5).foldRight(Nil: List[Int])((x, acc) => (x * 2) :: acc)
xs: List[Int] = List(2, 4, 6, 8, 10)
```

右畳み込みを使った式がどのように評価されるのかイメージしてみましょう。

```scala
(1 * 2) :: List(1,2,3,4,5).foldRight(Nil: List[Int])((x, acc) => (x * 2) :: acc)
(1 * 2) :: ((2 * 2) :: List(3,4,5).foldRight((2 * 2) :: Nil)((x, acc) => (x * 2) :: acc))
(1 * 2) :: ((2 * 2) :: ((3 * 2) :: List(4,5).foldRight((2 * 2) :: Nil)((x, acc) => (x * 2) :: acc)))
(1 * 2) :: ((2 * 2) :: ((3 * 2) :: ((4 * 2) :: List(5).foldRight((2 * 2) :: Nil)((x, acc) => (x * 2) :: acc))))
(1 * 2) :: ((2 * 2) :: ((3 * 2) :: ((4 * 2) :: ((5 * 2) :: List().foldRight((2 * 2) :: Nil)((x, acc) => (x * 2) :: acc)))))
(1 * 2) :: ((2 * 2) :: ((3 * 2) :: ((4 * 2) :: ((5 * 2) :: Nil))))
2 :: (4 :: (6 :: (8 :: (10 :: Nil))))
2 :: (4 :: (6 :: (8 :: List(10))))
2 :: (4 :: (6 :: List(8, 10)))
2 :: (4 :: List(6, 8, 10))
2 :: List(4, 6, 8, 10)
List(2, 4, 6, 8, 10)
```



## Option

型コンストラクタはList以外にも色々あります。ここではOptionを使ってみましょう。

Optionはリストのように値を格納することができますが、値を1つだけ持つか、もしくは何も持たないかです。値を持つ場合はOptionのサブクラスであるSome、値を持たない場合は同じくOptionのサブクラスであるNoneを使います。

```scala
scala> val opt = Some(2)
opt: Some[Int] = Some(2)

scala> val opt = None
opt: None.type = None
```

それぞれSome[Int]型とNone型です。これらの型はOption[Int]型としても扱えます。

```scala
scala> val opt: Option[Int] = None
scala> val opt: Option[Int] = Some(2)
```

また、Someはケースクラスであるため、パターンマッチでマッチさせつつ中身の値を取り出すことができます。

```scala
scala> opt match {
     |   case Some(x) => println(x)
     |   case None    => println("none")
     | }
2
```

Optionは値が存在しないかもしれない場合や、計算が失敗することがあるかもしれない、という場合に使います。正常に計算できた場合はSomeを、k結果が存在しない、もしくは計算に失敗した場合はNoneを返すようなメソッドを定義します。そうするとメソッドの返り値の型がOptionであることから、このメソッドは結果が存在しないことがあるんだな、と分かります。

さっきつくったheadメソッドは、空のリストを渡すとエラーになってしまっていました。headメソッドの返り値の型をOption[A]にして、空のリストの場合はNoneを返すようにしましょう。空でない場合はSome[A]を返します。

```scala
scala> def head[A](list: List[A]): Option[A] = list match {
     |   case Nil => None
     |   case x :: _ => Some(x)
     | }
head: [A](xs: List[A])Option[A]

scala> val xs = List(1,2,3)
scala> val res = head(xs)
res: Option[Int] = Some(1)

scala> res match {
     |   case None    => "error"
     |   case Some(x) => s.toString
     | }

scala> val xs = Nil
scala> val res = head(xs)
res: Option[Nothing] = None

scala> res match {
     |   case None    => "error"
     |   case Some(x) => s.toString
     | }
```

Optionから結果を取り出すときはパターンマッチを使いましたが他にも色々あります。こちらの記事が参考になります -> [ScalaのOptionステキさについてアツく語ってみる - ( ꒪⌓꒪) ゆるよろ日記](http://yuroyoro.hatenablog.com/entry/20100710/1278763193)

```scala
scala> Some("ABC").getOrElse("DEFAULT")
scala> Some("ABC").foreach(println(_))
scala> Some("ABC").exits(_ == "AAA")
```

Optionには、リストの高階メソッドで出てきたmapメソッドを持っています。

```scala
scala> val xs = List(1,2,3)
scala> head(xs).map(_ * 2)
res36: Option[Int] = Some(2)

scala> val xs: List[Int] = Nil
scala> head(xs).map(_ * 2)
res37: Option[Int] = None
```

mapメソッドの便利なところはOptionがSomeなのかNoneなのか気にしなくていいところです。パターンマッチを使って書くとこうなっているところでした。

```scala
scala> head(xs) match {
     |   case Some(x) => Some(x * 2)
     |   case None    => None
     | }
```

filterメソッドもあります。withFilterメソッドもあるのでmapとつなげる場合はこちらの方が高速でいいです。

```scala
scala> Some(5).filter(_ > 3)
res49: Option[Int] = Some(5)

scala> Some(5).filter(_ > 6)
res50: Option[Int] = None

scala> Some(5).withFilter(_ > 3).map(_ * 2)
res51: Option[Int] = Some(10)

scala> Some(5).withFilter(_ > 6).map(_ * 2)
res52: Option[Int] = None
```

flatMapメソッドもあります。これはOptionを返すメソッドを複数個つなげたい場合に便利です。例えば、割り算をするメソッドを用意します。0で割ろうとした場合にNoneを返します。これをさっきのheadメソッドとつなげてみましょう。

```scala
scala> def div(n: Int, d: Int): Option[Int] = if (d == 0) None else Some(n / d)

scala> head(List(2,4)).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
res57: Option[Int] = Some(20)

scala> head(List()).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
res58: Option[Int] = None

scala> head(List(0,4)).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
res59: Option[Int] = None

scala> head(List(2,4)).flatMap(div(6, _)).withFilter(_ > 3).map(_ * 5)
res60: Option[Int] = None
```

2つ目、3つ目、４つ目の結果がNoneになっています。しかし、Noneになった原因はそれぞれ異なります。2つ目は最初の`head(List())`の時点でNoneになり計算全体としての結果がNoneになります。3つ目は`flatMap(div(8, _)`の時点でNoneになります。3つ目は`withFilter(_ > 3)`の時点でNoneになります。

これらの高階メソッドを使うことで、計算の途中結果がSomeなのかNoneなのか気にしなくてよくなり、パターンマッチを何度も書かなくて済みます。



## コレクション以外でもfor式

リストのmap、withFilter、flatMapメソッドはfor式でも同じことができると言いました。実際、Scalaではmap、filter、withFilter、flatMap、foreachメソッドをfor式で書くことができます。

自分で定義した型でもこれらのメソッドを持っていればfor式で使えるのです。

```scala
case class Capsule[A](x: A) {
  def map[B](f: A => B): Capsule[B] = Capsule(f(x))
  def flatMap[B](f: A => Capsule[B]): Capsule[B] = f(x)
}
```

普通にメソッドを呼び出してみましょう。

```scala
scala> Capsule(2).map(x => x * 5)
res81: Capsule[Int] = Capsule(10)

scala> Capsule(2).flatMap(x => Capsule(x * 5)).map(x => x + 3)
res82: Capsule[Int] = Capsule(13)
```

for式で使ってみます。

```scala
scala> for (x <- Capsule(2);
     |      y <- Capsule(x * 5))
     |   yield y + 3
res83: Capsule[Int] = Capsule(13)
```

使えましたね。ScalaのAPIドキュメントなどを読んでいてmap、filter、withFilter、flatMap、foreachなどを見つけたら注目しましょう。これらを使ってfor式で書いてるコードがあるかもしれないですし、自分で書くときもfor式を使うと読みやすくなるかもしれません。for式への置き換えについては、こちらのサイトに分かりやすく書いてあります -> [Scala for実体メモ(Hishidama's Scala for-convert Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/collection/for.html)

Optionにもmap、filterWith、flatMapメソッドがありました。つまり、Optionをfor式で使えるということです。

flatMapを使った例をもう一度見てみましょう。

```scala
scala> head(List(2,4)).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
scala> head(List()).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
scala> head(List(0,4)).flatMap(div(8, _)).withFilter(_ > 3).map(_ * 5)
scala> head(List(2,4)).flatMap(div(6, _)).withFilter(_ > 3).map(_ * 5)
```

Optionは値を1つだけ入れられる入れ物のようですが、計算に失敗するかもしれないという文脈に入れていると捉えることもできます。map、filterWith、flatMapを使うことで、文脈から値を取り出すことなく文脈内の値を様々な関数に適用することができます。

上の例をfor式で書き直してみましょう。for式の1番外側の括弧は`{}`で書くこともできます。

```scala
scala> for(x <- head(List(2,4));
     |     y <- div(8, x);
     |     if (y > 3))
     |   yield y * 5

scala> for {
     |   x <- head(List());
     |   y <- div(8, x);
     |   if (y > 3)
     | } yield y * 5

scala> for {
     |   x <- head(List(0,4));
     |   y <- div(8, x);
     |   if (y > 3)
     | } yield y * 5

scala> for {
     |   x <- head(List(2,4));
     |   y <- div(6, x);
     |   if (y > 3)
     | } yield y * 5
```

for式で書いたものを上から順に読んでいくと、headの結果を`x`に代入して、`div(8, x)`を実行して結果を`y`に代入して・・・というように、手続き的に書いたプログラムのように読めます。forが関数の始まり、yieldがreturnだと考えると手続き的に書いた関数のようです。

OptionにmapとflatMapがあることで、Option内に格納されてる値に対して色々な関数を適用でき、for式に置き換えることでそれが手続き的なプログラミングように見えます。失敗するかもしれない計算が続く場合、つまりflatMapが何回も続くような場合に、for式で書いた方が読みやすく感じるかもしれません。

ScalaでmapとflatMapメソッド持つ型をモナドと呼んだりします。例えばOptionがモナドです。for式はコレクションのための構文ではなく、もっと抽象化されたモナドのための構文ということになります。モナドの厳密な話はできないのでここではしません（知識が足りないです、ごめんなさい・・・）。興味ある人はWebで調べたり、[Scala関数型デザイン&プログラミング](http://www.amazon.co.jp/Scala%E9%96%A2%E6%95%B0%E5%9E%8B%E3%83%87%E3%82%B6%E3%82%A4%E3%83%B3-%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0-%E2%80%95Scalaz%E3%82%B3%E3%83%B3%E3%83%88%E3%83%AA%E3%83%93%E3%83%A5%E3%83%BC%E3%82%BF%E3%83%BC%E3%81%AB%E3%82%88%E3%82%8B%E9%96%A2%E6%95%B0%E5%9E%8B%E5%BE%B9%E5%BA%95%E3%82%AC%E3%82%A4%E3%83%89-impress-gear/dp/4844337769/)を読んでみるとおもしろいと思います。



## Map

連想配列として使えるMapも型コンストラクタです。これは型パラメータを2つ受け取る型コンストラクタです。キーの型と値の型の２種類です。使ってみましょう。

```scala
scala> val m = Map[String, Int](("key1", 1), ("key2", 2))
scala> val m = Map(("key1", 1), ("key2", 2))
scala> val m = Map("key1" -> 1, "key2" -> 2)
m: scala.collection.immutable.Map[String,Int] = Map(key1 -> 1, key2 -> 2)

scala> m.get("key1")
res94: Option[Int] = Some(1)

scala> m.get("hogehoge")
res95: Option[Int] = None
```

MapシングルトンオブジェクトのapplyメソッドでMapのインスタンスをつくっています。引数にはキーと値をタプルで渡してます。メソッド呼び出し時の型パラメータは省略可能でした。Mapのインスタンスをつくるときは`Map(("key1", 1), ("key2", 2))`ではなく`Map("key1" -> 1, "key2" -> 2)`という風に書くことが多いです。この書き方についてはDay4で説明します。

Mapから値を取り出すにはgetメソッドを使いますが、getメソッドの返り値はOptionです。指定したキーが存在しない場合はNoneが返ります。

一度つくったマップに要素を追加してみましょう。追加は+メソッドです。Mapもイミュータブルであるため、要素が追加されたMapをつくるだけで、元々のMapに変更はありません。

```scala
scala> m + ("key3" -> 3)
res96: scala.collection.immutable.Map[String,Int] = Map(key1 -> 1, key2 -> 2, key3 -> 3)

scala> m
res97: scala.collection.immutable.Map[String,Int] = Map(key1 -> 1, key2 -> 2)
```



## 練習問題



## 今日出てきたキーワード

* 型パラメータ
* 多相
* 型コンストラクタ
* ファーストクラスな関数
* 関数リテラル
* 部分適用
* 高階メソッド
* ローンパターン
* 複数の引数リスト、カリー化されたメソッド
* 左畳み込み、右畳み込み
* Option



## 要注意ポイント

* ListやOptionなどは型ではなく型コンストラクタ
* Optionの高階メソッドを使うことで、Optionの結果を都度パターンマッチしなくて済む
* map、filter、withFilter、flatMap、foreachメソッドをfor式で書き直すことができる


## 参考サイト

* [Scala の省略ルール早覚え](https://gist.github.com/gakuzzzz/10104162)
* [Scala using(Hishidama's Scala loan-pattern Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/sample/using.html)
* [ScalaのOptionステキさについてアツく語ってみる - ( ꒪⌓꒪) ゆるよろ日記](http://yuroyoro.hatenablog.com/entry/20100710/1278763193)
* [Scala for実体メモ(Hishidama's Scala for-convert Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/collection/for.html)
* [Scala関数型デザイン&プログラミング](http://www.amazon.co.jp/Scala%E9%96%A2%E6%95%B0%E5%9E%8B%E3%83%87%E3%82%B6%E3%82%A4%E3%83%B3-%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0-%E2%80%95Scalaz%E3%82%B3%E3%83%B3%E3%83%88%E3%83%AA%E3%83%93%E3%83%A5%E3%83%BC%E3%82%BF%E3%83%BC%E3%81%AB%E3%82%88%E3%82%8B%E9%96%A2%E6%95%B0%E5%9E%8B%E5%BE%B9%E5%BA%95%E3%82%AC%E3%82%A4%E3%83%89-impress-gear/dp/4844337769/)

