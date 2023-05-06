package skan.ui

import tui.*
import tui.widgets.ParagraphWidget

import skan.BuildInfo

object Header:

  /** Render the header which displays the name and version for Skan.
    */
  val widget =
    ParagraphWidget(
      text = Text.fromSpans(
        Spans.styled(
          s"skan",
          Style(addModifier = Modifier.BOLD)
        ),
        Spans.styled(
          s"v${BuildInfo.version}",
          Style(fg = Some(Color.White), addModifier = Modifier.DIM)
            .addModifier(Modifier.ITALIC)
        )
      ),
      alignment = Alignment.Center
    )
