package skan.ui

import skan.*

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.tabs.TabsWidget

object NewItem:
  def render(frame: Frame, state: NewItemState): Unit =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(3),
      constraints = Array(
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3)
      )
    ).split(frame.size)

    val titleWidget = ParagraphWidget(
      text = Text.nostyle(state.title),
      block = Some(
        BlockWidget(borders = Borders.ALL, title = Some(Spans.nostyle("Title")))
      ),
      style = (state.focusedInput, state.inputMode) match
        case (InputSection.Title, InputMode.Input) =>
          Style.DEFAULT.fg(Color.Yellow)
        case _ => Style.DEFAULT
    )

    frame.render_widget(titleWidget, chunks(0))

    val descriptionWidget = ParagraphWidget(
      text = Text.nostyle(state.description),
      block = Some(
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Description"))
        )
      ),
      style = (state.focusedInput, state.inputMode) match
        case (InputSection.Description, InputMode.Input) =>
          Style.DEFAULT.fg(Color.Yellow)
        case _ => Style.DEFAULT
    )

    frame.render_widget(descriptionWidget, chunks(1))

    val priorities = Priority.values.map: priority =>
      Spans(Array(Span.nostyle(priority.toString())))

    val tabs = TabsWidget(
      titles = priorities,
      block = Some(
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Priority"))
        )
      ),
      selected = state.priority.ordinal,
      highlight_style =
        if state.focusedInput == InputSection.Priority then
          Style(add_modifier = Modifier.BOLD, fg = Some(Color.Yellow))
        else Style.DEFAULT
    )
    frame.render_widget(tabs, chunks(2))

    if state.focusedInput != InputSection.Priority then
      state.inputMode match
        case InputMode.Normal => ()
        case InputMode.Input =>
          val (focused, chunk) =
            if state.focusedInput == InputSection.Title then (state.title, 0)
            else (state.description, 1)
          frame.set_cursor(
            x = chunks(chunk).x + Grapheme(focused).width + 1,
            y = chunks(chunk).y + 1
          )

    val msg = state.focusedInput match
      case InputSection.Title | InputSection.Description =>
        state.inputMode match
          case InputMode.Normal =>
            Text.from(
              Span.styled("i ", Style(add_modifier = Modifier.BOLD)),
              Span.styled("(edit)", Style(add_modifier = Modifier.DIM)),
              Span.nostyle(" | "),
              Span.styled("q ", Style(add_modifier = Modifier.BOLD)),
              Span.styled("(exit)", Style(add_modifier = Modifier.DIM))
            )
          case InputMode.Input =>
            Text.from(
              Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
              Span.styled("(next)", Style(add_modifier = Modifier.DIM)),
              Span.nostyle(" | "),
              Span.styled("ESC ", Style(add_modifier = Modifier.BOLD)),
              Span.nostyle("(stop editing)")
            )
      case InputSection.Priority =>
        Text.from(
          Span.styled("TAB", Style(add_modifier = Modifier.BOLD)),
          Span.nostyle("(select next)"),
          Span.nostyle(" | "),
          Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
          Span.styled("(complete)", Style(add_modifier = Modifier.DIM)),
          Span.nostyle(" | "),
          Span.styled("q", Style(add_modifier = Modifier.BOLD)),
          Span.nostyle("(quit)")
        )

    val helpMessage = ParagraphWidget(text = msg)
    frame.render_widget(helpMessage, chunks(3))
  end render
end NewItem
