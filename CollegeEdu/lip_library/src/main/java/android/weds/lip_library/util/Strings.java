package android.weds.lip_library.util;

import java.io.UnsupportedEncodingException;

public class Strings {

    // Empty checks
    // -----------------------------------------------------------------------

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    // Contains
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if CharSequence contains a search character, handling {@code null}. This method uses {@link String#indexOf(int)} if possible.
     * </p>
     * <p>
     * A {@code null} or empty ("") CharSequence will return {@code false}.
     * </p>
     * <p/>
     * <p>
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @return true if the CharSequence contains the search character,
     * false if not or {@code null} string input
     * @since 3.0 Changed signature from contains(String, int) to contains(CharSequence, int)
     */
    public static boolean isContains(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return false;
        }
        return Chars.indexOf(seq, searchChar, 0) >= 0;
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses {@link String#indexOf(String)} if possible.
     * </p>
     * <p>
     * A {@code null} CharSequence will return {@code false}.
     * </p>
     * <p/>
     * <p>
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence,
     * false if not or {@code null} string input
     * @since 3.0 Changed signature from contains(String, String) to contains(CharSequence, CharSequence)
     */
    public static boolean isContains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }
        return Chars.indexOf(seq, searchSeq, 0) >= 0;
    }

    // 这里可以提供更多地编码格式,另外由于部分编码格式是一致的所以会返回 第一个匹配的编码格式 GBK 和 GB2312
    public static final String[] encodes = new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1", "ISO-8859-2"};

    /**
     * 获取字符串编码格式
     *
     * @param str
     * @return
     */
    public static String getEncode(String str) {
        byte[] data = str.getBytes();
        byte[] b = null;
        a:
        for (int i = 0; i < encodes.length; i++) {
            try {
                b = str.getBytes(encodes[i]);
                if (b.length != data.length)
                    continue;
                for (int j = 0; j < b.length; j++) {
                    if (b[j] != data[j]) {
                        continue a;
                    }
                }
                return encodes[i];
            } catch (UnsupportedEncodingException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 将字符串转换成指定编码格式
     *
     * @param str
     * @param encode
     * @return
     */
    public static String transcoding(String str, String encode) {
        String df = "UTF-8";
        try {
            String en = getEncode(str);
            if (en == null)
                en = df;
            return new String(str.getBytes(en), encode);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * UTF-8编码格式判断
     *
     * @param rawtext 需要分析的数据
     * @return 是否为UTF-8编码格式
     */
    public static boolean isUTF8(byte[] rawtext) {
        int score = 0;
        int i, rawtextlen = 0;
        int goodbytes = 0, asciibytes = 0;
        // Maybe also use UTF8 Byte Order Mark: EF BB BF
        // Check to see if characters fit into acceptable ranges
        rawtextlen = rawtext.length;
        for (i = 0; i < rawtextlen; i++) {
            if ((rawtext[i] & (byte) 0x7F) == rawtext[i]) {
                // 最高位是0的ASCII字符
                asciibytes++;
                // Ignore ASCII, can throw off count
            } else if (-64 <= rawtext[i] && rawtext[i] <= -33
                    //-0x40~-0x21
                    && // Two bytes
                    i + 1 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65) {
                goodbytes += 2;
                i++;
            } else if (-32 <= rawtext[i]
                    && rawtext[i] <= -17
                    && // Three bytes
                    i + 2 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65 && -128 <= rawtext[i + 2]
                    && rawtext[i + 2] <= -65) {
                goodbytes += 3;
                i += 2;
            }
        }
        if (asciibytes == rawtextlen) {
            return false;
        }
        score = 100 * goodbytes / (rawtextlen - asciibytes);
        // If not above 98, reduce to zero to prevent coincidental matches
        // Allows for some (few) bad formed sequences
        if (score > 98) {
            return true;
        } else if (score > 95 && goodbytes > 30) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * bytes 转string
     * @param s
     * @return
     */
    public static byte[] changerStr2C(String s) {
        byte[] bytes = (s + " ").getBytes();
        bytes[bytes.length - 1] = '\0';
        return bytes;
    }
}
