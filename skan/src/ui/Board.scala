package skan.ui

import skan.*

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.tabs.TabsWidget
import tui.widgets.ParagraphWidget.Wrap

object Board:
  def render(
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
      margin = Margin(2)
    ).split(frame.size)

    val horizontalChunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(
        Constraint.Percentage(50),
        Constraint.Percentage(50)
      )
    ).split(verticalChunk(1))

    val contexts = contextState.sortedKeys.map: context =>
      Spans(Array(Span.nostyle(context)))

    val tabs = TabsWidget(
      titles = contexts.toArray,
      block = Some(
        BlockWidget(
          borders = Borders.ALL,
          title = Some(Spans.nostyle("Contexts"))
        )
      ),
      selected = contextState.sortedKeys.indexOf(contextState.activeContext),
      highlight_style =
        Style(add_modifier = Modifier.BOLD, fg = Some(Color.Yellow))
    )
    frame.render_widget(tabs, verticalChunk(0))

    def toListItem(item: BoardItem, maxWidth: Int) =
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
          case Some(index) =>
            Spans.styled(
              s"TODOs-${index + 1}/${todoItems.size}",
              Style.DEFAULT.fg(Color.Yellow)
            )
          case None =>
            Spans.styled(
              s"TODOs-${todoItems.size}",
              Style.DEFAULT.fg(Color.Yellow)
            )
      else Spans.nostyle("TODOs")

    frame.render_stateful_widget(
      MyListWidget(
        items = todoItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(todoBorderTitle)
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
            Spans.styled(
              s"In Progress-${index + 1}/${inProgressItems.size}",
              Style.DEFAULT.fg(Color.Yellow)
            )
          case None =>
            Spans.styled(
              s"In Progress-${inProgressItems.size}",
              Style.DEFAULT.fg(Color.Yellow)
            )
      else Spans.nostyle("In Progress")

    frame.render_stateful_widget(
      MyListWidget(
        items = inProgressItems,
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(inProgressBorderTitle)
          )
        ),
        highlight_style = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      horizontalChunks(1)
    )(state.inProgressState)

    val msg = Text.from(
      Span.styled("j ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(↓)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("k ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(↑)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("h ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(←)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("l ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(→)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(progress)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("BACKSPACE ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(move back)", Style(add_modifier = Modifier.DIM)),
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

    val helpMessage =
      ParagraphWidget(text = msg, wrap = Some(Wrap(trim = true)))

    frame.render_widget(helpMessage, verticalChunk(2))
  end render
end Board