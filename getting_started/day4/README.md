# Day4 （書きかけ）

## 暗黙の型変換

MapをつくるときMapのapplyメソッドを使いました。`Map(("key1", 1), ("key2", 2))`という風に使いますが、`Map("key1" -> 1, "key2" -> 2)`とも書けました。`("key1", 1)`というタプルの代わりに`"key1" -> 1`を指定しています。"key1"というStringの->メソッドを呼び出し、その結果がタプルになっていれば辻褄が合います。しかし、ScalaのStringはJavaのStringを使っていて、JavaのStringには->メソッドは存在しません。

暗黙の型変換（implicit conversion）という仕組みを利用して、Stringに->メソッドがあるかのように振る舞わせることができます。

まず、暗黙の型変換とはなんでしょうか。Day2で出てきたRectangle型を思い出しましょう。

```scala
scala> case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double)
scala> val rec = Rectangle(0, 0, 2, 3)
```

Rectangleケースクラスは、Double型の値を4つ受け取る定義ですが、インスタンス化するときにInt型の値を指定しています。Int型の値を指定してるのにコンパイルが通るのは、暗黙の型変換によりInt型がDouble型に変換されてるためです。

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

暗黙の型変換を使うと、元々存在している型にメソッドを追加することができます。

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

Int型に新たにrepeatメソッドをつけ足すことができました。クラスに`implicit`をつけるやり方はScala2.10以降にできるようになりました。それ以前はPimp My Libraryと呼ばれるパターンが使われていました。参考サイト -> [Scala 2.10.0 M3の新機能を試してみる(2) - SIP-13 - Implicit classes](http://kmizu.hatenablog.com/entry/20120506/1336302407)

`implicit class`をつけた方法は毎回Repeaterクラスをnewしてしまいます。AnyValを継承すると効率を上げることができます。こちらを参考してください。[Scalaメソッド定義メモ(Hishidama's Scala def Memo) # 暗黙クラス（implicit class）](http://www.ne.jp/asahi/hishidama/home/tech/scala/def.html#h_implicit_class)

Mapで使った->メソッドも暗黙の型変換によって実現されています。`"key1" -> 1`と書くと暗黙の型変換によりStringである"key1"に->メソッドがあるかのように振る舞い、`("key1", 1)`というタプルを返します。

この暗黙の型変換は、Predefというシングルトンオブジェクトに定義されています。ScalaではデフォルトでPredefシングルトンオブジェクトのメソッドをimportしています。シングルトンオブジェクトが何かは次回やります。ここではいくつかの暗黙の型変換メソッドがデフォルトで用意されている、ということを分かってください。Prefefには便利な暗黙の型変換が用意されています。 [Scala Predefオブジェクトメモ(Hishidama's Scala Predef Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/predef.html)

このようにScalaでは暗黙の型変換を使うことで、既存のクラスを拡張できます。

暗黙の型変換について、コップ本に注意点が書いてあります。

> 暗黙の型変換の使い方を誤ると、クライアントコードを読みにくく理解しにくいものにしてしまう危険がある。暗黙の型変換はソースコードに明示的に書き出されるのではなく、コンパイラが暗黙のうちに適用するので、クライアントプログラマーからは、どのような暗黙の型変換が適用されているのかはっきりとはわからない。
> （中略）
> 簡潔さは読みやすさの大きな構成要素だが、簡潔すぎてわからないということもある。効果的な簡潔さをもたせて、わかりやすいクライアントコードを書けるライブラリーを設計すれば、クライアントプログラマーたちが生産的に仕事を進めるのを後押しできる。

引用元：[Scalaスケーラブルプログラミング第2版](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845/)



## Scalaの階層構造

Scalaの全てのクラスはAnyのサブクラスになるという話を前にしました。ただすぐ親のクラスAnyなわけではないです。

Anyのサブクラスには、AnyValとAnyRefがあります。

AnyValのサブクラスとしては、まず以下のような数値を表す売らすがあります。これらのクラスに親子関係はないですが、暗黙の型変換があります。Byte -> Short というように上から下への暗黙の型変換がPredefに定義されています。

* Byte
* Short
* Int
* Long
* Float
* Double

他には、1文字を表すChar、真理値のBoolean、副作用があったことを示すのによく用いられるUnitがあります。Char -> Int という暗黙の型変換がPredefに定義されています。

* Char
* Boolean
* Unit

Scalaが最初から用意しているAnyValのサブクラスはこれで全てです。

自分でAnyValのサブクラスをつくることもできます。こちらのサイトが参考になります -> [Scala Anyクラスメモ(Hishidama's Scala Any Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/any.html#h_AnyVal)

その他のScalaが用意しているクラスはAnyRefのサブクラスです。自分でクラスを定義したときにextendsを書かなかった場合は、自動的にAnyRefのサブクラスとなります。

AnyRefのサブクラス、全てのサブクラスになるNullというのがあります。Null型は他の言語にあるnullを表す型です。AnyRefのサブクラスである型には、nullを代入できますが、Scalaではnullは使いません。Javaとの互換性のために存在してます。

すべてのクラスのサブクラスになるNothingというのもあります。これはどんな型にも代入できる型なのですが、使いどころとしては例外を発生させる場合です。

例えば、Day2でリストの先頭要素を取り出すheadメソッドをつくってみました。このメソッドはNilを渡すとエラーになります。`case x`のときはInt型が返って、`case Nil`のときは例外が発生します。
```scala
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil       => throw new IllegalArgumentException
     |     case x :: tail => x
     |   }
     | }
```

headメソッドの返り値はInt型です。`throw`も式なのですが、`throw`の評価結果がInt型であるわけではないです。Int型を返さない場合があるにも関わらずメソッドの返り値をInt型と定義できるのは、`throw`式がNothing型を返すからです。Nothing型は全ての型に代入できるので、headメソッドの返り値の型をInt型にしてもコンパイルできることになります。



## パッケージ

今までつくってきたクラスはすべてグローバル空間に定義していました。プログラムが大きくなると管理するのが大変なので、Scalaではパッケージを使って、名前空間を分けます。パッケージは`package`で指定します。ファイルの先頭に指定するのが一般的です。

```scala
// Hoge.scala
package com.example.hoge

class Hoge(s: String) {
  def hello = "Hello, " + s + "!"
```

異なるパッケージのクラスはそのままでは使えません。使う場合はパッケージ+クラス名で書くか、インポートします。インポートは`import`で指定します。インポート時に`_`を使うことでパッケージ内のすべてをインポートすることもできます。

```scala
scala> :paste -raw src/Hoge.scala
scala> val a = new Hoge("World")
<console>:7: error: not found: type Hoge
       val a = new Hoge("World")
                   ^

scala> val a = new com.example.hoge.Hoge("World")
scala> import com.example.hoge.Hoge
scala> val a = new Hoge("World")
scala> a.hello
scala> :q // importをなかったことにしたいのでREPLを一度終了する

scala> :paste -raw Hoge.scala
scala> new Hoge
<console>:8: error: not found: type Hoge
              new Hoge
                  ^

scala> import com.example.hoge._
scala> val a = new Hoge("World")
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
package com.example.hoge

class Hoge(s: String) {
  private def hello = "Hello, " + s + "!"
}
```

`private`をつけたメソッドは呼べなくなりました。

```scala
scala> :paste -raw src/Hoge.scala
scala> import com.example.hoge._
scala> val a = new Hoge("World")
scala> a.hello
<console>:12: error: method hello in class Hoge cannot be accessed in com.example.hoge.Hoge
              a.hello
                ^
```

コンストラクタに`private`をつけてみます。

```
package com.example.hoge

class Hoge private (s: String) {
  def hello = "Hello, " + s + "!"
}
```

`private`をつけたコンストラクタにはアクセスできなくなりました。

```scala
scala> :paste -raw Hoge.scala
scala> import com.example.hoge._
scala> val a = new Hoge("World")
<console>:10: error: constructor Hoge in class Hoge cannot be accessed in object $iw
       val a = new Hoge("World")
               ^
```

もっと細かくアクセス制御したい場合は限定子というものを使います。限定子というのは`private[com.example.hoge]`というようにアクセス修飾子の後ろに`[]`で公開するパッケージやクラス名を指定します。こちらを参考にしてください -> [Scalaクラスメモ(Hishidama's Scala class Memo)](http://www.ne.jp/asahi/hishidama/home/tech/scala/class.html#h_access_modifiers)



## シングルトンオブジェクトとコンパニオンオブジェクト

クラス以外にも型をつくる方法はいろいろありますが、そのうちの1つがシングルトンオブジェクトです。これはその名の通りシングルトンオブジェクトをつくります。インスタンス化する、というような考え方はないです。定義したらそれが型でありオブジェクトになります。

シングルトンオブジェクトは、`object`を使って定義します。

```scala
object OriginPoint {
  val x = 0.0
  val y = 0.0
}
```

シングルトンオブジェクトは何か1つしかないデータを表すのに使うことよりも、他の使い方で使われることが多いです。

先ほど、Hogeクラスのコンストラクタを`private`にしました。そうするとことで`new Hoge`ができなくなりました。つまりどこからもインスタンス化できません。しかし、コンパニオンオブジェクトを使うことでインスタンス化できます。

コンパニオンオブジェクトとは、同名のクラス定義と同じファイル内で定義されたシングルトンオブジェクトのことです。コンパニオンオブジェクトは、同名のクラス（これをコンパニオンクラスと言う）の`private`なメソッドやコンストラクタを呼び出すことができます。`Hoge`クラスのコンパニオンオブジェクトを定義してみましょう。

```scala
package com.example.hoge

class Hoge private (s: String) {
  def hello = "Hello, " + s + "!"
}
object Hoge {
  def apply(s: String) = new Hoge(s)
}
```

使ってみましょう。applyメソッドはメソッド名を省略できましたね。

```scala
scala> :paste -raw src/Hoge.scala
scala> import com.example.hoge._
scala> val a = new Hoge("World")
<console>:14: error: constructor Hoge in class Hoge cannot be accessed in object $iw
              new Hoge("World")
              ^

scala> val a = Hoge("World")
```

コンパニオンオブジェクトは結構使われています。例えば今まで何回も使ってきた`List(1,2,3,4,5)`というのもListコンパニオンオブジェクトのapplyメソッドを呼び出しています。Listはクラスではなく抽象クラスです。Listコンパニオンオブジェクトのapplyメソッドでは、Listのサブクラスをインスタンス化して返却しています。コンパニオンオブジェクトを使うことで、使う側が実態のクラスを意識しなくて済むようになっています。



## 可変長引数

`List(1,2,3,4,5)`というのはListコンパニオンオブジェクトのapplyメソッドであることが分かりました。このメソッドはさらに可変長引数を受け取るようになっています。そのため、`List(1)`、`List(1, 2, 3, 4, 5)`というように引数の数を可変にできます。

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



## トレイトの初歩

これまでに型をつくる方法として、クラス、抽象クラス、ケースクラス、シングルトンオブジェクトを紹介しました。他の方法として、トレイトがあります。トレイトは抽象クラスと同様にインスタンス化できません。抽象クラスと異なるのは、クラスを定義するときに1つの抽象クラスしか継承できないのに対し、複数のトレイトを継承できます。トレイトを継承するときは「継承」ではなく「ミックスイン」と言います。ミックスインするときは`extends`もしくは`with`を使います。

自分でトレイトをつくる話


ScalaにはOrderedトレイトが用意されています。Orderedトレイトをミックスインして、compareメソッドをオーバーライドするだけで、オブジェクト比較するメソッドを使うことができます。Day2で定義したShapeを使ってみましょう。ShapeにOrderedトレイトをミックスインします。Orderedトレイトは1つ型パラメータを1つとる型コンストラクタです。オーバーライドするcompareメソッドの引数の型が型パラメータによって決まります。

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

Day2でやったクイックソートのコードを思い出してみましょう。

```scala
def quickSort(xs: List[Int]): List[Int] = xs match {
  case Nil       => Nil
  case x :: Nil  => List(x)
  case x :: tail => {
    val smallerOrEqual = for (y <- tail; if y <= x) yield y
    val larger         = for (y <- tail; if y > x) yield y
    quickSort(smallerOrEqual) ++ List(x) ++ quickSort(larger)
  }
}
```

これはList[Int]型にしか対応していませんが、List[Double]やList[Float]などにも対応して多相性を持たせたくなりました。では単純にList[Int]を型パラメータを使ってList[A]に書き換えてみましょう。

```scala
def quickSort[A](xs: List[A]): List[A] = xs match {
  ・・・
```

REPLで読み込んでみます。

```scala
scala> :load quicksort.scala
Loading quicksort.scala...
<console>:19: error: value <= is not a member of type parameter A
           val smallerOrEqual = for (y <- tail; if y <= x) yield y
                                                     ^
<console>:20: error: value > is not a member of type parameter A
           val larger         = for (y <- tail; if y > x) yield y
                                                     ^
```

比較演算子が存在しないよというコンパイルエラーになりました。最もな指摘ですね。型パラメータAを導入し、引数`xs`の型をList[A]型を受け取れるようにしたため、`xs`の要素であるオブジェクトに比較演算子が定義されていない型の可能性もあります。`xs`の要素がOrderedトレイトをミックスインしていれば、型パラメータAに指定できる型が、Orderedをミックスインした型に限定できればコンパイルできるはずです。

このように型パラメータに指定できる型を限定する方法が型パラメータの境界です。ある型のサブクラスに限定したい場合は、`<:`を使います。これを上限境界と呼びます。

```scala
def quickSort[A <: Ordered[A]](xs: List[A]): List[A] = xs match {
  ・・・
```

REPLで使ってみましょう。先ほどOrderedトレイトをミックスインしたShapeのリスト、List[Shape]型を並べ替えることができるようになりました。

```scala
scala> val xs: List[Shape] = List(Circle(0,0,5), Circle(0,0,8), Rectangle(0,0,2,3))
scala> quickSort(xs)
```

しかし、List[Int]型が並べ替えることができません！

```scala
scala> val xs: List[Int] = List(2,8,3,5,1,9)
scala> quickSort(xs)
<console>:18: error: inferred type arguments [Int] do not conform to method quickSort's type parameter bounds [A <: Ordered[A]]
              quickSort(xs)
              ^
<console>:18: error: type mismatch;
 found   : List[Int]
 required: List[A]
              quickSort(xs)
                        ^
```

Int型はOrderedのサブ型ではないことが原因です。Int型はPredefでRichInt型への暗黙の型変換が定義されています。そしてRichInt型はOrderedをミックスインしています。つまり、Int型はOrderedのサブ型ではないですが、暗黙の型変換によりOrderedのサブ型になることができます。

直接の継承だけでなく、暗黙の型変換まで含めて限定したい場合は、`<:`ではなく`<%`を使います。これを可視境界と呼びます。可視境界を使うことでList[Int]型も並べ替えることができます。

```scala
def quickSort[A <% Ordered[A]](xs: List[A]): List[A] = xs match {
  ・・・
```



## 型パラメータの変異指定アノテーション

型パラメータを使ったすごく簡単例を以前書きました。Capsuleというケースクラスです。今後の説明のためreplaceメソッドを追加しておきます。

```scala
scala> case class Capsule[A](x: A)
```

このCapsuleにShape達を格納してみましょう。そして、それらのオブジェクトをList[Capsule[Shape]型のリストに格納してみましょう。

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

このようにCapsule[Rectangle]型がCapsule[Shape]型のサブ型にならないことを非変（nonvariant）と呼びます。逆に、Capsule[Rectangle]型がCapsule[Shape]型のサブ型になることを共変（covariant）と呼びます。

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

```
scala> case class Capsule[+A](x: A) {
     |   def replace[B >: A](y: B) = Capsule(y)
     | }
defined class Capsule

scala> val rec: Capsule[Rectangle] = Capsule(Rectangle(0,0,2,3))
scala> val a: Capsule[Shape] = rec
scala> val b: Capsule[Shape] = rec.replace(Circle(0,0,5))
```

さて、なぜ引数`y`に下限境界が必要なのか考えてみましょう。Capsule[Rectangle]型のオブジェクトをCapsule[Shape]型の変数に束縛できました。これはRectangleがShapeのサブクラスでありCapsuleが共変だからです。次に、`a.replace(Circle(0,0,5))`とreplaceメソッドにCircle型のオブジェクトを指定しました。これは変数`a`の型がCapsule[Shape]型なので指定できて当然です。これを適切に処理するには、replaceメソッドに下限境界が指定されている必要があります。仮に下限境界がなかったとしたら、Capsule[Rectangle]型のreplaceメソッドの引数の型はRectangle型になります。そしてCapsule[Shape]型の変数に束縛され、Shape型をreplaceメソッドに指定されたときに実態となるオブジェクト（つまりCapsule[Rectangle]型）にはShape型を受け取るメソッドが存在しないためです。



## ScalaのAPIドキュメントを読んでみる

ScalaのAPIドキュメントでListを調べてみましょう。



## 今後の学習



## 今日出てきたキーワード
* シングルトンオブジェクト、コンパニオンオブジェクト



## 要注意ポイント
* `List(1,2,3,4,5)`というのは、Listシングルトンオブジェクトのapplyメソッドを呼び出している

