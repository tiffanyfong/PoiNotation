# PoiNotation

A domain-specific language (DSL) written in Scala for describing glowsticking/poi moves and choreography. This is an independent project for the DSLs course in Fall 2016. See the wiki for a more detailed [writeup](https://github.com/tiffanyfong/PoiNotation/wiki/Final-writeup) and design process.

## DSL Input

Poi moves are represented by JSONs containing spatial properties for a particular move. A single poi move spans one circular arm rotation; in other words, it only contains motion for 360 degrees. Currently, the DSL supports these properties described below.

| Property     | Description                                   | Accepted Values                   | Default |
|--------------|-----------------------------------------------|-----------------------------------|---------|
| `extended`   | the arm's distance from the body              | `true, false`                     | `false` |
| `armSpin`    | the arm's rotational direction                | `cw, ccw, none`                   | `cw`    |
| `handleSpin` | the wrist's/handle's rotation direction       | `cw, ccw, none, inspin, antispin` | `cw`    |
| `rotations`  | number of handle rotations per 1 arm rotation | any nonnegative integer           | 0       |

Any unspecified property will use the default value. Thus, `{}` is equivalent to `{extended: false, armSpin: cw, handleSpin: cw, rotations: 0}`, which represents a non-moving poi.

Moves can be sequenced to create choreographies. `~` concatenates moves, while `*` repeats a move some number of times. The sample input below describes a sequence of one clockwise extension followed by two 3-petal antispin flowers.

```
{extended: true, rotations: 1, armSpin: cw, handleSpin: cw} ~
{extended: true, rotations: 2, armSpin: ccw, handleSpin: antispin} * 2
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
