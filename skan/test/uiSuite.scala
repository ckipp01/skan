import tui.*

class uiSuite extends munit.FunSuite:

  import testData.*
  import util.*

  test("basic-board-todo"):
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)
    val boardState = BoardState.fromData(data)

    terminal.draw: frame =>
      ui.renderBoard(frame, boardState, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "     ┌TODOs-1/3────────────────────────┐┌In Progress──────────────────────┐     ",
      "     │NORMAL                 2023-04-12││URGENT                 2023-04-12│     ",
      "     │Here is a normal one             ││An urgent issue with no descri...│     ",
      "     │Some description                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │LOW                    2023-04-12││                                 │     ",
      "     │Here is a low one                ││                                 │     ",
      "     │Some lowly description           ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     └─────────────────────────────────┘└─────────────────────────────────┘     ",
      "     j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new)      ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                "
    )
    assertBuffer(backend, expected)

  test("basic-board-progress"):
    val backend = TestBackend(80, 25)
    val terminal = Terminal.init(backend)
    val boardState =
      BoardState.fromData(data).copy(focusedList = Status.INPROGRESS)

    terminal.draw: frame =>
      ui.renderBoard(frame, boardState, config)

    val expected = Buffer.with_lines(
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "                                                                                ",
      "     ┌TODOs────────────────────────────┐┌In Progress-1────────────────────┐     ",
      "     │NORMAL                 2023-04-12││URGENT                 2023-04-12│     ",
      "     │Here is a normal one             ││An urgent issue with no descri...│     ",
      "     │Some description                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │LOW                    2023-04-12││                                 │     ",
      "     │Here is a low one                ││                                 │     ",
      "     │Some lowly description           ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     │                                 ││                                 │     ",
      "     └─────────────────────────────────┘└─────────────────────────────────┘     ",
      "     j (down) | k (up) | h (left) | l (right) | ENTER (progress) | n (new)      ",
      "                                                                                ",
      "                                                                                ",
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
      "                                                                                ",
      "                                                                                ",
      "     ┌Title───────────────────────────────────────────────────────────────┐     ",
      "     │                                                                    │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     ┌Description─────────────────────────────────────────────────────────┐     ",
      "     │                                                                    │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     ┌Priority────────────────────────────────────────────────────────────┐     ",
      "     │ LOW │ NORMAL │ IMPORTANT │ URGENT                                  │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     i (edit) | q (exit)                                                        ",
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
      "                                                                                ",
      "                                                                                ",
      "     ┌Title───────────────────────────────────────────────────────────────┐     ",
      "     │Some title                                                          │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     ┌Description─────────────────────────────────────────────────────────┐     ",
      "     │Some description                                                    │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     ┌Priority────────────────────────────────────────────────────────────┐     ",
      "     │ LOW │ NORMAL │ IMPORTANT │ URGENT                                  │     ",
      "     └────────────────────────────────────────────────────────────────────┘     ",
      "     ENTER (next) | ESC(stop editing)                                           ",
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
