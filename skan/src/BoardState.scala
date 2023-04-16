package skan

/** State pertaining to a kanban board on the main UI.
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
    todoState: MyListWidget.State,
    inProgressState: MyListWidget.State,
    items: Array[BoardItem],
    focusedList: Status
):

  def todoItems(): Array[BoardItem] = items.collect:
    case item @ BoardItem(_, _, _, Status.TODO, _) => item

  def inProgressItems(): Array[BoardItem] = items.collect:
    case item @ BoardItem(_, _, _, Status.INPROGRESS, _) => item

  /** Switches the main column view in the board UI. The progression goes:
    *
    * TODO -> INPROGRESS
    *
    * INPROGRESS -> TODO
    *
    * @return
    *   the new BoardState
    */
  def switchColumn(): BoardState =
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
  def progress(): Unit =
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

  /** Delete the current focused item.
    *
    * @return
    *   The new state without the item.
    */
  def delete(): BoardState =
    focusedList match
      case Status.TODO =>
        todoState.selected match
          case None => this
          case Some(selectedIndex) =>
            val mainIndex = items.indexOf(todoItems()(selectedIndex))
            this.copy(items = items.filterNot(_ == items(mainIndex)))
      case Status.INPROGRESS =>
        inProgressState.selected match
          case None => this
          case Some(selectedIndex) =>
            val mainIndex = items.indexOf(inProgressItems()(selectedIndex))
            this.copy(items = items.filterNot(_ == items(mainIndex)))
      case _ => this

  /** Add a new item to the items in this state.
    *
    * @param dataItem
    *   The DataItem to add.
    * @return
    *   THe new state.
    */
  def withNewItem(dataItem: BoardItem): BoardState =
    this.copy(items = items.appended(dataItem))
end BoardState

object BoardState:
  /** Given items that have been loaded up from disk, create a new BoardState
    * out of them.
    *
    * @param items
    *   The items to create the state from.
    * @return
    *   The newly created state.
    */
  def fromData(items: Vector[BoardItem]): BoardState =
    BoardState(
      todoState =
        MyListWidget.State(selected = if items.size > 0 then Some(0) else None),
      inProgressState = MyListWidget.State(selected = None),
      items = items.toArray,
      focusedList = Status.TODO
    )
