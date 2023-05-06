package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.ContextState

object EditContext:
  def render(frame: Frame, state: ContextState, name: String) =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(3),
      constraints = Array(
        Constraint.Length(2),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(state.sortedKeys.size + 2),
        Constraint.Length(3)
      )
    ).split(frame.size)

    Header.render(frame, chunks(0))

    frame.renderWidget(
      ParagraphWidget(
        text = Text.nostyle(name),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("New context name"))
          )
        ),
        style = Style.DEFAULT.fg(Color.Yellow)
      ),
      chunks(1)
    )

    frame.setCursor(
      x = chunks(1).x + Grapheme(name).width + 1,
      y = chunks(1).y + 1
    )

    ContextMenu.drawCurrentContext(frame, state.activeContext, chunks(2))
    ContextMenu.drawAllContexts(frame, state.sortedKeys, chunks(3))

    val helpText = Text.from(
      Span.styled("ENTER ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(accept)", Style(addModifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ESC ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(addModifier = Modifier.DIM))
    )

    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))
    frame.renderWidget(helpWidget, chunks(4))
  end render
end EditContext
