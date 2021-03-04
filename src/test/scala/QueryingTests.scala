class QueryingTests extends TestSuite {
  test("when no matches then should return empty result") {
    val index = Index.createWithStrings(List(("f1", "a hat on the head"), ("f2", "a hat on the bad")))
    val queryRes = index.query("aaa bbb ccc")
    queryRes shouldBe List()
  }

  test("when there are matches then should return results properly ordered") {
    val index = Index.createWithStrings(List(("f1", "a hat on the head"), ("f2", "a hat on the bad")))
    val queryRes = index.query("hat and bad")
    queryRes shouldBe List("f2 : 67%", "f1 : 33%")
  }

  test("should limit result to 10 items") {
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
      "f12 : 100%",
      "f11 : 100%",
      "f9 : 86%",
      "f10 : 86%",
      "f8 : 86%",
      "f7 : 71%",
      "f6 : 57%",
      "f5 : 43%",
      "f4 : 29%",
      "f3 : 29%"
    )
  }
}
