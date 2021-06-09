package matrnr.utils;

import matrnr.Level;
import matrnr.Member;
import matrnr.Sports;
import matrnr.SportsClub;
import matrnr.Trainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultObjects {
    private static final Map<Class<?>, Object> mappings = new HashMap<>();
    private static final Member m = new Member("Hans");
    private static final Trainer t = new Trainer("Dieter", Map.of(Sports.ARCHERY, Level.BEGINNER));
    private static final SportsClub sc = new SportsClub("Club", BigDecimal.TEN);

    static {
        sc.addMember(m);
        sc.addMember(t);

        mappings.put(byte.class, (byte) 0);
        mappings.put(short.class, (short) 0);
        mappings.put(int.class, 0);
        mappings.put(long.class, 0L);
        mappings.put(float.class, 0f);
        mappings.put(double.class, 0.0);
        mappings.put(boolean.class, false);
        mappings.put(char.class, '\0');
        mappings.put(String.class, "testing");
        mappings.put(BigDecimal.class, BigDecimal.ZERO);
        mappings.put(Map.class, new HashMap<>());
        mappings.put(List.class, new ArrayList<>());
        mappings.put(Level.class, Level.BEGINNER);
        mappings.put(Sports.class, Sports.ARCHERY);
    }

    private DefaultObjects() {
    }

    public static Object get(Class<?> cls) {
        if (byte.class.equals(cls)) {
            return (byte) 0;
        } else if (short.class.equals(cls)) {
            return (short) 0;
        } else if (int.class.equals(cls)) {
            return 0;
        } else if (long.class.equals(cls)) {
            return 0L;
        } else if (float.class.equals(cls)) {
            return 0f;
        } else if (double.class.equals(cls)) {
            return 0.0;
        } else if (boolean.class.equals(cls)) {
            return false;
        } else if (char.class.equals(cls)) {
            return '\0';
        } else if (Member.class.equals(cls)) {
            return new Member(m.getName());
        } else if (Trainer.class.equals(cls)) {
            return new Trainer(t.getName(), t.getAccreditations());
        } else if (SportsClub.class.equals(cls)) {
            SportsClub cpy = new SportsClub(sc.getName(), sc.getFeePerSports());
            for (Member m : sc.getMembers()) {
                cpy.addMember(m);
            }
            return cpy;
        } else {
            return mappings.get(cls);
        }
    }
}

