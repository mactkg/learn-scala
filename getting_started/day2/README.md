# Day2（書きかけ）

## 関数
これまでに色々なメソッドを使ってきました。メソッドは何かしらのオブジェクトに属するもので単独では存在することができません。

Scalaではメソッドと似ていますが、オブジェクトに属さずに単独で存在できる関数があります。コップ本では関数は、ファーストクラスであると表現されています。ファーストクラスであるとは、オブジェクトに属さずにそれ単独で値のように扱えるというようなことです。値のように扱えるので、変数に代入したり、他のメソッドの引数に関数を渡したり、リストに複数の関数を格納したり、ということができます。

では関数をつくってみましょう。関数をつくために関数リテラルが用意されています。

```
scala> val f = (x: Int) => x * 2
```

引数として1つのIntオブジェクトを受け取り、2倍にして返す関数です。その関数を変数`f`に代入しています。

fを使ってみましょう。関数にある適当な数値を渡してみましょう。数値に関数fを適用する、と言うこともできます。

```
scala> f(10)
res1: Int = 20
```

関数を定義し、関数で定義した処理を実行することができました。


## コレクションの高階メソッド
関数がファーストクラスであることは分かりました。では関数は何に役立つのでしょうか？

まず、最初に役立つのは関数を受け取るメソッドです。関数を受け取るメソッドを高階メソッドと呼びます。高階メソッドの中でもコレクションが持つ高階メソッドはよく使うことになるでしょう。

```
scala> xs.map((x: Int) => x * 2)
```

xsがIntのリストなので、関数の引数であるxがIntとなることはScalaコンパイラが推論できます。なので、xの型は省略できます。

```
scala> xs.map((x) => x * 2)
```

関数の引数が1つの場合は`()`も省略できます。

```
scala> xs.map(x => x * 2)
```

さらに、引数を1回しか使っていない場合はもっと短く書くことができます。`x => x * 2` -> `_ => _ * 2` -> `_ * 2` といったイメージです。

```
scala> xs.map(_ * 2)
```

mapの他に、filterやflatMapといったメソッドもあります。map, filter, flatMap は特に使えるようになっておくいいです。

```
scala> xs.filter(_ > 50)
scala> xs.flatMap(0 to x)
```

他にもいろいろな高階メソッドがあります。



## 畳み込み
コレクションの高階メソッドの中でも、foldLeftとfolfRightを使った処理は畳み込み処理と呼ばれます。



## 練習問題



## 再帰
再帰でいろいろ書ける
末尾再帰
関数は再帰できない



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



