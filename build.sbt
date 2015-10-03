lazy val root = project.in(file("."))
	.enablePlugins(RundeckPlugin)
	.settings(
		name := "rundeck-openstack-plugin",
		organization := "io.sysa",
		version := "1.0.0",
		scalaVersion := "2.11.7",

		libraryDependencies ++= Seq(
			"org.apache.jclouds.api" % "openstack-nova" % "1.9.1",
			"com.google.code.findbugs" % "jsr305" % "3.0.0"
		),

		rundeckPluginClassnames := Seq("sysa.OpenstackResourceModelSourceFactory")
	)