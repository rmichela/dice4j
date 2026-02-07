# Dice4j

A minimal-dependency Java library for parsing and evaluating dice expressions commonly used in tabletop role-playing games like Dungeons & Dragons. Dice4j follows the dice notation syntax conventions of Foundry Virtual Tabletop and provides a simple, flexible API for rolling dice with modifiers, pools, and arithmetic operations.

## Features

- **Zero runtime dependencies** - ANTLR is used only for parsing, no external libs required at runtime
- **Comprehensive dice notation support** - Standard dice notation (NdS), keep/drop modifiers, reroll mechanics, exploding dice
- **Min/Max clamping** - Set minimum and maximum values for dice results
- **Arithmetic expressions** - Full support for addition, subtraction, multiplication, division, and parentheses
- **Dice pools** - Mix multiple dice types in a single expression
- **Type-safe API** - Clean interfaces with builder patterns
- **Deterministic testing** - Built-in support for fixed roll sequences

## Installation

Add Dice4j to your Maven project:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>dice4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

Requires Java 17 or higher.

## Basic Usage

The primary entry point is the `DiceExpression.evaluate()` method, which parses a dice expression string and returns a `Rollable` object:

```java
// Parse and roll a simple expression
Rollable dice = DiceExpression.evaluate("2d6");
Rolled result = dice.roll();
int total = result.total();

System.out.println("Rolled: " + total);
```

### Common Dice Expressions

```java
// Single die
Rollable d20 = DiceExpression.evaluate("1d20");

// Multiple dice
Rollable damage = DiceExpression.evaluate("3d6");

// With constant modifier
Rollable attackRoll = DiceExpression.evaluate("1d20 + 5");

// Advantage (roll 2d20, keep highest)
Rollable advantage = DiceExpression.evaluate("2d20kh1");

// Standard D&D ability score (4d6, keep 3 highest)
Rollable abilityScore = DiceExpression.evaluate("4d6kh3");
```

### Inspecting Roll Results

```java
Rollable dice = DiceExpression.evaluate("4d6kh3");
Rolled result = dice.roll();

// Get the total (only counts kept dice)
int total = result.total();

// Get individual die results
List<RolledDie> allDice = result.gather();
for (RolledDie die : allDice) {
    int value = die.getValue();
    int sides = die.getSides();
    boolean wasKept = die.isKept();

    System.out.println("d" + sides + ": " + value +
                       (wasKept ? " (kept)" : " (dropped)"));
}
```

### Testing with Fixed Rolls

For unit tests, use `FixedSequenceRoller` to generate predictable results:

```java
import com.example.dice4j.roller.DieRoller;
import com.example.dice4j.roller.FixedSequenceRoller;

Rollable dice = DiceExpression.evaluate("2d6 + 3");

// Create a roller that returns specific values in sequence
DieRoller fixedRoller = new FixedSequenceRoller(4, 5);
Rolled result = dice.roll(fixedRoller);

int total = result.total(); // Will be 4 + 5 + 3 = 12
```

## Advanced Usage

### Dice Pools

Dice4j supports two types of pools:

#### Implicit Pools (Standard Notation)

Created using the familiar NdS notation:

```java
// Roll 4 six-sided dice
Rollable pool = DiceExpression.evaluate("4d6");
int total = pool.roll().total();
```

#### Explicit Pools (Mixed Dice Types)

Use curly braces `{}` to create pools with different dice types or constants:

```java
// Mix a d6, d8, and d20 in one pool
Rollable mixed = DiceExpression.evaluate("{1d6, 1d8, 1d20}");

// Include constants in a pool
Rollable withConstant = DiceExpression.evaluate("{2d6, 5}");

// Multiple dice of different types
Rollable versatile = DiceExpression.evaluate("{1d8, 1d10}");
```

### Pool Modifiers

Modifiers manipulate which dice in a pool contribute to the final total. They can be stacked to create complex behaviors.

#### Keep Highest / Keep Lowest

```java
// Keep N highest dice (kh or khN)
Rollable advantageD20 = DiceExpression.evaluate("2d20kh1");  // D&D advantage
Rollable abilityScore = DiceExpression.evaluate("4d6kh3");   // D&D ability scores
Rollable bestOf5 = DiceExpression.evaluate("5d6kh");         // kh defaults to 1

// Keep N lowest dice (kl or klN)
Rollable disadvantage = DiceExpression.evaluate("2d20kl1");  // D&D disadvantage
Rollable worst3 = DiceExpression.evaluate("4d6kl3");
```

#### Drop Highest / Drop Lowest

```java
// Drop N highest dice (dh or dhN)
Rollable removeHigh = DiceExpression.evaluate("5d6dh2");     // Drop 2 highest

// Drop N lowest dice (dl or dlN)
Rollable removeLow = DiceExpression.evaluate("4d6dl1");      // Drop 1 lowest
```

#### Stacking Modifiers

Modifiers are applied left-to-right and can be combined:

```java
// Roll 6d6, keep 4 highest, then drop 1 lowest from those 4
Rollable complex = DiceExpression.evaluate("6d6kh4dl1");
```

#### Reroll Modifiers

Reroll dice that meet certain conditions:

```java
// Reroll once if condition is met (r[op][N])
Rollable reroll1s = DiceExpression.evaluate("2d6r1");        // Reroll any 1s once
Rollable rerollLow = DiceExpression.evaluate("2d6r<3");      // Reroll any roll < 3
Rollable rerollHigh = DiceExpression.evaluate("2d6r>5");     // Reroll any roll > 5
Rollable defaultReroll = DiceExpression.evaluate("2d6r");    // Defaults to r=1

// Recursive reroll (keeps rerolling until condition fails)
Rollable exploding = DiceExpression.evaluate("1d6rr6");      // Exploding sixes
Rollable noOnes = DiceExpression.evaluate("4d6rr1");         // Keep rerolling 1s
```

**Reroll Operators:**
- `=` - Equal (default if omitted)
- `<` - Less than
- `>` - Greater than

#### Exploding Dice

Exploding dice (also called "open-ended rolls") automatically re-roll when the maximum value is rolled, adding the new result to the total:

```java
// Explode once (x or xN)
Rollable explodeOnce = DiceExpression.evaluate("4d6x1");     // Each die explodes once on max
Rollable explode2 = DiceExpression.evaluate("2d10x2");       // Each die can explode up to 2 times

// Explode recursively (unlimited)
Rollable unlimited = DiceExpression.evaluate("1d6x");        // Keep exploding on max (x defaults to unlimited)
Rollable savageWorlds = DiceExpression.evaluate("1d10x");    // Classic exploding d10
```

**How it works:**
- When a die rolls its maximum value (e.g., 6 on a d6), it "explodes"
- The die is rolled again and the new result is added to the pool
- If the new die also rolls max, it explodes again (up to the explosion limit)
- `x` or `xN` where N is the maximum number of times any die can explode
- Without a number, `x` defaults to unlimited explosions

#### Min/Max Clamping

Replace dice values that fall outside a desired range:

```java
// Set minimum value (min)
Rollable minDamage = DiceExpression.evaluate("2d6min3");     // Any roll < 3 becomes 3
Rollable greatWeapon = DiceExpression.evaluate("2d6min2");   // Great Weapon Fighting (D&D 5e)

// Set maximum value (max)
Rollable capped = DiceExpression.evaluate("4d10max8");       // Any roll > 8 becomes 8

// Combine both (clamp to range)
Rollable range = DiceExpression.evaluate("4d10min3max8");    // All rolls between 3-8
```

**How it works:**
- `minN` - Any die result below N is replaced with N
- `maxN` - Any die result above N is replaced with N
- Can be combined to create a range: rolls are clamped to [min, max]
- Original dice remain in the pool but are marked as dropped

### Arithmetic Operations

Dice expressions support full arithmetic with proper operator precedence:

```java
// Addition and subtraction
Rollable attack = DiceExpression.evaluate("1d20 + 5");
Rollable penalty = DiceExpression.evaluate("1d20 - 2");

// Multiplication and division
Rollable critical = DiceExpression.evaluate("2d6 * 2");
Rollable halved = DiceExpression.evaluate("4d6 / 2");        // Integer division

// Negation
Rollable negative = DiceExpression.evaluate("-1d6");

// Parentheses for grouping
Rollable grouped = DiceExpression.evaluate("(1d6 + 2) * 3");
Rollable average = DiceExpression.evaluate("(1d6 + 1d6) / 2");

// Complex expressions
Rollable complex = DiceExpression.evaluate("2d20kh1 + 5 + 1d4");
```

**Operator Precedence (highest to lowest):**
1. Parentheses `()`
2. Unary negation `-`
3. Multiplication `*`, Division `/`
4. Addition `+`, Subtraction `-`

### Complete Examples

#### D&D 5e Character Creation

```java
// Generate ability scores using the "4d6 drop lowest" method
for (int i = 0; i < 6; i++) {
    Rollable abilityRoll = DiceExpression.evaluate("4d6kh3");
    int score = abilityRoll.roll().total();
    System.out.println("Ability " + (i + 1) + ": " + score);
}
```

#### Advantage/Disadvantage System

```java
// Advantage: roll 2d20, take the higher
Rollable advantage = DiceExpression.evaluate("2d20kh1 + 5");
int advantageRoll = advantage.roll().total();

// Disadvantage: roll 2d20, take the lower
Rollable disadvantage = DiceExpression.evaluate("2d20kl1 + 5");
int disadvantageRoll = disadvantage.roll().total();
```

#### Weapon Damage with Critical Hit

```java
// Normal damage
Rollable normalDamage = DiceExpression.evaluate("1d8 + 3");

// Critical hit (double the dice)
Rollable criticalDamage = DiceExpression.evaluate("2d8 + 3");

// Alternative: roll once and double the result
Rollable doubleDamage = DiceExpression.evaluate("(1d8 + 3) * 2");
```

#### Exploding Dice (Savage Worlds)

```java
// In Savage Worlds, when you roll the max value, you roll again and add
Rollable explodingD6 = DiceExpression.evaluate("1d6x");
int result = explodingD6.roll().total();
// Could roll 6, 6, 6, 3 = 21!

// Multiple exploding dice
Rollable skillRoll = DiceExpression.evaluate("2d10x");
// Each d10 that rolls a 10 will explode independently
```

#### Mixed Damage Types

```java
// A versatile weapon that can be used one or two-handed
Rollable oneHanded = DiceExpression.evaluate("1d8");
Rollable twoHanded = DiceExpression.evaluate("1d10");

// Or as a pool to roll both and compare
Rollable versatile = DiceExpression.evaluate("{1d8, 1d10}");
```

#### Great Weapon Fighting (D&D 5e)

```java
// Great Weapon Fighting: reroll 1s and 2s once
Rollable gwf = DiceExpression.evaluate("2d6min3");
int damage = gwf.roll().total();
// Any roll of 1 or 2 becomes a 3
```

#### Bounded Randomness

```java
// Ensure results stay within a specific range
Rollable bounded = DiceExpression.evaluate("4d10min3max8");
int result = bounded.roll().total();
// Each die result will be between 3 and 8
// Total will be between 12 (4×3) and 32 (4×8)
```

## API Reference

### Core Classes

- **`DiceExpression`** - Main entry point for parsing expressions
  - `static Rollable evaluate(String expression)` - Parse a dice expression string

- **`Rollable`** - Interface for anything that can be rolled
  - `Rolled roll()` - Roll with default random roller
  - `Rolled roll(DieRoller roller)` - Roll with custom roller

- **`Rolled`** - Interface for roll results
  - `int total()` - Sum of all kept dice
  - `List<RolledDie> gather()` - Flatten to individual die results

- **`DieRoller`** - Interface for generating random values
  - `RandomRoller` - Uses `java.util.Random`
  - `FixedSequenceRoller` - Returns predetermined values for testing

### Expression Syntax

| Syntax | Description | Example |
|--------|-------------|---------|
| `NdS` | Roll N dice with S sides | `2d6`, `1d20` |
| `{...}` | Explicit pool of mixed dice | `{1d6, 1d8, 1d20}` |
| `khN` | Keep N highest dice | `4d6kh3` |
| `klN` | Keep N lowest dice | `2d20kl1` |
| `dhN` | Drop N highest dice | `5d6dh2` |
| `dlN` | Drop N lowest dice | `4d6dl1` |
| `r[op][N]` | Reroll once if condition met | `2d6r1`, `2d6r<3` |
| `rr[op][N]` | Reroll recursively | `1d6rr6` |
| `x[N]` | Exploding dice (reroll on max) | `1d6x`, `4d6x2` |
| `minN` | Set minimum value (clamp low rolls) | `2d6min3` |
| `maxN` | Set maximum value (clamp high rolls) | `4d10max8` |
| `+` | Addition | `1d20 + 5` |
| `-` | Subtraction or negation | `1d20 - 2`, `-1d6` |
| `*` | Multiplication | `2d6 * 2` |
| `/` | Integer division (rounds down) | `4d6 / 2` |
| `()` | Grouping | `(1d6 + 2) * 3` |

## Contributing

Contributions are welcome! Please ensure all tests pass and code is formatted using Google Java Format.

```bash
# Run tests
mvn test

# Format code
mvn spotless:apply
```

## License

[MIT License](https://opensource.org/license/mit)
