import upickle.default.ReadWriter

import java.time.Instant

final case class Data(items: Vector[DataItem]) derives ReadWriter

object Data:
  def load(config: Config): Data =
    if os.exists(config.dataFile) then fromJson(os.read(config.dataFile))
    else
      os.write(target = config.dataFile, data = "", createFolders = true)
      Data(Vector.empty)

  def save(config: Config, items: Array[DataItem]) =
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

  def fromInput(title: String, description: String) =
    DataItem(Instant.now(), title, description, Status.TODO)

enum Status derives ReadWriter:
  def progress(): Status = this match
    case TODO       => INPROGRESS
    case INPROGRESS => DONE
    // TODO idn, for now just make these a no-op if you all them on here
    case DONE    => DONE
    case BLOCKED => BLOCKED

  case TODO, INPROGRESS, BLOCKED, DONE
