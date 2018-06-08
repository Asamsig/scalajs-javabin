enablePlugins(ScalaJSBundlerPlugin, SbtJsonPlugin)

name := "scalajs-javabin"

scalaVersion := "2.12.4"

npmDependencies in Compile ++= Seq("react" -> "16.3.2", "react-dom" -> "16.3.2", "react-proxy" -> "1.1.8")

npmDevDependencies in Compile ++= Seq(
  "file-loader"         -> "1.1.11",
  "style-loader"        -> "0.20.3",
  "css-loader"          -> "0.28.11",
  "html-webpack-plugin" -> "3.2.0",
  "copy-webpack-plugin" -> "4.5.1",
  "webpack-merge"       -> "4.1.2"
)

libraryDependencies ++= Seq(
  "me.shadaj"         %%% "slinky-web" % "0.4.1",
  "me.shadaj"         %%% "slinky-hot" % "0.4.1",
  "com.typesafe.play" %%% "play-json"  % "2.6.7",
  "org.scalatest"     %%% "scalatest"  % "3.0.5" % Test
)

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full)

version in webpack := "4.5.0"
version in startWebpackDevServer := "3.1.3"

webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js")
webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js")

webpackConfigFile in Test := Some(baseDirectory.value / "webpack-core.config.js")

webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot")
webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly()

jsonUrls += "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text=%27Trondheim,%20NO%27)%20and%20u=%27c%27&format=json"
jsonInterpreter := plainCaseClasses.withPlayJsonFormats
