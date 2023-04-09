//> using scala "3.3.0-RC3"
//> using lib "com.olvind.tui::tui:0.0.5"
//> using lib "com.lihaoyi::upickle:3.1.0"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "dev.dirs:directories:26"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"

import tui.*
import tui.crossterm.Event
import tui.crossterm.KeyCode

@main def run(): Unit = withTerminal: (jni, terminal) =>
  val config = Config.load()
  val data = Data.load(config)

  val initialBoardState = BoardState.fromData(data)

  def runBoard(state: BoardState): Unit =
    terminal.draw(f => ui.render(f, state))
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
          case char: KeyCode.Enter =>
            state.progress()
            runBoard(state)
          case _ => runBoard(state)
      case _ => runBoard(state)

  def runInput(boardState: BoardState, state: InputState): Unit =
    terminal.draw(f => ui.render(f, state))
    jni.read() match
      case key: Event.Key =>
        state.inputMode match
          case InputMode.Normal =>
            key.keyEvent().code() match
              case c: KeyCode.Char if c.c() == 'i' =>
                val newState = state.copy(inputMode = InputMode.Input)
                runInput(boardState, newState)
              case c: KeyCode.Char if c.c() == 'q' =>
                runBoard(boardState)
              case _ => runInput(boardState, state)

          case InputMode.Input =>
            key.keyEvent().code() match
              case _: KeyCode.Esc =>
                val newState = state.copy(inputMode = InputMode.Normal)
                runInput(boardState, newState)
              case c: KeyCode.Char =>
                state.focusedInput match
                  case InputSection.Title => state.title = state.title + c.c()
                  case InputSection.Description =>
                    state.description = state.description + c.c()
                runInput(boardState, state)
              case c: KeyCode.Backspace =>
                state.focusedInput match
                  case InputSection.Title =>
                    state.title =
                      state.title.substring(0, state.title.length - 1)
                  case InputSection.Description =>
                    state.description = state.description.substring(
                      0,
                      state.description.length - 1
                    )
                runInput(boardState, state)
              case _: KeyCode.Enter =>
                state.focusedInput match
                  case InputSection.Title =>
                    val newState =
                      state.copy(focusedInput = InputSection.Description)
                    runInput(boardState, newState)
                  case InputSection.Description =>
                    val newState = boardState.withNewItem(
                      DataItem.fromInput(state.title, state.description)
                    )
                    runBoard(newState)
      case _ => runInput(boardState, state)

  runBoard(initialBoardState)
