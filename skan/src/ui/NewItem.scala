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
        Constraint.Length(2),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3)
      )
    ).split(frame.size)

    Header.render(frame, chunks(0))

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

    frame.renderWidget(titleWidget, chunks(1))

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

    frame.renderWidget(descriptionWidget, chunks(2))

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
      highlightStyle =
        if state.focusedInput == InputSection.Priority then
          Style(addModifier = Modifier.BOLD, fg = Some(Color.Yellow))
        else Style.DEFAULT
    )
    frame.renderWidget(tabs, chunks(3))

    if state.focusedInput != InputSection.Priority then
      state.inputMode match
        case InputMode.Normal => ()
        case InputMode.Input =>
          val (focused, chunk) =
            if state.focusedInput == InputSection.Title then (state.title, 1)
            else (state.description, 2)
          frame.setCursor(
            x = chunks(chunk).x + Grapheme(focused).width + 1,
            y = chunks(chunk).y + 1
          )

    val msg = state.focusedInput match
      case InputSection.Title | InputSection.Description =>
        state.inputMode match
          case InputMode.Normal =>
            Text.from(
              Span.styled("i ", Style(addModifier = Modifier.BOLD)),
              Span.styled("(edit)", Style(addModifier = Modifier.DIM)),
              Span.nostyle(" | "),
              Span.styled("q ", Style(addModifier = Modifier.BOLD)),
              Span.styled("(exit)", Style(addModifier = Modifier.DIM))
            )
          case InputMode.Input =>
            Text.from(
              Span.styled("ENTER ", Style(addModifier = Modifier.BOLD)),
              Span.styled("(next)", Style(addModifier = Modifier.DIM)),
              Span.nostyle(" | "),
              Span.styled("ESC ", Style(addModifier = Modifier.BOLD)),
              Span.nostyle("(stop editing)")
            )
      case InputSection.Priority =>
        Text.from(
          Span.styled("TAB", Style(addModifier = Modifier.BOLD)),
          Span.nostyle("(select next)"),
          Span.nostyle(" | "),
          Span.styled("ENTER ", Style(addModifier = Modifier.BOLD)),
          Span.styled("(complete)", Style(addModifier = Modifier.DIM)),
          Span.nostyle(" | "),
          Span.styled("q", Style(addModifier = Modifier.BOLD)),
          Span.nostyle("(quit)")
        )

    val helpMessage = ParagraphWidget(text = msg)
    frame.renderWidget(helpMessage, chunks(4))
  end render
end NewItem
