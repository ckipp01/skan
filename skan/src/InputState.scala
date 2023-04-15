package skan

/** State pertaining to the input section.
  *
  * @param title
  *   The title of the item to be created
  * @param description
  *   The description of the item to be created
  * @param inputMode
  *   Which input mode the user is currently in
  * @param focusedInput
  *   Which inpt is currently focused
  */
final case class InputState(
    var title: String = "",
    var description: String = "",
    inputMode: InputMode = InputMode.Normal,
    priority: Priority = Priority.NORMAL,
    focusedInput: InputSection
):
  /** Switch the input mode. Since there are only two modes, this simply toggles
    * them back and forth.
    *
    * @return
    *   The new state.
    */
  def switchInputMode(): InputState =
    val newMode = inputMode match
      case InputMode.Normal => InputMode.Input
      case InputMode.Input  => InputMode.Normal
    this.copy(inputMode = newMode)

  /** Focus on the next portion of the input.
    *
    * @return
    *   The new state
    */
  def focusNext(): InputState =
    val newFocus = focusedInput match
      case InputSection.Title       => InputSection.Description
      case InputSection.Description => InputSection.Priority
      case InputSection.Priority    => InputSection.Title
    this.copy(focusedInput = newFocus)

end InputState

object InputState:
  /** A fresh InputState where essentiall everything is empty and the user is
    * focused on the title input.
    *
    * @return
    *   The fresh InputState
    */
  def fresh(): InputState = InputState(focusedInput = InputSection.Title)

/** Represents the two states a user can be in during the Input view. Normal,
  * where they are just viewing, and Input where they are editing.
  */
enum InputMode:
  case Normal, Input

/** The current input section the user is focused on. This is either the Title,
  * Description, or Priority.
  */
enum InputSection:
  case Title, Description, Priority
