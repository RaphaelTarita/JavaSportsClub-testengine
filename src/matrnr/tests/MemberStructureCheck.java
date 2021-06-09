package matrnr.tests;

import matrnr.Level;
import matrnr.Member;
import matrnr.Sports;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class MemberStructureCheck extends InternalStructureCheck {
    private static final Map<String, Predicate<Field>> STRUCTURE = new HashMap<>();

    static {
        STRUCTURE.put("name", f -> String.class.equals(f.getType()));
        STRUCTURE.put("sports", f -> {
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

    public MemberStructureCheck() {
        super(Member.class, 2, STRUCTURE);
    }
}
