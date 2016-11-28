# PoiNotation

A domain-specific language (DSL) writen in Scala for describing glowsticking/poi moves and choreography. See the wiki for a longer description and current progress.

## Install (once)

Ensure that you have installed [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), Scala, and [sbt](http://www.scala-sbt.org/download.html).

## Run

To run a PoiNotation program, go to the PoiNotation directory in terminal. 

Then, type `sbt "run <filepath>*"`, where `<filepath>*` represents any number of filepaths separated by spaces. The file path should lead to a text file containing the syntax for this DSL.

An alternate method is typing in `sbt` first. Once you are in sbt, type `run <filepath>*`.

Try `test.bot` to ensure that the DSL works with sample input.

## Output

Currently, this DSL will output a json that can be used in the [VisualSpinner3D](https://infiniteperplexity.github.io/visual-spinner-3d/composer.html) simulator. To simulate your poi moves from this DSL, click on "Import from JSON" and copy and paste the resulting json.
