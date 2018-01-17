package kartograffel.server.db

import cats.effect.IO
import cats.implicits._
import doobie.specs2.analysisspec.IOChecker
import doobie.util.transactor.Transactor
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import kartograffel.server.{BuildInfo, Config}
import org.specs2.mutable.Specification

trait DbSpecification extends Specification with IOChecker {
  DbSpecification.runMigrationOnce

  override def transactor: Transactor[IO] =
    DbSpecification.transactor
}

object DbSpecification {
  val dbConfig: Config.Db = {
    val path = s"${BuildInfo.crossTarget.toString}/h2/kartograffel"
    val url = NonEmptyString.unsafeFrom(
      s"jdbc:h2:$path;MODE=PostgreSQL;AUTO_SERVER=TRUE")

    Config.Db(
      driver = "org.h2.Driver",
      url = url,
      user = "",
      password = ""
    )
  }

  lazy val runMigrationOnce: Unit =
    migrate[IO](dbConfig).void.unsafeRunSync()

  val transactor: Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = dbConfig.driver,
      url = dbConfig.url,
      user = dbConfig.user,
      pass = dbConfig.password
    )
}
