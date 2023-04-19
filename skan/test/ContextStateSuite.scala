package skan

class ContextStateSuite extends munit.FunSuite:
  import TestData.*

  val contextState = ContextState.fromConfig(config)

  test("can-be-initialized"):
    assertEquals(contextState.boards.size, 2)
    assertEquals(contextState.activeContext, "a")

  test("keys-are-sorted"):
    assertEquals(contextState.sortedKeys, Vector("a", "b"))

  test("can-switch-context"):
    assertEquals(contextState.activeContext, "a")
    val newState = contextState.switchContext()
    assertEquals(newState.activeContext, "b")

  test("can-add-context"):
    val newContext = contextState.addContext("test")
    val expectedContext = contextState.copy(
      boards = contextState.boards
        .updated("test", BoardState.fromItems(Vector.empty)),
      activeContext = "test"
    )

    assertEquals(newContext.activeContext, expectedContext.activeContext)
    assertEquals(newContext.sortedKeys, expectedContext.sortedKeys)

  test("can-remove-context"):
    val newContext = contextState.addContext("test")
    assertEquals(newContext.activeContext, "test")
    assertEquals(newContext.sortedKeys.size, 3)
    val deleted = newContext.deleteContext(config)
    assertEquals(deleted.activeContext, "a")
    assertEquals(deleted.sortedKeys.size, 2)
end ContextStateSuite
