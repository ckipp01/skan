class InputStateSuite extends munit.FunSuite:
  test("can-be-initialized"):
    val state = InputState.fresh()
    assertEquals(state.title, "")
    assertEquals(state.description, "")
    assertEquals(state.inputMode, InputMode.Normal)
    assertEquals(state.priority, Priority.NORMAL)
    assertEquals(state.focusedInput, InputSection.Title)

  test("can-switch-inputmode"):
    val state = InputState.fresh()
    assertEquals(state.inputMode, InputMode.Normal)
    val swapped = state.switchInputMode()
    assertEquals(swapped.inputMode, InputMode.Input)
    val backToNormal = swapped.switchInputMode()
    assertEquals(backToNormal.inputMode, InputMode.Normal)

  test("can-focus-next"):
    val state = InputState.fresh()
    assertEquals(state.focusedInput, InputSection.Title)
    val focusedDescription = state.focusNext()
    assertEquals(focusedDescription.focusedInput, InputSection.Description)
    val focusedPriority = focusedDescription.focusNext()
    assertEquals(focusedPriority.focusedInput, InputSection.Priority)
    val backToTitle = focusedPriority.focusNext()
    assertEquals(backToTitle.focusedInput, InputSection.Title)
