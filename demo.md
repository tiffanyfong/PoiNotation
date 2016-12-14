# PoiNotation

## Why?

Describing poi is hard.

## Language

### Syntax (Demo)

```
{extended: true, rotations: 1} ~
{extended: true, handle: inspin, rotations: 3} * 2 ~
{extended: true, handle: antispin, rotations: 2} ~
{extended: true, handle: antispin, rotations: 3}
# extension, 2 * (2-petal inspin flower), triquetra, 4-petal antispin flower
```

### IR

```scala
case class OnePoiMove(extended: Boolean = false, armSpin: Spin = CW, handleSpin: Spin = CW, rotations: Int = 0)
```

### Semantics

IR -> Simulators

## Insights

1. Internal vs. External
2. So many features, so little time


