package matrnr.tests;

import matrnr.Level;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class LevelStructureCheck extends InternalStructureCheck {
    private static final Map<String, Predicate<Field>> STRUCTURE = new HashMap<>();

    static {
        STRUCTURE.put("mappedName", f -> String.class.equals(f.getType()));
    }

    public LevelStructureCheck() {
        super(Level.class, 1, STRUCTURE);
    }
}
