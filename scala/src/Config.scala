import upickle.default.ReadWriter
import dev.dirs.ProjectDirectories

/** Representation of all the configuration options of skan.
  *
  * @param dataFile
  *   The location of where to read the data from.
  */
final case class Config(dataFile: os.Path) derives ReadWriter

object Config:
  private val projectDirs = ProjectDirectories.from("io", "kipp", "skan")
  private lazy val dataDir = projectDirs.dataDir
  private val configDir = projectDirs.configDir
  private val configFile = os.Path(configDir) / "config.json"
  private lazy val defaultDataFile = os.Path(dataDir) / "data.json"

  private def fromJson(json: String) =
    upickle.default.read[Config](json)

  /** Load up the configuration file from disk.
    *
    * @return
    *   The Config created
    */
  def load(): Config =
    if os.exists(configFile) then fromJson(os.read(configFile))
    else Config(defaultDataFile)

  given ReadWriter[os.Path] = upickle.default
    .readwriter[String]
    .bimap[os.Path](
      path => path.toString(),
      string => os.Path(string)
    )
