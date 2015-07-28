val answer = for(x <- List(1, 2, 3);
                 y <- List(10, 100, 1000))
               yield x*y
println(answer)
