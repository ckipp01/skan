package skan

/** The top level state of the application containing all of the various
  * contexts. For most operations that have to do with the selected board this
  * just acts as a facilators and calls the correct operation on the the correct
  * board.
  *
  * @param boards
  *   The boards contained in the state, aka all the contexts and their name.
  * @param activeContext
  *   The name of the current active context.
  */
case class ContextState(
    boards: Map[String, BoardState],
    activeContext: String
):
  /** All the board names in a sorted fashion.
    */
  val sortedKeys = boards.keys.toVector.sorted

  /** Save all of the contexts to disk.
    *
    * @param config
    *   The application config.
    */
  def save(config: Config): Unit =
    for (name, board) <- boards do
      val json = ContextState.toJson(board.items)
      os.write.over(config.dataDir / s"${name}.json", json)

  /** Select the previous item in the current active board.
    */
  def previous(): Unit =
    boards(activeContext).previous()

  /** Select the next item in the current active board.
    */
  def next() =
    boards(activeContext).next()

  /** Delete the current item in the current active board.
    *
    * @return
    *   The new state.
    */
  def delete(): ContextState =
    val newBoardState = boards(activeContext).delete()
    this.copy(boards = boards.updated(activeContext, newBoardState))

  /** Switch the column view on the current active board.
    *
    * @return
    *   The new state.
    */
  def switchColumn(): ContextState =
    val newBoardState = boards(activeContext).switchColumn()
    this.copy(boards = boards.updated(activeContext, newBoardState))

    /** Progress the item on the current board.
      */
  def progress(): Unit =
    boards(activeContext).progress()

  /** Create a new item on the current selected board.
    *
    * @param item
    *   The new DataItem to add to the selected board.
    * @return
    *   The new state.
    */
  def withNewItem(item: BoardItem) =
    val newBoardState = boards(activeContext).withNewItem(item)
    this.copy(boards = boards.updated(activeContext, newBoardState))

    /** Switch the context to the next view.
      *
      * @return
      *   The new state.
      */
  def switchContext() =
    val index = sortedKeys.indexOf(activeContext)
    val newIndex = if index + 1 >= sortedKeys.length then 0 else index + 1
    this.copy(activeContext = sortedKeys(newIndex))

end ContextState

object ContextState:
  /** Given a config, load the data from the datafile location. If no file is
    * found, one will be created.
    *
    * @param config
    *   The Config holding the datafile location.
    * @return
    *   Data will the loaded items or a new Data with no items.
    */
  def fromConfig(config: Config): ContextState =
    if os.exists(config.dataDir) then
      val contextFiles = os.walk(
        path = config.dataDir,
        skip = _.ext != "json"
      )
      val boards = contextFiles
        .map: file =>
          val contents = os.read(file)
          val items = fromJson(contents)
          val name = file.baseName
          (name, BoardState.fromData(items))
        .toMap
      if boards.isEmpty then default(config.dataDir)
      else ContextState(boards, boards.keys.toVector.sorted.head)
    else default(config.dataDir)

  private def toJson(items: Array[BoardItem]): String =
    upickle.default.write(items)

  private def default(dir: os.Path) =
    os.write.over(
      target = dir / "default.json",
      data = "",
      createFolders = true
    )
    ContextState(Map("default" -> BoardState.fromData(Vector.empty)), "default")

  private def fromJson(json: String) =
    upickle.default.read[Vector[BoardItem]](json)

end ContextState
