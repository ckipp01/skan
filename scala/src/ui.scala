import tui.*
import tui.widgets.ListWidget
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget

object ui:
  def render(frame: Frame, state: BoardState): Unit =
    val verticalChunk = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Percentage(90),
        Constraint.Length(3)
      ),
      margin = Margin(5)
    ).split(frame.size)

    val horizontalChunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(
        Constraint.Percentage(50),
        Constraint.Percentage(50)
      )
    ).split(verticalChunk(0))

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
        toListItem(item, horizontalChunks(0).width)

    val inProgressItems = state
      .inProgressItems()
      .map: item =>
        toListItem(item, horizontalChunks(1).width)

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
      horizontalChunks(0)
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
      horizontalChunks(1)
    )(state.inProgressState)

    val msg = Text.from(
      Span.styled("j ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(down)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("k ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(up)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("h ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(left)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("l ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(right)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(progress)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("n ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(new)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("q ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(add_modifier = Modifier.DIM))
    )

    val helpMessage = ParagraphWidget(text = msg)
    frame.render_widget(helpMessage, verticalChunk(1))
  end render

  def render(frame: Frame, state: InputState): Unit =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(5),
      constraints =
        // TODO make this look nicer. I don't love this now
        Array(
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

    state.inputMode match
      case InputMode.Normal => ()
      case InputMode.Input =>
        val (focused, chunk) =
          if state.focusedInput == InputSection.Title then (state.title, 0)
          else (state.description, 1)
        // Make the cursor visible and ask tui-rs to put it at the specified coordinates after rendering
        frame.set_cursor(
          x = chunks(chunk).x + Grapheme(focused).width + 1,
          y = chunks(chunk).y + 1
        )

    val msg = state.inputMode match
      case InputMode.Normal =>
        (
          Text.from(
            Span.styled("q ", Style(add_modifier = Modifier.BOLD)),
            Span.styled("(exit)", Style(add_modifier = Modifier.DIM)),
            Span.nostyle(" | "),
            Span.styled("i ", Style(add_modifier = Modifier.BOLD)),
            Span.styled("(edit)", Style(add_modifier = Modifier.DIM))
          )
        )
      case InputMode.Input =>
        (
          Text.from(
            Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
            Span.styled("(next)", Style(add_modifier = Modifier.DIM)),
            Span.nostyle(" | "),
            Span.styled("ESC", Style(add_modifier = Modifier.BOLD)),
            Span.nostyle("(stop editing)")
          )
        )

    val helpMessage = ParagraphWidget(text = msg)
    frame.render_widget(helpMessage, chunks(2))
