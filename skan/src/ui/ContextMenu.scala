package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ListWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.*

object ContextMenu:
  def render(frame: Frame, state: ContextState, menuState: ListWidget.State) =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(3),
      constraints = Array(
        Constraint.Length(2),
        Constraint.Length(5),
        Constraint.Length(3),
        Constraint.Length(state.sortedKeys.size + 2),
        Constraint.Length(3)
      )
    ).split(frame.size)

    Header.render(frame, chunks(0))

    def toListItem(name: ContextAction) =
      ListWidget.Item(
        Text(Array(Spans.nostyle(s"- ${name.pretty()}")))
      )

    frame.renderStatefulWidget(
      ListWidget(
        items = ContextAction.values.map(toListItem),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Choose an action"))
          )
        ),
        highlightStyle = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
      ),
      chunks(1)
    )(menuState)

    drawCurrentContext(frame, state.activeContext, chunks(2))
    drawAllContexts(frame, state.sortedKeys, chunks(3))

    val helpText = Text.from(
      Span.styled("j ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(↓)", Style(addModifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("k ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(↑)", Style(addModifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("q ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(addModifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ENTER ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(select)", Style(addModifier = Modifier.DIM))
    )

    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))
    frame.renderWidget(helpWidget, chunks(4))
  end render

  def drawCurrentContext(frame: Frame, contextName: String, area: Rect) =
    frame.renderWidget(
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
    frame.renderWidget(
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
