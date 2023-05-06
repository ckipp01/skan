package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.ContextState

object EditContext:
  def render(frame: Frame, state: ContextState, name: String) =

    val newContextNameWidget = Widget: (area, buf) =>
      BlockWidget(
        borders = Borders.ALL,
        title = Some(Spans.nostyle("New context name")),
        borderStyle = Style.DEFAULT.fg(Color.Yellow)
      )(
        ParagraphWidget(
          text = Text.nostyle(name),
          style = Style.DEFAULT.fg(Color.Yellow)
        )
      ).render(area, buf)
      frame.setCursor(
        x = area.x + Grapheme(name).width + 1,
        y = area.y + 1
      )

    val helpText = Text.from(
      Span.styled("ENTER ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(accept)", Style(addModifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ESC ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(addModifier = Modifier.DIM))
    )

    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))

    Layout
      .detailed(direction = Direction.Vertical, margin = Margin(3))(
        (Constraint.Length(2), Header.widget),
        (Constraint.Length(3), newContextNameWidget),
        (
          Constraint.Length(3),
          ContextMenu.currentContextWidget(frame, state.activeContext)
        ),
        (
          Constraint.Length(state.sortedKeys.size + 2),
          ContextMenu.allContextsWidget(frame, state.sortedKeys)
        ),
        (Constraint.Length(3), helpWidget)
      )
      .render(frame.size, frame.buffer)

  end render
end EditContext
