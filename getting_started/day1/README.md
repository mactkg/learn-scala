# Day1

## REPL
REPLを使ってScalaを使ってみましょう！

REPLとは、対話型評価環境のことで、Read Eval Print Loop の頭文字をとってREPLと呼びます。

REPLは、ターミナルで`scala`コマンドで起動させます。そうするとプロンプトがこうなります。

```
scala>
```

止めるときは `scala> :q` です。



## val
変数に数値を代入してみましょう。変数宣言には`val`を使います。

```
scala > val x = 10
x: Int = 10
```

`x: Int = 10`と出力がありますが、これは「xという変数はInt型で、値が10です」という意味になります。

Int型という言葉が出てきました。Scalaでは値に型があります。型が合わない計算はエラーになります。例えば、Int型の値には文字列との`+`は定義されているので動作しますが、文字列との`-`は定義されていないためエラーになります。

```
scala> val y = x + "aaa"
y: String = 10aaa

scala> val z = x - "aaa"
<console>:8: error: overloaded method value - with alternatives:
  (x: Double)Double <and>
  (x: Float)Float <and>
  (x: Long)Long <and>
  (x: Int)Int <and>
  (x: Char)Int <and>
  (x: Short)Int <and>
  (x: Byte)Int
 cannot be applied to (String)
       val z = x - "aaa"
                 ^
```

型についてはいずれもうちょっと詳しく。。

`val`で宣言した変数に値を再度代入してみましょう。エラーが起きますか？

```
scala> x = 20
```

そうです、エラーになります。valで宣言した変数に値を代入した後、再度値を代入することはできません。

ただし、REPL上では`val`を使って同じ変数名で宣言し直すことができます。あくまでもREPL上だけです。REPLで同じ変数名が使えないのは不便ですからね。

次は適当に数値だけ入力してみましょう。

```
scala> 11
res1: Int = 11
```

`res1: Int = 11`というのは、先ほどの`x: Int = 10`と同様に解釈できます。REPLでは入力した式を評価した結果が自動的にres*X*という変数に代入されます。

```
scala> res1 + 2
res2: Int = 13
```

明示的に変数の型を書くこともできます。逆に言うと書かなくてもできる限り適切な型を自動でつけてくれます。この機能のことを型推論と呼びます。そのため開発者は常に型を書くわずらわしさから解放されます。

```
scala> val y: String = 10 + "aaa"
res3: String = 10aaa
```



## 文字列の操作
"で囲むと文字列リテラルになります。

```
scala> "abc"
res4: String = "abc"
```

Scalaの文字列は、Javaのjava.lang.Stringを使います。ScalaではJavaのクラスを使うことができます。このことは、Javaの資産が使えるためScalaの良いところである反面、Javaをやったことない人にはわかりづらくなる要因な気もします。。。

java.lang.Stringのメソッドをいくつか呼び出してみましょう。参考: [String (Java Platform SE 8)](http://docs.oracle.com/javase/jp/8/api/java/lang/String.html)

```
scala> "abc".startsWith("a")
scala> "abc".startsWith("b")
scala> "abcdefg".substring(2,5)
scala> "abc".length()
scala> "abc".length
```

最後の例では、`length`メソッドを`()`なしで呼び出しています。引数をとらないメソッドを呼び出すときは`()`を省略して呼び出すことができます。



## 数値同士の演算
演算子、`+`, `-`, `*`, `/`, `%` を使って数値計算してみましょう。`%`は剰余を求めます。括弧を使って計算の優先順位も変えれます。

```
scala> 1 + 2
scala> 5 - 9
scala> 2 * 3 + 4
scala> 2 * (3 + 4)
scala> 12 / 2 % 4
```

これらの演算子は、実はInt型であるオブジェクトのメソッドです。そのため、`1 + 2`というのは`1.+(2)`と同じ意味になります。

まず、Scalaでは全ての値はオブジェクトです。そのため、`1`に対してメソッド呼び出しができます。`1.+(2)`というように`+`メソッドを呼び出しています。

そして、Scalaではメソッド呼び出しの`.`を省略できます。`1 + (2)`になります。

さらに、単一の引数をとるメソッドの`()`が省略できます。`1 + 2`になります。これでメソッド呼び出しを演算子のように扱うことができました。

オブジェクトとは、JavaやRubyで言うところのオブジェクトと同じです。クラスがあってインスタンス化する、っていうアレです。なのでScalaにもクラスがあって、それをインスタンス化できます。クラスについては後の方でやります。


## 数値同士の比較
比較演算子もメソッドとして定義されています。`>`, `<`, `>=`, `<=`, `==`, `!=`を適当に使ってみましょう。Booleanには論理演算子`&&`, `||`が定義されています。

```
scala> 21 < 50
scala> 21 >= 50
scala> 101 == 100 + 1
scala> 101 != 100 + 1
scala> (21 < 50) && (101 != 100 + 1)
scala> 21 < 50 && 101 != 100 + 1
scala> (21 < 50) || (101 != 100 + 1)
```



## if式
他の言語のifと同じように使えます。Scalaのifは式なので値を返します。

```
scala> val s = if (21 < 50) "hoge" else "fuga"
s: String = "hoge"

scala> val s =
     |   if (21 < 50) {
     |     "hoge"
     |   } else {
     |     "fuga"
     |   }
s: String = "hoge"
```



## Unit型
先ほどのif式のelseを省略することもできます。しかし、さっきとはちょっと違うことが起きます。

```
scala> val s = if (21 < 50) "hoge"
s: Any = hoge
```

変数`s`の型がStringではなくAnyになってしまいました。さっきのif式の条件の比較演算子を逆にしてみましょう。

```
scala> val s = if (21 > 50) "hoge"
s: Any = ()
```

変数`s`の値が`()`になってます！これはなんでしょうか？

`()`はユニットと呼ばれる値でJavaでいうところのvoidです。Scalaでは副作用のある処理の返り値に使われます。副作用とは何かの状態を変える処理です。オブジェクトの状態を変える、DBに対して操作をする、コンソールに文字列を出力するなどです。

コンソールに文字列を出力するときは`println`が使えます。

```
scala> println("Hello")
Hello

scala> val x = println("Hello")
Hello
x: Unit = ()
```

`println`の返りを`x`に代入しました。`x`の値、つまり`println`の返り値が`()`になっています。そして`()`の型は`Unit`であることが分かります。

話を`val s = if (21 < 50) "hoge"`に戻します。このif式は条件がtrueなのかfalseなのかによって、`"hoge"`というString型の値、もしくは、`()`というUnit型の値のどちらかを返します。このときStringとUnitは別の型であるため、どちらの型の値でも大丈夫なように、変数`s`の型は`Any`という型になってしまいます。こうなるともはや`s`をStringとして扱うことはできません。

```
scala> val s = if (21 < 50) "hoge"
s: Any = ()

scala> s.length
<console>:9: error: value length is not a member of Any
              s.length
                ^
```

このような挙動は望んでいるものではないでしょう。そのためif式の返り値を使いたい場合は基本的にelseも書くようにします。

`val`で変数宣言をするときに型を明示しておくと、変数が予想外に`Any`になってしまうことを防ぐことができます。こうしておくと、`s`を使うときではなく`s`に値を代入するときにエラーを発生させることができるので、よりエラーの原因を特定しやすくなります。デバッグ時などに型を明示的に指定することで予想と異なる型になってしまっている箇所を探したりできます。

```
scala> val s: String = if (21 < 50) "hoge"
<console>:7: error: type mismatch;
 found   : Unit
 required: String
       val s: String = if (21 < 50) "hoge"
                       ^
```



## リスト
リストには同じ型の値を複数個格納できます。リストは`List`という型で表されます。同じ型じゃない、例えば数値と文字列、両方を格納することもできちゃいますが、やらない方がいいでしょう。

```
scala> List(1, 2, 3, 4, 5)
res5: List[Int] = List(1, 2, 3, 4, 5)

scala> List("a", "b", "c")
res6: List[String] = List(a, b, c)

scala> List(List(1,2,3), List(4,5,6), List(7,8,9))
res7: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))
```

`xs: List[Int]`というのは、変数`xs`はList[Int]型であるという意味です。要素としてInt型の値を持ってるリストであるということが分かります。

List同士は、`++`で連結できます。連結元のリストの要素が増えるわけではなく、連結した新しいリストをつくって返します。`val`で宣言した変数と同様に、一度つくったリストの中身を書き換えることはできません。このように中身を書き換えることができないオブジェクトのことをイミュータブルであると言います。逆に状態を変えれることをミュータブルなオブジェクトと言います。Scalaでは、イミュータブルなオブジェクトとvalによる変数を使うことで副作用のある操作を制限できるようにしています。

```
scala> val xs = List(1,2,3)
scala> val ys = List(4,5,6)
scala> val zs = xs ++ ys
scala> xs
scala> ys
```

`updated`メソッドを使うと指定した箇所の要素の値を更新できますが、この場合も元のリストを上書きするわけではなく、値を更新した新しいリストを返します。

```
scala> val xs = List(1,2,3)
scala> val ys = xs.updated(2, 1)
scala> xs
```

リストの要素を取得するには`apply`メソッドを使います。`apply`メソッドはちょっと特殊で、`()`のみで`apply`メソッドを呼び出すことができます。

```
scala> xs.apply(2)
res8: Int = 3
scala> xs(2)
res9: Int = 3
```



## cons
リストをつくるには別の方法もあります。その1つが`::`メソッドを使う方法です。

```
scala> Nil.::(3)
res10: List[Int] = List(3)
```

Nilというのは空のリスト表します。空のリストの`::`メソッドを使って要素を増やしました。もう1つ増やしてみます。

```
scala> Nil.::(3).::(2)
res11: List[Int] = List(2,3)
```

また要素が増えました。よく見るとリストの最後ではなく最初に追加されてます。`::`メソッドは最後ではなく先頭に要素を追加します。ちょっと直感的ではないように感じますが通常はこういう風には書かず、もう少し分かりやすく書き直して使います。

Scalaではメソッド名の末尾が:であるメソッドをピリオドを使わずに演算子として使う場合、右結合になります。つまりこう書けます。

```
scala> 2 :: 3 :: Nil
res12: List[Int] = List(2,3)
```

見やすくなりましたね。`::`メソッドは:で終わってるので右結合になり、さっきの`Nil.::(3).::(2)`と全く同じ意味になります。ちなみに`::`をcons演算子と呼びます。

リストの最後に要素を追加したい場合は`:+`メソッドを使います。ただし注意が必要です。理由は後の方で説明しますが、`:+`や`++`は、要素の追加に要する時間がリストのサイズに比例して増えて行きます。リストに要素を1つずつ追加する処理が何回も行われる場合は、逆順のリストに対して`::`で追加し、最後に`reverse`で順番を逆にする方がいいです。



## リストの比較
==, != で比較できます。

```
scala> List(1,2,3) == List(1,2,3)
scala> List(1,2,3) == List(2,3,4)
scala> List(1,2,3) == List(1,2,3,4)
```



## リストのメソッド
Listの要素を先頭の要素を取得するとき`apply`メソッドを使って`xs(0)`と書かなくても`head`メソッドが使えます。ただし注意が必要です。

```
scala> xs.head()
<console>:9: error: Int does not take parameters
              xs.head()
                     ^

scala> xs.head
```

なんと`()`をつけて呼び出すとエラーになってしまいました。引数をとらないメソッドを呼び出すときは`()`を省略できると先ほど言いましたが、今回は**省略しないといけない**です。

引数なしのメソッドにおける省略について、以下の2パターンがあることになります。

* `hoge`と書けるし`hoge()`とも書ける
* `hoge`とは書けるが`hoge()`とは書けない

なぜでしょうか？

> なぜその様な特殊なルールになっているか、というと、理想としては
>
> * 副作用を伴わないメソッド(getterみたいなやつ)は括弧を付けない
> * 副作用を伴うメソッドは括弧をつける
>
> という方向にしたいからです。
>
> 単純に定義時に () つけたメソッドは省略不可、定義時に () つけなかったメソッドは () の記述不可 としてしまうと、Java のライブラリを呼び出すときに整合性がつかなくなるので、 Javaとの互換を意識して一見すると妙なルールになっています。

[引用元 : [@gakuzzzz さんのScalaの省略ルール早覚え](https://gist.github.com/gakuzzzz/10104162)]

ということで、Scalaでは副作用のない引数なしのメソッドを呼び出すときは`()`をつけずに呼び出します。逆に副作用のあるメソッドを呼び出すときは`()`をつけます。

`head`だけでなく他のメソッドも使ってみましょう。

```
scala> val xs = List(1,2,3,4,5)
scala> xs.head
scala> xs.tail
scala> xs.isEmpty
scala> xs.length
scala> xs.last
scala> xs.init
scala> xs.take(2)
scala> xs.drop(3)
scala> xs.max
scala> xs.sum
scala> xs.product
scala> xs.contains(3)
scala> xs.contains(10)
scala> xs.reverse
scala> List(List(1,2,3), List(4,5,6), List(7,8,9)).flatten
```



## Range
1から20までのリストをつくります。全部書いてもいいですが、`Int`の`to`メソッドを使うともっとスマートにできます。

```
scala> List(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
res13: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

scala> (1 to 20).toList
res14: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
```

`(1 to 20)`は何を返しているのでしょうか？

```
scala> 1 to 20
res15: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
```

`Inclusive`という型でした。これはRange、つまり値の範囲を表していて、リストのように複数の値を持っています。`Inclusive`も`List`で使った`head`や`tail`、`take`などが使えます。

`List`や`Inclusive`の他にもScalaには複数の値を持つためのデータ構造が色々用意されています。このようなデータ構造をコレクションと呼びます。ほとんど同じように使えますが、内部の実装が異なっておりデータ構造に対して行いたい操作が一番高速になるようなデータ構造を選ぶことが大切です。

こちらのドキュメントが参考になります。[Collections - 性能特性 - Scala Documentation](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)

Listの内部構造やその他のコレクションについてはそのうち詳しくやります。

レンジをもうちょっと使ってみましょう。1文字を表す`Char`にも`to`メソッドがあります。

```
scala> 2 to 20 by 2
res16: scala.collection.immutable.Range = Range(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

scala> 10 to 0 by -1
res17: scala.collection.immutable.Range = Range(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)

scala> (13 to 13 * 24 by 13).toList
res18: List[Int] = List(13, 26, 39, 52, 65, 78, 91, 104, 117, 130, 143, 156, 169, 182, 195, 208, 221, 234, 247, 260, 273, 286, 299, 312)

scala> 'a' to 'z'
res19: scala.collection.immutable.NumericRange.Inclusive[Char] = NumericRange(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z)
```



## for式
for式を使うとコレクションに対する繰り返し処理を行うことができます。

コレクションの変換、あるコレクションから別のコレクションをつくることはよくありますよね。1から10までのコレクションから、各要素を2倍したコレクションをつくってみましょう。

```
scala> val xs = 1 to 10
xs: scala.collection.immutable.Range.Inclusive = Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

scala> val ys = for(x <- xs) yield x * 2
ys: scala.collection.immutable.IndexedSeq[Int] = Vector(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)
```

型が`IndexedSeq[Int]`となっていますが、これもコレクションの一種です。今は深く追わないことにします。

`x <- xs`は、「xsから取り出した各要素の値をxが受け取る」という意味です。`x <- xs`の部分をジェネレータと呼びます。

`yield x * 2`の部分はfor式の出力になります。取り出した各要素をどうやって変換するかを指定します。上の例では、各要素を2倍する、と指定しています。

for式にフィルタを追加することもできます。フィルタを使うことでジェネレータで生成する要素の数を減らすことができます。フィルタの条件を満たす値だけを元のコレクションから取り出すことができるのです。

フィルタはジェネレータのところにifを使って書きます。このifは先ほどやったif式とは異なるものです。ジェネレータのところに書いたifはあくまでフィルタの役目で、条件分岐ではありません。

さっきの例に、2倍した結果が12より大きくなるものだけを取り出すようにしてみます。

```
scala> for (x <- 1 to 10 if x * 2 > 12) yield x * 2
res20: scala.collection.immutable.IndexedSeq[Int] = Vector(14, 16, 18, 20)
```

複数のコレクションから値を取り出すこともできます。2重ループに相当する処理ですね。

```
scala> for (x <- List(1,2,3); y <- List(10, 100, 1000)) yield x + y
res21: List[Int] = List(11, 101, 1001, 12, 102, 1002, 13, 103, 1003)
```

横に長くなるのが気になるのであれば、改行しても良いです。

```
scala> for (x <- List(1,2,3);
     |      y <- List(10, 100, 1000))
     |   yield x + y
res22: List[Int] = List(11, 101, 1001, 12, 102, 1002, 13, 103, 1003)
```

コレクションの変換ではなく、コレクションの各要素に対する繰り返し処理でもfor式は使えます。例えば各要素をコンソールに出力するような処理です。この場合のようにfor式が値を返す必要がない場合、`yield`は不要です。for式は`()`を返します。

```
scala> for (s <- List("Apple", "Banana")) {
     |   println(s)
     | }
Apple
Banana
```



## タプル
コレクションは1つの型の値を複数格納するのに便利。タプルは複数の違う型の値を格納するのに便利です。また、任意の数を格納できるコレクションとは異なり、タプルは要素数が固定になります。

```
scala> val pair1 = (1, 3)
pair1: (Int, Int) = (1,3)

scala> val pair2 = (1, 'a')
pair2: (Int, Char) = (1,a)
```

`pair1`と`pair2`は両方と要素数が2のタプルであることには変わりはないですが、型は`(Int, Int)`と`(Int, Char)`で異なっています。

タプルはメソッドから複数の値を返すのに便利です。またシンプルにデータを表現するのにも便利です。

例えば、2次元ベクトルを表すのにリストを使ってしまうと、`List(1, 2)`と`List(4,2,3)`とが同じ`List[Int]`型になってしまい、2次元ベクトルと3次元ベクトルの違いが分かりません。タプルを使えば、`(1, 2)`と`(4, 2, 3)`とは、型が`(Int, Int)`と`(Int, Int, Int)`になり異なるため取り扱いを間違えることはありません。

```
scala> val vector1: List[Int] = List(1, 2)
scala> val vector2: List[Int] = List(4, 2, 3)

scala> val vector1: (Int, Int) = (1, 2)
scala> val vector2: (Int, Int) = (4, 2, 3)
<console>:7: error: type mismatch;
 found   : (Int, Int, Int)
 required: (Int, Int)
       val vector2: (Int, Int) = (4, 2, 3)
                                 ^
```

要素を取得するときは`_1`です。

```
scala> (1, 3)._1
res23: Int = 1

scala> (1, 3, 7, 9)._3
res24: Int = 7
```

要素数が2のタプルをペアと呼んだりもします。ペアは2つのリストをくっつけてペアのリストにしたり、リストに添字をつけてペアのリストにしたり、という場面でも便利です。

```
scala> List(5, 5, 6, 6, 7, 7) zip List("abc", "def", "ghi", "jkl", "mno", "pqr")
res25: List[(Int, String)] = List((5,abc), (5,def), (6,ghi), (6,jkl), (7,mno), (7,pqr))

scala> List("abc", "def", "ghi", "jkl", "mno", "pqr").zipWithIndex
res26: List[(String, Int)] = List((abc,0), (def,1), (ghi,2), (jkl,3), (mno,4), (pqr,5))
```



## メソッド
これまでIntやString, Listのメソッドを呼び出して使ってきました。REPL上で自分で新しくメソッドを定義することができるます。

メソッドというのはオブジェクトに属するものです。オブジェクトなしで単独では存在できません。

しかし、REPLでは特別に何らかのオブジェクトに属する形じゃなくてもメソッドを定義できます（REPLに属している、というかグローバルなオブジェクトに属してると考えてもいいかも・・・）。

では定義してみましょう。引数に指定した数値を2倍にして返すメソッドです。メソッドを定義するときはdefキーワードを使います。続いてメソッド名、()の中に引数、返り値の型、=、メソッドの本体です。

```
scala> def twice(x: Int): Int = x * 2
twice: (x: Int)Int

scala> twice(10)
res27: Int = 20
```

メソッド本体が複数行になる場合は{}で囲みます。最後の行の結果がメソッドの結果になります。明示的にreturnのようなものを書く必要はありません。

```
scala> def plusOneTwice(x: Int): Int = {
     |   val n = x + 1
     |   n * 2
     | }
plusOneTwice: (x: Int)Int

scala> plusOneTwice(10)
res28: Int = 22
```

メソッドが副作用を起こす場合、返り値の型がUnitになります。Unitを返すメソッドは慣例として返り値の型と`=`は省略して書きます。

```
scala> def printTwice(x: Int) {
     |   println(x * 2)
     | }
printTwice: (x: Int)Unit

scala> printTwice(10)
20
```

副作用を起こす処理とそうではない処理は別のメソッドとして、メソッドの返り値の型で副作用を起こすメソッドなのかどうかが判断できるようにしておくことが大事です。副作用を起こす処理が局所化されていると、メソッドを使う側としてはプログラミングが簡単になりますし、テストもしやすくなります。

副作用を起こさず、かつ、引数をとらない場合、引数リストを省略しましょう。`()`つきで呼び出すことができなくなるので、副作用が起こさないメソッドを呼び出していると後でコードを呼んだときに分かりやすくなります。

```
scala> def twiceTen: Int = 10 * 2
twiceTen: Int

scala> twiceTen
res29: Int = 20

scala> twiceTen()
<console>:9: error: Int does not take parameters
              twiceTen()
                      ^
```

ここでファイルに定義したScalaコードをREPLに読み込んでみましょう。negate.scalaというファイルをつくり、以下を書いてください。

```
def negate(x: Int): Int = - x
```

これをREPLに読み込むには、`scala> :load`を使います。

```
scala> :load negate.scala
scala> negate(15)
res30 : Int = -15
```

negateメソッドをちょっと書き換えてみます。

```
def negate(x: Int): Int = "hogehoge"
```

返り値がIntのメソッドで"hogehoge"というStringを返すようにしてみました。

```
scala> :load negate.scala
Loading negate.scala...
<console>:7: error: type mismatch;
 found   : String("hogehoge")
 required: Int
       def negate(x: Int): Int = "hogehoge"
                                 ^
```

エラーになりました。Scalaは静的型づけなので、メソッドの実行時ではなく、コードをREPLに読み込んだ時点で型によるエラーを検知できます。REPLを使用していない場合はコンパイル時にエラーを検出できます。これは実行時より早くエラーを検出できることに加えて、型によるエラーがないことをコンパイラが保証してくれることになります。この例はシンプル過ぎてありがたみがないですが、大きなコードになっても同じようにコンパイラがチェックしてくれるのは大変助かります。コップ本にはこう書いてあります。

> 静的型システムは単体テストの代わりになるわけではないが、プログラムの性質を確認するために普通なら必要だった単体テストの数を減らしてくれる。さらに、単体テストは静的型付けの代わりにはならない。Edsger Dijkstra（エドガー・ダイクストラ）が言ったように、テストが証明できるのはエラー存在であって、エラーの不在ではない。静的型付けが与えてくれる保証は単純なものかもしれないが、どれだけテストをしたとしても得られない本物の保証なのである。

[引用: [Scalaスケーラブルプログラミング](http://www.amazon.co.jp/Scala%E3%82%B9%E3%82%B1%E3%83%BC%E3%83%A9%E3%83%96%E3%83%AB%E3%83%97%E3%83%AD%E3%82%B0%E3%83%A9%E3%83%9F%E3%83%B3%E3%82%B0%E7%AC%AC2%E7%89%88-Martin-Odersky/dp/4844330845)]



## 練習問題
1. List(1,2,3)とList(10,100,1000)の各要素を掛け合わせた結果からなるコレクションをつくるfor式を書いてください
2. 掛け合わせた結果が50以上になるコレクションをつくるfor式を書いてください。
3. 100までの偶数の中で、7で割った余りが3になる数値からなるコレクションをつくってください。
4. List("Apple", "Banana", "Pineapple", "Lemon") から、各要素の頭文字からなるコレクションをつくってください。
5. FizzBuzzを書いてください。
6. 要素数の異なるリストを`zip`でつなげるとどうなりますか？
7. 次のすべての条件を満たす直角三角形を見つけるメソッドを書いてください。直角三角形はタプルで表現してください。
    * 3辺の長さはすべて整数である
    * 各辺それぞれの長さはメソッドの引数で渡された数値以下である
    * 周囲の長さは24に等しい

ヒント：まずは各辺が10以下になるすべての三角形をつくってみましょう。その後、直角三角形となる条件、ピタゴラスの定理が成り立つ三角形だけを抽出してみましょう。

※ 7つめの問題は、[すごいHaskellたのしく学ぼう!](http://www.amazon.co.jp/%E3%81%99%E3%81%94%E3%81%84Haskell%E3%81%9F%E3%81%AE%E3%81%97%E3%81%8F%E5%AD%A6%E3%81%BC%E3%81%86-Miran-Lipova%C4%8Da/dp/4274068854)からの引用しました。



## 今日出てきたキーワード

* REPL
* val
* 型
* イミュータブル
* if式
* `()` Unit型
* 副作用
* 型推論
* リスト
* `::` cons
* Nil
* Range
* for式
* ジェネレータ
* yield
* コレクション
* タプル
* メソッド
* 静的型付け



## 要注意ポイント

* 全ての値がオブジェクト
* 演算子もメソッド
* メソッド呼び出しの `.` は省略できる
* 引数が1つのメソッドの場合、メソッド呼び出しの `()` は省略できる
* メソッド名の末尾が : の場合、演算子として使うと右結合になる
* Listに対する末尾への要素追加は、リストサイズに比例した時間がかかる
* `apply`メソッドは省略して`()`のみで呼び出すことができる
* 引数なしで副作用がないメソッドを呼ぶ場合、`()`はつけない
* 引数なしで副作用がないメソッドを定義する場合、`()`はつけない
* 副作用のある・なしを意識してメソッドを定義することを心がける



## 参考資料

* [String (Java Platform SE 8)](http://docs.oracle.com/javase/jp/8/api/java/lang/String.html)
* [Scalaの省略ルール早覚え](https://gist.github.com/gakuzzzz/10104162)
* [Collections - 性能特性 - Scala Documentation](http://docs.scala-lang.org/ja/overviews/collections/performance-characteristics.html)

