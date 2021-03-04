class WordsExtractionTests extends TestSuite {
  test("word extraction - empty lines") {
    Index.extractWords("").toList shouldBe List()
    Index.extractWords("   ").toList shouldBe List()
  }

  test("words extraction - 'normal' words") {
    Index.extractWords("abc").toList shouldBe List("abc")
    Index.extractWords("Abc").toList shouldBe List("abc")
    Index.extractWords("aBcdaf").toList shouldBe List("abcdaf")
    Index.extractWords("123").toList shouldBe List("123")
    Index.extractWords("AAA").toList shouldBe List("aaa")
  }

  test("words extraction - word has to be at least of length 2") {
    Index.extractWords("x").toList shouldBe List()
    Index.extractWords("A").toList shouldBe List()
    Index.extractWords("6").toList shouldBe List()
  }

  test("words extraction - words with specials") {
    Index.extractWords("abc_").toList shouldBe List("abc")
    Index.extractWords("(16)").toList shouldBe List("16")
    Index.extractWords("16$").toList shouldBe List("16")
    Index.extractWords("stop.").toList shouldBe List("stop")
  }

  test("words extraction - these are not words") {
    Index.extractWords("!!").toList shouldBe List()
    Index.extractWords("x@d?").toList shouldBe List()
    Index.extractWords("__c__").toList shouldBe List()
    Index.extractWords("7*9").toList shouldBe List()
  }

  test("words extraction - line") {
    Index.extractWords("a hat on the head").toList shouldBe List("hat", "on", "the", "head")
    Index.extractWords("     SOFTWARE | Wildcat ").toList shouldBe List("software", "wildcat")
    Index.extractWords("From @MIT-MC:Telecom-Request@MIT-MC  Fri Aug  3 16:02:38 1984").toList shouldBe
      List("from", "mit", "mc", "telecom", "request", "mit", "mc", "fri", "aug", "16", "02", "38", "1984")
  }
}
