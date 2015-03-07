# Day2

## パターンマッチの初歩
Day1でif式をやりましたが、パターンマッチを使うと条件分岐をよりスマートに書けることがあります。パターンマッチにはmatch式を使います。

```scala
scala> val x = 7
scala> x match {
     |   case 1 => "Good!"
     |   case _ => "Sorry"
     | }
res0: String = Sorry
```

`case 1 =>` というのが、xの値が1だった場合 `=>` の右側の式を評価（実行）しますよ、ということです。`case _ =>` というのは、それ以外の場合 `=>` の右側の式を評価しますよ、ということになります。matchも式なので値を返します。上の例だと、xの値は1ではないので"Sorry"という文字列が返ってますね。

パターンマッチはリストにも使えます。条件分岐しつつリストの先頭要素を変数に束縛することもできます。よく分からないかもしれませんが、コードを見れば分かると思います。

リストの先頭要素を取得するheadメソッドをパターンマッチを使ってつくってみましょう。

```scala
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil       => throw new IllegalArgumentException
     |     case x :: tail => x
     |   }
     | }
head: (xs: List[Int])Int

scala> head(List(1,2,3))
res1: Int = 1

scala> head(List())
java.lang.IllegalArgumentException
  at .head(<console>:9)
  ... 32 elided

scala> head(List(5))
res2: Int = 5
```

`Nil`は空のリストでしたね。`case Nil` は`xs`が空のリストだったら、という意味です。この例ではエラーを発生させています（例外はいずれ・・・）。

`case x :: tail` は`xs`が空じゃない場合にマッチしますが、マッチさせると同時に変数`x`に先頭要素を代入しています。`::`は前回cons演算子のところでやったように値とリストをつなげるときに使っていました。パターンマッチのときでも同じような意味になります。`case x :: tail`というのは、`xs`が`x`と`tail`をconsでつなげたものだったら、という意味になるわけです。`tail`は空のリストでもそうでなくても構いません、どちらにしろconsでつなげることができますからね。`tail`は`=>`の右側で使っていないので、`_`で置き換えてしまいましょう。

```scala
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil    => throw new IllegalArgumentException
     |     case x :: _ => x
     |   }
     | }
```

パターンマッチを使って条件分岐しつつ変数に値を代入することができました。パターンマッチに限りませんが変数に値を代入することを「束縛する」と言うこともできます。

headメソッドは結局1つのmatch式で出来上がってます。この場合、外側の`{}`はいらないので消してしまいましょう。

```scala
scala> def head(xs: List[Int]): Int = xs match {
     |   case Nil => throw new IllegalArgumentException
     |   case x :: _ => x
     | }
```

パターンマッチはタプルにも使えます。条件分岐しつつタプルの要素を変数に束縛することもできます。

例えば、3要素のタプルがあり、1つめの要素が動物の種類、2つ目の要素が名前、3つ目の要素が年齢だとします。

```scala
scala> val x = ("dog", "papico", 3)
```

そして、種別が犬（"doc"）の場合とクモ（"spider"）の場合とそれ以外で処理を分けたいとき、こう書けます。

```scala
scala> def f(x: (String, String, Int)): String = x match {
     |   case ("dog", name, age)  => "I like dog! " + name + " is " + age + " years old!"
     |   case ("spider", name, _) => "I don't like spider. Sorry " + name
     |   case _ => "I have no interest"
     | }
f: (x: (String, String, Int))String

scala> f(x)
res0: String = I like dog! papico is 3 years old!

scala> f(("spider", "papico", 3))
res1: String = I don't like spider. Sorry papico

scala> f(("cat", "papico", 3))
res2: String = I have no interest
```

これをif式で書くとすると、条件式を書くところで`x._1 == "dog"`って書いて、本体のところでも`x._1`って書くことになって、ちょっとメンドイしスマートじゃない感じがします。タプルでパターンマッチを使って分岐すれば、場合分けしつつタプルの中身の値を変数に代入できることが分かりました。パターンマッチの場合に限りませんが、変数に値を代入することを「束縛する」と言うこともできます。

タプルとリストを組み合わせることだってできます。さっきつくったfメソッドを改良してみましょう。年齢の後ろに好きな食べ物をリストで持たせます。

```scala
scala> def f(x: (String, String, Int, List[String])): String = x match {
     |   case ("dog", name, age, Nil)       => "I like dog! " + name + " is " + age + " years old!"
     |   case ("dog", name, age, food :: _) => "I like dog! " + name + " is " + age + " years old! " + name + " like " + food + "!"
     |   case ("spider", name, _, _)        => "I don't like spider. Sorry " + name
     |   case _ => "I have no interest"
     | }
f: (x: (String, String, Int, List[String]))String

scala> f(("dog", "papico", 3, List("meet", "fish")))
res6: String = I like dog! papico is 3 years old!papico like meet!
```


## String interpolation
さっきのfメソッドですが、文字列結合の部分がちょっと面倒でしたよね。これはString interpolationと呼ばれる仕組みできれいに書けます。いわゆる変数展開みたいに使えます。

```scala
scala> def f(x: (String, String, Int, List[String])): String = x match {
     |   case ("dog", name, age, Nil) => s"I like dog! $name is $age years old!"
     |   case ("dog", name, age, food :: _) => s"I like dog! $name is $age years old! $name like $food!"
     |   case ("spider", name, _, _) => s"I don't like spider. Sorry $name"
     |   case _ => "I have no interest"
     | }
```

変数展開よりも色々できるのですが、ここではやりません。こちらが参考になります => [文字列の補間](http://docs.scala-lang.org/ja/overviews/core/string-interpolation.html)



## 再帰
変数宣言のときに`val`を使っていました。一度値を代入したらもう二度と代入することはできませんでした。

Scalaでは`val`の他に`var`で変数宣言をすることもできます。`var`で宣言した変数には何度でも値を代入することができてしまいます。

Intのリストに格納されている数値の中から一番大きな値を取得するメソッドを考えてみましょう。

```scala
def maximum(xs: List[Int]): Int = {
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

`var`を使ってmaximumメソッドを実装できました。しかし、副作用のない処理を書くには再代入可能な変数は邪魔になることが多く、理由がない限り`val`を使うべきでしょう。

では、maximumメソッドを`var`を使わずに定義するにはどうしたらいいでしょうか？再帰を使いましょう。

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

再帰を使うことで`var`をなくしつつループ処理を実現できました。さらに再帰を使うとコードが「どうやって求めるか」という手続きではなく、「求めるものが何であるか」という宣言に近づいていきます。

再帰を書くにはコツがあります。

> 再帰を使う際の定跡は、まず基底部を見極め、次に解くべき問題をより小さな部分問題へと分割する方法を考えることです。基底部と部分問題さえ正しく選んだなら、全体として何が起こるかの詳細を考える必要はありません。部分問題の解が正しいという保証をもとに、より大きな最終問題の解を構築すればよいだけです。

[引用元 : すごいHaskll楽しく学ぼう！]

Intのリストから要素の合計値を求めるメソッドを再帰を使って書いてみましょう。基底部を見極め、そして部分問題へと分割します。

```scala
def sum(xs: List[Int]): Int = xs match {
  case Nil       => 0
  case x :: tail => x + sum(tail)
}
```

実際に使ってみましょう。イメージが掴みづらい場合は簡単な例から試してイメージしていくと分かりやすいです。

```scala
scala> sum(List())
scala> sum(List(1))
scala> sum(List(1,2))
scala> sum(List(1,2,3,4,5))
```

リストの中に特定の要素が含まれているかを調べるメソッドも再帰で書いてみます。

```scala
def contains(x: Int, xs: List[Int]): Boolean = xs match {
  case Nil       => false
  case y :: tail =>
    if (x == y) true
    else contains(x, tail)
}
```

クイックソードのアルゴリズムを書いてみましょう。リストの要素のどれか1つをピボットとします。ピボットよりも小さい値のリストと大きい値のリストに分けてそれぞれソートします。それぞれソートした結果とピボットを結合すればソート済みのリストが手に入ります。簡単のためピボットは先頭要素としましょう。

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

どうでしょうか。やりたいことをそのまま宣言的に書けたのではないでしょうか。部分問題への分割とリストのパターンマッチが相性抜群ですね。



## 末尾再帰

再帰を使うとループ処理と比べて宣言的に書くことができ、かつ、再代入可能な変数がないため、間違えにくく読みやすいコードになることが多いです。しかし、ループと比べてメソッド呼び出しの回数が増えます。これはパフォーマンスの劣化につながりますし、大量のメソッド呼び出しによりエラーが発生する可能性もあります。

でも大丈夫です。再帰処理を「末尾再帰」で書くことにより、この問題を回避できます。末尾再帰とは、関数の最後で自分自身を呼び出す再帰のことです。末尾再帰はコンパイル時にコンパイラによってループ処理に置き換えられるため、問題を回避できるのです。

今まで出てきた例の中では、containsの例が末尾再帰です。それ以外は末尾再帰ではないです。しかし、単純なループで書けるものは変数を追加することで末尾再帰に置き換えることができます。たとえば、sumを末尾再帰に書き換えたsum2メソッドを書いてみましょう。

```scala
def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

accという変数が増えました。これはアキュムレータ（蓄積変数）と呼ばれます。元々のsumの場合は`x + sum(tail)`というように再帰呼び出しの結果を足し合わせていました。sum2の場合は`sum(tail, x + acc)`というようにアキュムレータに足し合わせて再帰呼び出しをしています。これによって関数の最後で再帰呼び出しをすることになりました。つまり末尾再帰になりました。sum2は末尾再帰なのでコンパイル時にループ処理に置き換えられるためループよりもパフォーマンスが悪かっりすることはありません。

ただ、引数が増えたことにより、メソッドを使う側がちょっと使いづらくなってしまいましたね。sum2メソッドを使う人は適切なアキュムレータの初期値を指定しないといけません。sum2の場合は0です。

```scala
scala> sum(List(1,2,3,4,5))
scala> sum2(List(1,2,3,4,5), 0)
```

アキュムレータの初期値はメソッドの性質によってことなります。足し合わせる場合は0でいいですが、掛け合わせる場合は1が適切です。このような判断をメソッドを使う側が考えるのは使い勝手が悪いですし、間違えてしまうかもしれません。なので以下のようにsumからsum2を呼び出すようにします。

```scala
def sum(xs: List[Int]): Int = sum2(xs, 0)

def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

これでメソッドを使う側は、sumメソッドを使っている限りアキュムレータを意識する必要はありません。さらにsumメソッドの内部では末尾再帰のsum2メソッドを呼び出しているのでループに比べてパフォーマンスが劣るということもありません。

ただ、sum2メソッドを直接呼び出してしまうかもしれませんし、末尾再帰を実現するたびにこのようなメソッドが増えてしまいます。メソッドを使う側にはsum2メソッドの存在が分からない方がいいでしょう。これを実現する方法は3つあります。

1つ目は、`private`修飾子を使う方法です。JavaやRubyでもおなじみの方法です。`private`をつけたメソッドは外部から呼び出せなくなります。

```scala
def sum(xs: List[Int]): Int = sum2(xs, 0)

private def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

2つ目は、メソッド内にメソッドを定義する方法です。defの中でdefを使えるのです。メソッド内で定義されたメソッドはprivate扱いになり外部から呼び出すことはできません。

```scala
def sum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil       => acc
    case x :: tail => loop(tail, x + acc)
  }
  loop(xs, 0)
}
```

3つ目は、引数のデフォルト値を使う方法です。Scalaでは引数が未指定の場合のデフォルト値を設定できます。ただ、この方法では間違ったアキュムレータの初期値を与えることができる状態なので好ましくないでしょう。

```scala
def sum(xs: List[Int], acc: Int = 0): Int = xs match {
  case Nil       => acc
  case x :: tail => sum(tail, x + acc)
}
```

maximumも末尾再帰で書き換えてみましょう。

```scala
def maximum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil       => throw new IllegalArgumentException
    case x :: Nil  => if (x > acc) x else acc
    case x :: tail => loop(tail, if (x > acc) x else acc)
  }
  loop(xs, 0)
}
```



## クラス

以前、2次元ベクトルを表すのにタプルを使いました。長方形を表すにはどうしたらいいでしょうか？Intを4つ持つタプルを使うのもいいですが、自分で長方形を表す型を定義してしまう方がより良いでしょう。

自分で型をつくってみましょう。型を定義する方法はいくつかありますが、クラスを使う方法を見ていきます。

まず、クラスを定義するときは`class`を使います。インスタンス化するときは`new`を使います。

```scala
scala> class Rectangle
scala> val x = new Rectangle
```

Rectangleをいろいろいじっていくので、Shape.scalaに書いて使うときはREPLにロードして使いましょう。

長方形を左下の座標と右上の座標で表すことにします。コンストラクタで指定できるようにしましょう。コンストラクタの引数は、クラス名の右側に書きます。

```scala
// Rectangle.scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double)
```

使ってみましょう。

```scala
scala> :load Rectangle.scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle@7cef4e59
```

REPLの出力が何やら分かりづらいですね。REPLの出力ではオブジェクトのtoStringメソッドが呼ばれます。なので、RectangleにもtoStringメソッドを追加しましょう。

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

エラーになってしまいました。"method toString needs override modifier" と表示されています。ScalaではすべてのクラスはAnyクラスを継承しています。そしてAnyクラスでtoStringが定義されているため、RectangleクラスでtoStringを定義するとオーバーライドすることになります。Scalaではオーバーライドするときは`override`をdefに左側に書く必要があります。

```scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

これで無事読み込むことができます。REPLの出力も分かりやすくなりました。

```scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle(0.0, 0.0, 2.0, 3.0)
```

コンストラクタのパラメータに`val`、もしくは、`var`をつけることで、外部からパラメータにアクセスできるようになります。理由がない限り公開しなくていいでしょう。また公開するにしても`var`ではなく`val`にしましょう。`var`にしてしまうとオブジェクトがミュータブルになってしまい扱いづらくなります。

```scala
class Rectangle(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {
  override def toString = s"Rectangle($x1, $y1, $x2, $y2)"
}
```

外部からコンストラクタパラメータにアクセスする場合は、`.`の後にパラメータ名を指定するだけです。以下の例の場合は`x1`という名前のメソッドを呼び出して、パラメータ`x1`の値にアクセスできます。

```scala
scala> val a = new Rectangle(0,0,2,3)
a: Rectangle = Rectangle(0.0, 0.0, 2.0, 3.0)

scala> a.x1
res6: Double = 0.0
```

面積を求めるメソッドを追加してみましょう。副作用のないメソッドなのでメソッド定義時に`()`はつけない方がいいでしょう。

```scala
class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
  def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}
```

Rectangleという型を自分でつくることができました。自分でつくった型のリストをつくったりももちろんできます。Rectangleのリストを受け取って面積の合計を返すメソッドをつくって試してみましょう。

```scala
scala> def sumArea(xs: List[Rectangle]): Double = {
     |   def loop(xs: List[Rectangle], acc: Double): Double = xs match {
     |     case Nil => acc
     |     case x :: tail => loop(tail, x.area + acc)
     |   }
     |   loop(xs, 0)
     | }
scala> val xs = List(Rectangle(0, 0, 2, 2), Rectangle(0, 0, 3, 3), Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```



## 継承
さて、長方形だけでなく円もつくりたくなりました。しかも`sumArea`メソッドでは長方形と円と両方を受け取って面積の合計を返したいです。こういう場合は、長方形と円を抽象化して図形として扱えるようにすればいいです。JavaやRubyでも継承ってものがありますが、Scalaにもあります。

抽象化して抽出された図形という型はインスタンス化することはないので、`abstract`というキーワードを`class`の前につけます。図形をShapeという型で表すことにします。Shape型に属するケースクラスを定義するときは`extends`キーワードを使います。

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

Rectangleは先ほどと同じように、CircleもRectangleと同じように使えます。

```scala
scala> val rec = new Rectangle(0, 0, 2, 2)
scala> val circle = new Circle(2, 4, 3)
scala> circle.x
scala> circle.r
```

両方をShapeとして扱うこともできます。

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

ケースクラスを使うには、`class`と書いたところを`case class`にするだけです。toStringなども削除してしまいましょう。あとはコンストラクタ引数の`val`も不要です。

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
rec: Rectangle = Rectangle(1, 2, 5, 6)

scala> rec.x1
res1: Double = 1.0

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

ケースクラスの場合だけに限った話ではないですが、パターンマッチするときはパターンに漏れがないようにすることが大事です。これは結構大変なことです。例えば三角形を追加した場合、Shapeでmatch式している箇所全体が影響してしまいます。

例えば、先ほどのfメソッドからRectangleにマッチする1行を削除して定義し直して使ってみましょう。

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

パターンから漏れていたRectangleを指定するとエラーが発生しました。実行時エラーです。

この問題に対応するのが`sealed`修飾子です。`sealed`を`abstract class`の前に書くことで、パターンマッチに漏れがある場合にコンパイラが警告を出してくれます。

では、Shapeに`sealed`をつけてみます。

```scala
sealed abstract class Shape {
   ・・・
```

もう一度fメソッドをRectangleにはマッチしない形で定義してみます。

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

コンパイラが警告を出してくれます。これは非常に助かります。ケースクラスが継承する`abstract class`には基本的に`sealed`をつけた方がいいでしょう。

ケースクラスはクラスと比べて非常に便利です。データを表すようなクラスを作る場合は基本的にケースクラスを使うといいです。



## シングルトンオブジェクト

クラス以外にも型をつくる方法はいろいろありますが、そのうちの1つがシングルトンオブジェクトです。これはその名の通りシングルトンオブジェクトをつくります。インスタンス化する、というような考え方はないです。定義したらそれが型でありオブジェクトになります。

シングルトンオブジェクトは、`object`を使って定義します。

```scala
object OriginPoint {
  val x = 0.0
  val y = 0.0
}
```

シングルトンオブジェクトは、コンパニオンオブジェクトとして使われることが多いです。コンパニオンオブジェクトとはなんでしょうか？

今までリストをつくるときに、`List(1,2,3,4,5)`というように書いていました。この書き方はなんでしょうか？これは`apply`メソッドが省略されていて、省略せずに書くと`List.apply(1,2,3,4,5)`となります。Listはケースクラスではないです。Listという名前は、クラスとシングルトンオブジェクト、両方で定義されています。そして、`List(1,2,3,4,5)`というのは、シングルトンオブジェクトのapplyメソッドを呼び出していることになります。

Listがクラスとしても定義されているなら、`List(1,2,3,4,5)`ではなく、`new List(1,2,3,4,5)`というように書けないのでしょうか？書けません。なぜなら、Listのコンストラクタは`private`になっているためです。なので、`List(1,2,3,4,5)`というようにリストを作ります。ListオブジェクトからはListクラスの`private`なコンストラクタが呼び出せるということになります。クラスと同じファイル内で、同名のシングルトンオブジェクトを定義すると、それはコンパニオンオブジェクトとなります。コンパニオンオブジェクトは同名のクラスの`private`なメソッドにアクセスすることができるのです。

```scala
class Hoge private (x: Int)
object Hoge {
  def apply(x: Int): Hoge = new Hoge(x)
}
```

これをREPLに読み込んでみましょう。ここで注意が必要です。:loadでファイルを読み込むとファイル単位ではなく1行ずつ評価するため、`object Hoge`がコンパニオンオブジェクトとして認識されません。ファイル全体を一度にREPLに評価してほしいときは、:loadではなく:pasteを使いましょう。

```scala
scala> new Hoge(1)
<console>:10: error: constructor Hoge in class Hoge cannot be accessed in object $iw
              new Hoge(1)
              ^

scala> Hoge(1)
res1: Hoge = Hoge@59f95c5d
```

`List(1,2,3,4,5)`というのは、Listクラスのコンパニオンオブジェクトのapplyメソッドを呼び出していた、ということが分かりました。



## 練習問題

1. 階乗を計算するメソッドを再帰を使って書いてください。
1. 上の問題でつくったメソッドを末尾再帰の形に書き換えてください。
1. リストのtakeメソッドと同じことをするメソッドを末尾再帰で書いてください。
1. ソート済みの数値のリストから2分探索をするメソッドを書いてください。以下のようなシグニチャのメソッドです。
   
   ```scala
   def binarySearch(xs: List[Int], x: Int): Boolean
   ```
   
1. 2分探索木をつくってみましょう。REPLに読み込むときは、:loadではなく:pasteで読み込んでください（:loadだとこの後の問題を解いてるとどこかでエラーになります。エラーになることを試してみてもいいかもしれません）。
   
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
* 再帰
* 末尾再帰
* アキュムレータ（蓄積変数）
* private修飾子
* クラス
* abstract、extends
* ケースクラス
* シングルトンオブジェクト、コンパニオンオブジェクト


## 要注意ポイント

* パターンマッチで条件分岐しつつ変数束縛ができる
* String interpolationを使うと文字列結合をスッキリ書ける
* ループ処理を再帰で書くと`var`をなくせる
* 再帰処理にアキュムレータを導入することで末尾再帰に書き換えることができる
* クラスよりケースクラスの方がかなり便利である
* `sealed`をつけた`abstract class`を継承することでパターンマッチに漏れがある場合にコンパイラが注意してくれる
* `List(1,2,3,4,5)`というのは、Listクラスのコンパニオンオブジェクトのapplyメソッドを呼び出している



## 参考資料

* [文字列の補間](http://docs.scala-lang.org/ja/overviews/core/string-interpolation.html)
* [Scalaスケーラブルプログラミング第2版](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845)
* [すごいHaskellたのしく学ぼう!](http://www.amazon.co.jp/%E3%81%99%E3%81%94%E3%81%84Haskell%E3%81%9F%E3%81%AE%E3%81%97%E3%81%8F%E5%AD%A6%E3%81%BC%E3%81%86-Miran-Lipova%C4%8Da/dp/4274068854)

