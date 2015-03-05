# Day2（書きかけ）

## パターンマッチの初歩
Day1でif式をやりましたが、パターンマッチを使うと条件分岐をよりスマートに書けることがあります。パターンマッチにはmatch式を使います。

```
scala> val x = 7
scala> x match {
     |   case 1 => "Good!"
     |   case _ => "Sorry"
     | }
res43: String = Sorry
```

`case 1 =>` というのが、xの値が1だった場合 `=>` の右側の式を評価（実行）しますよ、ということです。`case _ =>` というのは、それ以外の場合 `=>` の右側の式を評価しますよ、ということになります。matchも式なので値を返します。上の例だと、xの値は1ではないので"Sorry"という文字列が返ってますね。

パターンマッチはリストにも使えます。条件分岐しつつリストの先頭要素を変数に束縛することもできます。よく分からないかもしれませんが、コードを見れば分かると思います。

リストの先頭要素を取得するheadメソッドをパターンマッチを使ってつくってみましょう。
```
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil       => throw new IllegalArgumentException
     |     case x :: tail => x
     |   }
     | }
head: (xs: List[Int])Int

scala> head(List(1,2,3))
res3: Int = 1

scala> head(List())
java.lang.IllegalArgumentException
  at .head(<console>:9)
  ... 32 elided

scala> head(List(5))
res5: Int = 5
```

`case Nil` は`xs`が空のリストだったら、という意味です。この例ではエラーを発生させています（例外はいずれ・・・）。

`case x :: tail` は`xs`が空じゃない場合にマッチしますが、マッチさせると同時に変数`x`に先頭要素を代入しています。`::`は前回cons演算子のところでやったように値とリストをつなげるときに使っていました。パターンマッチのときでも同じような意味になります。`case x :: tail`は`xs`が`x`と`tail`をつなげたものとだったら、という意味になるわけですね。`tail`は空のリストでもそうでなくても構いません。`=>`の右側で使っていないので`_`で置き換えてしまいましょう。
```
scala> def head(xs: List[Int]): Int = {
     |   xs match {
     |     case Nil    => throw new IllegalArgumentException
     |     case x :: _ => x
     |   }
     | }
```

パターンマッチを使って条件分岐しつつ変数に値を代入することができました。パターンマッチに限りませんが変数に値を代入することを「束縛する」と言うこともできます。

headメソッドは結局1つのmatch式で出来上がってます。この場合、外側の`{}`はいらないので消してしまいましょう。
```
scala> def head(xs: List[Int]): Int = xs match {
     |   case Nil => throw new IllegalArgumentException
     |   case x :: _ => x
     | }
```

パターンマッチはタプルにも使えます。条件分岐しつつタプルの要素を変数に束縛することもできます。

例えば、3要素のタプルがあり、1つめの要素が動物の種類、2つ目の要素が名前、3つ目の要素が年齢だとします。そして、種別が犬（"doc"）の場合と猫（"cat"）の場合とそれ以外で処理を分けたいとき、こう書けます。
```
scala> def f(x: (String, String, Int)): String = x match {
     |   case ("dog", name, age)  => "I like dog! " + name + " is " + age + " years old!"
     |   case ("spider", name, _) => "I don't like spider. Sorry " + name
     |   case _ => "I have no interest"
     | }
f: (x: (String, String, Int))String

scala> f(("dog", "papico", 3))
res0: String = I like dog! papico is 3 years old!

scala> f(("spider", "papico", 3))
res1: String = I don't like spider. Sorry papico

scala> f(("cat", "papico", 3))
res2: String = I have no interest
```

これに比べてif式で書くと、条件式を書くところで`x._1 == "dog"`って書いて、本体のところでも`x._1`って書くことになって、ちょっとメンドイしスマートじゃない感じがしますよね。タプルでパターンマッチを使って分岐すれば、場合分けしつつタプルの中身の値を変数に代入できることが分かりました。パターンマッチの場合に限りませんが、変数に値を代入することを「束縛する」と言うこともできます。

タプルとリストを組み合わせることだってできます。さっきつくったfメソッドを改良してみましょう。年齢の後ろに好きな食べ物をリストで持たせます。
```
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
さっきのfメソッドですが、文字列結合の部分がちょっと面倒でしたよね。これはString interpolationと呼ばれる仕組みできれいに書けます。いわゆる数展開みたいに使えます。
```
scala> def f(x: (String, String, Int, List[String])): String = x match {
     |   case ("dog", name, age, Nil) => s"I like dog! $name is $age years old!"
     |   case ("dog", name, age, food :: _) => s"I like dog! $name is $age years old! $name like $food!"
     |   case ("spider", name, _, _) => s"I don't like spider. Sorry $name"
     |   case _ => "I have no interest"
     | }
```

変数展開よりも色々できるのですが、ここではやりません。こちらが参考になります => [文字列の補間](http://docs.scala-lang.org/ja/overviews/core/string-interpolation.html)



## 再帰
変数宣言のときにvalを使っていました。一度値を代入したらもう二度と代入できませんでした。

Scalaではvalの他にvarで変数宣言をすることもできます。varで宣言した変数には何度でも値を代入することができてしまいます。

Intのリストに格納されている数値の中から一番大きな値を取得するメソッドを考えてみましょう。
```
def maximum(xs: List[Int]): Int = {
  var max = 0
  for(x <- xs) {
    if (max < x) max = x
  }
  max
}
```

REPLで読み込んで実行してみます。
```
scala> :load maximum.scala
scala> val xs = List(3, 6, 1, 7, 2, 5)
scala> maximum(xs)
```

varを使ってmaximumメソッドを実装できました。しかし、副作用のない処理を書くには再代入可能な変数は邪魔になることが多く、理由がない限りvalを使うべきでしょう。

では、maximumメソッドをvarを使わずに定義するにはどうしたらいいでしょうか？再帰を使いましょう。

再帰とはメソッド内で自分自身を呼び出すことです。下のmaximumメソッドでは、メソッド本体で自分自身であるmaximumメソッドを呼び出しています。
```
def maximum(xs: List[Int]): Int = xs match {
  case Nil       => throw new IllegalArgumentException
  case x :: Nil  => x
  case x :: tail => {
    val y = maximum(tail)
    if (x > y) x else y
  }
}
```

再帰を使うことでvarをなくしつつループ処理を実現できました。さらに再帰を使うとコードが「どうやって求めるか」という手続きではなく、「求めるものが何であるか」という宣言に近づいていきます。

再帰を書くにはコツがあります。

> 再帰を使う際の定跡は、まず基底部を見極め、次に解くべき問題をより小さな部分問題へと分割する方法を考えることです。基底部と部分問題さえ正しく選んだなら、全体として何が起こるかの詳細を考える必要はありません。部分問題の解が正しいという保証をもとに、より大きな最終問題の解を構築すればよいだけです。

[引用元 : すごいHaskll楽しく学ぼう！]

Intのリストから要素の合計値を求めるメソッドを書いてみましょう。基底部を見極め、そして部分問題へと分割しましょう。

```
def sum(xs: List[Int]): Int = xs match {
  case Nil       => 0
  case x :: tail => x + sum(tail)
}
```

リストの中に特定の要素が含まれているかを調べるメソッドも再帰で書いてみます。

```
def contains(x: Int, xs: List[Int]): Boolean = xs match {
  case Nil       => false
  case y :: tail =>
    if (x == y) true
    else contains(x, tail)
}
```


クイックソードのアルゴリズムを書いてみましょう。リストのある要素をピボットとします。ピボットよりも小さい値のリストと大きい値のリストに分けてそれぞれソートします。簡単のためピボットは先頭要素としましょう。

```
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

再帰を使うとループ処理と比べて宣言的に書くことができ、かつ、再代入可能な変数がないため、間違えにくく読みやすいコードになることが多いです。しかし、ループと比べてメソッド呼び出しの回数が増えます。これはパフォーマンスの劣化につながりますし、大量のメソッド呼び出しによりエラーが発生する可能性もあります。

でも大丈夫です。再帰処理を「末尾再帰」で書くことにより、この問題を回避できます。なぜなら末尾再帰はコンパイル時にループ処理に置き換えられるからです。関数の最後で自分自身を呼び出すような再帰を末尾再帰と言います。

今まで出てきた例の中では、containsの例が末尾再帰です。それ以外は末尾再帰ではないです。しかし、単純なループで書けるものは変数を追加することで末尾再帰に置き換えることができます。たとえば、sumを末尾再帰に書き換えたsum2メソッドを書いてみましょう。

```
def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

accという変数が増えました。これはアキュムレータ（蓄積変数）と呼ばれます。元々のsumの場合は`x + sum(tail)`というように再帰呼び出しの結果を足し合わせていました。sum2の場合は`sum(tail, x + acc)`というようにアキュムレータに足し合わせて再帰呼び出しをしています。これによって関数の最後で再帰呼び出しをすることに、つまり末尾再帰になります。sum2は末尾再帰なのでコンパイル時にループ処理に置き換えられるためループよりもパフォーマンスが悪かっりすることはありません。

ただ、引数が増えたことにより、メソッドを使う側がちょっと使いづらくなってしまいますね。適切なアキュムレータを指定しないといけなくなってしまいます。sum2の場合は0です。

```
scala> sum(List(1,2,3,4,5))
scala> sum2(List(1,2,3,4,5), 0)
```

アキュムレータの初期値はメソッドの性質によってことなります。足し合わせる場合は0でいいですが、掛け合わせる場合は1が適切です。このような判断をメソッドを使う側が考えるのは使い勝手が悪いですし、間違えてしまうかもしれません。なので以下のようにsumからsum2を呼び出すようにします。

```
def sum(xs: List[Int]): Int = sum2(xs, 0)

def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

これでメソッドを使う側は、sumメソッドを使っている限りアキュムレータを意識する必要はありません。さらにsumメソッドの内部では末尾再帰のsum2メソッドを呼び出しているのでループに比べてパフォーマンスが劣るということもありません。

この方法だと末尾再帰を実現するたびにメソッドが増えてしまいます。メソッドを使う側にはsum2メソッドの存在が分からない方がいいでしょう。これを実現する方法は3つあります。

1つ目は、`private`修飾子を使う方法です。JavaやRubyでもおなじみの方法です。`private`をつけたメソッドは外部から呼び出せなくなります。

```
def sum(xs: List[Int]): Int = sum2(xs, 0)

private def sum2(xs: List[Int], acc: Int): Int = xs match {
  case Nil       => acc
  case x :: tail => sum2(tail, x + acc)
}
```

2つ目は、メソッド内にメソッドを定義する方法です。defの中でdefを使えるのです。メソッド内で定義されたメソッドはprivate扱いになり外部から呼び出すことはできません。

```
def sum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil       => acc
    case x :: tail => loop(tail, x + acc)
  }
  loop(xs, 0)
}
```

3つ目は、引数のデフォルト値を使う方法です。Scalaでは引数が未指定の場合のデフォルト値を設定できます。ただ、この方法では間違ったアキュムレータの初期値を与えることができる状態なので好ましくないでしょう。

```
def sum(xs: List[Int], acc: Int = 0): Int = xs match {
  case Nil       => acc
  case x :: tail => sum(tail, x + acc)
}
```

maximumも末尾再帰で書き換えてみましょう。

```
def maximum(xs: List[Int]): Int = {
  def loop(xs: List[Int], acc: Int): Int = xs match {
    case Nil       => throw new IllegalArgumentException
    case x :: Nil  => if (x > acc) x else acc
    case x :: tail => loop(tail, if (x > acc) x else acc)
  }
  loop(xs, 0)
}
```



## 関数
これまでに色々なメソッドを使ってきました。メソッドは何かしらのオブジェクトに属するもので単独では存在することができません（REPL上は例外です）。

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
ここで再帰でやったことを思い出しましょう。リストを受け取って再帰的になにかやるメソッドをいくつか作りました。特にアキュムレータと呼ばれる引数を追加することで末尾再帰に書き換えれるものがありました。これらは、基底部を定義してリストのパターンマッチを使い、先頭とそれ以降のリストに分割、分割されたリストとアキュムレータを使って自分自身を呼び出す、という構造になっていました。この構造はよく出てきます。よく出てくるのであれば抽象化して再利用したくないですか？したいですよね。このような構造は「畳み込み」と呼ばれコレクションの高階メソッドとして定義されています。

では、sumメソッドを畳み込みで書き直してみましょう。

```
scala> List(1,2,3,4,5).foldLeft(0)(_ + _)
```




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


## 練習問題
2分探索木

