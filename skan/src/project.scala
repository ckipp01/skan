//> using scala "3.3.1-RC1-bin-20230411-d577300-NIGHTLY"
//> using lib "com.olvind.tui::tui:0.0.5"
//> using lib "com.lihaoyi::upickle:3.1.0"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "dev.dirs:directories:26"
//> using resourceDir "../resources"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"

import tui.*
import tui.crossterm.Event
import tui.crossterm.KeyCode

@main def run(): Unit = withTerminal: (jni, terminal) =>
  val config = Config.load()
  val data = Data.load(config)

  val initialBoardState = BoardState.fromData(data)

  def runBoard(state: BoardState): Unit =
    terminal.draw(f => ui.renderBoard(f, state, config))
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case char: KeyCode.Char if char.c() == 'q' =>
            Data.save(config, state.items)
            ()
          case char: KeyCode.Char if char.c() == 'k' =>
            state.previous()
            runBoard(state)
          case char: KeyCode.Char if char.c() == 'j' =>
            state.next()
            runBoard(state)
          case char: KeyCode.Char if char.c() == 'l' || char.c() == 'h' =>
            val newState = state.switchView()
            runBoard(newState)
          case char: KeyCode.Char if char.c() == 'n' =>
            runInput(state, InputState.fresh())
          case char: KeyCode.Char if char.c() == 'x' =>
            val newState = state.delete()
            runBoard(newState)
          case char: KeyCode.Enter =>
            state.progress()
            runBoard(state)
          case _ => runBoard(state)
      case _ => runBoard(state)

  def runInput(boardState: BoardState, state: InputState): Unit =
    def handleNormalMode(keyCode: KeyCode) =
      keyCode match
        case c: KeyCode.Char if c.c() == 'i' =>
          val newState = state.switchInputMode()
          runInput(boardState, newState)
        case c: KeyCode.Char if c.c() == 'q' =>
          runBoard(boardState)
        case _ => runInput(boardState, state)

    def handleTextInput(
        keyCode: KeyCode,
        char: (c: Char) => Unit,
        backSpace: () => Unit
    ) =
      keyCode match
        case _: KeyCode.Esc =>
          val newState = state.switchInputMode()
          runInput(boardState, newState)

        case c: KeyCode.Char =>
          char(c.c())
          runInput(boardState, state)

        case c: KeyCode.Backspace =>
          backSpace()
          runInput(boardState, state)

        case _: KeyCode.Enter =>
          val newState = state.focusNext()
          runInput(boardState, newState)

        case _ => runInput(boardState, state)

    terminal.draw(f => ui.renderInput(f, state))

    jni.read() match
      case key: Event.Key =>
        state.focusedInput match
          case InputSection.Title =>
            state.inputMode match
              case InputMode.Normal =>
                handleNormalMode(key.keyEvent().code())

              case InputMode.Input =>
                handleTextInput(
                  key.keyEvent().code(),
                  (char: Char) => state.title = state.title + char,
                  () =>
                    state.title =
                      state.title.substring(0, state.title.length - 1)
                )

          case InputSection.Description =>
            state.inputMode match
              case InputMode.Normal =>
                handleNormalMode(key.keyEvent().code())

              case InputMode.Input =>
                handleTextInput(
                  key.keyEvent().code(),
                  (char: Char) => state.description = state.description + char,
                  () =>
                    state.description = state.description
                      .substring(0, state.description.length - 1)
                )

          case InputSection.Priority =>
            key.keyEvent().code() match
              case _: KeyCode.Enter =>
                if state.title.isEmpty() && state.description.isEmpty() then
                  runBoard(boardState)
                else
                  val newState = boardState.withNewItem(
                    DataItem.fromInput(
                      state.title,
                      state.description,
                      state.priority
                    )
                  )
                  runBoard(newState)
              case _: KeyCode.Tab =>
                val newState = state.copy(priority = state.priority.shift())
                runInput(boardState, newState)
              case c: KeyCode.Char if c.c() == 'q' =>
                runBoard(boardState)
              case _ => runInput(boardState, state)

      case _ => runInput(boardState, state)
    end match
  end runInput

  runBoard(initialBoardState)
