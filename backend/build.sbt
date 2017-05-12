import BuildSettings.{buildSettings, appName}
import PublishSettings._
import AssemblySettings._


lazy val root = Project(appName, file("."), settings = buildSettings ++ publishSettings).settings(mergeSetting)
