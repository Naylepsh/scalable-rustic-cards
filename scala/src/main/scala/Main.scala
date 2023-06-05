import cats.data.NonEmptyList
@main def hello: Unit =
  val ids = NonEmptyList.fromListUnsafe((1 to 4).toList)
  val result = War.run(1000)(ids)
  println(result)
