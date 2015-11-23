package ng.prk.prkngandroid.util;

/**
 * Utility methods for arrays
 */
public class ArrayUtils {

    public static String join(Object[] array) {
        return join(array, ",");
    }

    public static String join(Object[] array, String sep) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(sep);
            sb.append(array[i]);
        }
        return sb.toString();
    }
}