//> using scala "3.3.0-RC3"
//> using lib "com.olvind.tui::tui:0.0.5"
//> using lib "com.lihaoyi::upickle:3.0.0"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "dev.dirs:directories:26"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"

import tui._
import tui.crossterm.Event
import tui.crossterm.KeyCode

@main def run(): Unit = withTerminal: (jni, terminal) =>
  val data = fs.retrieveData() match
    case None =>
      fs.createEmptyData()
      Data.empty()
    case Some(data) => Data(items = json.deserialize(data))

  val appState = AppState.fromData(data)

  def run(state: AppState): Unit =
    terminal.draw(f => ui.render(f, state))
    jni.read() match
      case key: Event.Key =>
        key.keyEvent().code() match
          case char: KeyCode.Char if char.c() == 'q' =>
            fs.saveData(json.serialize(state.items))
            ()
          case char: KeyCode.Char if char.c() == 'k' =>
            state.previous()
            run(state)
          case char: KeyCode.Char if char.c() == 'j' =>
            state.next()
            run(state)
          case char: KeyCode.Char if char.c() == 'l' || char.c() == 'h' =>
            val newState = state.switchView()
            run(newState)
          case char: KeyCode.Enter =>
            state.progress()
            run(state)
          case _ => run(state)
      case _ => run(state)

  run(appState)
