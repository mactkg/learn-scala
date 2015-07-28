val answer = for(i <- (1 to 100).toList if i%7 == 3) yield i
println(answer)

val answer2 = (3 to 100 by 7).toList
println(answer2)
