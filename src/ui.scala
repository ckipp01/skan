import tui._
import tui.widgets.ListWidget
import tui.widgets.BlockWidget

object ui:
  def render(frame: Frame, state: AppState) =
    val chunks = Layout(
      direction = Direction.Horizontal,
      constraints = Array(
        Constraint.Percentage(50),
        Constraint.Percentage(50)
      ),
      margin = Margin(5, 5)
    ).split(frame.size)

    def toListItem(item: DataItem) =
      val titleSpans = Spans.from(
        Span.styled(item.title, Style(add_modifier = Modifier.BOLD))
      )
      val descriptionSpan = Spans.from(Span.nostyle(item.description))
      ListWidget.Item(Text(Array(titleSpans, descriptionSpan)))

    val todoItems = state
      .todoItems()
      .map: item =>
        toListItem(item)

    val inProgressItems = state
      .inProgressItems()
      .map: item =>
        toListItem(item)

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
