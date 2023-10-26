package skan

import java.time.ZoneId

import dev.dirs.ProjectDirectories
import upickle.default.ReadWriter

/** Representation of all the configuration options of skan.
  *
  * @param dataDir
  *   The location of where to read the data from.
  * @param zoneId
  *   The ZoneId of the user.
  * @param boardOrder
  *   How the items on the board are sorted
  */
final case class Config(
    dataDir: os.Path = Config.defaultDataDir,
    zoneId: ZoneId = ZoneId.of("GMT+2"),
    boardOrder: Order = Order.priority
) derives ReadWriter

object Config:
  private val projectDirs = ProjectDirectories.from("io", "kipp", "skan")
  private lazy val dataDir = projectDirs.dataDir
  private val configDir = projectDirs.configDir
  private lazy val defaultDataDir = os.Path(dataDir) / "contexts"
  val configFile = os.Path(configDir) / "config.json"
  val archiveDir = os.Path(dataDir) / "archive"

  private def fromJson(json: String) =
    upickle.default.read[Config](json)

  /** Load up the configuration file from disk.
    *
    * @return
    *   The created Config
    */
  def load(): Config =
    if os.exists(configFile) then fromJson(os.read(configFile))
    else Config()

  given ReadWriter[os.Path] = upickle.default
    .readwriter[String]
    .bimap[os.Path](
      path => path.toString(),
      string => os.Path(string)
    )

  given ReadWriter[ZoneId] = upickle.default
    .readwriter[String]
    .bimap[ZoneId](
      zoneId => zoneId.getId(),
      string => ZoneId.of(string)
    )

  given ReadWriter[Order] = upickle.default
    .readwriter[String]
    .bimap[Order](
      order => order.toString().toLowerCase(),
      string => Order.valueOf(string.toLowerCase())
    )

end Config

enum Order:
  case date, priority
