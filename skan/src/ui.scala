package skan

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.tabs.TabsWidget

object ui:
  def renderBoard(
      frame: Frame,
      contextState: ContextState,
      config: Config
  ): Unit =
    val state = contextState.boards(contextState.activeContext)

    val verticalChunk = Layout(
      direction = Direction.Vertical,
      constraints = Array(
        Constraint.Length(3),
        Constraint.Percentage(80),
        Constraint.Length(3)
      ),
      margin = Margin(3)
    ).split(frame.size)

    val horizontalChunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(
        Constraint.Percentage(50),
        Constraint.Percentage(50)
      )
    ).split(verticalChunk(1))

    val keys = contextState.boards.keys.toArray.sorted
    val contexts = keys.map: context =>
      Spans(Array(Span.nostyle(context)))

    val tabs = TabsWidget(
      titles = contexts,
      block = Some(
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Contexts"))
        )
      ),
      selected = keys.indexOf(contextState.activeContext),
      highlight_style =
        Style(add_modifier = Modifier.BOLD, fg = Some(Color.Yellow))
    )
    frame.render_widget(tabs, verticalChunk(0))

    def toListItem(item: DataItem, maxWidth: Int) =
      val priorityStyle = item.priority match
        case Priority.LOW       => Style(fg = Some(Color.LightBlue))
        case Priority.NORMAL    => Style(fg = Some(Color.Blue))
        case Priority.IMPORTANT => Style(fg = Some(Color.Yellow))
        case Priority.URGENT    => Style(fg = Some(Color.Red))

      val title =
        if item.title.length() > maxWidth - 5 then
          item.title.substring(0, maxWidth - 5) + "..."
        else item.title
      val description =
        if item.description.length() > maxWidth - 5 then
          item.description.substring(0, maxWidth - 5) + "..."
        else item.description

      val localDate =
        item.date.atZone(config.zoneId).toLocalDate().toString()
      val priority = item.priority.toString()
      val headerSpans =
        Spans.from(
          Span.styled(priority, priorityStyle),
          Span.nostyle(
            " ".repeat(
              horizontalChunks(0).width - (localDate
                .length() + priority.length) - 2
            )
          ),
          Span.styled(
            item.date.toString(),
            Style(fg = Some(Color.Gray), add_modifier = Modifier.ITALIC)
              .add_modifier(Modifier.DIM)
          )
        )
      val titleSpans = Spans.from(
        Span.styled(title, Style(add_modifier = Modifier.BOLD))
      )
      val descriptionSpans = Spans.from(
        Span.styled(description, Style(add_modifier = Modifier.DIM))
      )
      val spacerSpans = Spans.from(
        Span.styled(
          " ".repeat(horizontalChunks(0).width),
          Style(add_modifier = Modifier.DIM)
        )
      )
      MyListWidget.Item(
        Text(Array(headerSpans, titleSpans, descriptionSpans, spacerSpans))
      )
    end toListItem

    val todoItems = state
      .todoItems()
      .map: item =>
        toListItem(item, horizontalChunks(0).width)

    val inProgressItems = state
      .inProgressItems()
      .map: item =>
        toListItem(item, horizontalChunks(1).width)

    val todoBorderTitle =
      if state.focusedList == Status.TODO then
        state.todoState.selected match
          case Some(index) => s"TODOs-${index + 1}/${todoItems.size}"
          case None        => s"TODOs-${todoItems.size}"
      else "TODOs"

    frame.render_stateful_widget(
      MyListWidget(
        items = todoItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle(todoBorderTitle))
          )
        ),
        highlight_style = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      horizontalChunks(0)
    )(state.todoState)

    val inProgressBorderTitle =
      if state.focusedList == Status.INPROGRESS then
        state.inProgressState.selected match
          case Some(index) =>
            s"In Progress-${index + 1}/${inProgressItems.size}"
          case None => s"In Progress-${inProgressItems.size}"
      else "In Progress"

    frame.render_stateful_widget(
      MyListWidget(
        items = inProgressItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle(inProgressBorderTitle))
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
      Span.styled("(quit)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("x ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(delete)", Style(add_modifier = Modifier.DIM))
    )

    val helpMessage = ParagraphWidget(text = msg)
    frame.render_widget(helpMessage, verticalChunk(2))
  end renderBoard

  def renderInput(frame: Frame, state: InputState): Unit =
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
              Span.styled("ESC", Style(add_modifier = Modifier.BOLD)),
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
  end renderInput
end ui
