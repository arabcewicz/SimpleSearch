import Index._

class RankTests extends TestSuite {
  test("rank's result string should properly calculate relative value with base") {
    Rank(0).toResultString(100) shouldBe "0%"
    Rank(1).toResultString(1) shouldBe "100%"
    Rank(1).toResultString(2) shouldBe "50%"
    Rank(1).toResultString(3) shouldBe "33%"
  }
}
