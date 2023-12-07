package skan.ui

/** A message to be shown to the user. This will typically be done in the head
  * and is meant to update the user about something that either went well or
  * didn't.
  *
  * @param message
  *   The message to show to the user.
  * @param borderColor
  *   The color the message should be displayed with.
  */
final case class Message(message: String, borderColor: tui.Color)
