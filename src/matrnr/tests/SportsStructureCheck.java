package matrnr.tests;

import matrnr.Sports;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SportsStructureCheck extends InternalStructureCheck {
    private static final Map<String, Predicate<Field>> STRUCTURE = new HashMap<>();

    public SportsStructureCheck() {
        super(Sports.class, 0, STRUCTURE);
    }
}
