class WordsExtractionTests extends TestSuite {
  test("empty/blank lines should not contain words") {
    Index.extractWords("").toList shouldBe List()
    Index.extractWords("   ").toList shouldBe List()
  }

  test("'normal' words should be extracted") {
    Index.extractWords("abc").toList shouldBe List("abc")
    Index.extractWords("Abc").toList shouldBe List("abc")
    Index.extractWords("aBcdaf").toList shouldBe List("abcdaf")
    Index.extractWords("123").toList shouldBe List("123")
    Index.extractWords("AAA").toList shouldBe List("aaa")
  }

  test("words has to be at least of length 2") {
    Index.extractWords("x").toList shouldBe List()
    Index.extractWords("A").toList shouldBe List()
    Index.extractWords("6").toList shouldBe List()
  }

  test("words with specials should be properly extracted") {
    Index.extractWords("abc_").toList shouldBe List("abc")
    Index.extractWords("(16)").toList shouldBe List("16")
    Index.extractWords("16$").toList shouldBe List("16")
    Index.extractWords("stop.").toList shouldBe List("stop")
  }

  test("strings that are not words should not be extracted") {
    Index.extractWords("!!").toList shouldBe List()
    Index.extractWords("x@d?").toList shouldBe List()
    Index.extractWords("__c__").toList shouldBe List()
    Index.extractWords("7*9").toList shouldBe List()
  }

  test("words should be properly extracted from line") {
    Index.extractWords("a hat on the head").toList shouldBe List("hat", "on", "the", "head")
    Index.extractWords("     SOFTWARE | Wildcat ").toList shouldBe List("software", "wildcat")
    Index.extractWords("From @MIT-MC:Telecom-Request@MIT-MC  Fri Aug  3 16:02:38 1984").toList shouldBe
      List("from", "mit", "mc", "telecom", "request", "mit", "mc", "fri", "aug", "16", "02", "38", "1984")
  }
}
