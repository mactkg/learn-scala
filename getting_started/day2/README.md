# Day2（書きかけ）

## 再帰
再帰でいろいろ書ける
末尾再帰
関数は再帰できない



## 関数
これまでに色々なメソッドを使ってきました。メソッドは何かしらのオブジェクトに属するもので単独では存在することができません。

Scalaではメソッドと似ていますが、オブジェクトに属さずに単独で存在できる関数があります。Scalaにおいて関数はファーストクラスオブジェクトです。ファーストクラスであるとは、オブジェクトに属さずにそれ単独で値のように扱えるというようなことです。値のように扱えるので、変数に代入したり、他のメソッドの引数に関数を渡したり、リストに複数の関数を格納したり、ということができます。

では関数をつくってみましょう。関数をつくために関数リテラルが用意されています。

```
scala> val f = (x: Int) => x * 2
f: Int => Int = <function1>
```

引数として1つのIntオブジェクトを受け取り、2倍にして返す関数です。その関数を変数`f`に代入しています。
REPLの出力を見てみましょう。`f: Int => Int = <function1>`というのは、「fという変数は Int => Int 型で、値は <function1> です」という意味になります。Int => Int 型というのは、引数として1つのIntをとり返り値がIntである関数を表します。値の <function1> というのは関数本体を書きたいところだけど書くと長くなっちゃうので<function1>と書いて値が関数であることを示してるんだと解釈してます。

では、fを使ってみましょう。関数にある適当な数値を渡してみましょう。関数fに数値10を適用する、と言うこともできます。

```
scala> f(10)
res1: Int = 20
```

関数を定義し、関数で定義した処理を実行することができました。


## コレクションの高階メソッド
関数がファーストクラスオブジェクトであることは分かりました。では関数は何に役立つのでしょうか？

役立つシーンの1つに関数を受け取るメソッドがあります。関数を受け取るメソッドを高階メソッドと呼びます。高階メソッドの中でもコレクションが持つ高階メソッドはよく使うことになるでしょう。

コレクションの高階メソッドの1つ、foreachメソッドを使ってみます。引数で受け取った関数にコレクションそれぞれの要素を適用します。
```
scala> val cs = ('A' to 'Z')
scala> cs.foreach((c: Char) => println("Hello, " + c))
```

csがCharのコレクションなので、関数の引数であるcがCharであることはScalaが推論できます。なので、cの型は省略できます。

```
scala> cs.foreach((c) => println("Hello, " + c))
```

関数の引数が1つの場合は`()`も省略できます。

```
scala> cs.foreach(c => println("Hello, " + c))
```

さらに、引数を1回しか使っていない場合はもっと短く書くことができます。`c => println("Hello, " + c)` が `_ => println("Hello, " + _)` になって、さらに `println("Hello, " + _)` になる、といったイメージです。

```
scala> cs.foreach(println("Hello, " + _))
```

だいぶすっきりしますね。

次は、mapメソッドを使ってみます。mapメソッドは、引数で受け取った関数にコレクションそれぞれの要素を適用した結果からなる新たなコレクションを作ります。
```
scala> val xs = (1 to 10)
scala> xs.map((x: Int) => x * 2)
```

このmapメソッド。あるコレクションを別のコレクションに変換します。これってどこかで聞き覚えないですか？そうです、前回やったfor式ですね。上のmapを使った例はfor式でも書けますね。

```
scala> for(x <- xs) yield x * 2
```

どちらを使っても同じことができます。どちらを使うべきかというのは現時点では特にないので読みやすいを使えばいいかと思います。

foreach、 mapの他に、filterという高階メソッドがあります。filterにはBooleanを返す関数を渡すことができます。Booleanを返す関数のことを「述語」と呼んだりします。述語関数に各要素を適用した結果がtrueになる要素のみからなるコレクションを取得できます。

```
scala> xs.filter((x: Int) => x > 6)
scala> xs.filter(x => x > 6)
scala> xs.filter(_ > 6)
```

mapとfilterを組み合わせてみます。filterの後ろに続くのがmapかforeachの場合はfilterより高速に動作するwithFilterが使えます。

```
scala> xs.filter(_ > 6).map(_ * 2)
scala> xs.withFilter(_ > 6).map(_ * 2)
```

このfilter、withFilterもfor式で同じことができますね。for式ではifを使ってフィルタできるのでした。

```
scala> for(x <- xs; if x > 6) yield x * 2
```

もう1つ、flatMapメソッドを見てみましょう。flatMapはmapと同じように値変換するような感じなのですが、ちょっと違います。flatMapに渡す変換のための関数ではコレクションを返す必要があるのです。flatMapはコレクションを返す関数に各要素を適用した結果、複数のコレクションが手に入るのでそれらを結合します。

```
scala> List(1,2,3,4).flatMap((a: Int) => 1 to a)
scala> List(1,2,3,4).flatMap(a => 1 to a)
scala> List(1,2,3,4).flatMap(1 to _)
```

このflatMapもfor式と関わりがあります。ジェネレータを複数回続けた場合と同じことになります。2つのコレクションをfor式の2つのジェネレータに書くと、各要素を組み合わせた処理を書けました。

例えば前回の練習問題で三角形を3要素のタプルで表現するのがありました。for式、flatMap、両方を使った場合はこうなります。

```
scala> for(c <- 1 to 10; a <- 1 to c; b <- 1 to a) yield (a,b,c)
scala> (1 to 10).flatMap(c => (1 to c).flatMap(a => (1 to a).map(b => (a,b,c))))
```

直角三角形だけを抽出したい場合はこうなります。

```
scala> for(c <- 1 to 10; a <- 1 to c; b <- 1 to a; if c*c == a*a + b*b) yield (a,b,c)
scala> (1 to 10).flatMap(c => (1 to c).flatMap(a => (1 to a).withFilter(b => c*c == a*a + b*b).map(b => (a,b,c))))
```

うーん、これはfor式を使った方が見やすい気がしますね。

他にもいろいろな高階メソッドがあります。



## 畳み込み
コレクションの高階メソッドの中でも、foldLeftとfolfRightを使った処理は畳み込み処理と呼ばれます。




## ケースクラス

以前、2次元ベクトルを表すのにタプルを使いました。長方形を表すにはどうしたらいいでしょうか？Intを4つ持つタプルを使うのもいいですが、自分で長方形を表す型を定義してしまう方がより良いでしょう。

自分で型をつくってみましょう。型を定義する方法はいくつかありますが、ケースクラスを使うのが一番楽です。ケースクラスを定義するときは`case class`というキーワードを使います。

```
case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double)
```

これでRectangle型を定義できました。使ってみましょう。ケースクラスをインスタンス化して、Rectangle型のオブジェクトを手に入れるには`apply`メソッドを呼び出します。

```
scala> :load Shape.scala
scala> val rec = Rectangle(1, 2, 5, 6)
rec: Rectangle = Rectangle(1, 2, 5, 6)
```

ケースクラスでは、コンストラクタのパラメータへアクセスできます。ケースクラスはイミュータブルなのでコンストラクタのパラメータを書き換えることはできません。また、`toString`メソッドが自動で生成され呼び出すと各パラメータの値を出力できます。

```
scala> rec.x1
res1: Double = 1.0

scala> print(rec)
Rectangle(1, 2, 5, 6)
```

ケースクラスからつくったオブジェクトは、各パラメータを使って等しいかどうかの比較もできます。
```
scala> rec == Rectangle(1, 2, 5, 6)
scala> rec == Rectangle(3, 2, 5, 6)
```

面積を求めるメソッドを追加してみましょう。副作用のないメソッドなのでメソッド定義時に`()`はつけない方がいいでしょう。

```
case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) {
def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}
```

自分でつくった型のリストをつくったりももちろんできます。Rectangleのリストを受け取って面積の合計を返すメソッドをつくって試してみましょう。畳み込み処理が使えますね。

```
scala> def sumArea(xs: List[Rectangle]): Double = xs.foldLeft(0) { _ + _.area }
scala> val xs = List(Rectangle(0, 0, 2, 2), Rectangle(0, 0, 3, 3), Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```

さて、長方形だけでなく円もつくりたくなりました。しかも`sumArea`メソッドでは長方形と円と両方を受け取って面積の合計を返したいです。こういう場合は、長方形と円を抽象化して図形として扱えるようにすればいいです。JavaやRubyでも継承ってものがありますが、Scalaにもあります。

抽象化して抽出された図形という型はインスタンス化することはないので、`abstract`というキーワードを`case class`の前につけます。図形という型をShapeというケースクラスで表すことにします。Shape型に属するケースクラスを定義するときは`extends`キーワードを使います。

```
abstract case class Shape {
def area: Double
}

case class Rectangle(x1: Double, y1: Double, x2: Double, y2: Double) extends Shape {
def area: Double = math.abs(x2 - x1) * math.abs(y2 - y1)
}

case class Circle(x: Double, y: Double, r: Double) extends Shape {
def area: Double = r * r * math.pi
}
```

Rectangleは先ほどと同じように、CircleもRectangleと同じように使えます。

```
scala> val rec = Rectangle(0, 0, 2, 2)
scala> val circle = Circle(2, 4, 3)
scala> circle.x
scala> circle.r
```

両方をShapeとして扱うこともできます。

```
scala> def sumArea(xs: List[Shape]): Double = xs.foldLeft(0) { _ + _.area }
scala> val xs = List(rec, circle, Rectangle(0, 0, 2, 4))
scala> sumArea(xs)
```


## パターンマッチ



## 練習問題
2分探索木



## 型パラメータ



## Option



