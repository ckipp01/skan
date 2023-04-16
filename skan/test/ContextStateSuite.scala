package skan

class ContextStateSuite extends munit.FunSuite:
  import testData.*

  test("can-be-initialized"):
    assertEquals(contextState.boards.size, 2)
    assertEquals(contextState.activeContext, "a")

  test("keys-are-sorted"):
    assertEquals(contextState.sortedKeys, Vector("a", "b"))

  test("can-switch-context"):
    assertEquals(contextState.activeContext, "a")
    val newState = contextState.switchContext()
    assertEquals(newState.activeContext, "b")
