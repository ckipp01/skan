package skan

import upickle.default.ReadWriter
import java.time.Instant

/** The representation of what makes up an "item" on your lists.
  *
  * @param date
  *   The date the item was created
  * @param title
  *   The title of the item
  * @param description
  *   A description of the item
  * @param status
  *   The current status of the item
  * @param priority
  *   The priority level of the item
  */
final case class BoardItem(
    date: Instant,
    title: String,
    description: String,
    status: Status,
    priority: Priority
) derives ReadWriter

object BoardItem:
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
  def fromInput(
      title: String,
      description: String,
      priority: Priority
  ): BoardItem =
    BoardItem(Instant.now(), title, description, Status.TODO, priority)

/** The various states that a DataItem can be in.
  */
enum Status derives ReadWriter:
  /** Given a Status, progress it to the next level. For now this is limited to
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

  /** Move the current status back.
    *
    * @return
    *   THe new Status
    */
  def moveBack(): Status = this match
    // Can't move something back from TODO
    case TODO       => TODO
    case INPROGRESS => TODO
    case BLOCKED    => INPROGRESS
    case DONE       => INPROGRESS

  case TODO, INPROGRESS, BLOCKED, DONE

enum Priority derives ReadWriter:

  /** This shifts the priority level. If you're currently on URGENT it will
    * shift it back around to LOW.
    *
    * @return
    *   the new Priority level.
    */
  def shift(): Priority = this match
    case URGENT    => LOW
    case IMPORTANT => URGENT
    case NORMAL    => IMPORTANT
    case LOW       => NORMAL

  case LOW, NORMAL, IMPORTANT, URGENT
