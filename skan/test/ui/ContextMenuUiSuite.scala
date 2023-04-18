package skan.ui

import tui.*
import Util.*

import skan.BoardState
import skan.ContextState
import skan.MyListWidget

class ContextMenuUiSuite extends munit.FunSuite:
  test("basic-context"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromItems(Vector.empty),
        "b" -> BoardState.fromItems(Vector.empty)
      ),
      activeContext = "a"
    )
    val menuState = MyListWidget.State(selected = Some(0))

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Choose an action────────────────────────────────────────────────────────┐   ",
      "   │- Edit current context                                                  │   ",
      "   │- Delete current context                                                │   ",
      "   │- Create new context                                                    │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Current context─────────────────────────────────────────────────────────┐   ",
      "   │a                                                                       │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌All contexts────────────────────────────────────────────────────────────┐   ",
      "   │- a                                                                     │   ",
      "   │- b                                                                     │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   j (↓) | k (↑) | q (quit) | ENTER (select)                                    ",
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
    checkContextMenuUi(state, menuState, expected)

  test("edit-context"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromItems(Vector.empty)
      ),
      activeContext = "a"
    )

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌New context name────────────────────────────────────────────────────────┐   ",
      "   │a                                                                       │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌Current context─────────────────────────────────────────────────────────┐   ",
      "   │a                                                                       │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌All contexts────────────────────────────────────────────────────────────┐   ",
      "   │- a                                                                     │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ENTER (accept) | ESC (quit)                                                  ",
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
    checkEditContextUi(state, "a", expected)
end ContextMenuUiSuite
