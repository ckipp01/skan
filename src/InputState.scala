// TODO I don't love the mix of mutability and immutability in here,
// but for now let's just stick close to the example and try to adjust later.

/** State pertaining to the input section.
  *
  * @param title
  * @param description
  * @param inputMode
  * @param focusedInput
  */
final case class InputState(
    var title: String = "",
    var description: String = "",
    inputMode: InputMode = InputMode.Normal,
    focusedInput: InputSection
)

object InputState:
  def fresh() = InputState(focusedInput = InputSection.Title)

enum InputMode:
  case Normal, Input

enum InputSection:
  case Title, Description
