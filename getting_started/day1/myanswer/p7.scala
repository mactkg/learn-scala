def getRightTriangle(l: Int): List[(Int, Int, Int)] = {
  val tmp = for(l1 <- 1 to l;
                l2 <- 1 to l1;
                l3 <- 1 to l2;
                if l1+l2+l3 == 24 && (l2*l2+l3*l3==l1*l1))
              yield (l1, l2, l3)
  tmp.toList
}
println(getRightTriangle(10))
