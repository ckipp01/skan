package skan.ui

import tui.*
import tui.widgets.BlockWidget
import tui.widgets.ParagraphWidget
import tui.widgets.ParagraphWidget.Wrap

import skan.Config

object Info:
  def render(frame: Frame, config: Config) =
    val chunks = Layout(
      direction = Direction.Vertical,
      margin = Margin(3),
      constraints = Array(
        Constraint.Length(2),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3),
        Constraint.Length(3)
      )
    ).split(frame.size)

    Header.render(frame, chunks(0))

    frame.renderWidget(
      ParagraphWidget(
        text = Text.nostyle(config.dataDir.toString),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Data Dir"))
          )
        )
      ),
      chunks(1)
    )

    frame.renderWidget(
      ParagraphWidget(
        text = Text.nostyle(config.zoneId.toString),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Zone ID"))
          )
        )
      ),
      chunks(2)
    )

    frame.renderWidget(
      ParagraphWidget(
        text = Text.nostyle(config.boardOrder.toString),
        block = Some(
          BlockWidget(
            borders = Borders.ALL,
            title = Some(Spans.nostyle("Board Order"))
          )
        )
      ),
      chunks(3)
    )

    val helpText = Text.from(
      Span.styled("q ", Style(addModifier = Modifier.BOLD)),
      Span.styled("(quit)", Style(addModifier = Modifier.DIM))
    )
    val helpWidget =
      ParagraphWidget(text = helpText, wrap = Some(Wrap(trim = true)))

    frame.renderWidget(helpWidget, chunks(4))
  end render
end Info
