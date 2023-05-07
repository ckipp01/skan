package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ListWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.*

object ContextMenu:
  def render(frame: Frame, state: ContextState, menuState: ListWidget.State) =
    def toListItem(name: ContextAction) =
      ListWidget.Item(
        Text(Array(Spans.nostyle(s"- ${name.pretty()}")))
      )

    val actionsWidget =
      BlockWidget(
        borders = Borders.ALL,
        title = Some(Spans.nostyle("Choose an action"))
      )(
        ListWidget(
          state = menuState,
          items = ContextAction.values.map(toListItem),
          highlightStyle = Style(bg = Some(Color.Gray), fg = Some(Color.Black))
        )
      )

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

    Layout
      .detailed(direction = Direction.Vertical, margin = Margin(3))(
        (Constraint.Length(2), Header.widget),
        (Constraint.Length(5), actionsWidget),
        (
          Constraint.Length(3),
          currentContextWidget(frame, state.activeContext)
        ),
        (
          Constraint.Length(state.sortedKeys.size + 2),
          allContextsWidget(frame, state.sortedKeys)
        ),
        (Constraint.Length(3), helpWidget)
      )
      .render(frame.size, frame.buffer)

  end render

  def currentContextWidget(frame: Frame, contextName: String) =
    BlockWidget(
      borders = Borders.ALL,
      title = Some(Spans.nostyle("Current context"))
    )(
      ParagraphWidget(
        text = Text.nostyle(contextName)
      )
    )

  def allContextsWidget(frame: Frame, contextNames: Vector[String]) =
    BlockWidget(
      borders = Borders.ALL,
      title = Some(Spans.nostyle("All contexts"))
    )(
      ParagraphWidget(
        text = Text.fromSpans(
          contextNames.map(context => Spans.nostyle(s"- $context"))*
        )
      )
    )
end ContextMenu
