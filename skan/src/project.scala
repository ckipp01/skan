//> using scala "3.3.1-RC1-bin-20230411-d577300-NIGHTLY"
//> using lib "com.olvind.tui::tui:0.0.5"
//> using lib "com.lihaoyi::upickle:3.1.0"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "dev.dirs:directories:26"
//> using resourceDir "../resources"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"

package skan

import tui.*
import tui.crossterm.Event
import tui.crossterm.KeyCode

@main def run(): Unit = withTerminal: (jni, terminal) =>
  val config = Config.load()
  val contextState = ContextState.fromConfig(config)

  def run(state: ContextState): Unit =
    terminal.draw(f => ui.renderBoard(f, state, config))
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case char: KeyCode.Char if char.c() == 'q' => state.save(config)
          case char: KeyCode.Char if char.c() == 'k' =>
            state.previous()
            run(state)
          case char: KeyCode.Char if char.c() == 'j' =>
            state.next()
            run(state)
          case char: KeyCode.Char if char.c() == 'l' || char.c() == 'h' =>
            val newState = state.switchColumn()
            run(newState)
          case char: KeyCode.Char if char.c() == 'n' =>
            runInput(state, InputState.fresh())
          case char: KeyCode.Char if char.c() == 'x' =>
            val newState = state.delete()
            run(newState)
          case _: KeyCode.Enter =>
            state.progress()
            run(state)
          case _: KeyCode.Backspace =>
            state.moveBack()
            run(state)
          case _: KeyCode.Tab =>
            val newState = state.switchContext()
            run(newState)
          case _ => run(state)
      case _ => run(state)
  end run

  def runInput(contextState: ContextState, state: InputState): Unit =
    def handleNormalMode(keyCode: KeyCode) =
      keyCode match
        case c: KeyCode.Char if c.c() == 'i' =>
          val newState = state.switchInputMode()
          runInput(contextState, newState)
        case c: KeyCode.Char if c.c() == 'q' =>
          run(contextState)
        case _ => runInput(contextState, state)

    def handleTextInput(
        keyCode: KeyCode,
        char: (c: Char) => Unit,
        backSpace: () => Unit
    ) =
      keyCode match
        case _: KeyCode.Esc =>
          val newState = state.switchInputMode()
          runInput(contextState, newState)

        case c: KeyCode.Char =>
          char(c.c())
          runInput(contextState, state)

        case c: KeyCode.Backspace =>
          backSpace()
          runInput(contextState, state)

        case _: KeyCode.Enter =>
          val newState = state.focusNext()
          runInput(contextState, newState)

        case _ => runInput(contextState, state)

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
                  run(contextState)
                else
                  val newState = contextState.withNewItem(
                    BoardItem.fromInput(
                      state.title,
                      state.description,
                      state.priority
                    )
                  )
                  run(newState)
              case _: KeyCode.Tab =>
                val newState = state.copy(priority = state.priority.shift())
                runInput(contextState, newState)
              case c: KeyCode.Char if c.c() == 'q' =>
                run(contextState)
              case _ => runInput(contextState, state)

      case _ => runInput(contextState, state)
    end match
  end runInput

  run(contextState)
