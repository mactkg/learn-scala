// 1のコレクションの中で掛けあわせた結果が50以上という意味らしい
val answer = for(x <- List(1, 2, 3);
                 y <- List(10, 100, 1000);
                 if x*y >= 50)
               yield x*y
println(answer);
