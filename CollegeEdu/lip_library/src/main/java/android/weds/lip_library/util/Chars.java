package android.weds.lip_library.util;

/**
 * Apache Commons CharSequenceUtils
 *
 * @author YRain
 */
public class Chars {
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Finds the first index in the {@code CharSequence} that matches the specified character.
     * </p>
     *
     * @param cs         the {@code CharSequence} to be processed, not null
     * @param searchChar the char to be searched for
     * @param start      the start index, negative starts at the string start
     * @return the index where the search char was found, -1 if not found
     */
    public static int indexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar, start);
        } else {
            final int sz = cs.length();
            if (start < 0) {
                start = 0;
            }
            for (int i = start; i < sz; i++) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
     *
     * @param cs         the {@code CharSequence} to be processed
     * @param searchChar the {@code CharSequence} to be searched for
     * @param start      the start index
     * @return the index where the search sequence was found
     */
    public static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
        // if (cs instanceof String && searchChar instanceof String) {
        // // TODO: Do we assume searchChar is usually relatively small;
        // // If so then calling toString() on it is better than reverting to
        // // the green implementation in the else block
        // return ((String) cs).indexOf((String) searchChar, start);
        // } else {
        // // TODO: Implement rather than convert to String
        // return cs.toString().indexOf(searchChar.toString(), start);
        // }
    }
}
