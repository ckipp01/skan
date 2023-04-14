class InputStateSuite extends munit.FunSuite:
  test("can-be-initialized"):
    val inputState = InputState.fresh()
    assertEquals(inputState.title, "")
    assertEquals(inputState.description, "")
    assertEquals(inputState.inputMode, InputMode.Normal)
    assertEquals(inputState.priority, Priority.NORMAL)
    assertEquals(inputState.focusedInput, InputSection.Title)
