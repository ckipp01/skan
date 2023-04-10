// TODO I don't love the mix of mutability and immutability in here,
// but for now let's just stick close to the example and try to adjust later.

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
    focusedInput: InputSection
)

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

/** The current input section the user is focused on. This is either the Title
  * or the Description.
  */
enum InputSection:
  case Title, Description
