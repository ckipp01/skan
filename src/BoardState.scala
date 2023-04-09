import tui.widgets.ListWidget

/** State pertaining to the main board ui.
  *
  * @param todoState
  *   state of the todo board
  * @param inProgressState
  *   state of the progress board
  * @param items
  *   all items that were loaded up
  * @param focusedList
  *   the focused list on the board
  */
final case class BoardState(
    todoState: ListWidget.State,
    inProgressState: ListWidget.State,
    items: Array[DataItem],
    focusedList: Status
):

  def todoItems() = items.collect:
    case item @ DataItem(_, _, _, Status.TODO) => item

  def inProgressItems() = items.collect:
    case item @ DataItem(_, _, _, Status.INPROGRESS) => item

  def switchView() =
    focusedList match
      case Status.TODO =>
        todoState.selected = None
        inProgressState.selected = Some(0)
        this.copy(focusedList = Status.INPROGRESS)
      case Status.INPROGRESS =>
        inProgressState.selected = None
        todoState.selected = Some(0)
        this.copy(focusedList = Status.TODO)
      case _ => this

  /** Select the next item in the list
    */
  def next(): Unit =
    focusedList match
      case Status.TODO =>
        val i = todoState.selected match
          case None    => 0
          case Some(i) => if i >= todoItems().length - 1 then 0 else i + 1
        todoState.select(Some(i))
      case Status.INPROGRESS =>
        val i = inProgressState.selected match
          case None    => 0
          case Some(i) => if i >= inProgressItems().length - 1 then 0 else i + 1
        inProgressState.select(Some(i))
      case _ => ()

  /** Select the previous item in the list
    */
  def previous(): Unit =
    focusedList match
      case Status.TODO =>
        val i = todoState.selected match
          case None    => 0
          case Some(i) => if i == 0 then todoItems().length - 1 else i - 1
        todoState.select(Some(i))
      case Status.INPROGRESS =>
        val i = inProgressState.selected match
          case None    => 0
          case Some(i) => if i == 0 then inProgressItems().length - 1 else i - 1
        inProgressState.select(Some(i))
      case _ => ()

  /** Progress the state of the item to the next state.
    */
  def progress() =
    focusedList match
      case Status.TODO =>
        todoState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val mainIndex = items.indexOf(todoItems()(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.progress())
      case Status.INPROGRESS =>
        inProgressState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val mainIndex = items.indexOf(inProgressItems()(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.progress())
      case _ => ()

  def withNewItem(dataItem: DataItem) =
    this.copy(items = items.appended(dataItem))

object BoardState:
  def fromData(data: Data): BoardState =
    BoardState(
      todoState = ListWidget.State(selected = Some(0)),
      inProgressState = ListWidget.State(selected = None),
      items = data.items.toArray,
      focusedList = Status.TODO
    )
