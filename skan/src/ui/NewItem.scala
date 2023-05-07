package skan.ui

import skan.*

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.tabs.TabsWidget

object NewItem:
  def render(frame: Frame, state: NewItemState): Unit =

    val titleWidget = Widget: (area, buf) =>
      BlockWidget(
        borders = Borders.ALL,
        title = Some(Spans.nostyle("Title")),
        borderStyle = (state.focusedInput, state.inputMode) match
          case (InputSection.Title, InputMode.Input) =>
            Style.DEFAULT.fg(Color.Yellow)
          case _ => Style.DEFAULT
      )(
        ParagraphWidget(
          text = Text.nostyle(state.title),
          style = (state.focusedInput, state.inputMode) match
            case (InputSection.Title, InputMode.Input) =>
              Style.DEFAULT.fg(Color.Yellow)
            case _ => Style.DEFAULT
        )
      ).render(area, buf)
      if state.focusedInput == InputSection.Title && state.inputMode == InputMode.Input
      then
        frame.setCursor(
          x = area.x + Grapheme(state.title).width + 1,
          y = area.y + 1
        )

    val descriptionWidget = Widget: (area, buf) =>
      BlockWidget(
        borders = Borders.ALL,
        title = Some(Spans.nostyle("Description")),
        borderStyle = (state.focusedInput, state.inputMode) match
          case (InputSection.Title, InputMode.Input) =>
            Style.DEFAULT.fg(Color.Yellow)
          case _ => Style.DEFAULT
      )(
        ParagraphWidget(
          text = Text.nostyle(state.description),
          style = (state.focusedInput, state.inputMode) match
            case (InputSection.Description, InputMode.Input) =>
              Style.DEFAULT.fg(Color.Yellow)
            case _ => Style.DEFAULT
        )
      ).render(area, buf)
      if state.focusedInput == InputSection.Description && state.inputMode == InputMode.Input
      then
        frame.setCursor(
          x = area.x + Grapheme(state.description).width + 1,
          y = area.y + 1
        )

    val priorities = Priority.values.map: priority =>
      Spans(Array(Span.nostyle(priority.toString())))

    val tabs = BlockWidget(
      borders = Borders.ALL,
      title = Some(Spans.nostyle("Priority"))
    )(
      TabsWidget(
        titles = priorities,
        selected = state.priority.ordinal,
        highlightStyle =
          if state.focusedInput == InputSection.Priority then
            Style(addModifier = Modifier.BOLD, fg = Some(Color.Yellow))
          else Style.DEFAULT
      )
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

    Layout
      .detailed(direction = Direction.Vertical, margin = Margin(3))(
        (Constraint.Length(2), Header.widget),
        (Constraint.Length(3), titleWidget),
        (Constraint.Length(3), descriptionWidget),
        (Constraint.Length(3), tabs),
        (Constraint.Length(3), helpMessage)
      )
      .render(frame.size, frame.buffer)

  end render
end NewItem
