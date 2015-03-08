# Day3 書きかけ

## 型パラメータ

前回リストの専用要素返すheadメソッドを自分で作ってみました。

```scala
def head(xs: List[Int]): Int = xs match {
  case Nil => throw new IllegalArgumentException
  case x :: _ => x
}
```

このメソッドはList[Int]型にしか対応していませんが、List[String]やList[Double]など、他の型のリストにも対応できるはずです。`xs: List[Int]`という部分を`xs: List[A]`としてAの部分にIntやStringなど、色々な型を取る可能性がありますよ、という風にしたいです。これには型パラメータを使います。型パラメータはメソッド名のすぐ後ろ、引数の前に書きます。

```scala
def head[A](xs: List[A]): A = xs match {
  case Nil => throw new IllegalArgumentException
  case x :: _ => x
}
head: [A](xs: List[A])A
```

使うときも`[]`で型パラメータを指定します。型パラメータは、引数の型から推論可能なので省略できます。例えば、`List(1,2,3)`が指定された場合は、引数xsの型、`List[A]`が`List[Int]`であるので、型パラメータ`A`が`Int`であると推論できます。

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


これでheadメソッドは色々な型に対応できるようになりました。このように色々な型を受け取れるメソッドは多相である、と言います。

多相には2種類あります。今回のように型パラメータを使った多相を普遍的多相と言います。Shapeを継承した2種類のRectangleとCircleを定義しました。そしてShapeを受け取れるメソッドをつくりました。この場合もShape継承したクラスであれば受け取れるため多相と言えます。このような継承を利用した多相をサブタイプ多相と言います。

型パラメータはメソッドだけでなく、クラスなどでも使えます。

これまでList[Int]型のリストをたくさん使ってきました。List型というのはなくList[Int]型です、というようなことを説明しました。Listは型ではありません。Listは型コンストラクタと呼ばれるものです。型コンストラクタに型パラメータを与えると型になります。コンストラクタにパラメータを渡すとインスタンスが取得できるのと同じようなイメージです。Listという型コンストラクタにIntという型パラメータを渡すと、List[Int]型になります。

型コンスタクタをつくる場合も型パラメータは`[]`を使います。例えばケースクラスで型パラメータを使ってみましょう。インスタンス化すると、型が`Capsule[Int]`、`Capsule[String]`、`Capsule[List[Int]]`となり、型コンストラクタから複数の型をつくれることが分かります。

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

Scalaではメソッドと似ていますが、オブジェクトに属さずに単独で存在できる「関数」があります。Scalaにおいて関数はファーストクラスオブジェクトです。ファーストクラスオブジェクトであるとは、なんらかのオブジェクトに属さずにそれ単独で値のように扱えるというようなことです。値のように扱えるので、変数に代入したり、他のメソッドの引数に関数を渡したり、リストに複数の関数を格納したり、ということができます。

では関数をつくってみましょう。関数をつくために関数リテラルが用意されています。

```scala
scala> val f = (x: Int) => x * 2
f: Int => Int = <function1>
```

引数として1つのIntオブジェクトを受け取り、2倍にして返す関数です。その関数を変数`f`に代入しています。
REPLの出力を見てみましょう。`f: Int => Int = <function1>`というのは、「fという変数は Int => Int 型で、値は <function1> です」という意味になります。Int => Int 型というのは、引数として1つのIntをとり返り値がIntである関数を表します。値の <function1> というのは関数本体を書きたいところだけど書くと長くなっちゃうので<function1>と書いて値が関数であることを示してるんだと解釈してます。

では、fを使ってみましょう。関数にある適当な数値を渡してみましょう。関数fに数値10を適用する、と言うこともできます。

```scala
scala> f(10)
res1: Int = 20
```

関数を定義し、関数で定義した処理を実行することができました。



## コレクションの高階メソッド
関数がファーストクラスオブジェクトであることは分かりました。では関数は何に役立つのでしょうか？

役立つシーンの1つに関数を受け取るメソッドがあります。関数を受け取るメソッドを高階メソッドと呼びます。高階メソッドの中でもコレクションが持つ高階メソッドはよく使うことになるでしょう。

コレクションの高階メソッドの1つ、foreachメソッドを使ってみます。foreachメソッドに渡せる関数の型は、A => Unit です。foreachは、受け取った関数にコレクションの要素をそれぞれ順番に適用します。

```scala
scala> val cs = ('A' to 'Z')
scala> cs.foreach((c: Char) => println("Hello, " + c))
```

csがCharのコレクションなので、関数の引数であるcがCharであることはScalaが推論できます。なので、cの型は省略できます。

```scala
scala> cs.foreach((c) => println("Hello, " + c))
```

関数の引数が1つの場合は`()`も省略できます。

```scala
scala> cs.foreach(c => println("Hello, " + c))
```

さらに、引数を1回しか使っていない場合はもっと短く書くことができます。`c => println("Hello, " + c)` が `_ => println("Hello, " + _)` になって、さらに `println("Hello, " + _)` になる、といったイメージです。

```scala
scala> cs.foreach(println("Hello, " + _))
```

だいぶすっきりしますね。

次は、mapメソッドを使ってみます。mapメソッドは、引数で受け取った関数にコレクションそれぞれの要素を適用した結果からなる新たなコレクションを作ります。mapに渡せる関数の型は A => B となります。

```scala
scala> val xs = (1 to 10)
scala> xs.map((x: Int) => x * 2)
```

このmapメソッド。あるコレクションを別のコレクションに変換します。これってどこかで聞き覚えないですか？そうです、前回やったfor式ですね。上のmapを使った例はfor式でも書けますね。

```scala
scala> for(x <- xs) yield x * 2
```

どちらを使っても同じことができます。どちらを使うべきかというのは現時点では特にないので読みやすいを使えばいいかと思います。

foreach、 mapの他に、filterという高階メソッドがあります。filterにはBooleanを返す関数を渡すことができます。Booleanを返す関数のことを「述語」と呼んだりします。述語関数に各要素を適用した結果がtrueになる要素のみからなるコレクションを取得できます。filterに渡せる関数の型は A => Boolean です。

```scala
scala> xs.filter((x: Int) => x > 6)
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

もう1つ、flatMapメソッドを見てみましょう。flatMapはmapと同じように値変換するような感じなのですが、ちょっと違います。flatMapに渡す変換のための関数ではコレクションを返す必要があるのです。flatMapはコレクションを返す関数に各要素を適用した結果、複数のコレクションが手に入るのでそれらを結合します。flatMapに渡せる関数の型は A => List[B] となります。

```scala
scala> List(1,2,3,4).flatMap((a: Int) => 1 to a)
scala> List(1,2,3,4).flatMap(a => 1 to a)
scala> List(1,2,3,4).flatMap(1 to _)
```

このflatMapもfor式と関わりがあります。ジェネレータを複数回続けた場合と同じことになります。2つのコレクションをfor式の2つのジェネレータに書くと、各要素を組み合わせた処理を書けました。

例えば前回の練習問題で三角形を3要素のタプルで表現するのがありました。for式、flatMap、両方を使った場合はこうなります。

```scala
scala> for(c <- 1 to 10; a <- 1 to c; b <- 1 to a) yield (a,b,c)
scala> (1 to 10).flatMap(c => (1 to c).flatMap(a => (1 to a).map(b => (a,b,c))))
```

直角三角形だけを抽出したい場合はこうなります。

```scala
scala> for(c <- 1 to 10; a <- 1 to c; b <- 1 to a; if c*c == a*a + b*b) yield (a,b,c)
scala> (1 to 10).flatMap(c => (1 to c).flatMap(a => (1 to a).withFilter(b => c*c == a*a + b*b).map(b => (a,b,c))))
```

うーん、これはfor式を使った方が見やすい気がしますね。

他にもいろいろな高階メソッドがありますが、今日のところはここまでにして次に進みます。



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

ファイル入出力はJavaのjava.nio.file.Filesクラスなどを使います。クラス名より前の部分"java.nio.file"の部分をパッケージと言います。これは名前空間で、同じクラス名でも別パッケージであれば定義できるようnなっています（例えば、hoge.FilesクラスをつくってもOK）。上の例だとFilesとPathsがjava.nio.fileパッケージなのですが、パッケージ名から書くのは大変です。こういうときは`import`でパッケージを指定すると、指定したパッケージ内のクラスはクラス名だけで使うことができるようになります。

注目したいのは`try { } finally { }`の部分です。`try { }`で囲まれたコードでエラーが発生しても`finally { }`で囲まれたコードが実行されます。エラーが発生しなくても`finally { }`で囲まれた部分は実行されます。ファイルのクローズのようにエラーが発生しようがしまいが必ず実行したいコードは`finally { }`で囲みます。

ファイルの書き込み処理を行う箇所では、この`try { } finally { }`を書く必要があります。都度書いているとDRYではなくなります。こういう場合に便利なのがローンパターンと呼ばれる方法です。`try { } finally { }`という構造を別メソッドで定義し、`try { }`で囲みたい処理は、java.io.Writer => Any という型の関数で渡してあげるようにします。

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
  using(fileName, (writer) => writer.write(body))
}
```

これでファイルを書き込むときはusingメソッドを使えばよく、`try { } finally { }`をいろんなところに書く必要はなくなりました。ローンパターンはこちらのページに色々載ってます -> [Scala using（ローンパターン）](http://www.ne.jp/asahi/hishidama/home/tech/scala/sample/using.html)



## カリー化されたメソッド

usingメソッドを使うことでファイルのオープン・クローズをDRYにできました。usingメソッドですが、言語の組み込み機能であってもよさそうですよね。残念ながらScalaには最初からusingは用意されていません。しかし、usingをあたかも組み込みの構文のように見せることができます。usingを定義するときの引数リスト、メソッド名の右にある`()`の部分を2つに分けてみましょう。

```scala
def using(fileName: String)(f: java.io.Writer => Any): Unit = {
  ・・・
```

この例のように、Scalaでは複数引数がある場合に、引数リストを複数個に分けることができます。これをカリー化されたメソッドと呼びます。カリー化されたメソッドを呼び出す方も2つの引数リストを指定します。

```scala
def writeTo(fileName: String, body: String): Unit = {
  using(fileName)((writer) => writer.write(body))
}
```

さらに、Scalaでは引数が1つの引数リストの`()`は`{}`と書くことができます。これを使うとusingが組み込みの構文であるかのように使えます。

```scala
def writeTo(fileName: String, body: String): Unit = {
  using(fileName) {
    writer => writer.write(body)
  }
}
```

このように言語を拡張していける柔軟性がScalaの特徴の1つです。



## 畳み込み

コレクションの高階メソッドに戻ります。ここで紹介するのはfoldLeftとfoldRightです。これらの高階メソッドは畳み込みと呼ばれます。

Day2で再帰をやりましたが、再帰でよく出てきたパターンがありました。リストに対して先頭要素と先頭要素以外のリストに分割し、先頭要素以外のリストを部分問題として扱う、というパターンです。畳み込みとはこのパターンのためにあります。

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

これをfoldLeftメソッドで書き換えたのがこちらです。foldLeftはカリー化されていて、2つの引数リストを持ちます。1つ目の引数リストにはアキュムレータの初期値を、2つ目の引数リストにはアキュムレータとリストの要素を使う計算を行う関数を指定します。

```scala
def sum(xs: List[Int]): Int = xs match {
  xs.foldLeft(0)((acc, x) => x + acc
}
```

なんと、sumメソッドは、リストに用意されているfoldLeftメソッドを使うだけで実現できてしまいます。わざわざsumメソッドを定義するまでもないかもしれません。REPLで試してみましょう。foldLeftの引数は (B)((B, A) => B という感じになります。

```scala
scala> val sum = List(1,2,3,4,5).foldLeft(0)((acc, x) => acc + x)
sum: Int = 15
```

関数内で、引数が1度しか使われていないのであれば、`_`で置き換えることができるのでした。これは引数が複数個でも同様です。つまり、こう書けます。

```scala
scala> val sum = List(1,2,3,4,5).foldLeft(0)(_ + _)
sum: Int = 15
```

このようにリストを先頭とそれ以外のリストに分割するような再帰を畳み込みで実現できました。

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

mapと同じ動きができました。リストの++メソッドによる連結は問題があります。計算速度が、++メソッドのレシーバであるリストの長さに依存します。こういう場合はfoldRightによる右畳み込みが使えます。右畳み込みはレシーバであるリストの右から順番に処理します。そのため、++メソッドよりも効率よく処理できる::メソッドを使うことができます。foldRightの引数は (B)((A, B) => B) とい感じです。

```scala
scala> val xs = List(1,2,3,4,5).foldRight(Nil: List[Int])((x, acc) => (x * 2) :: acc)
xs: List[Int] = List(2, 4, 6, 8, 10)
```



## Option

型コンストラクタはList以外にも色々あります。ここではOptionを使ってみましょう。



## Map

連想配列として使えるMapも型コンストラクタです。これは型パラメータを2つ受け取る型コンストラクタです。



## 練習問題

