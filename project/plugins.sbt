scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"    % "3.11.3")
addSbtPlugin("com.github.sbt"     % "sbt-pgp"         % "2.3.0")
addSbtPlugin("org.scalariform"  % "sbt-scalariform" % "1.8.3")
