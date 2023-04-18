package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.*

object ContextMenu:
  def render(frame: Frame, state: ContextState, menuState: MyListWidget.State) =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(3),
      constraints = Array(
        Constraint.Length(5),
        Constraint.Length(3),
        Constraint.Length(state.sortedKeys.size + 2),
        Constraint.Length(3)
      )
    ).split(frame.size)

    def toListItem(name: ContextAction) =
      MyListWidget.Item(
        Text(Array(Spans.nostyle(s"- ${name.pretty()}")))
      )

    frame.render_stateful_widget(
      MyListWidget(
        items = ContextAction.values.map(toListItem),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Choose an action"))
          )
        ),
        highlight_style = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      chunks(0)
    )(menuState)

    drawCurrentContext(frame, state.activeContext, chunks(1))
    drawAllContexts(frame, state.sortedKeys, chunks(2))

    val helpText = Text.from(
      Span.styled("j ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(↓)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("k ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(↑)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("q ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(select)", Style(add_modifier = Modifier.DIM))
    )

    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))
    frame.render_widget(helpWidget, chunks(3))
  end render

  def drawCurrentContext(frame: Frame, contextName: String, area: Rect) =
    frame.render_widget(
      ParagraphWidget(
        text = Text.nostyle(contextName),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Current context"))
          )
        )
      ),
      area
    )

  def drawAllContexts(frame: Frame, contextNames: Vector[String], area: Rect) =
    frame.render_widget(
      ParagraphWidget(
        text = Text.fromSpans(
          contextNames.map(context => Spans.nostyle(s"- $context"))*
        ),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("All contexts"))
          )
        )
      ),
      area
    )
end ContextMenu
