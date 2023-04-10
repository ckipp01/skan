import upickle.default.ReadWriter

import java.time.Instant

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
    *   The Config containing the datafile location.
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

final case class DataItem(
    date: Instant,
    title: String,
    description: String,
    status: Status
) derives ReadWriter

object DataItem:
  given ReadWriter[Instant] = upickle.default
    .readwriter[String]
    .bimap[Instant](
      instant => instant.toString(),
      string => Instant.parse(string)
    )

  /** Given user input, create a DataItem from it.
    *
    * @param title
    *   The titile of the new item
    * @param description
    *   The description of the new item
    * @return
    *   The new DataItem created
    */
  def fromInput(title: String, description: String) =
    DataItem(Instant.now(), title, description, Status.TODO)

/** The various states that a DataItem can be in.
  */
enum Status derives ReadWriter:
  /** Given a Status, progress it to the next level. For now this is limited to
    * TODO going to INPROGRESS and INPROGRESS moving to DONE.
    *
    * @return
    *   The new Status
    */
  def progress(): Status = this match
    case TODO       => INPROGRESS
    case INPROGRESS => DONE
    // TODO idn, for now just make these a no-op if you all them on here
    case DONE    => DONE
    case BLOCKED => BLOCKED

  case TODO, INPROGRESS, BLOCKED, DONE
