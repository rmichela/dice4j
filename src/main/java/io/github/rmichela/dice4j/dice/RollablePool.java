package io.github.rmichela.dice4j.dice;

import java.util.List;

public interface RollablePool extends Rollable {
    List<Rollable> getPool();
}
