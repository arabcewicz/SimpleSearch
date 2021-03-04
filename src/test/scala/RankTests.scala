import Index._

class RankTests extends TestSuite {
  test("result string generation") {
    Rank(0).toResultString(100) shouldBe "0%"
    Rank(1).toResultString(1) shouldBe "100%"
    Rank(1).toResultString(2) shouldBe "50%"
    Rank(1).toResultString(3) shouldBe "33%"
  }
}
