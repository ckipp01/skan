package skan

import java.time.Instant

/** Basically set up what we need for testing. We start with 5 items, write them
  * to disk in a tmp context files, and then load them back up to create our
  * initial ContextState.
  */
object TestData:
  val defaultItems = Vector(
    BoardItem(
      date = Instant.parse("2023-04-11T20:48:25.061615Z"),
      title = "Here is a normal one",
      description = "Some description",
      status = Status.TODO,
      priority = Priority.NORMAL
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:48:43.892637Z"),
      title = "Here is a low one",
      description = "Some lowly description",
      status = Status.TODO,
      priority = Priority.LOW
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:49:05.638360Z"),
      title = "Here is an Important issue",
      description = "Short description",
      status = Status.TODO,
      priority = Priority.IMPORTANT
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:49:19.998189Z"),
      title = "An urgent issue with no description!",
      description = "",
      status = Status.INPROGRESS,
      priority = Priority.URGENT
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:49:30.305568Z"),
      title = "Some issue that is already done",
      description = "blah",
      status = Status.DONE,
      priority = Priority.NORMAL
    )
  )

  val secondaryItems = Vector(
    BoardItem(
      date = Instant.parse("2023-04-11T20:48:25.061615Z"),
      title = "Here is a normal one",
      description = "Some description",
      status = Status.TODO,
      priority = Priority.NORMAL
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:48:43.892637Z"),
      title = "Here is a low one",
      description = "Some lowly description",
      status = Status.INPROGRESS,
      priority = Priority.LOW
    ),
    BoardItem(
      date = Instant.parse("2023-04-12T20:49:05.638360Z"),
      title = "Here is an Important issue",
      description = "Short description",
      status = Status.DONE,
      priority = Priority.IMPORTANT
    )
  )

  private val tmp = os.temp.dir(prefix = "skan-")

  private val preContext = ContextState(
    Map(
      "a" -> BoardState.fromItems(defaultItems),
      "b" -> BoardState.fromItems(secondaryItems)
    ),
    activeContext = "a"
  )

  // Basically a minimal round-trip just to ensure we can read and write
  val config = Config(dataDir = tmp)
  preContext.save(config)
end TestData
