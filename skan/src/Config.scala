import java.time.ZoneId

import dev.dirs.ProjectDirectories
import upickle.default.ReadWriter

/** Representation of all the configuration options of skan.
  *
  * @param dataDir
  *   The location of where to read the data from.
  * @param zoneId
  *   The ZoneId of the user.
  */
final case class Config(
    dataDir: os.Path = Config.defaultDataDir,
    zoneId: ZoneId = ZoneId.of("GMT+2")
) derives ReadWriter

object Config:
  private val projectDirs = ProjectDirectories.from("io", "kipp", "skan")
  private lazy val dataDir = projectDirs.dataDir
  private val configDir = projectDirs.configDir
  private val configFile = os.Path(configDir) / "config.json"
  private lazy val defaultDataDir = os.Path(dataDir) / "contexts"

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
end Config
