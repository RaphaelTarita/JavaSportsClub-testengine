package matrnr.tests;

import matrnr.Member;
import matrnr.Sports;
import matrnr.SportsClub;
import matrnr.Trainer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class SportsClubStructureCheck extends InternalStructureCheck {
    private static final Map<String, Predicate<Field>> STRUCTURE = new HashMap<>();

    static {
        STRUCTURE.put("name", f -> String.class.equals(f.getType()));
        STRUCTURE.put("members", f -> {
            if (!Set.class.isAssignableFrom(f.getType())) {
                return false;
            }

            Type type = f.getGenericType();
            if (type instanceof ParameterizedType) {
                return Member.class.equals(((ParameterizedType) type).getActualTypeArguments()[0]);
            }
            return false;
        });
        STRUCTURE.put("offeredSports", f -> {
            if (!Map.class.isAssignableFrom(f.getType())) {
                return false;
            }

            Type type = f.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;

                Type nestedType1 = ptype.getActualTypeArguments()[0];
                Type nestedType2 = ptype.getActualTypeArguments()[1];
                if (!Sports.class.equals((nestedType1))) {
                    return false;
                }

                if (nestedType2 instanceof ParameterizedType) {
                    ParameterizedType nestedPType2 = (ParameterizedType) nestedType2;
                    Class<?> nestedType2AsClass;
                    try {
                        nestedType2AsClass = nestedPType2.getRawType() instanceof Class<?>
                            ? (Class<?>) nestedPType2.getRawType()
                            : SportsClub.class.getClassLoader().loadClass(nestedPType2.getTypeName());
                    } catch (ClassNotFoundException ex) {
                        return false;
                    }

                    return Set.class.isAssignableFrom(nestedType2AsClass) && Trainer.class.equals(nestedPType2.getActualTypeArguments()[0]);
                }
            }
            return false;
        });
        STRUCTURE.put("feePerSports", f -> BigDecimal.class.equals(f.getType()));
    }

    public SportsClubStructureCheck() {
        super(SportsClub.class, 4, STRUCTURE);
    }
}
