# Day4 （書きかけ）

## 暗黙の型変換

MapをつくるときMapのapplyメソッドを使いました。`Map(("key1", 1), ("key2", 2))`という風に使いますが、`Map("key1" -> 1, "key2" -> 2)`とも書けました。

`("key1", 1)`というタプルの代わりに`"key1" -> 1`を指定しています。"key1"というStringの->メソッドを呼び出し、その結果がタプルになっていれば辻褄が合います。しかし、ScalaのStringはJavaのStringを使っていて、JavaのStringには->メソッドは存在しません。

暗黙の型変換（implicit conversion）という仕組みを利用して、Stringに->メソッドがあるかのように振る舞わせることができます。

まず、暗黙の型変換とはなんでしょうか。Day2で出てきたRectangle型を思い出しましょう。

```scala
scala> case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double)
scala> val rec = Rectangle(0, 0, 2, 3)
```

Rectangleケースクラスは、Double型の値を4つ受け取るように定義されていますが、インスタンス化するときにInt型の値を指定しています。Int型の値を指定してるのにコンパイルが通るのは、暗黙の型変換によりInt型がDouble型に変換されてるためです。

Rectangleケースクラスに、Rectangle型の値を受け取り、自分自身と面積を比べて大きい方を返すmaxメソッドを追加してみます。

```scala
scala> case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
     |   val area = math.abs(x2 - x1) * math.abs(y2 - y1)
     |   def max(a: Rectangle): Rectangle = if (a.area > this.area) a else this
     | }

scala> val rec = Rectangle(0, 0, 2, 3)
scala> rec.max(Rectangle(0, 0, 1, 2))
```

4要素のタプルをmaxメソッドに渡してみましょう。

```scala
scala> rec.max((0, 0, 1, 2))
<console>:11: error: type mismatch;
 found   : (Int, Int, Int, Int)
 required: Rectangle
              rec.max((0, 0, 1, 2))
                      ^
```

当然コンパイルエラーになりますね。ここで、Int型が4つのタプルを、Rectangle型へ変換するメソッドをつくってみます。

```scala
scala> def toRectangle(t: (Int, Int, Int, Int)): Rectangle = Rectangle(t._1, t._2, t._3, t._4)
scala> rec.max(toRectangle(0, 0, 1, 2))
```

この`toRectangle(0, 0, 1, 2)`というメソッド呼び出しを暗黙的に実行させ、暗黙の型変換を行うようにします。暗黙の型変換は、変換処理を行うメソッドに`implicit`をつけます。

```scala
scala> implicit def toRectangle(t: (Int, Int, Int, Int)): Rectangle = Rectangle(t._1, t._2, t._3, t._4)
scala> rec.max((0, 0, 1, 2))
```

4要素のタプルをRectangle型へ変換を行う暗黙の型変換を定義できました。

この暗黙の型変換を使うと、元々存在している型にメソッドを追加することができます。

例えば、数値に文字列を渡すと、数値の回数だけ渡された文字列を繰り返すrepeatというメソッドを、Intにつけ足してみましょう。`3.repeat("Hoge")`というように呼べたらOKです。

まずは、RepeatorというIntとは別のクラスを作ってみましょう。

```scala
scala> class Repeater(x: Int) {
     |   def repeat(s: String) = (0 until x).foldLeft("")((acc, a) => acc + s)
     | }

scala> new Repeater(3).repeat("hogehoge")
```

`new Repeator(3)`というのを暗黙的に行うようにします。クラスに`implicit`をつけます。

```scala
scala> implicit class Repeater(x: Int) {
     |   def repeat(s: String) = (0 until x).foldLeft("")((acc, a) => acc + s)
     | }
defined class Repeater

scala> 3.repeat("hogehoge")
```

Int型に新たにrepeatメソッドをつけ足すことができました。クラスに`implicit`をつけるやり方はScala2.10以降にできるようになりました。それ以前はメソッドにimplicitをつける方法を使用するPimp My Libraryと呼ばれるパターンが使われていました。参考サイト -> [Scala 2.10.0 M3の新機能を試してみる(2) - SIP-13 - Implicit classes](http://kmizu.hatenablog.com/entry/20120506/1336302407)

`implicit class`をつけた方法は毎回Repeaterクラスをnewしてしまいます。AnyValを継承すると効率を上げることができます。こちらを参考してください。[Scalaメソッド定義メモ(Hishidama's Scala def Memo) # 暗黙クラス（implicit class）](http://www.ne.jp/asahi/hishidama/home/tech/scala/def.html#h_implicit_class)

Mapで使った->メソッドも暗黙の型変換によって実現されています。`"key1" -> 1`と書くと暗黙の型変換によりStringである"key1"に->メソッドがあるかのように振る舞い、`("key1", 1)`というタプルを返します。

この暗黙の型変換は、Predefというシングルトンオブジェクトに定義されています。ScalaではデフォルトでPredefシングルトンオブジェクトのメソッドをimportしています。シングルトンオブジェクトというのは初めて出てきましたが、ちょっと後でやります。ここではいくつかの暗黙の型変換メソッドがデフォルトで用意されている、ということを分かってください。Prefefには便利な暗黙の型変換が用意されています。 [Scala Predefオブジェクトメモ(Hishidama's Scala Predef Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/predef.html)

このようにScalaでは暗黙の型変換を使うことで、既存のクラスを拡張できます。

暗黙の型変換について、コップ本に注意点が書いてあります。

> 暗黙の型変換の使い方を誤ると、クライアントコードを読みにくく理解しにくいものにしてしまう危険がある。暗黙の型変換はソースコードに明示的に書き出されるのではなく、コンパイラが暗黙のうちに適用するので、クライアントプログラマーからは、どのような暗黙の型変換が適用されているのかはっきりとはわからない。
> （中略）
> 簡潔さは読みやすさの大きな構成要素だが、簡潔すぎてわからないということもある。効果的な簡潔さをもたせて、わかりやすいクライアントコードを書けるライブラリーを設計すれば、クライアントプログラマーたちが生産的に仕事を進めるのを後押しできる。

引用元：[Scalaスケーラブルプログラミング第2版](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845/)



## 暗黙の引数

暗黙の型変換で利用した`implicit`というキーワードですが、型変換とは別のところでも利用します。それが暗黙の引数です。メソッドの引数リストのうち一番最後の引数リストに`implicit`をつけることができます。

```scala
scala> def greet(name: String)(implicit s: String) = s"${s}, ${name}!"
```

このメソッドは今まで通り使うことができます。2つ目の引数を省略したらエラーですね。

```scala
scala> greet("Taro")("Hello")
res0: String = Hello, Taro

scala> greet("Taro")
<console>:9: error: could not find implicit value for parameter s: String
              greet("Taro")
                   ^
```

エラーメッセージをよく読むと、「引数が足りません」ではなく、「implicit value が見つかりません」というようなことが書いてあります。「implicit value」とは、変数宣言に`implicit`をつけたものです。

`implicit`がついた変数を用意してみましょう。

```scala
scala> implicit val x = "Hello"
x: String = Hello

scala> greet("Taro")
res2: String = Hello, Taro
```

2つ目の引数を指定しなくてもgreetメソッドが動きました。このように`implict`がついた引数リストを暗黙の引数、`implict`がついた値を暗黙の値といいます。

これが何の役に立つのでしょうか。1つ例を挙げるとDBへのコネクションなどを色々なメソッドで使いまわしたいオブジェクトがあるときに便利です。何度も何度も引数に渡すことになるのでグローバル変数で扱えるようにしたくなりますが、それは引数以外の状態に依存することになりテストしづらくなったりします。グローバル変数で扱う代わりに、DBコネクションを暗黙の値として定義し、DBコネクションを利用するメソッドは暗黙の引数としてDBコネクションを受け取るようにすることで、面倒さを回避できます。

シンプルなのでありがたみが余りないですが、

```scala
val connection = connectDb
val user = findById(userId, connection)
user.modifyName("Jiro")
update(user, connection)
```

というコードを以下のように書き換えることができる、という意味です。

```scala
implicit val connection = connectDb
val user = findById(userId)
user.modifyName("Jiro")
update(user)
```



## activator(sbt)

sbtというのはScalaのビルドツールです。activatorというのはsbtにプロジェクトのテンプレート作成機能などを追加したツールです。

以下のコマンドでヘルプが出力されます。

```
$ activator -h
```

以下のコマンドでテンプレート一覧が表示されます。かなりの量です。

```
$ activator list-templates
```

"hello-scala-2_11"というテンプレートでプロジェクトをつくってみましょう。

```
$ activator new hello-project hello-scala-2_11
```

このようなファイル達が自動で生成されます。

```
$ tree -a hello-project/
hello-project/
├── .gitignore
├── LICENSE
├── activator
├── activator-launch-1.3.2.jar
├── activator.bat
├── build.sbt
├── project
│   └── build.properties
└── src
    ├── main
    │   └── scala
    │       └── Hello.scala
    └── test
        └── scala
            └── HelloSpec.scala
```

以下のファイルたちはプロジェクトで使うactivatorです。これらをGitなどのバージョン管理ツールに入れておくことで、プロジェクトメンバー間やCIツールで同じバージョンのactivatorを使うことができます。

* activator
* activator-launch-1.3.2.jar
* activator.bat
* project/build.properties

以下のファイルがactivator(sbt)で使うファイルです。ビルドの設定ファイルです。

* build.sbt

build.sbtの中身を見てましょう。中身はScalaのDSLになっています。`name`や`libraryDependencies`といったキーに対して値を設定することでビルドの設定を行います。キーは値を1つだけ設定できるものと複数設定できるものがあります。`name`は前者で`libraryDependencies`は後者です。

```scala
name := """hello-project"""

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test"
```

build.sbt内の各キーに対する設定は空行で区切ってください。

`:=`というのは代入になります。`name`や`version`という設定を行っています。これらはビルドの成果物のファイル名に使われます。

`+=`というのはコレクションへ要素を追加しています。`libraryDependencies`というコレクションに`"org.scalatest" % "scalatest_2.11" % "2.1.3" % "test"`を追加しています。`libraryDependencies`というのは名前の通り依存するライブラリを設定しておくコレクションです。ここに設定されたライブラリがビルド時にダウンロードされます。

複数のライブラリを使う場合は`+=`を複数回書く必要があります。build.sbtはScalaのDSLですので、Scalaのコードを書くことができます。そのため、`++=`を使って`libraryDependencies`にSeqでつけたすこともできます。例えば以下のように書けます。

```scala
libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.1.3" % "test",
  "com.typesafe.play" %% "play-slick" % "0.8.1"
)
```

以下のファイルがソースコードです。`src/main/scala`ディレクトリにあるソースコードがプロダクションコードです。`src/text/scala`ディレクトリにあるソースコードがテストコードです。これらのディレクトリ内にコードを追加していきます。

* src/main/scala/Hello.scala
* src/test/scala/HelloSpec.scala

ソースコードを見てみましょう。

```scala
object Hello {
  def main(args: Array[String]): Unit = {
    println("Hello, world!")
  }
}
```

Scalaではコンパイル後のファイルを実行するとき、シングルトンオブジェクトの`main(args: Array[String]): Unit` メソッドが実行されます。ソースコードを見てみるとテンプレートで生成されたアプリケーションは"Hello, world!"と標準出力に出力するだけですね。

activatorコマンド使ってビルド・実行をしてみましょう。build.sbtがあるディレクトリでactivatorコマンドを実行すると対話モードになります。対話モードでrunコマンドを実行しましょう。初回はライブラリなどのダウンロードがあるためかなり時間が掛かります。

```
$ ./activator

> run

[info] Running Hello
Hello, world!
[success] Total time: 63 s, completed 2015/04/05 10:46:56
```

動きましたね！対話モードを抜けるときは`exit`と入力しましょう。

```
> exit
```

sbtのドキュメントはこちらにあるので読んでみましょう。 [始めるsbt](http://www.scala-sbt.org/0.13/tutorial/ja/index.html)



## パッケージ

今までつくってきたクラスはすべてグローバル空間に定義していました。プログラムが大きくなると管理するのが大変なので、Scalaではパッケージを使って、メソッド名やクラス名の衝突を防いだり、別パッケージからは見えないメソッドを作ったりします。パッケージは`package`で指定します。ファイルの先頭に指定するのが一般的です。

```scala
// src/scala/Hoge.scala
package example.hoge

case class Hoge(s: String) {
  def hello = "Hello, " + s + "!"
}
```

異なるパッケージのクラスはクラス名だけで使えません。

```scala
object Hello {
  def main(args: Array[String]): Unit = {
    println(new Hoge("World").hello)
  }
}
```

この状態で`activator run`してみましょう。コンパイルエラーになりますね。

```
$ activator run
[error] /・・・/hello-project/src/main/scala/Hello.scala:4: not found: type Hoge
[error]     println(new Hoge("World").hello)
[error]                 ^
[error] one error found
[error] (compile:compile) Compilation failed
```

使う場合はパッケージ+クラス名で書くか、インポートします。インポートは`import`で指定します。インポート時に`_`を使うことでパッケージ内のすべてをインポートすることもできます。

```scala
import example.hoge._

object Hello {
  def main(args: Array[String]): Unit = {
    println(Hoge("World").hello)
  }
}
```

Scalaは、暗黙のうちに以下のインポートをすべてのプログラムに対して行っています。

```scala
import java.lang._
import scala._
import Predef._
```



## アクセス修飾子

定義したクラスやメソッドを外部からアクセスできないようにしたいときに、アクセス修飾子を使います。`private`をつけると同じクラスのみからしかアクセスできなくなります。`protected`をつけると同じクラスかサブクラスからしかアクセスできなくなります。

メソッドに`private`をつけてみます。

```scala
// Hoge.scala
package example.hoge

class Hoge(s: String) {
  private def hello = "Hello, " + s + "!"
}
```

`private`をつけたメソッドは呼べなくなりました。

```
$ activator run
[error] /・・・/hello-project/src/main/scala/Hello.scala:5: method hello in class Hoge cannot be accessed in example.hoge.Hoge
[error]     println(new Hoge("World").hello)
[error]                               ^
[error] one error found
[error] (compile:compile) Compilation failed
```

コンストラクタに`private`をつけてみます。

```scala
package example.hoge

class Hoge private (s: String) {
  def hello = "Hello, " + s + "!"
}
```

`private`をつけたコンストラクタにはアクセスできなくなりました。

```
$ activator run
[error] /・・・/hello-project/src/main/scala/Hello.scala:5: constructor Hoge in class Hoge cannot be accessed in object Hello
[error]     println(new Hoge("World").hello)
[error]             ^
[error] one error found
[error] (compile:compile) Compilation failed
```

もっと細かくアクセス制御したい場合は限定子というものを使います。限定子というのは`private[com.example.hoge]`というようにアクセス修飾子の後ろに`[]`で公開するパッケージやクラス名を指定します。こちらを参考にしてください -> [Scalaクラスメモ(Hishidama's Scala class Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/class.html#h_access_modifiers)



## シングルトンオブジェクトとコンパニオンオブジェクト

クラスやケースクラス以外にも型をつくる方法はいくつかありますが、そのうちの1つがシングルトンオブジェクトです。これはその名の通りシングルトンオブジェクトをつくります。インスタンス化する、というような考え方はないです。定義したらそれが型でありオブジェクトになります。

シングルトンオブジェクトは、`object`を使って定義します。

```scala
object OriginPoint {
  val x = 0.0
  val y = 0.0
}
```

シングルトンオブジェクトは何か1つしかないデータを表すのに使うことよりも、他の使い方で使われることが多いです。

先ほど、Hogeケースクラスのコンストラクタを`private`にしました。そうするとことで`Hoge("world")`ができなくなりました。つまりどこからもインスタンス化できません。しかし、コンパニオンオブジェクトを使うことでインスタンス化できます。

コンパニオンオブジェクトとは、同名のクラス定義と同じファイル内で定義されたシングルトンオブジェクトのことです。コンパニオンオブジェクトは、同名のクラス（これをコンパニオンクラスと言う）の`private`なメソッドやコンストラクタを呼び出すことができます。`Hoge`クラスのコンパニオンオブジェクトを定義してみましょう。

```scala
package example.hoge

case class Hoge private (s: String) {
  def hello = "Hello, " + s + "!"
}
object Hoge {
  def apply(s: String) = new Hoge(s)
}
```

使ってみましょう。applyメソッドはメソッド名を省略できましたね。これはコンパイルできます。

```scala
import example.hoge._

object Hello {
  def main(args: Array[String]): Unit = {
    println(Hoge("World").hello)
  }
}
```

コンパニオンオブジェクトは結構使われています。例えば今まで何回も使ってきた`List(1,2,3,4,5)`というのもListコンパニオンオブジェクトのapplyメソッドを呼び出しています。Listはクラスではなく抽象クラスです。Listコンパニオンオブジェクトのapplyメソッドでは、Listのサブクラスをインスタンス化して返却しています。コンパニオンオブジェクトを使うことで、使う側が実態のクラスを意識しなくて済むようになっています。



## 可変長引数

`List(1,2,3,4,5)`というのはListコンパニオンオブジェクトのapplyメソッドであることが分かりました。このメソッドは可変長引数を受け取るようになっています。そのため、`List(1)`、`List(1, 2, 3, 4, 5)`というように引数の数を可変にできます。

Int型を複数受け取って合計を返すsumメソッドをつくってみましょう。可変長引数を使う場合は引数の型を`Int*`というように`*`をつけます。可変長引数で受け取った引数はSeq型として扱うことができます。Seq型はリストのスーパークラスです。

```scala
scala> def sum(xs: Int*): Int = xs.foldLeft(0)(_ + _)

scala> sum()
scala> sum(1,2,3)
```

可変長引数にList型やSeq型を渡すときは、`: _*`を使います。

```scala
scala> val xs = Seq(1,2,3)
scala> sum(xs: _*)
```



## トレイトの紹介

これまでに型をつくる方法として、クラス、抽象クラス、ケースクラス、シングルトンオブジェクトを紹介しました。他の方法として、トレイトがあります。トレイトは抽象クラスと同様にインスタンス化できません。抽象クラスと異なるのは、抽象クラスは1つしか継承できないのに対し、トレイトの場合は複数継承できます。トレイトを継承するときは「継承」ではなく「ミックスイン」と言います。ミックスインするときは`extends`もしくは`with`を使います。

抽象クラスとトレイトの使い分けに関する参考資料です。 [trait と abstract class の使い分け](https://gist.github.com/gakuzzzz/10081860)

ScalaにはOrderedトレイトが用意されています。Orderedトレイトをミックスインして、compareメソッドをオーバーライドするだけで、オブジェクト比較するメソッドを使うことができます。

Day2で定義したShapeを使ってみましょう。ShapeにOrderedトレイトをミックスインします。Orderedトレイトは1つ型パラメータを1つとる型コンストラクタです。オーバーライドするcompareメソッドの引数の型が型パラメータによって決まります。

```scala
abstract class Shape extends Ordered[Shape]{
  def area: Double
  override def compare(that: Shape) = (this.area - that.area).toInt
}

case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) extends Shape {
  def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}

case class Circle(x: Double, y: Double, r: Double) extends Shape {
  def area: Double = r * r * math.Pi
}
```

使ってみましょう。比較演算子が使えるようになりました。

```scala
scala> val c = Circle(0, 0, 5)
scala> val a = Rectangle(0, 0, 3, 4)
scala> a > c
scala> a >= c
scala> a < c
scala> a <= c
```



## 型パラメータの境界

Day2の練習問題でやったバイナリサーチのコードを思い出してみましょう。ソート済みのリストから値を検索します。

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

これはList[Int]型にしか対応していませんが、List[Double]やList[Float]などにも対応して多相性を持たせたくなりました。では単純にList[Int]を型パラメータを使ってList[A]に書き換えてみましょう。

```scala
def binarySearch[A](list: List[A], a: A): Boolean = list match {
  ・・・
```

REPLで読み込んでみます。

```scala
scala> :lo src/binarySearch.scala
Loading src/binarySearch.scala...
<console>:16: error: value < is not a member of type parameter A
           else if (a < m) binarySearch(list.take(p), a)
                                 ^
```

比較演算子が存在しないよというコンパイルエラーになりました。最もな指摘ですね。型パラメータAを導入し、引数`list`の型をList[A]型にしたため、`list`の要素であるA型のオブジェクトに比較演算子が定義されていない可能性があります。型パラメータAに指定できる型が、Orderedトレイトをミックスインした型に限定できればコンパイルできるはずです。

このように型パラメータに指定できる型を限定する方法が型パラメータの境界です。ある型のサブクラスに限定したい場合は、`<:`を使います。これを上限境界と呼びます。

```scala
def binarySearch[A <: Ordered[A]](list: List[A], a: A): Boolean = list match {
  ・・・
```

REPLで使ってみましょう。先ほどOrderedトレイトをミックスインしたShapeのリスト、List[Shape]型に対して2分探索できるようになりました。

```scala
scala> val xs: List[Shape] = List(Circle(0,0,5), Circle(0,0,8), Rectangle(0,0,2,3))
scala> binarySearch(xs, Circle(0,0,5))
```

しかし、List[Int]型の2分探索ができません！

```scala
scala> val xs: List[Int] = List(1,2,3,4,5)
xs: List[Int] = List(1, 2, 3, 4, 5)

scala> binarySearch(xs, 3)
<console>:11: error: inferred type arguments [Int] do not conform to method binarySearch's type parameter bounds [A <: Ordered[A]]
              binarySearch(xs, 3)
              ^
<console>:11: error: type mismatch;
 found   : List[Int]
 required: List[A]
              binarySearch(xs, 3)
                           ^
<console>:11: error: type mismatch;
 found   : Int(3)
 required: A
              binarySearch(xs, 3)
                               ^
```

Int型はOrderedのサブ型ではないことが原因です。Int型はPredefでRichInt型への暗黙の型変換が定義されています。そしてRichInt型はOrderedのサブ型になっています。つまり、Int型はOrderedのサブ型ではないですが、暗黙の型変換によりOrderedのサブ型になることができます。

継承だけでなく、暗黙の型変換まで含めて限定したい場合は、`<:`ではなく`<%`を使います。これを可視境界と呼びます。可視境界を使うことでList[Int]型も2分探索できるようになります。

```scala
def binarySearch[A <% Ordered[A]](list: List[A], a: A): Boolean = list match {
  ・・・
```



## 型パラメータの変位指定アノテーション

型パラメータを使ったすごく簡単な例を以前書きました。Capsuleというケースクラスです。

```scala
scala> case class Capsule[A](x: A)
```

このCapsuleにShape達を格納してみましょう。そして、それらのオブジェクトをList[Capsule[Shape]]型のリストに格納してみましょう。

```scala
scala> val rec = Capsule(Rectangle(0,0,2,3))
rec: Capsule[Rectangle] = Capsule(Rectangle(0.0,0.0,2.0,3.0))

scala> val cir = Capsule(Circle(0,0,5))
cir: Capsule[Circle] = Capsule(Circle(0.0,0.0,5.0))

scala> val xs: List[Capsule[Shape]] = List(rec, cir)
<console>:17: error: type mismatch;
 found   : Capsule[Rectangle]
 required: Capsule[Shape]
Note: Rectangle <: Shape, but class Capsule is invariant in type A.
You may wish to define A as +A instead. (SLS 4.5)
       val xs: List[Capsule[Shape]] = List(rec, cir)
                                           ^
<console>:17: error: type mismatch;
 found   : Capsule[Circle]
 required: Capsule[Shape]
Note: Circle <: Shape, but class Capsule is invariant in type A.
You may wish to define A as +A instead. (SLS 4.5)
       val xs: List[Capsule[Shape]] = List(rec, cir)
                                                ^
```

変数`rec`と`cir`にオブジェクトを束縛できたところはいいです。問題はList[Capsule[Shape]]型のリストの要素として、Capsule[Rectangle]型とCapsule[Circle]型のオブジェクトを格納できないところです。原因はどこにあるのでしょうか？

シンプルなところから考えてみましょう。Capsule[Shape]型の変数に、Capsule[Rectangle]型のオブジェクトを束縛してみます。

```scala
scala> val shape: Capsule[Shape] = rec
<console>:16: error: type mismatch;
 found   : Capsule[Rectangle]
 required: Capsule[Shape]
Note: Rectangle <: Shape, but class Capsule is invariant in type A.
You may wish to define A as +A instead. (SLS 4.5)
       val shape: Capsule[Shape] = rec
                                   ^
```

なんとエラーになってしまいました！RectangleはShapeのサブクラスであるにも関わらずです。エラーメッセージをよく見ると"type mismatch"と出力されています。RectangleがShapeのサブ型だからと言って、Capsule[Rectangle]がCapsule[Shape]のサブ型というわけではないのです。

このようにRectangleがShapeのサブ型なのにCapsule[Rectangle]型がCapsule[Shape]型のサブ型にならないことを非変（nonvariant）と呼びます。逆に、RectangleがShapeのサブ型である場合にCapsule[Rectangle]型がCapsule[Shape]型のサブ型になることを共変（covariant）と呼びます。

デフォルトでは非変ですが、変位アノテーションを使うことで共変にすることができます。共変にするには型パラメータの左側に"+"マークをつけます。このマークが変位アノテーションです。

```scala
scala> case class Capsule[+A](x: A)
scala> val rec: Capsule[Rectangle] = Capsule(Rectangle(0,0,2,3))
scala> val cir: Capsule[Circle] = Capsule(Circle(0,0,5))
scala> val shape: Capsule[Shape] = rec
scala> val xs: List[Capsule[Shape]] = List(rec, cir)
```

共変にしたことで、Capsule[Shape]型の変数にCapsule[Rectangle]型のオブジェクトを束縛することができました。

共変とは逆に、"-"マークをつけると反変（contravariance）になります。反変の場合はCapsule[Shape]型がCapsule[Rectangle]型のサブクラスになるような状態です。

いったん変位アノテーションを外して、メソッドを追加してみましょう。

```scala
scala> case class Capsule[A](x: A) {
     |   def replace(y: A) = Capsule(y)
     | }
scala> val a = Capsule(Rectangle(0,0,2,3))
scala> val b = a.replace(Rectangle(0,0,4,5))
```

この状態で変位アノテーションをつけてみます。

```scala
scala> case class Capsule[+A](x: A) {
     |   def replace(y: A) = Capsule(y)
     | }
<console>:16: error: covariant type A occurs in contravariant position in type A of value y
         def replace(y: A) = Capsule(y)
                     ^
```

コンパイルエラーです。エラーメッセージをよく読むと、"contravariant position"と出ています。このメッセージが出たら引数yには下限境界を指定しろ、という意味です。下限境界は先よどやった上限境界の逆、すまり、Aのスーパー型を指定できるような型パラメータです。下限境界は`>:`を使います。

```scala
scala> case class Capsule[+A](x: A) {
     |   def replace[B >: A](y: B) = Capsule(y)
     | }
defined class Capsule

scala> val rec: Capsule[Rectangle] = Capsule(Rectangle(0,0,2,3))
scala> val a: Capsule[Shape] = rec
scala> val b: Capsule[Shape] = a.replace(Circle(0,0,5))
```

さて、なぜ引数`y`に下限境界が必要なのか考えてみましょう。

Capsule[Rectangle]型のオブジェクトをCapsule[Shape]型の変数に束縛できました。これはRectangleがShapeのサブ型でありCapsuleが共変だからです。

次に、`a.replace(Circle(0,0,5))`とreplaceメソッドにCircle型のオブジェクトを指定しました。これは変数`a`の型がCapsule[Shape]型なので指定できてくれないと困ります。これを適切に処理するには、replaceメソッドに下限境界が指定されている必要があります。

仮に下限境界がなかったとしたら、Capsule[Rectangle]型のオブジェクト、つまり変数`a`に束縛されているオブジェクトのreplaceメソッドの引数の型はRectangle型になります。これではCircle型を`a.replace`に渡すことができません。

この問題に対応できるようにするため、`a.replace`はRectangle型のスーパー型を受け取れるようにしておく必要があります。そのため、A型を下限境界とする型パラメータBをreplaceメソッドの引数の型として定義します。

ややこしく感じますが、心配いりません。下限境界を指定し忘れてしまった場合は、先ほど見たようにコンパイラが教えてくれます。まずはコンパイラが教えてくれた内容に対応できるようになりましょう。



## ScalaのAPIドキュメントを読んでみる

ScalaのAPIドキュメントでListを調べてみましょう。[http://www.scala-lang.org/api/current/](http://www.scala-lang.org/api/current/)

コンパニオンオブジェクトとクラスに分かれていることが分かると思います。コンパニオンオブジェクトのapplyメソッドや、クラスのこれまでに使ってきたメソッドを見てみましょう。

またListの宣言部を見るとabstractなクラスであり、様々なトレイトを実装していることが分かります。Listクラス自体はabstractで実態はなんでしょうか。"Type Hierarchy"という項目を見ると、::というクラスとNilというシングルトンオブジェクトがサブクラスとなっており、それらが実態となります。

Intクラスも見てみましょう。"Type Hierarchy"という項目を見ると、RichIntなどへ暗黙の型変換されることが分かります。



## 今後の学習

お疲れさまです！Scala入門ハンズオンはこれで終了です！今後さらにScalaを勉強するにはどうしたらいいでしょうか？

Scalaの豊富なコレクションについては知っておいた方がいいです。性能特性も確認しましょう。

* [可変コレクションおよび不変コレクション](http://docs.scala-lang.org/ja/overviews/collections/overview.html)
* [性能特性](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)
* [なぜListではなくSeqを使うべきなのか](http://qiita.com/i524/items/2eb2ca12291fee86e87b)

暗黙の引数や抽出子など、入門ハンズオンでは説明できなかったScalaの機能がまだあります。Scalaという言語自体をもっと知りたい場合は以下の書籍を読みましょう。通称コップ本と呼ばれておりScalaのバイブル的な書籍です。Scala公式ドキュメントも載せておきます。

* [Scalaスケーラブルプログラミング第2版](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845/)
* [ガイドと概要 - Scala Documentation](http://docs.scala-lang.org/ja/overviews/index.html)

Scalaで何かつくるときはScala逆引きレシピという書籍があるとリファレンスとして役立つでしょう。ひしだまさんのサイトにもすごくお世話になります。

* [Scala逆引きレシピ](http://www.amazon.co.jp/Scala%E9%80%86%E5%BC%95%E3%81%8D%E3%83%AC%E3%82%B7%E3%83%94-PROGRAMMER%E2%80%99S-RECiPE-%E7%AB%B9%E6%B7%BB-%E7%9B%B4%E6%A8%B9/dp/4798125415/)
* [Scalaメモ(Hishidama's Scala Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/)

Webアプリケーションをつくりたい場合はPlay2というフレームワークがよく使われます。ハンズオン用のドキュメントがWeb上にあるのでやってみると良さそうです。

* [Play2 + Slickハンズオン](https://github.com/bizreach/play2-hands-on)

関数型プログラミングについてもっと学びたい、という場合は以下の書籍を読むといいと思います。Scalaを使って関数型プログラミングについて説明されています。

* [Scala関数型デザイン&プログラミング](http://www.amazon.co.jp/Scala%E9%96%A2%E6%95%B0%E5%9E%8B%E3%83%87%E3%82%B6%E3%82%A4%E3%83%B3-%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0-%E2%80%95Scalaz%E3%82%B3%E3%83%B3%E3%83%88%E3%83%AA%E3%83%93%E3%83%A5%E3%83%BC%E3%82%BF%E3%83%BC%E3%81%AB%E3%82%88%E3%82%8B%E9%96%A2%E6%95%B0%E5%9E%8B%E5%BE%B9%E5%BA%95%E3%82%AC%E3%82%A4%E3%83%89-impress-gear/dp/4844337769/)

Twitter社によるベストプラクティスが書かれたEffective Scalaもおすすめです。

* [Effective Scala](http://twitter.github.io/effectivescala/index-ja.html)

日本のScalaコミュニティのサイトはこちらです。

* [日本Scalaユーザーズグループ](http://jp.scala-users.org/)



## 練習問題



## 今日出てきたキーワード

* シングルトンオブジェクト、コンパニオンオブジェクト



## 要注意ポイント
* `List(1,2,3,4,5)`というのは、Listシングルトンオブジェクトのapplyメソッドを呼び出している

