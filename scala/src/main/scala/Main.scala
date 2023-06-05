import cats.data.NonEmptyList

@main def run: Unit =
  val ids    = NonEmptyList.fromListUnsafe((1 to 11).toList)
  val result = War.run(1000)(ids)
  println(result)
