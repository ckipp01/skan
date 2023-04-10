import tui.*
import tui.widgets.ListWidget
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget

object ui:
  def render(frame: Frame, state: BoardState): Unit =
    val chunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(
        Constraint.Percentage(50),
        Constraint.Percentage(50)
      ),
      margin = Margin(5)
    ).split(frame.size)

    def toListItem(item: DataItem, maxWidth: Int) =
      val title =
        if item.title.length() > maxWidth - 5 then
          item.title.substring(0, maxWidth - 5) + "..."
        else item.title
      val description =
        if item.description.length() > maxWidth - 5 then
          item.description.substring(0, maxWidth - 5) + "..."
        else item.description

      val titleSpans = Spans.from(
        Span.styled(title, Style(add_modifier = Modifier.BOLD))
      )
      val descriptionSpan = Spans.from(
        Span.styled(description, Style(add_modifier = Modifier.DIM))
      )
      ListWidget.Item(Text(Array(titleSpans, descriptionSpan)))

    val todoItems = state
      .todoItems()
      .map: item =>
        toListItem(item, chunks(0).width)

    val inProgressItems = state
      .inProgressItems()
      .map: item =>
        toListItem(item, chunks(1).width)

    frame.render_stateful_widget(
      ListWidget(
        items = todoItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("TODOs"))
          )
        ),
        highlight_style = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      chunks(0)
    )(state.todoState)

    frame.render_stateful_widget(
      ListWidget(
        items = inProgressItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("In Progress"))
          )
        ),
        highlight_style = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      chunks(1)
    )(state.inProgressState)
  end render

  def render(frame: Frame, state: InputState): Unit =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(5),
      constraints =
        // TODO make this look nicer. I don't love this now
        Array(
          Constraint.Percentage(5),
          Constraint.Percentage(10),
          Constraint.Percentage(10),
          Constraint.Percentage(75)
        )
    ).split(frame.size)

    val (msg, style) = state.inputMode match
      case InputMode.Normal =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("q", Style.DEFAULT.add_modifier(Modifier.BOLD)),
            Span.nostyle(" to exit, "),
            Span.styled("i", Style.DEFAULT.add_modifier(Modifier.BOLD)),
            Span.nostyle(" to start editing.")
          ),
          Style.DEFAULT.add_modifier(Modifier.SLOW_BLINK)
        )
      case InputMode.Input =>
        (
          Text.from(
            Span.nostyle("Press "),
            Span.styled("Esc", Style.DEFAULT.add_modifier(Modifier.BOLD)),
            Span.nostyle(" to stop editing, "),
            Span.styled("Enter", Style.DEFAULT.add_modifier(Modifier.BOLD)),
            Span.nostyle(" to move to the next step.")
          ),
          Style.DEFAULT
        )

    val text = msg.overwrittenStyle(style)

    val helpMessage = ParagraphWidget(text = text)
    frame.render_widget(helpMessage, chunks(0))

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

    frame.render_widget(titleWidget, chunks(1))

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

    frame.render_widget(descriptionWidget, chunks(2))

    state.inputMode match
      case InputMode.Normal => ()
      case InputMode.Input =>
        val (focused, chunk) =
          if state.focusedInput == InputSection.Title then (state.title, 1)
          else (state.description, 2)
        // Make the cursor visible and ask tui-rs to put it at the specified coordinates after rendering
        frame.set_cursor(
          x = chunks(chunk).x + Grapheme(focused).width + 1,
          y = chunks(chunk).y + 1
        )
