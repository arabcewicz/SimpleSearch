class IndexingTests extends TestSuite {
  test("indexing one 'file'") {
    Index
      .createWithStrings(
        List(
          ("f1", "a the hat on the head")
        )
      )
      .idx shouldBe Map("the" -> Set("f1"), "hat" -> Set("f1"), "on" -> Set("f1"), "head" -> Set("f1"))
  }

  test("indexing two 'files'") {
    Index
      .createWithStrings(
        List(
          ("f1", "a hat on the head"),
          ("f2", "a hat on the bad")
        )
      )
      .idx shouldBe Map(
      "the" -> Set("f1", "f2"),
      "head" -> Set("f1"),
      "bad" -> Set("f2"),
      "hat" -> Set("f1", "f2"),
      "on" -> Set("f1", "f2")
    )
  }

  test("indexing two multiline 'files'") {
    Index
      .createWithStrings(
        List(
          ("f1", "a hat\n on the  \nhead  \n   .\\\""),
          ("f2", "\n\na \nhat on \nthe bad\n")
        )
      )
      .idx shouldBe Map(
      "the" -> Set("f1", "f2"),
      "head" -> Set("f1"),
      "bad" -> Set("f2"),
      "hat" -> Set("f1", "f2"),
      "on" -> Set("f1", "f2")
    )
  }

}
