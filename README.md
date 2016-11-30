# PoiNotation

A domain-specific language (DSL) writen in Scala for describing glowsticking/poi moves and choreography. See the wiki for a longer description and current progress.

## DSL Input

Poi Moves are represented by JSONs containing spatial properties for a particular move. Currently, this DSL supports these properties: `extended` (is the arm extended or not), `armSpin` (which direction is the arm moving), `handleSpin` (which direction is the handle/wrist moving), and `rotations` (number of handle rotations per one arm rotation).

Moves can be sequenced to create choreographies. `~` concatenates moves, while `*` repeats a move some number of times. The sample input below describes a sequence of one clockwise extension followed by two 3-petal antispin flowers.

```
{extended: true, rotations: 1, armSpin: CW, handleSpin: CW} ~
{extended: true, rotations 2, armSpin: CCW, handleSpin: ANTISPIN} * 2
```

## Install (once)

Ensure that you have installed [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), Scala, and [sbt](http://www.scala-sbt.org/download.html).

Then, clone this repository.

```
$ git clone https://github.com/tiffanyfong/PoiNotation.git
```

## Run

```
$ cd PoiNotation
$ sbt "run FILEPATH*"
```

or

```
$ cd PoiNotation
$ sbt
  > run FILEPATH*
```

`FILEPATH*` represents any number of filepaths separated by spaces. The file path should lead to a text file containing the syntax for this DSL.

Try `test.bot` to ensure that the DSL works with sample input.

## Output

Currently, this DSL will output a json that can be used in the [VisualSpinner3D](https://infiniteperplexity.github.io/visual-spinner-3d/composer.html) simulator. To simulate your poi moves from this DSL, click on "Import from JSON" and copy and paste the resulting json.

Moves can also be simulated by the [2D Poi Pattern Visualizer](https://rfong.github.io/poi/). The DSL will output a URL containing specific settings for a particular move.
