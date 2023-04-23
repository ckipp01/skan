package skan

class NewItemStateSuite extends munit.FunSuite:
  test("can-be-initialized"):
    val state = NewItemState.fresh()
    assertEquals(state.title, "")
    assertEquals(state.description, "")
    assertEquals(state.inputMode, InputMode.Input)
    assertEquals(state.priority, Priority.NORMAL)
    assertEquals(state.focusedInput, InputSection.Title)

  test("can-switch-inputmode"):
    val state = NewItemState.fresh()
    assertEquals(state.inputMode, InputMode.Input)
    val swapped = state.switchInputMode()
    assertEquals(swapped.inputMode, InputMode.Normal)
    val backToInput = swapped.switchInputMode()
    assertEquals(backToInput.inputMode, InputMode.Input)

  test("can-focus-next"):
    val state = NewItemState.fresh()
    assertEquals(state.focusedInput, InputSection.Title)
    val focusedDescription = state.focusNext()
    assertEquals(focusedDescription.focusedInput, InputSection.Description)
    val focusedPriority = focusedDescription.focusNext()
    assertEquals(focusedPriority.focusedInput, InputSection.Priority)
    val backToTitle = focusedPriority.focusNext()
    assertEquals(backToTitle.focusedInput, InputSection.Title)
