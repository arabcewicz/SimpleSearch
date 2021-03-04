import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

trait TestSuite
    extends AnyFunSuite
    with should.Matchers
    with GivenWhenThen
    with BeforeAndAfterAll
    with BeforeAndAfterEach
