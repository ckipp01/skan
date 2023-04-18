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
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(state.sortedKeys.size + 2),
        Constraint.Length(3)
      )
    ).split(frame.size)

    frame.render_widget(
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
      chunks(0)
    )

    frame.set_cursor(
      x = chunks(0).x + Grapheme(name).width + 1,
      y = chunks(0).y + 1
    )

    ContextMenu.drawCurrentContext(frame, state.activeContext, chunks(1))
    ContextMenu.drawAllContexts(frame, state.sortedKeys, chunks(2))

    val helpText = Text.from(
      Span.styled("ENTER ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(accept)", Style(add_modifier = Modifier.DIM)),
      Span.nostyle(" | "),
      Span.styled("ESC ", Style(add_modifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(add_modifier = Modifier.DIM))
    )

    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))
    frame.render_widget(helpWidget, chunks(3))
  end render
end EditContext
