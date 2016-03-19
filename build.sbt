lazy val root = project.in(file("."))
	.enablePlugins(RundeckPlugin)
	.settings(
		name := "rundeck-openstack-plugin",
		organization := "io.sysa",
		version := "1.2.0",
		scalaVersion := "2.11.8",
		scalacOptions += "-target:jvm-1.7",

		libraryDependencies ++= Seq(
			"org.apache.jclouds.api" % "openstack-nova" % "1.9.2",
			"com.google.code.findbugs" % "jsr305" % "3.0.1"
		),

		rundeckPluginClassnames := Seq("sysa.OpenstackResourceModelSourceFactory"),
		rundeckLibraryVersion := "2.6.3"
	)