//> using scala "3.3.0-RC4"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "com.outr::scribe:3.11.1"

package skan.scripts

import java.io.File
import java.util.*, regex.Pattern

import scala.PartialFunction
import scala.util.*

import scala.sys.process.Process
import scala.sys.process.ProcessLogger

@main def run() =
  val target = os.pwd / "skan" / ".scala-build" / "BuildInfo.scala"
  val version = DynVer.version(Date())
  scribe.info(s"Current version is ${version}")
  scribe.info(s"Generating BuildInfo.scala into ${target}")
  val buildInfo = s"""|package skan
                      |
                      |object BuildInfo:
                      |  val version = "${version}"
                      |""".stripMargin
  os.write.over(target = target, data = buildInfo, createFolders = true)

//////////////////////////////////////////////////////////////////
// Everything below this point is just copied right from https://github.com/sbt/sbt-dynver/blob/master/dynver/src/main/scala/sbtdynver/DynVer.scala
// There is a library that we can use here, but it's only published for 2.12. For now, let's just wait until https://github.com/sbt/sbt-dynver/pull/245
// is merged and released, and then we'll start using it from here and delete everything below here.
//////////////////////////////////////////////////////////////////
object DynVer extends DynVer(None) with (Option[File] => DynVer):
  override def apply(wd: Option[File]) = new DynVer(wd)

object NoProcessLogger extends ProcessLogger:
  def info(s: => String) = ()
  def out(s: => String) = ()
  def error(s: => String) = ()
  def err(s: => String) = ()
  def buffer[T](f: => T) = f

sealed case class GitRef(value: String)
final case class GitCommitSuffix(distance: Int, sha: String)
final case class GitDirtySuffix(value: String)

final class GitTag(value: String, val prefix: String) extends GitRef(value):
  override def toString = s"GitTag($value, prefix=$prefix)"

object GitRef extends (String => GitRef):
  final implicit class GitRefOps(val x: GitRef) extends AnyVal:
    import x.*
    private def prefix = x match
      case x: GitTag => x.prefix
      case _         => DynVer.tagPrefix

    def isTag: Boolean = value.startsWith(prefix)
    def dropPrefix: String = value.stripPrefix(prefix)

    def mkString(prefix: String, suffix: String): String =
      if value.isEmpty then "" else prefix + value + suffix

    @deprecated(
      "Generalised to all prefixes, use dropPrefix (note it returns just the string)",
      "4.1.0"
    )
    def dropV: GitRef =
      if value.startsWith("v") then GitRef(value.stripPrefix("v")) else x

object GitCommitSuffix extends ((Int, String) => GitCommitSuffix):
  final implicit class GitCommitSuffixOps(val x: GitCommitSuffix)
      extends AnyVal:
    import x.*
    def isEmpty: Boolean = distance <= 0 || sha.isEmpty

    def mkString(prefix: String, infix: String, suffix: String): String =
      if sha.isEmpty then "" else prefix + distance + infix + sha + suffix

object GitDirtySuffix extends (String => GitDirtySuffix):
  final implicit class GitDirtySuffixOps(val x: GitDirtySuffix) extends AnyVal:
    import x.*
    def dropPlus: GitDirtySuffix = GitDirtySuffix(value.replaceAll("^\\+", ""))
    def withSeparator(separator: String): String =
      dropPlus.mkString(separator, "")

    def mkString(prefix: String, suffix: String): String =
      if value.isEmpty then "" else prefix + value + suffix

final case class GitDescribeOutput(
    ref: GitRef,
    commitSuffix: GitCommitSuffix,
    dirtySuffix: GitDirtySuffix
):
  def version(sep: String): String =
    if isCleanAfterTag then ref.dropPrefix
    else
      ref.dropPrefix + commitSuffix.mkString(sep, "-", "") + dirtySuffix
        .withSeparator(sep)

  def sonatypeVersion(sep: String): String =
    if isSnapshot() then version(sep) + "-SNAPSHOT" else version(sep)
  def previousVersion: String = ref.dropPrefix

  def version: String = version(DynVer.separator)
  def sonatypeVersion: String = sonatypeVersion(DynVer.separator)

  def hasNoTags(): Boolean = !ref.isTag
  def isDirty(): Boolean = dirtySuffix.value.nonEmpty

  def isVersionStable(): Boolean = !isDirty()
  def isSnapshot(): Boolean = hasNoTags() || !commitSuffix.isEmpty || isDirty()
  def isCleanAfterTag: Boolean = ref.isTag && commitSuffix.isEmpty && !isDirty()

object GitDescribeOutput
    extends ((GitRef, GitCommitSuffix, GitDirtySuffix) => GitDescribeOutput):
  private val OptWs =
    """[\s\n]*""" // doesn't \s include \n? why can't this call .r?
  private val Distance = """\+([0-9]+)""".r
  private val Sha = """([0-9a-f]{8})""".r
  private val HEAD = """HEAD""".r
  private val CommitSuffix = s"""(?:$Distance-$Sha)""".r
  private val TstampSuffix = """(\+[0-9]{8}-[0-9]{4})""".r

  final class Parser(tagPrefix: String):
    private val tagBody = tagPrefix match
      case "" =>
        """([0-9]+\.[^+]*?)""" // Use a dot to distinguish tags for SHAs...
      case _ =>
        """([0-9]+[^+]*?)""" // ... but not when there's a prefix (e.g. v2 is a tag)
    private val Tag =
      s"${Pattern.quote(tagPrefix)}$tagBody".r // quote the prefix so it doesn't interact

    private val FromTag =
      s"""^$OptWs$Tag$CommitSuffix?$TstampSuffix?$OptWs$$""".r
    private val FromSha = s"""^$OptWs$Sha$TstampSuffix?$OptWs$$""".r
    private val FromHead = s"""^$OptWs$HEAD$TstampSuffix$OptWs$$""".r

    def parse: String PartialFunction GitDescribeOutput = {
      case FromTag(tag, null, null, dirty) =>
        mk(
          mkTag(tag),
          GitCommitSuffix(0, ""),
          GitDirtySuffix(if dirty == null then "" else dirty)
        )
      case FromTag(tag, dist, sha, dirty) =>
        mk(
          mkTag(tag),
          GitCommitSuffix(dist.toInt, sha),
          GitDirtySuffix(if dirty == null then "" else dirty)
        )
      case FromSha(sha, dirty) =>
        mk(
          GitRef(sha),
          GitCommitSuffix(0, ""),
          GitDirtySuffix(if dirty == null then "" else dirty)
        )
      case FromHead(dirty) =>
        mk(GitRef("HEAD"), GitCommitSuffix(0, ""), GitDirtySuffix(dirty))
    }

    private def mkTag(tag: String) = new GitTag(tagPrefix + tag, tagPrefix)

    private def mk(
        ref: GitRef,
        commitSuffix: GitCommitSuffix,
        dirtySuffix: GitDirtySuffix
    ) =
      GitDescribeOutput(ref, commitSuffix, dirtySuffix)
  end Parser

  implicit class OptGitDescribeOutputOps(val _x: Option[GitDescribeOutput])
      extends AnyVal:
    def mkVersion(f: GitDescribeOutput => String, fallback: => String): String =
      _x.fold(fallback)(f)

    def getVersion(
        date: Date,
        separator: String,
        sonatypeSnapshots: Boolean
    ): String =
      if sonatypeSnapshots then sonatypeVersionWithSep(date, separator)
      else versionWithSep(date, separator)

    def versionWithSep(d: Date, sep: String): String =
      mkVersion(_.version(sep), fallback(sep, d))
    def sonatypeVersionWithSep(d: Date, sep: String): String =
      mkVersion(_.sonatypeVersion(sep), fallback(sep, d))

    def version(d: Date): String = versionWithSep(d, DynVer.separator)
    def sonatypeVersion(d: Date): String =
      sonatypeVersionWithSep(d, DynVer.separator)

    def previousVersion: Option[String] = _x.map(_.previousVersion)

    def hasNoTags: Boolean = _x.fold(true)(_.hasNoTags())
    def isDirty: Boolean = _x.fold(true)(_.isDirty())

    def isSnapshot: Boolean = _x.forall(_.isSnapshot())
    def isVersionStable: Boolean = _x.exists(_.isVersionStable())

    def assertTagVersion(version: String): Unit =
      if hasNoTags then
        throw new RuntimeException(
          s"Failed to derive version from git tags. Maybe run `git fetch --unshallow`? Version: $version"
        )
  end OptGitDescribeOutputOps

  def timestamp(d: Date): String = f"$d%tY$d%tm$d%td-$d%tH$d%tM"
  def fallback(separator: String, d: Date) = s"HEAD$separator${timestamp(d)}"
end GitDescribeOutput

sealed case class DynVer(
    wd: Option[File],
    separator: String,
    tagPrefix: String
):
  private def this(wd: Option[File], separator: String, vTagPrefix: Boolean) =
    this(wd, separator, if vTagPrefix then "v" else "")
  private def this(wd: Option[File], separator: String) =
    this(wd, separator, true)
  private def this(wd: Option[File]) = this(wd, "+")

  def vTagPrefix = tagPrefix == "v"

  val TagPattern =
    s"$tagPrefix[0-9]*" // used by `git describe` to filter the tags
  val parser = new GitDescribeOutput.Parser(tagPrefix) // .. then parsed back

  def version(d: Date): String =
    getGitDescribeOutput(d).versionWithSep(d, separator)
  def sonatypeVersion(d: Date): String =
    getGitDescribeOutput(d).sonatypeVersionWithSep(d, separator)
  def previousVersion: Option[String] = getGitPreviousStableTag.previousVersion
  def isSnapshot(): Boolean = getGitDescribeOutput(new Date).isSnapshot
  def isVersionStable(): Boolean = getGitDescribeOutput(
    new Date
  ).isVersionStable

  def makeDynVer(d: Date): Option[String] =
    getGitDescribeOutput(d).map(_.version(separator))
  def isDirty(): Boolean = getGitDescribeOutput(new Date).isDirty
  def hasNoTags(): Boolean = getGitDescribeOutput(new Date).hasNoTags

  def getDistanceToFirstCommit(): Option[Int] =
    val process = Process(s"git rev-list --count HEAD", wd)
    Try(process !! NoProcessLogger).toOption.map(_.trim.toInt)

  def getGitDescribeOutput(d: Date): Option[GitDescribeOutput] =
    val process = Process(
      s"git describe --long --tags --abbrev=8 --match $TagPattern --always --dirty=+${timestamp(d)}",
      wd
    )
    Try(process !! NoProcessLogger).toOption
      .map(_.replaceAll("-([0-9]+)-g([0-9a-f]{8})", "+$1-$2"))
      .map(parser.parse)
      .flatMap { out =>
        if out.hasNoTags() then
          getDistanceToFirstCommit().map { distance =>
            GitDescribeOutput(
              GitRef("0.0.0"),
              GitCommitSuffix(distance, out.ref.value),
              out.dirtySuffix
            )
          }
        else Some(out)
      }

  def getGitPreviousStableTag: Option[GitDescribeOutput] =
    for
      // Find the parent of the current commit. The "^1" instructs it to show only the first parent,
      // as merge commits can have multiple parents
      parentHash <- execAndHandleEmptyOutput(
        "git --no-pager log --pretty=%H -n 1 HEAD^1"
      )
      // Find the closest tag of the parent commit
      tag <- execAndHandleEmptyOutput(
        s"git describe --tags --abbrev=0 --match $TagPattern --always $parentHash"
      )
      out <- PartialFunction.condOpt(tag)(parser.parse)
    yield out

  def timestamp(d: Date): String = GitDescribeOutput.timestamp(d)
  def fallback(d: Date): String = GitDescribeOutput.fallback(separator, d)

  private def execAndHandleEmptyOutput(cmd: String): Option[String] =
    Try(Process(cmd, wd) !! NoProcessLogger).toOption
      .filter(_.trim.nonEmpty)
end DynVer
