package skan

case class ContextState(
    boards: Map[String, BoardState],
    activeContext: String
):

  def save(config: Config): Unit =
    for (name, board) <- boards do
      val json = ContextState.toJson(board.items)
      os.write.over(config.dataDir / s"${name}.json", json)

  def previous() =
    boards(activeContext).previous()

  def next() =
    boards(activeContext).next()

  def delete() =
    val newBoardState = boards(activeContext).delete()
    this.copy(boards = boards.updated(activeContext, newBoardState))

  def switchView() =
    val newBoardState = boards(activeContext).switchView()
    this.copy(boards = boards.updated(activeContext, newBoardState))

  def progress() =
    boards(activeContext).progress()

  def withNewItem(item: DataItem) =
    val newBoardState = boards(activeContext).withNewItem(item)
    this.copy(boards = boards.updated(activeContext, newBoardState))

  def switchContext() =
    val keys = boards.keys.toVector
    val index = keys.indexOf(activeContext)
    val newIndex = if index + 1 >= keys.length then 0 else index + 1
    this.copy(activeContext = keys(newIndex))

end ContextState

object ContextState:
  private def toJson(items: Array[DataItem]): String =
    upickle.default.write(items)

  private def default(dir: os.Path) =
    os.write.over(
      target = dir / "default.json",
      data = "",
      createFolders = true
    )
    ContextState(Map("default" -> BoardState.fromData(Vector.empty)), "default")

  /** Given a config, load the data from the datafile location. If no file is
    * found, one will be created.
    *
    * @param config
    *   The Config holding the datafile location.
    * @return
    *   Data will the loaded items or a new Data with no items.
    */
  def load(config: Config): ContextState =
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
      else ContextState(boards, boards.head._1)
    else default(config.dataDir)

  private def fromJson(json: String) =
    upickle.default.read[Vector[DataItem]](json)

end ContextState
