import dev.dirs.ProjectDirectories

object fs:
  private lazy val projectDirs = ProjectDirectories.from("io", "kipp", "daily")
  private lazy val dataDir = projectDirs.dataDir
  private lazy val dataFile = os.Path(dataDir) / "data.json"

  def createEmptyData(): Unit =
    os.write(target = dataFile, data = "", createFolders = true)

  def retrieveData(): Option[String] =
    if os.exists(dataFile) then Some(os.read(dataFile)) else None

  def saveData(data: String) = os.write.over(dataFile, data)
