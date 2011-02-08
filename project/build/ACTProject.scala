import sbt._

// A project that defines a webstart application in the 'app' directory
// and a web project to serve it in the 'web' directory.
class WebstartTest(info: ProjectInfo) extends ParentProject(info) {
  val app = project("app", "Webstart  Application", new ApplicationProject(_))
  val web = project("web", "Webstart Test Server", new ServerProject(_), app)

  // The web project.  Running 'jetty-run' on this project will serve the
  // webstart application from http://localhost:8080 using jetty.
  class ServerProject(info: ProjectInfo) extends DefaultWebProject(info) {
    // Jetty is only need for the 'test' classpath
    val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test"
    // We need the packaged jar from the application project
    override def prepareWebappAction = super.prepareWebappAction dependsOn app.`package`
    // Include the webstart files directly in the web app root directory.
    override def extraWebappFiles = (app.webstartOutputDirectory ##) ** AllPassFilter
  }
  class ApplicationProject(info: ProjectInfo) extends DefaultWebstartProject(info) with AutoCompilerPlugins {
    import SignJar._

    val codebase = if (true) "http://localhost:8080/" else "http://basking-cat.appspot.com/act"

    val cont = compilerPlugin("org.scala-lang.plugins" % "continuations" % "2.8.1")
    override def compileOptions = super.compileOptions ++ compileOptions("-P:continuations:enable")

    val scala_tools_snapshots = ("Scala-Tools Maven2 Repository - snapshots" at "http://scala-tools.org/repo-snapshots")
    val scala_stm = "org.scala-tools" %% "scala-stm" % "0.3-SNAPSHOT"

    override def webstartSignConfiguration = Some(new SignConfiguration("act", storePassword("3939cat") :: Nil))
    //override def webstartPack200 = false // uncomment to disable pack200 compression, which is enabled by default
    //override def webstartGzip = false// uncomment to disable gzip compression, which is enabled by default

    def jnlpXML(libraries: Seq[WebstartJarResource]) =
      <jnlp spec="1.0+" codebase={ codebase } href={ artifactBaseName + ".jnlp" }>
        <information>
          <title>ACT</title>
          <vendor>baskingcat</vendor>
          <description>横スクロールアクションゲーム</description>
          <offline-allowed/>
        </information>
        <security>
          <all-permissions/>
        </security>
        <resources>
          <j2se version="1.5+"/>
          { defaultElements(libraries filter {library => !library.name.contains("lwjgl") && library.name != "jinput" }) }
          <extension name="LWJGLExtension" href="http://lwjgl.org/jnlp/extension.php"/>
        </resources>
        <application-desc main-class="baskingcat.act.ACT"/>
      </jnlp>
  }
}
