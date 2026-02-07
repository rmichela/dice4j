package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RolledDieTest {

    @Test
    void testValidConstruction() {
        RolledDie die = new RolledDie(6, 4);
        assertThat(die.getSides()).isEqualTo(6);
        assertThat(die.getValue()).isEqualTo(4);
        assertThat(die.isKept()).isTrue();
    }

    @Test
    void testConstructionWithMinValue() {
        RolledDie die = new RolledDie(6, 1);
        assertThat(die.getValue()).isEqualTo(1);
    }

    @Test
    void testConstructionWithMaxValue() {
        RolledDie die = new RolledDie(6, 6);
        assertThat(die.getValue()).isEqualTo(6);
        assertThat(die.isMax()).isTrue();
    }

    @Test
    void testConstructionWithInvalidSides() {
        assertThatThrownBy(() -> new RolledDie(0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die must have a positive number of sides");

        assertThatThrownBy(() -> new RolledDie(-1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die must have a positive number of sides");
    }

    @Test
    void testConstructionWithValueTooLarge() {
        assertThatThrownBy(() -> new RolledDie(6, 7))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die value must be between 1 and 6");
    }

    @Test
    void testConstructionWithValueTooSmall() {
        assertThatThrownBy(() -> new RolledDie(6, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die value must be between 1 and 6");
    }

    @Test
    void testConstantDie() {
        RolledDie die = RolledDie.constant(42);
        assertThat(die.getSides()).isEqualTo(RolledDie.CONSTANT_SIDES);
        assertThat(die.getValue()).isEqualTo(42);
        assertThat(die.isConstant()).isTrue();
    }

    @Test
    void testConstantDieWithNegativeValue() {
        RolledDie die = RolledDie.constant(-10);
        assertThat(die.getValue()).isEqualTo(-10);
        assertThat(die.isConstant()).isTrue();
    }

    @Test
    void testKeep() {
        RolledDie die = new RolledDie(6, 4);
        die.drop();
        assertThat(die.isKept()).isFalse();
        die.keep();
        assertThat(die.isKept()).isTrue();
    }

    @Test
    void testDrop() {
        RolledDie die = new RolledDie(6, 4);
        assertThat(die.isKept()).isTrue();
        die.drop();
        assertThat(die.isKept()).isFalse();
    }

    @Test
    void testIsMax() {
        RolledDie maxDie = new RolledDie(6, 6);
        assertThat(maxDie.isMax()).isTrue();

        RolledDie notMaxDie = new RolledDie(6, 5);
        assertThat(notMaxDie.isMax()).isFalse();
    }

    @Test
    void testIsConstant() {
        RolledDie constant = RolledDie.constant(5);
        assertThat(constant.isConstant()).isTrue();

        RolledDie normalDie = new RolledDie(6, 4);
        assertThat(normalDie.isConstant()).isFalse();
    }

    @Test
    void testGatherDice() {
        RolledDie die = new RolledDie(6, 4);
        assertThat(die.gatherDice()).containsExactly(die);
    }

    @Test
    void testTotal() {
        RolledDie keptDie = new RolledDie(6, 4);
        assertThat(keptDie.total()).isEqualTo(4);

        RolledDie droppedDie = new RolledDie(6, 3);
        droppedDie.drop();
        assertThat(droppedDie.total()).isEqualTo(0);
    }

    @Test
    void testToStringForKeptDie() {
        RolledDie die = new RolledDie(6, 4);
        assertThat(die.toString()).isEqualTo("[4/6]");
    }

    @Test
    void testToStringForDroppedDie() {
        RolledDie die = new RolledDie(6, 4);
        die.drop();
        assertThat(die.toString()).isEqualTo("[4/6x]");
    }

    @Test
    void testToStringForConstant() {
        RolledDie die = RolledDie.constant(10);
        assertThat(die.toString()).isEqualTo("[10/1]");
    }

    @Test
    void testCompareTo() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 5);
        RolledDie die3 = new RolledDie(6, 2);

        assertThat(die1.compareTo(die2)).isNegative();
        assertThat(die2.compareTo(die1)).isPositive();
        assertThat(die1.compareTo(die3)).isZero();
    }

    @Test
    void testEquals() {
        RolledDie die1 = new RolledDie(6, 4);
        RolledDie die2 = new RolledDie(6, 4);
        RolledDie die3 = new RolledDie(6, 5);
        RolledDie die4 = new RolledDie(8, 4);

        assertThat(die1).isEqualTo(die2);
        assertThat(die1).isNotEqualTo(die3);
        assertThat(die1).isNotEqualTo(die4);
    }

    @Test
    void testHashCode() {
        RolledDie die1 = new RolledDie(6, 4);
        RolledDie die2 = new RolledDie(6, 4);

        assertThat(die1.hashCode()).isEqualTo(die2.hashCode());
    }

    @Test
    void testKeptStatusAffectsEquality() {
        RolledDie die1 = new RolledDie(6, 4);
        RolledDie die2 = new RolledDie(6, 4);
        die2.drop();

        assertThat(die1).isNotEqualTo(die2);
    }

    @Test
    void testGatherPools() {
        RolledDie die = new RolledDie(6, 4);
        var pools = die.gatherPools();

        assertThat(pools).hasSize(1);
        assertThat(pools.get(0).gatherDice()).containsExactly(die);
        assertThat(pools.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsForConstantDie() {
        RolledDie die = RolledDie.constant(10);
        var pools = die.gatherPools();

        assertThat(pools).hasSize(1);
        assertThat(pools.get(0).gatherDice()).containsExactly(die);
        assertThat(pools.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsForDroppedDie() {
        RolledDie die = new RolledDie(6, 3);
        die.drop();
        var pools = die.gatherPools();

        assertThat(pools).hasSize(1);
        assertThat(pools.get(0).gatherDice()).containsExactly(die);
        assertThat(pools.get(0).getModifiedBy()).isNull();
    }
}
