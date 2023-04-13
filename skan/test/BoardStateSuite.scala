import java.time.Instant
class BoardStateSuite extends munit.FunSuite:
  import testData.*

  test("can-be-initialized"):
    val boardState = BoardState.fromData(data)
    assertEquals(boardState.items.size, 5)
    assertEquals(boardState.todoState.selected, Some(0))
    assertEquals(boardState.todoState.offset, 0)
    assertEquals(boardState.inProgressState.selected, None)
    assertEquals(boardState.inProgressState.offset, 0)
    assertEquals(boardState.focusedList, Status.TODO)
    assertEquals(boardState.todoItems().size, 3)
    assertEquals(boardState.inProgressItems().size, 1)

  test("can-switch-view"):
    val boardState = BoardState.fromData(data)
    val newState = boardState.switchView()
    assertEquals(newState.todoState.selected, None)
    assertEquals(newState.inProgressState.selected, Some(0))
    assertEquals(newState.focusedList, Status.INPROGRESS)

    val backAgain = newState.switchView()
    assertEquals(backAgain.todoState.selected, Some(0))
    assertEquals(backAgain.inProgressState.selected, None)
    assertEquals(backAgain.focusedList, Status.TODO)

  test("can-next"):
    val boardState = BoardState.fromData(data)
    boardState.next()
    assertEquals(boardState.todoState.selected, Some(1))

    val newState = boardState.switchView()
    assertEquals(newState.inProgressState.selected, Some(0))
    newState.next()
    // There is only 1 inProgressItem, so we assume it will stay the same
    assertEquals(newState.inProgressState.selected, Some(0))

  test("can-previous"):
    val boardState = BoardState.fromData(data)
    assertEquals(boardState.todoState.selected, Some(0))
    boardState.previous()
    assertEquals(boardState.todoState.selected, Some(2))

    val newState = boardState.switchView()
    assertEquals(newState.inProgressState.selected, Some(0))
    newState.previous()
    // There is only 1 inProgressItem, so we assume it will stay the same
    assertEquals(newState.inProgressState.selected, Some(0))

  test("can-delete"):
    val boardState = BoardState.fromData(data)
    assertEquals(boardState.items.size, 5)
    assertEquals(boardState.todoItems().size, 3)
    val newState = boardState.delete()
    assertEquals(newState.items.size, 4)
    assertEquals(newState.todoItems().size, 2)

    val tempState = newState.switchView()
    val newerState = tempState.delete()
    assertEquals(newerState.items.size, 3)
    assertEquals(newerState.inProgressItems().size, 0)

  test("new-item"):
    val boardState = BoardState.fromData(data)
    assertEquals(boardState.items.size, 5)
    val newState = boardState.withNewItem(
      DataItem(
        Instant.now(),
        "title",
        "description",
        Status.TODO,
        Priority.NORMAL
      )
    )
    assertEquals(newState.items.size, 6)

end BoardStateSuite
