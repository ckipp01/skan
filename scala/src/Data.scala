import upickle.default.ReadWriter

/** Just a wrapper around DataItems to represent the data that is read up from
  * disk.
  *
  * @param items
  *   all the items in the data file.
  */
final case class Data(items: Vector[DataItem]) derives ReadWriter

object Data:
  /** Given a config, load the data from the datafile location. If no file is
    * found, one will be created.
    *
    * @param config
    *   The Config holding the datafile location.
    * @return
    *   Data will the loaded items or a new Data with no items.
    */
  def load(config: Config): Data =
    if os.exists(config.dataFile) then fromJson(os.read(config.dataFile))
    else
      os.write(target = config.dataFile, data = "", createFolders = true)
      Data(Vector.empty)

  /** Save data back to the datafile location.
    *
    * @param config
    *
    * The Config containing the datafile location.
    * @param items
    *   Items to write to disk
    */
  def save(config: Config, items: Array[DataItem]): Unit =
    os.write.over(config.dataFile, toJson(items))

  private def fromJson(json: String): Data =
    val items = upickle.default.read[Vector[DataItem]](json)
    Data(items)

  private def toJson(items: Array[DataItem]): String =
    upickle.default.write(items)
end Data
