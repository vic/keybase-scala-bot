// -*- mode: scala -*-
import mill._, scalalib._, publish._

import $ivy.`com.lihaoyi::mill-contrib-docker:$MILL_VERSION`
import contrib.docker.DockerModule


object keybase extends ScalaModule with DockerModule {

  def publishVersion = os.read(os.pwd / "VERSION").trim

  // use versions installed from .tool-versions
  // def scalaVersion = scala.util.Properties.versionNumberString
  def scalaVersion = "2.13.1"
  def millVersion = System.getProperty("MILL_VERSION")

  override def ivyDeps = Agg(
    ivy"com.lihaoyi:ammonite_${scalaVersion()}:2.0.4",
    ivy"dev.zio::zio:1.0.0-RC18"
  )

  object docker extends DockerConfig {
    override def tags = List("keybase-amm")

    override def dockerfile = T {
      val jarName = assembly().path.last
      s"""
         |FROM keybaseio/client as base
         |FROM ${baseImage()}
         |COPY --from=base /usr/bin/keybase /usr/bin/keybase
         |COPY --from=base /usr/bin/keybase.sig /usr/bin/keybase.sig
         |COPY $jarName /$jarName
         |ENTRYPOINT ["java", "-cp", "/$jarName", "keybase.bot"]
      """.stripMargin
    }
  }


}