//> using scala "3.3.0"
//> using options "-deprecation", "-feature", "-explain", "-Wunused:all"
//> using lib "com.lihaoyi::os-lib:0.9.1"
//> using lib "com.outr::scribe:3.11.7"
//> using lib "com.github.sbt::dynver:5.0.1"

package skan.scripts

import java.util.Date

import sbtdynver.DynVer

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
