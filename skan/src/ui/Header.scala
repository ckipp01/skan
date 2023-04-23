package skan.ui

import tui.*
import tui.widgets.ParagraphWidget

import skan.BuildInfo

object Header:
  /** Render the header which displays the name and version for Skan.
    *
    * @param frame
    *   The frame used to render
    * @param rect
    *   The rect you want it rendered in.
    */
  def render(frame: Frame, rect: Rect) =
    val header =
      ParagraphWidget(
        text = Text.fromSpans(
          Spans.styled(
            s"skan",
            Style(add_modifier = Modifier.BOLD)
          ),
          Spans.styled(
            s"v${BuildInfo.version}",
            Style(fg = Some(Color.White), add_modifier = Modifier.DIM)
              .add_modifier(Modifier.ITALIC)
          )
        ),
        alignment = Alignment.Center
      )

    frame.render_widget(header, rect)
