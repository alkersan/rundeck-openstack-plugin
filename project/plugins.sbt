resolvers += Resolver.url("sysa-bintray-repo", url("https://dl.bintray.com/sysa/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("io.sysa" % "sbt-rundeck" % "0.1.0")