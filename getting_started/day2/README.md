# Day2

## パターンマッチの初歩
Day1でif式をやりましたが、パターンマッチを使うと条件分岐をよりスマートに書けることがあります。パターンマッチにはmatch式を使います。

```scala
scala> val x = 7
scala> x match {
     |   case 1 => "Great!"
     |   case 2 => "Good!"
     |   case _ => "Sorry"
     | }
res0: String = Sorry
```

`case 1 =>` というのが、xの値が1だった場合 `=>` の右側の式を評価（実行）しますよ、ということです。`case 2 =>` というのは、xの値が2だった場合になります。`case _ =>` というのは、それ以外の場合 `=>` の右側の式を評価しますよ、ということになります。matchも式なので値を返します。上の例だと、xの値は1ではないので"Sorry"という文字列が返ってますね。

パターンマッチはリストにも使えます。条件分岐しつつリストの先頭要素と残りのリストを変数に束縛することもできます。よく分からないかもしれませんが、コードを見れば分かると思います。

リストの先頭要素を取得するheadメソッドをパターンマッチを使ってつくってみましょう。

```scala
// head.scala
def head(xs: List[Int]): Int = {
  xs match {
    case Nil       => throw new IllegalArgumentException
    case x :: tail => x
  }
}
```

`Nil`は空のリストでしたね。`case Nil` は`xs`が空のリストだったら、という意味です。このheadメソッドではエラーを発生させています（例外はいずれ・・・）。

`case x :: tail` は`xs`が空じゃない場合にマッチしますが、マッチさせると同時に変数`x`に先頭要素を、変数`tail`に先頭を取り除いた残りのリストを代入します。`::`は前回cons演算子のところで値とリストをつなげるときに使っていました。パターンマッチのときでも同じような意味になります。`case x :: tail`というのは、パターンマッチの対象（`xs`）が`x`と`tail`をconsでつなげたものだったら、という意味になるわけです。`tail`は空のリストでもそうでなくても構いません、どちらにしろconsでつなげることができますからね。

`case x :: tail =>` というのは以下のように書いた場合と同じことをしてくれてると考えれば分かるのではないでしょうか。

```scala
case list => {
  val x = list.head
  val tail = list.tail
}
```

REPLに読み込んで使ってみます。

```scala
scala> :load head.scala

scala> head(List(1,2,3))
res1: Int = 1

scala> head(List(5))
res2: Int = 5

scala> head(List())
java.lang.IllegalArgumentException
  at .head(<console>:9)
  ... 32 elided
```

ちゃんと動いてます。

`tail`は`=>`の右側で使っていないので、`_`で置き換えてしまいましょう。置き換えなくてもいいですが、`_`で置き換えることで先頭要素以外は使ってないんだな、と分かりやすくなります。

```scala
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil    => throw new IllegalArgumentException
     |     case x :: _ => x
     |   }
     | }
```

パターンマッチを使って条件分岐しつつ変数に値を代入することができました。パターンマッチに限りませんが変数に値を代入することを「束縛する」と言うこともできます。

headメソッドは結局1つのmatch式で出来上がってます。1つの式からなるメソッドの場合、外側の`{}`はいらないので消してしまいましょう。

```scala
scala> def head(xs: List[Int]): Int = xs match {
     |   case Nil => throw new IllegalArgumentException
     |   case x :: _ => x
     | }
```

パターンマッチはタプルにも使えます。条件分岐しつつタプルの要素を変数に束縛することもできます。

例えば、3要素のタプルがあり、1つめの要素が動物の種類、2つ目の要素が名前、3つ目の要素が年齢だとします。

```scala
scala> val a = ("dog", "papico", 3)
```

そして、種別が犬（"doc"）の場合とクモ（"spider"）の場合とそれ以外で処理を分けたいとき、こう書けます。

```scala
def f(x: (String, String, Int)): String = x match {
  case ("dog",    name, age)  => "I like dog! " + name + " is " + age + " years old!"
  case ("spider", name, _)    => "I don't like spider. Sorry " + name
  case _                      => "I have no interest"
}
```

```scala
scala> val a = ("dog", "papico", 3)
scala> f(a)
res0: String = I like dog! papico is 3 years old!

scala> f(("spider", "papico", 3))
res1: String = I don't like spider. Sorry papico

scala> f(("cat", "papico", 3))
res2: String = I have no interest
```

これをif式で書くとすると、条件式を書くところで`x._1 == "dog"`って書いて、本体のところでも`x._1`って書くことになって、ちょっとメンドイしスマートじゃない感じがします。タプルでパターンマッチを使って分岐すれば、場合分けしつつタプルの中身の値を変数に代入できることが分かりました。

タプルとリストを組み合わせることだってできます。さっきつくったfメソッドを改良してみましょう。年齢の後ろに好きな食べ物をリストで持たせます。

```scala
def f(x: (String, String, Int, List[String])): String = x match {
  case ("dog",    name, age, Nil)       => "I like dog! " + name + " is " + age + " years old!"
  case ("dog",    name, age, food :: _) => "I like dog! " + name + " is " + age + " years old! " + name + " likes " + food + "!"
  case ("spider", name, _, _)           => "I don't like spider. Sorry " + name
  case _                                => "I have no interest"
}
```

```scala
scala> f(("dog", "papico", 3, List("meet", "fish")))
res6: String = I like dog! papico is 3 years old!papico like meet!
```

これの何が便利かって入れ子になってるリストにもパターンマッチできるところです。入れ子になってるケースクラスももちろん対応できます。こういった分岐をif式だけで書くと複雑になってしまうでしょう。



## String interpolation
さっきのfメソッドですが、文字列結合の部分がちょっと面倒でしたよね。これはString interpolationと呼ばれる仕組みできれいに書けます。いわゆる変数展開みたいに使えます。文字列リテラルの先頭に"s"という書きます。リテラル内で変数を使うときは `$変数名` です。

```scala
def f(x: (String, String, Int, List[String])): String = x match {
  case ("dog",    name, age, Nil)       => s"I like dog! $name is $age years old!"
  case ("dog",    name, age, food :: _) => s"I like dog! $name is $age years old! $name likes $food !"
  case ("spider", name, _, _)           => s"I don't like spider. Sorry $name"
  case _                                => s"I have no interest"
}
```

変数展開よりも色々できるのですが、ここではやりません。こちらが参考になります => [文字列の補間](http://docs.scala-lang.org/ja/overviews/core/string-interpolation.html)



## 再帰
変数宣言のときに`val`を使っていました。一度値を代入したらもう二度と代入することはできませんでした。

Scalaでは`val`の他に`var`で変数宣言をすることもできます。`var`で宣言した変数には何度でも値を代入することができてしまいます。

Intのリストに格納されている数値の中から一番大きな値を取得するメソッドを考えてみましょう。

```scala
def maximum(xs: List[Int]): Int = {
  if (xs.size == 0) throw new IllegalArgumentException
  var max = 0
  for(x <- xs) {
    if (max < x) max = x
  }
  max
}
```

REPLで読み込んで実行してみます。

```scala
scala> :load maximum.scala
scala> val xs = List(3, 6, 1, 7, 2, 5)
scala> maximum(xs)
```

`var`を使ってmaximumメソッドを実装できました。しかし、副作用のない処理を書くには再代入可能な変数は邪魔になることが多く、また予期しないところ代入してしまってバグとなってしまったりします。理由がない限りは`var`ではなく`val`を使うべきでしょう。

では、maximumメソッドのようなループ処理を`var`を使わずに定義するにはどうしたらいいでしょうか？再帰を使いましょう。

再帰とはメソッド内で自分自身を呼び出すことです。下のmaximumメソッドでは、メソッド本体で自分自身であるmaximumメソッドを呼び出しています。

```scala
def maximum(xs: List[Int]): Int = xs match {
  case Nil       => throw new IllegalArgumentException
  case x :: Nil  => x
  case x :: tail => {
    val y = maximum(tail)
    if (x > y) x else y
  }
}
```

再帰を使うことで`var`をなくしつつループ処理を実現できました。慣れるまでは1ステップずつじっくりと変数の値の変化をメモしながらトレースするといいと思います。

再帰を書くにはコツがあります。

> 再帰を使う際の定跡は、まず基底部を見極め、次に解くべき問題をより小さな部分問題へと分割する方法を考えることです。基底部と部分問題さえ正しく選んだなら、全体として何が起こるかの詳細を考える必要はありません。部分問題の解が正しいという保証をもとに、より大きな最終問題の解を構築すればよいだけです。

[引用元 : すごいHaskll楽しく学ぼう！]

リストの中に特定の要素が含まれているかを調べるメソッドを再帰で書いてみます。基底部を見極め、そして部分問題へと分割します。基底部は、Nilの場合です。部分問題への分割はリストのパターンマッチを使いましょう。

```scala
def contains(a: Int, xs: List[Int]): Boolean = xs match {
  case Nil       => false
  case x :: tail =>
    if (x == a) true
    else        contains(a, tail)
}
```

実際に使ってみましょう。イメージが掴みづらい場合は簡単な例から1ステップずつイメージしていくと分かりやすいです。

```scala
scala> contains(1, List())
scala> contains(1, List(1))
scala> contains(1, List(2,3))
scala> contains(1, List(2,3,1))
```

階乗の計算も再帰で書いてみましょう。BigIntは大きな数値でも扱うことができる型です。

```scala
def fact(n: Int): BigInt = n match {
  case 0 => 1
  case _ => n * fact(n - 1)
}
```

クイックソートを書いてみましょう。リストの要素のどれか1つをピボットとします。ピボットよりも小さい値のリストと大きい値のリストに分けてそれぞれソートします。それぞれソートした結果とピボットを結合すればソート済みのリストが手に入ります。簡単にするためにピボットは先頭要素で固定としましょう。

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

どうでしょうか。やりたいことをそのまま宣言的に書けたのではないでしょうか。部分問題への分割とリストのパターンマッチが相性抜群ですね。ループではどうやって計算するかという手続きを書くことになりますが、再帰とパターンマッチを使うとやりたいことを宣言的に書けることが多いです。



## 末尾再帰

再帰を使うとループ処理と比べて宣言的に書くことができ、かつ、再代入可能な変数がないため、間違えにくく読みやすいコードになることが多いです。しかし、ループと比べてメソッド呼び出しの回数が増えます。これはパフォーマンスの劣化につながりますし、大量のメソッド呼び出しによりエラー（スタックオーバーフロー）が発生する可能性もあります。

実際、factに大きな値を指定するとスタックオーバーフローが起きます。指定する数値は環境によって異なるので小さな値から徐々に大きくしてみてください。

```scala
scala> fact(10000)
java.lang.StackOverflowError
```

再帰処理を「末尾再帰」で書くことにより、この問題を回避できます。末尾再帰とは、関数の最後で自分自身を呼び出す再帰のことです。末尾再帰はコンパイル時にコンパイラによってループ処理に置き換えられるため、問題を回避できるのです。

今まで出てきた例の中では、containsメソッドが末尾再帰です。それ以外は末尾再帰ではないです。factメソッドも末尾再帰に見えますが、最後に評価されるのは` * `であるため末尾再帰ではありません。

単純なループで書けるものは変数を追加することでただの再帰を末尾再帰に置き換えることができます。factメソッドを末尾再帰の形に書き換えてみましょう。

```scala
def fact2(n: Int, acc: BigInt): BigInt = n match {
  case 0 => acc
  case _ => fact2(n - 1, acc * n)
}
```

`acc`という変数が増えました。これはアキュムレータ（蓄積変数）と呼ばれます。元々のfactメソッドの場合は`n * fact(n - 1)`というように再帰呼び出しの結果を掛け合わせていました。fact2メソッドの場合は`fact2(n - 1, acc * n)`というようにアキュムレータに掛け合わせてから再帰呼び出しをしています。これによって関数の最後で再帰呼び出しをすることになりました。つまり末尾再帰になりました。

fact2メソッドは末尾再帰なのでコンパイル時にループ処理に置き換えられるためスタックオーバーフローが発生しません。また、ループよりもパフォーマンスが悪かったりすることもありません。

ただ、引数が増えたことにより、メソッドを使う側がちょっと使いづらくなってしまいましたね。fact2メソッドを使う人は適切なアキュムレータの初期値を指定しないといけません。fact2メソッドの場合は1です。

```scala
scala> fact(10)
scala> fact2(10, 1)
```

アキュムレータの初期値はメソッドの性質によってことなります。掛け合わせる場合は1ですが、足し合わせる場合は0が適切です。このような判断をメソッドを使う側が考えるのは使い勝手が悪いですし、間違えてしまうかもしれません。なので以下のようにfactからfact2を呼び出すようにします。

（※ 以下のコードをREPLで:loadを使って読み込んでもうまく動きません。:loadの読み込みは1行ずつ評価するためfactメソッドが定義されたタイミングではfact2メソッドは存在していない、と判断されてしまいます。:loadの代わりに、:pasteで読み込むとこの問題を解決できます）。

```scala
def fact(n: Int): BigInt = fact2(n, 1)

def fact2(n: Int, acc: BigInt): BigInt = n match {
  case 0 => acc
  case _ => fact2(n - 1, acc * n)
}
```

これでメソッドを使う側は、factメソッドを使っている限りアキュムレータを意識する必要はありません。さらにfactメソッドの内部では末尾再帰のfact2メソッドを呼び出しているのでスタックオーバーフローが発生したり、ループに比べてパフォーマンスが劣るということもありません。

まだ不十分です。fact2メソッドを直接呼び出してしまうかもしれませんし、他の末尾再帰なメソッドを定義するたびに余分なメソッドが増えてしまいます。メソッドを使う側にはfact2メソッドの存在が分からない方がいいでしょう。これを実現する方法は3つあります。

1つ目は、`private`修飾子を使う方法です。JavaやRubyでもおなじみの方法です。`private`をつけたメソッドは外部から呼び出せなくなります

```scala
def fact(n: Int): BigInt = fact2(n, 1)

private def fact2(n: Int, acc: BigInt): BigInt = n match {
  case 0 => acc
  case _ => fact2(n - 1, acc * n)
}
```

2つ目は、メソッド内にメソッドを定義する方法です。`def`の中で`def`を使えるのです。メソッド内で定義されたメソッドは`private`扱いになり外部から呼び出すことはできません。

```scala
def fact(n: Int): BigInt = {
  def loop(n: Int, acc: BigInt): BigInt = n match {
    case 0 => acc
    case _ => loop(n - 1, acc * n)
  }
  loop(n, 1)
}

```

3つ目は、引数のデフォルト値を使う方法です。Scalaでは引数が未指定の場合のデフォルト値を設定できます。ただ、この方法では間違ったアキュムレータの初期値を与えることができる状態なので好ましくないでしょう。

```scala
def fact(n: Int, acc: BigInt = 1): BigInt = n match {
  case 0 => acc
  case _ => fact(n - 1, acc * n)
}
```

maximumメソッドも末尾再帰に書き直してみましょう。

```
def maximum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil => throw new IllegalArgumentException
    case x :: Nil => acc
    case x :: tail => loop(tail, if (x > acc) x else acc)
  }
  loop(xs, 0)
}
```



## クラス

以前、2次元ベクトルを表すのにタプルを使いました。長方形を表すにはどうしたらいいでしょうか？Intを4つ持つタプルを使うのもいいですが、自分で長方形を表す型を定義してしまう方がより良いでしょう。

自分で型をつくってみましょう。型を定義する方法はいくつかありますが、クラスを使う方法を見ていきます。

まず、クラスを定義するときは`class`を使います。インスタンス化するときは`new`を使います。Rectangleクラスをインスタンス化してつくったオブジェクトの型はRectangle型になります。

```scala
scala> class Rectangle
scala> val x = new Rectangle
```

Rectangleクラスをいろいろいじっていくので、Shape.scalaに書いて使うときはREPLにロードして使うことにします。

長方形を左下の座標と右上の座標で表すことにしましょう。コンストラクタで指定できるようにします。コンストラクタの引数は、クラス名の右側に書きます。

```scala
// Rectangle.scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double)
```

```scala
scala> :load Rectangle.scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle@7cef4e59
```

REPLの出力が何やら分かりづらいですね。REPLの出力ではオブジェクトのtoStringメソッドが呼ばれます。なので、RectangleクラスにもtoStringメソッドを追加しましょう。

```scala
// Rectangle.scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
  def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

REPLに読み込んでみましょう。

```
scala> :load Rectangle.scala
Loading Rectangle.scala...
<console>:8: error: overriding method toString in class Object of type ()String;
 method toString needs `override' modifier
         def toString: String = s"Rectangle($x1, $y1, $x2, $y2)"
             ^
```

コンパイルエラーになってしまいました。"method toString needs override modifier" と表示されています。ScalaではすべてのクラスはAnyクラスを継承しています。そしてAnyクラスでtoStringメソッドが定義されているため、RectangleクラスでtoStringメソッドを定義するとオーバーライドすることになります。Scalaではオーバーライドするときは`override`を`def`に左側に書く必要があります。

```scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

これで無事読み込むことができます。REPLの出力も分かりやすくなりました。

```scala
scala> :load Rectangle.scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle(0.0, 0.0, 2.0, 3.0)
```

メソッドだけでなくメンバー変数を定義することもできます。

```scala
class Rectangle(ax: Double, ay: Double, bx: Double, by: Double) {
  val x1 = ax
  val y1 = ay
  val x2 = bx
  val y2 = by
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

上のように単純にコンストラクタのパラメータを公開したいだけの場合は、コンストラクタのパラメータに`val`をつけることで、外部からパラメータにアクセスできるようになります。

`val`じゃなくて`var`も使えますが、理由がない限り`val`にしましょう。`var`にしてしまうとオブジェクトがミュータブルになってしまい扱いづらくなります。

```scala
class Rectangle(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

外部からメンバ変数にアクセスする場合は、メソッド呼び出しと同様に`.`の後にメンバ変数名を指定するだけです。

```scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle(0.0, 0.0, 2.0, 3.0)

scala> a.x1
res6: Double = 0.0
```

面積を求めるメソッドを追加してみましょう。副作用のないメソッドなのでメソッド定義時に`()`はつけない方がいいでしょう。

```scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
  def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}
```

Rectangleという型をクラスを使ってつくることができました。自分でつくった型のリストをつくったりももちろんできます。Rectangle型のリストを受け取って面積の合計を返すメソッドをつくって試してみましょう。

```scala
scala> def sumArea(xs: List[Rectangle]): Double = {
     |   def loop(xs: List[Rectangle], acc: Double): Double = xs match {
     |     case Nil => acc
     |     case x :: tail => loop(tail, acc + x.area)
     |   }
     |   loop(xs, 0)
     | }
scala> val xs = List(Rectangle(0, 0, 2, 2), Rectangle(0, 0, 3, 3), Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```



## 継承

さて、長方形だけでなく円もつくりたくなりました。しかも`sumArea`メソッドでは長方形と円と両方を受け取って面積の合計を返したいです。こういう場合は、長方形と円を抽象化して図形として扱えるようにすればいいです。JavaやRubyでも継承ってものがありますが、Scalaにもあります。

抽象化して抽出された図形という型はインスタンス化はする必要がないので、`abstract`というキーワードを`class`の前につけます。`abstract`がついたクラスを抽象クラスと呼び、インスタンス化することはできません。図形をShapeという型で表すことにします。そして、RectangleクラスとCircleクラスは、Shapeクラスを継承します。継承は`extends`キーワードを使います。

抽象クラスを継承することにより、Rectangle型とCircle型は、Shape型のサブ型となります。逆に、Shape型は、Rectangle型とCircle型のスーパー型となります。

```scala
abstract class Shape {
  def area: Double
}

class Rectangle(val x1: Double, val y1: Double, val x2: Double, val y2: Double) extends Shape {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
  override def area = math.abs(x2 - x1) * math.abs(y2 - y1)
}

class Circle(val x: Double, val y: Double, val r: Double) extends Shape {
  override def toString = s"Circle($x, $y, $r)"
  override def area = r * r * math.Pi
}
```

Rectangleクラスは先ほどと同じように、CircleクラスもRectangleクラスと同じように使えます。インスタンス化してつくったオブジェクトはそれぞれ、Rectangle型とCircle型になります。

```scala
scala> val rec = new Rectangle(0, 0, 2, 2)
scala> val circle = new Circle(2, 4, 3)
scala> circle.x
scala> circle.r
```

Rectangle型とCircle型はShape型のサブ型なので、Shape型のオブジェクトとして扱うこともできます。

```scala
scala> def sumArea(xs: List[Shape]): Double = {
     |   def loop(xs: List[Shape], acc: Double): Double = xs match {
     |     case Nil => acc
     |     case x :: tail => loop(tail, x.area + acc)
     |   }
     |   loop(xs, 0)
     | }
scala> val xs = List(rec, circle, Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```



## ケースクラス

クラスを使って長方形や円を表す型をつくってみました。クラスを使わずにケースクラスというものを使って型をつくることもできます。しかも、ケースクラスを使った場合は、toStringや==メソッドなどを自動でつくってくれます。さらに、パターンマッチで使うこともできます。

ケースクラスを使うには、`class`と書いたところを`case class`にするだけです。toStringも削除してしまいましょう。あとはコンストラクタ引数の`val`も不要です。

```scala
abstract class Shape {
  def area: Double
}

case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) extends Shape {
  def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}

case class Circle(x: Double, y: Double, r: Double) extends Shape {
  def area: Double = r * r * math.pi
}
```

使ってみましょう。ケースクラスをインスタンス化して、Rectangle型のオブジェクトを手に入れるには`new`を使うのではなく、`apply`メソッドを呼び出します。`apply`メソッドは省略できるんでしたね。

```scala
scala> :load Shape.scala
scala> val rec = Rectangle.apply(1, 2, 5, 6)
scala> val rec = Rectangle(1, 2, 5, 6)
scala> rec.x1
scala> rec == Rectangle(1, 2, 5, 6)
scala> rec == Rectangle(3, 2, 5, 6)
scala> val circle = new Circle(2, 4, 3)
scala> val xs = List(rec, circle, Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```

ケースクラスはパターンマッチできます。条件分岐しつつ変数束縛するってやつです。

```scala
scala> def f(x: Shape): String = x match {
     |   case Circle(0.0, 0.0, _) => s"Center is origin. Area is ${x.area}."
     |   case _: Circle           => "this is circle."
     |   case _: Rectangle        => "this is rectangle."
     | }
scala> f(rec)
scala> f(circle)
scala> f(Circle(0,0,5))
```

ケースクラスの場合だけに限った話ではないですが、パターンマッチするときはパターンに漏れがないようにすることが大事です。これは結構大変なことです。例えば三角形をShape型のサブ型として追加した場合、Shape型でmatch式している箇所全体が影響してしまいます。

例えば、先ほどのfメソッドからRectangle型にマッチする1行を削除して定義し直して使ってみましょう。

```scala
scala> def f(x: Shape): String = x match {
     |   case Circle(0.0, 0.0, _) => s"Center is origin. area is ${x.area}."
     |   case _: Circle => "this is circle."
     | }
f: (x: Shape)String

scala> f(Circle(0, 0, 5))
res10: String = Center is origin. area is 78.53981633974483.

scala> f(Rectangle(0, 0, 3, 4))
scala.MatchError: Rectangle(0.0,0.0,3.0,4.0) (of class Rectangle)
  at .f(<console>:10)
  ... 32 elided
```

パターンから漏れていたRectangle型を指定するとエラーが発生しました。実行時エラーです。

この問題に対応するのが`sealed`修飾子です。`sealed`を`abstract class`の前に書くことで、パターンマッチに漏れがある場合にコンパイラが警告を出してくれます。

では、Shape型に`sealed`をつけてみます。

```scala
sealed abstract class Shape {
   ・・・
```

もう一度fメソッドをRectangle型にはマッチしない形で定義してみます。

```scala
scala> def f(x: Shape): String = x match {
     |   case Circle(0.0, 0.0, r) => s"Center is origin. radius is $r."
     |   case _: Circle => "this is circle."
     | }
<console>:10: warning: match may not be exhaustive.
It would fail on the following input: Rectangle(_, _, _, _)
       def f(x: Shape): String = x match {
                                 ^
f: (x: Shape)String
```

コンパイラが警告を出してくれます。これは非常に助かります。ケースクラスが継承する`abstract class`には`sealed`をつけた方がいいでしょう。

ケースクラスはクラスと比べて非常に便利です。データを表すようなクラスを作る場合は基本的にケースクラスと`sealed`を使うといいです。`sealed`を使って定義された型は代数的データ型で言うところの直和型なります。代数的データ型については深堀りできないのでしませんが（知識が足りなくてできません・・・）、調べてみるとおもしろいと思います。



## this

図形の面積を比較するメソッドを追加してみましょう。これはどの図形でも同じ処理になるので、Shapeクラスに実装できそうです。

```scala
abstract class Shape {
  def area: Double
  def lessThan(a: Shape): Boolean = area < a.area
}
```

次に、面積の大きい方のオブジェクトを返すmaxメソッドを定義しましょう。先ほどつくったlessThanメソッドを使って引数で指定された図形の方が大きければ引数で指定されたオブジェクトを、そうでなければ自分自身を返します。自分自身を表すオブジェクトが必要なときは、`this`を使います。

```scala
abstract class Shape {
  def area: Double
  def lessThan(a: Shape): Boolean = area < a.area
  def max(a: Shape): Shape = if (lessThan(a)) a else this
}
```



## 練習問題

1. 数値のリストを受け取って全ての要素の合計を返すsumメソッドを再帰を使って書いてください。
1. sumメソッドを末尾再帰で書いてください。
1. ソート済みの数値のリストから2分探索をするメソッドを書いてください。以下のようなシグニチャのメソッドです。
   
   ```scala
   def binarySearch(xs: List[Int], x: Int): Boolean
   ```
   
1. 2分探索木をつくってみましょう。注意点があります。REPLに読み込むときは、:loadではなく:pasteで読み込んでください（:loadだとファイル単位でなく1行ずつ評価するため、この後の問題を解いてるとどこかでエラーになると思います）。
   
   ```scala
   sealed abstract class Tree
   case class Empty() extend Tree
   case class Node(a: Int, left: Tree, right: Tree) extend Tree
   ```
   REPLから使う場合は、`scala> Empty()`というように使います。
   
   2分探索木に値を追加するinsertメソッドをつくりましょう。insertメソッドを使うことで次のように2分探索木をつくることができます。`scala> Empty().insert(2).insert(5)`
   
   ```scala
   sealed abstract class Tree {
     def insert(x: Int): Tree
   }
   ```
   
1. 上でつくった2分探索木に、指定された値が含まれているかを探すメソッドをつくりましょう。例えば次のような場合はtrueが返ってくるはずです。`scala> Empty().insert(2).insert(5).contains(2)`
   
   ```scala
   sealed abstract case class Tree {
     def insert(x: Int): Tree
     def contains(x: Int): Boolean
   }
   ```
   
1. 上でつくった2分探索木に、指定された値を持つノードを削除するメソッドをつくりましょう。ノードを削除するときのアルゴリズムについては調べてください。
   
   ```scala
   sealed abstract case class Tree {
     def insert(x: Int): Tree
     def contains(x: Int): Boolean
     def remove(x: Int): Tree
   }
   ```


## 今日出てきたキーワード

* match式、パターンマッチ
* String interpolation
* `var`
* 再帰
* 末尾再帰
* アキュムレータ（蓄積変数）
* `private`修飾子
* 引数のデフォルト値
* クラス
* `override`
* `abstract`、`extends`
* ケースクラス
* 代数的データ型



## 要注意ポイント

* パターンマッチで条件分岐しつつ変数束縛ができる
* String interpolationを使うと文字列結合をスッキリ書ける
* ループ処理を再帰で書くと`var`をなくせる
* 再帰はスタックオーバーフローが発生する危険があり、ループで書いたときよりパフォーマンスが落ちる可能性がある
* 再帰処理にアキュムレータを導入することで末尾再帰に書き換えることができる
* 末尾再帰はコンパイル時にループ処理に置き換えられる
* 継承元のクラスが実装してるメソッドをオーバーライドするときは`override`をつける
* クラスよりケースクラスの方がかなり便利である
* `sealed`をつけた`abstract class`を継承することでパターンマッチに漏れがある場合にコンパイラが注意してくれる
* REPLで:loadだと1行ずつ読み込まれる、:pasteだとファイル単位で読み込まれる



## 参考資料

* [文字列の補間](http://docs.scala-lang.org/ja/overviews/core/string-interpolation.html)
* [Scalaスケーラブルプログラミング第2版](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845)
* [すごいHaskellたのしく学ぼう!](http://www.amazon.co.jp/%E3%81%99%E3%81%94%E3%81%84Haskell%E3%81%9F%E3%81%AE%E3%81%97%E3%81%8F%E5%AD%A6%E3%81%BC%E3%81%86-Miran-Lipova%C4%8Da/dp/4274068854)

