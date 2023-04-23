package skan.ui

import tui.*

import skan.NewItemState
import skan.InputMode
import Util.*

class NewItemUiSuite extends munit.FunSuite:
  test("basic-input-fresh"):
    val inputState = NewItemState.fresh()
    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Title───────────────────────────────────────────────────────────────────┐   ",
      "   │                                                                        │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Description─────────────────────────────────────────────────────────────┐   ",
      "   │                                                                        │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Priority────────────────────────────────────────────────────────────────┐   ",
      "   │ LOW │ NORMAL │ IMPORTANT │ URGENT                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ENTER (next) | ESC (stop editing)                                            ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkInputUi(inputState, expected)

  test("basic-input-normal"):
    val inputState = NewItemState.fresh()
    val newState = inputState.switchInputMode()
    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Title───────────────────────────────────────────────────────────────────┐   ",
      "   │                                                                        │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Description─────────────────────────────────────────────────────────────┐   ",
      "   │                                                                        │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Priority────────────────────────────────────────────────────────────────┐   ",
      "   │ LOW │ NORMAL │ IMPORTANT │ URGENT                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   i (edit) | q (exit)                                                          ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkInputUi(newState, expected)

  test("basic-input-filled"):
    val inputState = NewItemState
      .fresh()
      .copy(
        title = "Some title",
        description = "Some description",
        inputMode = InputMode.Input
      )

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Title───────────────────────────────────────────────────────────────────┐   ",
      "   │Some title                                                              │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Description─────────────────────────────────────────────────────────────┐   ",
      "   │Some description                                                        │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Priority────────────────────────────────────────────────────────────────┐   ",
      "   │ LOW │ NORMAL │ IMPORTANT │ URGENT                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ENTER (next) | ESC (stop editing)                                            ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkInputUi(inputState, expected)
end NewItemUiSuite
