# PoiNotation

A domain-specific language (DSL) writen in Scala for describing glowsticking/poi moves and choreography. See the wiki for a longer description and current progress.

## Install (once)

Ensure that you have installed [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), Scala, and [sbt](http://www.scala-sbt.org/download.html).

Clone this repository, and go to the PoiNotation directory in terminal. Type `sbt` to ensure that sbt was installed properly. If you just installed sbt, this might take a while.

## Run

To run a PoiNotation program, go to the PoiNotation directory in terminal. Then, type `sbt run` (or if you're already in sbt, then simply `run`). You will be prompted for a file path that contains the syntax for this DSL. Try `test.bot` to ensure that the DSL works with sample input.
