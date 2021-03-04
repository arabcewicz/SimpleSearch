import Index._

class QueryingTests extends TestSuite {
  test("no results") {
    val index = Index.createWithStrings(List(("f1", "a hat on the head"), ("f2", "a hat on the bad")))
    val queryRes = index.query("aaa bbb ccc")
    queryRes shouldBe List()
  }

  test("basic test - proper ordering") {
    val index = Index.createWithStrings(List(("f1", "a hat on the head"), ("f2", "a hat on the bad")))
    val queryRes = index.query("hat and bad")
    queryRes shouldBe List(ResultItem("f2", Rank(2)), ResultItem("f1", Rank(1)))
  }

  test("only 10 result items") {
    val index = Index.createWithStrings(
      List(
        ("f1", "aa"),
        ("f2", "aa bb"),
        ("f3", "aa bb cc"),
        ("f4", "aa bb cc dd"),
        ("f5", "aa bb cc dd ee"),
        ("f6", "aa bb cc dd ee ff"),
        ("f7", "aa bb cc dd ee ff gg"),
        ("f8", "aa bb cc dd ee ff gg hh"),
        ("f9", "aa bb cc dd ee ff gg hh ii"),
        ("f10", "aa bb cc dd ee ff gg hh ii jj"),
        ("f11", "aa bb cc dd ee ff gg hh ii jj kk"),
        ("f12", "aa bb cc dd ee ff gg hh ii jj kk ll")
      )
    )
    val queryRes = index.query("aa cc ee ff gg hh kk")
    queryRes shouldBe List(
      ResultItem("f12", Rank(7)),
      ResultItem("f11", Rank(7)),
      ResultItem("f9", Rank(6)),
      ResultItem("f10", Rank(6)),
      ResultItem("f8", Rank(6)),
      ResultItem("f7", Rank(5)),
      ResultItem("f6", Rank(4)),
      ResultItem("f5", Rank(3)),
      ResultItem("f4", Rank(2)),
      ResultItem("f3", Rank(2))
    )
  }
}
