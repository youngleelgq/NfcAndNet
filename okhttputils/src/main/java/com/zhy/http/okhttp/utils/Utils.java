
package com.zhy.http.okhttp.utils;



/**
 * @author yangningbo
 * @version V1.0
 * @Description：字符转换工具类 <p>
 * 创建日期：2013-9-10
 * </p>
 * @see
 */
public class Utils {
    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    // 字节数组转String
    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, 0, bytes.length);
    }

    // 字节数组转String
    public static String bytesToString(byte[] bytes, int len) {
        return bytesToString(bytes, 0, len);
    }

    // 字节数组转String
    public static String bytesToString(byte[] bytes, int pos, int len) {
        return new String(bytes, pos, len);
    }

    //// 字节数组转BCD码String
    public static String byte2BCDStr(byte[] bytes) {
//        return new String(bytes, 0, bytes.length);
        String bcdStr = Utils.bytesToHexString(bytes);
        return bcdStr;
    }

    /**
     * 字节数组转bcd int类型
     *
     * @param bytes
     * @return
     */
    public static int byte2BCDInt(byte[] bytes) {
        String bcdStr = new String(bytes, 0, bytes.length);
        return Integer.valueOf(bcdStr);
    }

    // 字节数组转16进制String
    public static String bytesToHexString(byte[] bytes) {
        return bytesToHexString(bytes, "");
    }

    // 字节数组转16进制String
    public static String bytesToHexString(byte[] bytes, String separator) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02X" + separator, b & 0xFF));
        }
        return sb.toString();
    }

    // 字节数组转16进制String
    public static String bytesToHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;

        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

    // 字节数组转short
    public static short bytesToShort(byte[] bytes) {
        return (short) (((bytes[0] & 0xFF) << 8) + (bytes[1] & 0xFF));
    }

    // short转字节数组
    public static byte[] shortToBytes(short value) {
        return new byte[]{
                (byte) (value >>> 8), (byte) (value)
        };
    }

    // 字节数组转int
    public static int bytesToInt(byte[] bytes) {
        int ret = 0;

        for (byte b : bytes) {
            ret <<= 8;
            ret |= b & 0xFF;
        }
        return ret;
    }

    // 字节数组转int
    public static int bytesToInt(byte[] bytes, int s, int n) {
        int ret = 0;

        final int e = s + n;
        for (int i = s; i < e; ++i) {
            ret <<= 8;
            ret |= bytes[i] & 0xFF;
        }
        return ret;
    }

    // int转字节数组
    public static byte[] intToBytes(int n) {
        return new byte[]{
                (byte) (0x000000ff & (n >>> 24)),
                (byte) (0x000000ff & (n >>> 16)),
                (byte) (0x000000ff & (n >>> 8)), (byte) (0x000000ff & (n))
        };
    }

    public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // 获取apdu响应码
    public static int getSW1SW2(byte[] data) {
        if (data.length < 2) {
            return 0;
        }

        int sw1sw2 = (data[data.length - 2] << 8) & 0xFF00;
        sw1sw2 |= data[data.length - 1] & 0x00FF;
        return sw1sw2;
    }

    public static int getSW1(byte[] data) {
        int sw1sw2 = getSW1SW2(data);
        byte[] content = intToBytes(sw1sw2);
        byte[] result = new byte[1];
        result[0] = content[0];
        return bytesToInt(result);
    }

    // 去除apdu响应码
    public static byte[] stripSW1SW2(byte[] data) {
        if (data.length < 2) {
            return new byte[0];
        }

        byte[] strippedData = new byte[data.length - 2];
        for (int i = 0; i < data.length - 2; i++)
            strippedData[i] = data[i];
        return strippedData;
    }

    // 分转元
    public static String fen2Yuan(String fen) {
        float yuan = Integer.valueOf(fen) / 100.0f;
        return String.format("%.2f", yuan);
    }

    // 元转分
    public static String yuan2Fen(String yuan) {
        return String.valueOf((int) (Float.valueOf(yuan) * 100));
    }

    private static String toRandom(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return Utils.bytesToHexString(data)/* .substring(0, 8) */;
    }
    //避免用户连续快速点击某个按钮
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;

        }
        lastClickTime = time;
        return false;
    }

    /**
     * 字节数组高4位与低4位互换
     *
     * @param a
     * @return
     */
    public static byte bswap(byte a) {
        byte b = 0;
        for (int i = 0; i < 8; ++i)
            b |= ((a & (1 << i)) == 0 ? 0 : 1) << (7 - i);
        return b;
    }

    public static byte[] removePadding(byte[] bodyContentPadding) {
        String paddingStr = String.format("%02X", bodyContentPadding[bodyContentPadding.length - 1] & 0xFF);//byte转HexString
        int padding = Integer.valueOf(paddingStr, 16);
        byte[] result = new byte[bodyContentPadding.length - padding];
        System.arraycopy(bodyContentPadding, 0, result, 0, result.length);
        return result;
    }
}

