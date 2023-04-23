//> using scala "3.3.0-RC4"
//> using lib "io.get-coursier:interface:1.0.15"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "com.outr::scribe:3.11.1"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"

package skan.scripts

import coursierapi.*
import scala.jdk.CollectionConverters.*

/** There is no way I would have figured this out without Øyvind. This is
  * basically a temporary workaround until a new graal is released to handle
  * this better.
  * https://github.com/oyvindberg/tui-scala/discussions/37#discussioncomment-5565814
  */
@main def setup() =
  val jar = os.Path(fetch().asScala.toVector.head)
  val filesToCopy = getSystemFiles(jar)
  filesToCopy.foreach: file =>
    os.copy(
      from = file,
      to = os.pwd / "skan" / "resources" / file.last,
      replaceExisting = true
    )
  scribe.info("files copied and ready to package")

def fetch() =
  // XXX make sure this matches the version we're actually using
  val dep = Dependency.of("com.olvind.tui", "crossterm", "0.0.5");
  scribe.info(s"fetching $dep")
  val fetch = Fetch.create().addDependencies(dep)
  fetch.fetch()

def getSystemFiles(jar: os.Path) =
  val userOs = System.getProperty("os.name").toLowerCase()
  val userArch = System.getProperty("os.arch")

  val tmp = os.temp.dir(prefix = "skan-")
  scribe.info(s"Copying jar file to $tmp")
  os.copy(from = jar, to = tmp / jar.last, replaceExisting = true)
  scribe.info("Unzipping jar file")
  val result = os.proc("jar", "xf", tmp / jar.last).call(cwd = tmp)
  if result.exitCode == 0 then
    scribe.info("collecting files to copy")
    val resources = os
      .walk(tmp)
      .collect:
        case file if file.last == "jni-config.json" => file
        case file
            if userOs.contains("windows") && file.last == "crossterm.dll" =>
          file
        case file
            if userOs.contains("linux") && file.last == "libcrossterm.so" =>
          file
        case file
            if userOs.contains(
              "mac"
            ) && userArch == "aarch64" && file.last == "libcrossterm.dylib" && file.toString
              .contains("arm64-darwin") =>
          file
        case file
            if userOs.contains(
              "mac"
            ) && file.last == "libcrossterm.dylib" && file.toString.contains(
              "x86_64-darwin"
            ) =>
          file
    scribe.info("found the following files to copy")
    resources.foreach(path => scribe.info(path.toString))
    resources
  else throw new RuntimeException("Can't uzip jar")
end getSystemFiles
