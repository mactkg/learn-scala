val answer = for(i <- 1 to 100) yield {
  if(i%3+i%5 == 0) "FizzBuzz"
  else if(i%3 == 0) "Fizz"
  else if(i%5 == 0) "Buzz"
  else i.toString
}

println(answer)
