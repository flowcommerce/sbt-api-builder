package apibuilder.sbt

import java.nio.file.{Path, PathMatcher}

final case class ApiBuilderRequest(path: String, matchers: Seq[PathMatcher], maybeTargetPath: Option[Path])

object ApiBuilderRequests {

  // I wish there was a way to do this dynamically...
  private val ResourceGenerators = Seq(
    "schema-evolution-manager",
    "play_2_x_routes",
    "swagger"
  )

  private val resourceGenerator = ResourceGenerators.contains(_: String)

  def fromCLIConfig(cliConfig: CLIConfig, includeResources: Boolean): Seq[ApiBuilderRequest] = {
    for {
      (org, orgConfig)                                          <- cliConfig.organizationFor.toList
      (app, ApplicationConfig(version, generators))             <- orgConfig.applicationFor
      GeneratorConfig(generator, maybeTargetPath, pathMatchers) <- generators
      if (resourceGenerator(generator) && includeResources) || !resourceGenerator(generator)
    } yield ApiBuilderRequest(s"$org/$app/$version/$generator", pathMatchers, maybeTargetPath)
  }
}
