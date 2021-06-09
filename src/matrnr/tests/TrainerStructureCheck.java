package matrnr.tests;

import matrnr.Level;
import matrnr.Sports;
import matrnr.Trainer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class TrainerStructureCheck extends InternalStructureCheck {
    private static final Map<String, Predicate<Field>> STRUCTURE = new HashMap<>();

    static {
        STRUCTURE.put("accreditations", f -> {
            if (!Map.class.isAssignableFrom(f.getType())) {
                return false;
            }

            Type type = f.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                return Sports.class.equals(ptype.getActualTypeArguments()[0])
                    && Level.class.equals(ptype.getActualTypeArguments()[1]);
            }
            return false;
        });
    }

    public TrainerStructureCheck() {
        super(Trainer.class, 1, STRUCTURE);
    }
}
