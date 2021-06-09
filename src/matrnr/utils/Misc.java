package matrnr.utils;

import matrnr.Level;
import matrnr.Sports;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Misc {
    private Misc() {
    }

    public static String getRandomString(int size) {
        Random r = new Random();
        int leftlimit = 0x30; // '0'
        int rightLimit = 0x7A; // 'z'

        return r.ints(leftlimit, rightLimit + 1)
            .filter(i -> (i <= 0x39 || i >= 0x41) && (i <= 0x5A || i >= 0x61))
            .limit(size)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    public static String getRandomString() {
        return getRandomString(new Random().nextInt(19) + 1);
    }

    public static Map<Sports, Level> getRandomSportsMap(double size) {
        Sports[] all = Sports.values();
        int actualSize = (int) (all.length * size);
        Random r = new Random();

        return r.ints(0, all.length - 1)
            .distinct()
            .limit(actualSize)
            .collect(HashMap::new, (m, i) -> m.put(all[i], Level.PROFESSIONAL), HashMap::putAll);
    }

    public static Map<Sports, Level> getRandomSportsMap() {
        return getRandomSportsMap(0.5);
    }
}
