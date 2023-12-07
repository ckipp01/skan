package skan.ui

import scala.cli.build.BuildInfo
import tui.*
import tui.widgets.ParagraphWidget

object Header:
  /** Render the header which displays the name and version for Skan.
    *
    * @param frame
    *   The frame used to render
    * @param rect
    *   The rect you want it rendered in.
    */
  def render(frame: Frame, rect: Rect, message: Option[Message] = None) =
    val defaults = Vector(
      Spans.styled(
        s"skan",
        Style(addModifier = Modifier.BOLD)
      ),
      Spans.styled(
        s"v${BuildInfo.projectVersion.getOrElse("NO-VERSION")}",
        Style(fg = Some(Color.White), addModifier = Modifier.DIM)
          .addModifier(Modifier.ITALIC)
      )
    )

    val spans = message match
      case None => defaults
      case Some(value) =>
        defaults :+ Spans.styled(
          value.message,
          Style(fg = Some(value.borderColor))
        )

    val header =
      ParagraphWidget(
        text = Text.fromSpans(spans*),
        alignment = Alignment.Center
      )

    frame.renderWidget(header, rect)
  end render
end Header
