package skan.ui

import skan.*
import skan.TestData.*
import Util.*

import tui.*

class BoardUiSuite extends munit.FunSuite:
  test("basic-board-todo"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromItems(defaultItems),
        "b" -> BoardState.fromItems(secondaryItems)
      ),
      activeContext = "a"
    )

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a │ b                                                                    │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-1/3───────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │IMPORTANT                 2023-04-12││URGENT                    2023-04-12│  ",
      "  │Here is an Important issue          ││An urgent issue with no descripti...│  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │NORMAL                    2023-04-11││                                    │  ",
      "  │Here is a normal one                ││                                    │  ",
      "  │Some description                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││                                    │  ",
      "  │Here is a low one                   ││                                    │  ",
      "  │Some lowly description              ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config)

  test("basic-board-by-date"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromItems(defaultItems),
        "b" -> BoardState.fromItems(secondaryItems)
      ),
      activeContext = "a"
    )

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a │ b                                                                    │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-1/3───────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │NORMAL                    2023-04-11││URGENT                    2023-04-12│  ",
      "  │Here is a normal one                ││An urgent issue with no descripti...│  ",
      "  │Some description                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││                                    │  ",
      "  │Here is a low one                   ││                                    │  ",
      "  │Some lowly description              ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │IMPORTANT                 2023-04-12││                                    │  ",
      "  │Here is an Important issue          ││                                    │  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config.copy(boardOrder = Order.date))

  test("basic-board-in-progress"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromItems(defaultItems),
        "b" -> BoardState.fromItems(secondaryItems)
      ),
      activeContext = "a"
    )
    val switched = state.switchColumn()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a │ b                                                                    │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs───────────────────────────────┐┌In Progress-1/1─────────────────────┐  ",
      "  │IMPORTANT                 2023-04-12││URGENT                    2023-04-12│  ",
      "  │Here is an Important issue          ││An urgent issue with no descripti...│  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │NORMAL                    2023-04-11││                                    │  ",
      "  │Here is a normal one                ││                                    │  ",
      "  │Some description                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││                                    │  ",
      "  │Here is a low one                   ││                                    │  ",
      "  │Some lowly description              ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(switched, expected, config)

  test("basic-board-progress"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems)),
      activeContext = "a"
    )

    state.progress()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a                                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-1/2───────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │IMPORTANT                 2023-04-12││URGENT                    2023-04-12│  ",
      "  │Here is an Important issue          ││An urgent issue with no descripti...│  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││NORMAL                    2023-04-11│  ",
      "  │Here is a low one                   ││Here is a normal one                │  ",
      "  │Some lowly description              ││Some description                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config)

  test("basic-board-progress-single"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems.slice(0, 1))),
      activeContext = "a"
    )
    state.progress()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a                                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││NORMAL                    2023-04-11│  ",
      "  │                                    ││Here is a normal one                │  ",
      "  │                                    ││Some description                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config)

  test("basic-board-back"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems)),
      activeContext = "a"
    )

    val inProgress = state.switchColumn()
    inProgress.moveBack()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a                                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs───────────────────────────────┐┌In Progress-0───────────────────────┐  ",
      "  │URGENT                    2023-04-12││                                    │  ",
      "  │An urgent issue with no descripti...││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │IMPORTANT                 2023-04-12││                                    │  ",
      "  │Here is an Important issue          ││                                    │  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │NORMAL                    2023-04-11││                                    │  ",
      "  │Here is a normal one                ││                                    │  ",
      "  │Some description                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││                                    │  ",
      "  │Here is a low one                   ││                                    │  ",
      "  │Some lowly description              ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(inProgress, expected, config)

  test("empty-board-basic"):
    val state = ContextState(
      boards = Map("empty" -> BoardState.fromItems(Vector.empty)),
      activeContext = "empty"
    )

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ empty                                                                    │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config)

  test("empty-board-stay-0"):
    val state = ContextState(
      boards = Map("empty" -> BoardState.fromItems(Vector.empty)),
      activeContext = "empty"
    )
    // Calling next here shouldn't change the 0 after TODOs
    state.next()
    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ empty                                                                    │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(state, expected, config)

  test("basic-board-delete"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems)),
      activeContext = "a"
    )
    val newState = state.deleteItem()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a                                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-1/2───────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │IMPORTANT                 2023-04-12││URGENT                    2023-04-12│  ",
      "  │Here is an Important issue          ││An urgent issue with no descripti...│  ",
      "  │Short description                   ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │LOW                       2023-04-12││                                    │  ",
      "  │Here is a low one                   ││                                    │  ",
      "  │Some lowly description              ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(newState, expected, config)

  test("basic-board-delete-single"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems.slice(0, 1))),
      activeContext = "a"
    )
    val first = state.deleteItem()
    val second = first.deleteItem()

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a                                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(second, expected, config)

  test("add-context"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromItems(defaultItems.slice(0, 1))),
      activeContext = "a"
    )

    val updated = state.addContext("a-new-context")

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ a │ a-new-context                                                        │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(updated, expected, config)

  test("delete-context"):
    val state = ContextState(
      boards = Map(
        "deleteMe" -> BoardState.fromItems(Vector.empty),
        "keep" -> BoardState.fromItems(Vector.empty)
      ),
      activeContext = "deleteMe"
    )
    val newState = state.deleteContext(config)

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ keep                                                                     │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-0─────────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(newState, expected, config)

  test("update-context"):
    val renameMe = "rename-me"
    val newConfg = Config(dataDir = os.temp.dir())
    os.write(newConfg.dataDir / s"${renameMe}.json", "[]")
    val state = ContextState(
      boards =
        Map("rename-me" -> BoardState.fromItems(defaultItems.slice(0, 1))),
      activeContext = "rename-me"
    )

    val updated = state.renameContext("renamed-context", newConfg)

    val expected = Buffer.withLines(
      "                                                                                ",
      "                                                                                ",
      "                                      skan                                      ",
      versionLine(80),
      "  ┌Contexts──────────────────────────────────────────────────────────────────┐  ",
      "  │ renamed-context                                                          │  ",
      "  └──────────────────────────────────────────────────────────────────────────┘  ",
      "  ┌TODOs-1/1───────────────────────────┐┌In Progress─────────────────────────┐  ",
      "  │NORMAL                    2023-04-11││                                    │  ",
      "  │Here is a normal one                ││                                    │  ",
      "  │Some description                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  │                                    ││                                    │  ",
      "  └────────────────────────────────────┘└────────────────────────────────────┘  ",
      "  j (↓) | k (↑) | h (←) | l (→) | ENTER (progress) | BACKSPACE (move back) | n  ",
      "  (new) | c (context menu) | TAB (switch context) | q (quit) | x (delete)       ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    checkUi(updated, expected, newConfg)
end BoardUiSuite
