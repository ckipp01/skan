import java.time.Instant

/** Basically set up what we need for testing. We start with 5 items, write them
  * to disk in a tmp file, then load them back up to create our initial Data.
  */
object testData:
  private val defaultItems = Vector(
    DataItem(
      date = Instant.parse("2023-04-12T20:48:25.061615Z"),
      title = "Here is a normal one",
      description = "Some description",
      status = Status.TODO,
      priority = Priority.NORMAL
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:48:43.892637Z"),
      title = "Here is a low one",
      description = "Some lowly description",
      status = Status.TODO,
      priority = Priority.LOW
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:49:05.638360Z"),
      title = "Here is an Important issue",
      description = "Short description",
      status = Status.TODO,
      priority = Priority.IMPORTANT
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:49:19.998189Z"),
      title = "An urgent issue with no description!",
      description = "",
      status = Status.INPROGRESS,
      priority = Priority.URGENT
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:49:30.305568Z"),
      title = "Some issue that is already done",
      description = "blah",
      status = Status.DONE,
      priority = Priority.NORMAL
    )
  )

  private val secondaryItems = Vector(
    DataItem(
      date = Instant.parse("2023-04-12T20:48:25.061615Z"),
      title = "Here is a normal one",
      description = "Some description",
      status = Status.TODO,
      priority = Priority.NORMAL
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:48:43.892637Z"),
      title = "Here is a low one",
      description = "Some lowly description",
      status = Status.INPROGRESS,
      priority = Priority.LOW
    ),
    DataItem(
      date = Instant.parse("2023-04-12T20:49:05.638360Z"),
      title = "Here is an Important issue",
      description = "Short description",
      status = Status.DONE,
      priority = Priority.IMPORTANT
    )
  )

  private val tmp = os.temp(prefix = "skan-")

  val preContext = ContextState(
    Map(
      "default" -> BoardState.fromData(defaultItems),
      "secondary" -> BoardState.fromData(secondaryItems)
    ),
    activeContext = "default"
  )

  // Basically a minimal round-trip just to ensure we can read and write
  val config = Config(dataDir = tmp)
  preContext.save(config)
  val contextState = ContextState.load(config)
end testData
