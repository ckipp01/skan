import java.time.Instant
import upickle.default.*

final case class Data(items: Vector[DataItem]) derives ReadWriter

object Data:
  def empty() = Data(Vector.empty)

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
  def progress() = this match
    case TODO       => INPROGRESS
    case INPROGRESS => DONE
    // TODO idn, for now just make these a no-op if you all them on here
    case DONE    => DONE
    case BLOCKED => BLOCKED

  case TODO, INPROGRESS, BLOCKED, DONE
