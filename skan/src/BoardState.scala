package skan

import tui.widgets.ListWidget

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
    todoState: ListWidget.State,
    inProgressState: ListWidget.State,
    items: Array[BoardItem],
    focusedList: Status,
    boardOrder: Order
):

  // TODO an improvement would be during progress don't edit the array in place,
  // instead make it a vector and then these can be vals here.
  def todoItems(): Array[BoardItem] = items
    .collect:
      case item @ BoardItem(_, _, _, Status.TODO, _) => item
    .sortWith(ordering)

  def inProgressItems(): Array[BoardItem] = items
    .collect:
      case item @ BoardItem(_, _, _, Status.INPROGRESS, _) => item
    .sortWith(ordering)

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
        if inProgressItems().isEmpty then None
        else inProgressState.selected = Some(0)
        this.copy(focusedList = Status.INPROGRESS)
      case Status.INPROGRESS =>
        inProgressState.selected = None
        if todoItems().isEmpty then None
        else todoState.selected = Some(0)
        this.copy(focusedList = Status.TODO)
      case _ => this

  /** Select the next item in the list
    */
  def next(): Unit =
    focusedList match
      case Status.TODO =>
        val newlySelected = todoState.selected match
          case None => if todoItems().isEmpty then None else Some(0)
          case Some(i) =>
            if i >= todoItems().length - 1 then Some(0) else Some(i + 1)
        todoState.select(newlySelected)
      case Status.INPROGRESS =>
        val newlySelected = inProgressState.selected match
          case None => if inProgressItems().isEmpty then None else Some(0)
          case Some(i) =>
            if i >= inProgressItems().length - 1 then Some(0) else Some(i + 1)
        inProgressState.select(newlySelected)
      case _ => ()

  /** Select the previous item in the list
    */
  def previous(): Unit =
    focusedList match
      case Status.TODO =>
        val newlySelected = todoState.selected match
          case None => if todoItems().isEmpty then None else Some(0)
          case Some(i) =>
            if todoItems().isEmpty then None
            else if i == 0 then Some(todoItems().length - 1)
            else Some(i - 1)
        todoState.select(newlySelected)
      case Status.INPROGRESS =>
        val newlySelected = inProgressState.selected match
          case None => if inProgressItems().isEmpty then None else Some(0)
          case Some(i) =>
            if inProgressItems().isEmpty then None
            else if i == 0 then Some(inProgressItems().length - 1)
            else Some(i - 1)
        inProgressState.select(newlySelected)
      case _ => ()

  /** Progress the state of the item to the next state.
    */
  def progress(): Unit =
    focusedList match
      case Status.TODO =>
        todoState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val todos = todoItems()
            val mainIndex = items.indexOf(todos(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.progress())
            // We special case this to make sure that if a user progresses an
            // item at the bottom of the list, we move the selected up.
            if selectedIndex + 1 == todos.length then previous() else ()
      case Status.INPROGRESS =>
        inProgressState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val inProgress = inProgressItems()
            val mainIndex = items.indexOf(inProgress(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.progress())
            if selectedIndex + 1 == inProgress.length then previous() else ()
      case _ => ()

  // TODO this and the above can probably be refactored to be a shared method.
  // The only thing that differs is the call to progress or moveBack.
  /** Move the current item back on the board.
    */
  def moveBack(): Unit =
    focusedList match
      case Status.TODO =>
        todoState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val todos = todoItems()
            val mainIndex = items.indexOf(todos(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.moveBack())
            if selectedIndex + 1 == todos.length then previous() else ()
      case Status.INPROGRESS =>
        inProgressState.selected match
          case None => ()
          case Some(selectedIndex) =>
            val inProgress = inProgressItems()
            val mainIndex = items.indexOf(inProgress(selectedIndex))
            items(mainIndex) =
              items(mainIndex).copy(status = items(mainIndex).status.moveBack())
            if selectedIndex + 1 == inProgress.length then previous() else ()
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
            val todos = todoItems()
            if todos.isEmpty then this
            else
              val mainIndex = items.indexOf(todos(selectedIndex))
              val newState =
                this.copy(items = items.filterNot(_ == items(mainIndex)))
              if selectedIndex + 1 >= todos.length - 1 then newState.previous()
              else ()
              newState
      case Status.INPROGRESS =>
        inProgressState.selected match
          case None => this
          case Some(selectedIndex) =>
            val inProgress = inProgressItems()
            if inProgress.isEmpty then this
            else
              val mainIndex = items.indexOf(inProgress(selectedIndex))
              val newState =
                this.copy(items = items.filterNot(_ == items(mainIndex)))
              if selectedIndex + 1 >= inProgress.length - 1 then
                newState.previous()
              else ()
              newState
      case _ => this

  /** Add a new item to the items in this state.
    *
    * @param item
    *   The BoardItem to add.
    * @return
    *   The new state.
    */
  def withNewItem(item: BoardItem): BoardState =
    this.copy(items = items.appended(item))

  private val ordering: (BoardItem, BoardItem) => Boolean = (a, b) =>
    boardOrder match
      case Order.date     => a.date.compareTo(b.date) < 0
      case Order.priority => a.priority.ordinal > b.priority.ordinal
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
  def fromItems(
      items: Vector[BoardItem],
      boardOrder: Order = Order.priority
  ): BoardState =
    BoardState(
      todoState =
        ListWidget.State(selected = if items.size > 0 then Some(0) else None),
      inProgressState = ListWidget.State(selected = None),
      items = items.toArray,
      focusedList = Status.TODO,
      boardOrder = boardOrder
    )
