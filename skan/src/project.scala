//> using scala 3.3.1
//> using jvm 17
//> using dep com.olvind.tui::tui:0.0.7
//> using dep com.lihaoyi::upickle:3.1.3
//> using dep com.lihaoyi::os-lib:0.9.1
//> using dep dev.dirs:directories:26
//> using test.dep org.scalameta::munit::0.7.29
//> using options -deprecation -feature -explain -Wunused:all
//> using computeVersion git:dynver
//> using buildInfo

package skan

import tui.*
import tui.crossterm.Event
import tui.crossterm.KeyCode
import tui.widgets.ListWidget

import skan.ui.*

@main def run(): Unit = withTerminal: (jni, terminal) =>
  val config = Config.load()
  val contextState = ContextState.fromConfig(config)

  def runBoard(state: ContextState): Unit =
    terminal.draw(frame => Board.render(frame, state, config))
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case char: KeyCode.Char if char.c() == 'q' => state.save(config)
          case char: KeyCode.Char if char.c() == 'k' =>
            state.previous()
            runBoard(state)
          case char: KeyCode.Char if char.c() == 'j' =>
            state.next()
            runBoard(state)
          case char: KeyCode.Char if char.c() == 'l' || char.c() == 'h' =>
            val newState = state.switchColumn()
            runBoard(newState)
          case char: KeyCode.Char if char.c() == 'n' =>
            runNewItem(state, NewItemState.fresh())
          case char: KeyCode.Char if char.c() == 'x' =>
            val newState = state.deleteItem()
            runBoard(newState)
          case char: KeyCode.Char if char.c() == 'c' =>
            runContextMenu(state, ListWidget.State(selected = Some(0)))
          case _: KeyCode.Enter =>
            state.progress()
            runBoard(state)
          case _: KeyCode.Backspace =>
            state.moveBack()
            runBoard(state)
          case _: KeyCode.Tab =>
            val newState = state.switchContext()
            runBoard(newState)
          case _ => runBoard(state)
      case _ => runBoard(state)
    end match
  end runBoard

  def runContextMenu(
      contextState: ContextState,
      menuState: ListWidget.State
  ): Unit =
    terminal.draw(frame => ContextMenu.render(frame, contextState, menuState))
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case char: KeyCode.Char if char.c() == 'j' =>
            val index = menuState.selected.getOrElse(0)
            val selected =
              if index >= ContextAction.values.size - 1 then 0
              else index + 1
            menuState.selected = Some(selected)
            runContextMenu(contextState, menuState)
          case char: KeyCode.Char if char.c() == 'k' =>
            val index = menuState.selected.getOrElse(0)
            val selected =
              if index == 0 then ContextAction.values.size - 1
              else index - 1
            menuState.selected = Some(selected)
            runContextMenu(contextState, menuState)
          case char: KeyCode.Char if char.c() == 'q' =>
            runBoard(contextState)
          case _: KeyCode.Enter =>
            if menuState.selected.isEmpty then runBoard(contextState)
            else
              ContextAction.fromOrdinal(menuState.selected.get) match
                case ContextAction.EditCurrentContext =>
                  runEditContext(contextState, contextState.activeContext)
                case ContextAction.DeleteCurrentContext =>
                  val newState = contextState.deleteContext(config)
                  runBoard(newState)
                case ContextAction.CreateNewContext =>
                  runNewContext(contextState, newContextName = "")
          case _ => runContextMenu(contextState, menuState)
      case _ => runContextMenu(contextState, menuState)
    end match
  end runContextMenu

  def runEditContext(contextState: ContextState, newContextName: String): Unit =
    terminal.draw(frame =>
      EditContext.render(frame, contextState, newContextName)
    )
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case c: KeyCode.Char =>
            val newText = newContextName + c.c()
            runEditContext(contextState, newText)
          case _: KeyCode.Backspace =>
            if newContextName.isEmpty() then
              runEditContext(contextState, newContextName)
            else
              val newText =
                newContextName.substring(0, newContextName.length - 1)
              runEditContext(contextState, newText)
          case _: KeyCode.Enter =>
            if newContextName.nonEmpty then
              val newState = contextState.renameContext(newContextName, config)
              runBoard(newState)
            else runBoard(contextState)
          case _: KeyCode.Esc =>
            runBoard(contextState)
          case _ => runEditContext(contextState, newContextName)
      case _ => runEditContext(contextState, newContextName)

  def runNewContext(contextState: ContextState, newContextName: String): Unit =
    terminal.draw(frame =>
      EditContext.render(frame, contextState, newContextName)
    )
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case c: KeyCode.Char =>
            val newText = newContextName + c.c()
            runNewContext(contextState, newText)
          case _: KeyCode.Backspace =>
            if newContextName.isEmpty() then
              runNewContext(contextState, newContextName)
            else
              val newText =
                newContextName.substring(0, newContextName.length - 1)
              runNewContext(contextState, newText)
          case _: KeyCode.Enter =>
            if newContextName.nonEmpty then
              val newState = contextState.addContext(newContextName)
              runBoard(newState)
            else runBoard(contextState)
          case _: KeyCode.Esc =>
            runBoard(contextState)
          case _ => runNewContext(contextState, newContextName)
      case _ => runNewContext(contextState, newContextName)

  def runNewItem(contextState: ContextState, state: NewItemState): Unit =
    def handleNormalMode(keyCode: KeyCode) =
      keyCode match
        case c: KeyCode.Char if c.c() == 'i' =>
          val newState = state.switchInputMode()
          runNewItem(contextState, newState)
        case c: KeyCode.Char if c.c() == 'q' =>
          runBoard(contextState)
        case _ => runNewItem(contextState, state)

    def handleTextInput(
        keyCode: KeyCode,
        char: Char => Unit,
        backSpace: () => Unit
    ) =
      keyCode match
        case _: KeyCode.Esc =>
          val newState = state.switchInputMode()
          runNewItem(contextState, newState)

        case c: KeyCode.Char =>
          char(c.c())
          runNewItem(contextState, state)

        case c: KeyCode.Backspace =>
          backSpace()
          runNewItem(contextState, state)

        case _: KeyCode.Enter =>
          val newState = state.focusNext()
          runNewItem(contextState, newState)

        case _ => runNewItem(contextState, state)

    terminal.draw(f => NewItem.render(f, state))

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
                    if state.title.nonEmpty then
                      state.title =
                        state.title.substring(0, state.title.length - 1)
                    else state.title
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
                    if state.description.nonEmpty then
                      state.description = state.description
                        .substring(0, state.description.length - 1)
                    else state.description
                )

          case InputSection.Priority =>
            key.keyEvent().code() match
              case _: KeyCode.Enter =>
                if state.title.isEmpty() && state.description.isEmpty() then
                  runBoard(contextState)
                else
                  val newState = contextState.withNewItem(
                    BoardItem.fromInput(
                      state.title,
                      state.description,
                      state.priority
                    )
                  )
                  runBoard(newState)
              case _: KeyCode.Tab =>
                val newState = state.copy(priority = state.priority.shift())
                runNewItem(contextState, newState)
              case c: KeyCode.Char if c.c() == 'q' =>
                runBoard(contextState)
              case _ => runNewItem(contextState, state)

      case _ => runNewItem(contextState, state)
    end match
  end runNewItem

  runBoard(contextState)
