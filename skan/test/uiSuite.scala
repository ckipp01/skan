package skan

import tui.*
import testData.*
import util.*

class uiSuite extends munit.FunSuite:

  test("basic-board-todo"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromData(defaultItems),
        "b" -> BoardState.fromData(secondaryItems)
      ),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      ui.renderBoard(frame, state, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a │ b                                                                  │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-1/3──────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │NORMAL                   2023-04-12││URGENT                   2023-04-12│   ",
      "   │Here is a normal one               ││An urgent issue with no descript...│   ",
      "   │Some description                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │LOW                      2023-04-12││                                   │   ",
      "   │Here is a low one                  ││                                   │   ",
      "   │Some lowly description             ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │IMPORTANT                2023-04-12││                                   │   ",
      "   │Here is an Important issue         ││                                   │   ",
      "   │Short description                  ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-in-progress"):
    val state = ContextState(
      boards = Map(
        "a" -> BoardState.fromData(defaultItems),
        "b" -> BoardState.fromData(secondaryItems)
      ),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)
    val switched = state.switchColumn()

    terminal.draw: frame =>
      ui.renderBoard(frame, switched, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a │ b                                                                  │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs──────────────────────────────┐┌In Progress-1/1────────────────────┐   ",
      "   │NORMAL                   2023-04-12││URGENT                   2023-04-12│   ",
      "   │Here is a normal one               ││An urgent issue with no descript...│   ",
      "   │Some description                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │LOW                      2023-04-12││                                   │   ",
      "   │Here is a low one                  ││                                   │   ",
      "   │Some lowly description             ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │IMPORTANT                2023-04-12││                                   │   ",
      "   │Here is an Important issue         ││                                   │   ",
      "   │Short description                  ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-progress"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromData(defaultItems)),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    state.progress()

    terminal.draw: frame =>
      ui.renderBoard(frame, state, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a                                                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-1/2──────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │LOW                      2023-04-12││NORMAL                   2023-04-12│   ",
      "   │Here is a low one                  ││Here is a normal one               │   ",
      "   │Some lowly description             ││Some description                   │   ",
      "   │                                   ││                                   │   ",
      "   │IMPORTANT                2023-04-12││URGENT                   2023-04-12│   ",
      "   │Here is an Important issue         ││An urgent issue with no descript...│   ",
      "   │Short description                  ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-progress-single"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromData(defaultItems.slice(0, 1))),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    state.progress()

    terminal.draw: frame =>
      ui.renderBoard(frame, state, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a                                                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-0────────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │                                   ││NORMAL                   2023-04-12│   ",
      "   │                                   ││Here is a normal one               │   ",
      "   │                                   ││Some description                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("empty-board-basic"):
    val state = ContextState(
      boards = Map("empty" -> BoardState.fromData(Vector.empty)),
      activeContext = "empty"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      ui.renderBoard(frame, state, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ empty                                                                  │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-0────────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("empty-board-stay-0"):
    val state = ContextState(
      boards = Map("empty" -> BoardState.fromData(Vector.empty)),
      activeContext = "empty"
    )
    // Calling next here shouldn't change the 0 after TODOs
    state.next()
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    terminal.draw: frame =>
      ui.renderBoard(frame, state, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ empty                                                                  │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-0────────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-delete"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromData(defaultItems)),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    val newState = state.delete()

    terminal.draw: frame =>
      ui.renderBoard(frame, newState, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a                                                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-1/2──────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │LOW                      2023-04-12││URGENT                   2023-04-12│   ",
      "   │Here is a low one                  ││An urgent issue with no descript...│   ",
      "   │Some lowly description             ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │IMPORTANT                2023-04-12││                                   │   ",
      "   │Here is an Important issue         ││                                   │   ",
      "   │Short description                  ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-delete-single"):
    val state = ContextState(
      boards = Map("a" -> BoardState.fromData(defaultItems.slice(0, 1))),
      activeContext = "a"
    )
    val backend = TestBackend(80, 30)
    val terminal = Terminal.init(backend)

    val first = state.delete()
    val second = first.delete()

    terminal.draw: frame =>
      ui.renderBoard(frame, second, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "   ┌Contexts────────────────────────────────────────────────────────────────┐   ",
      "   │ a                                                                      │   ",
      "   └────────────────────────────────────────────────────────────────────────┘   ",
      "   ┌TODOs-0────────────────────────────┐┌In Progress────────────────────────┐   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   │                                   ││                                   │   ",
      "   └───────────────────────────────────┘└───────────────────────────────────┘   ",
      "   j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new) | q    ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-input-normal"):
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)
    val inputState = InputState.fresh()

    terminal.draw: frame =>
      ui.renderInput(frame, inputState)

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
    assertBuffer(backend, expected)

  test("basic-input-filled"):
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)
    val inputState = InputState
      .fresh()
      .copy(
        title = "Some title",
        description = "Some description",
        inputMode = InputMode.Input
      )

    terminal.draw: frame =>
      ui.renderInput(frame, inputState)

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
    assertBuffer(backend, expected)

end uiSuite
