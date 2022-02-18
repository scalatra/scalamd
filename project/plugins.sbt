scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"    % "3.9.12")
addSbtPlugin("com.github.sbt"     % "sbt-pgp"         % "2.1.2")
addSbtPlugin("org.scalariform"  % "sbt-scalariform" % "1.8.3")
