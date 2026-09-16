package test;

import java.util.Locale;

// Zajednicko formatiranje CSV reda: spaja polja zapetama, duple na 3 decimale
public final class Csv {
    private Csv() {
    }

    public static String row(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            Object field = fields[i];
            if (field instanceof Double d) {
                sb.append(String.format(Locale.US, "%.3f", d));
            } else {
                sb.append(field);
            }
        }
        return sb.toString();
    }
}
