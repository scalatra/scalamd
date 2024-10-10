scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"    % "3.12.0")
addSbtPlugin("com.github.sbt"     % "sbt-pgp"         % "2.3.0")
addSbtPlugin("org.scalariform"  % "sbt-scalariform" % "1.8.3")
