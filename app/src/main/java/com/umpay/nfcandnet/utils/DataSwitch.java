package com.umpay.nfcandnet.utils;

import android.text.TextUtils;

import java.io.StringReader;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：字符串转换工具类 <p>
 * 创建日期：2013-9-10
 * </p>
 * @see
 */
public class DataSwitch {

    /**
     * string转unic
     *
     * @param str
     * @return
     */
    public static String encodeUnicode(String str) {

        if (null == str) {
            str = "";
        }

        StringBuffer strUnicode = new StringBuffer();

        for (char ch : str.toCharArray()) {
            if (ch > 128) {
                strUnicode.append(Integer.toHexString(ch));
            } else {
                strUnicode.append("00" + Integer.toHexString(ch));
            }
        }
        return strUnicode.toString().toUpperCase();
    }

    public static String encode80VUnicode(String str) {

        if (null == str) {
            str = "";
        }

        StringBuffer strUnicode = new StringBuffer();

        for (char ch : str.toCharArray()) {
            if (ch > 128) {
                strUnicode.append(Integer.toHexString(ch));
            } else {
                strUnicode.append("00" + Integer.toHexString(ch));
            }
        }
        return "80" + encodeUnicode(str);
    }

    /**
     * 解码TLV格式的数据，得到姓名 T:80-汉字 81-英文
     *
     * @param str
     * @return
     * @throws RuntimeException
     */
    public static String decodeName_TLV(String str) throws RuntimeException {

        if (TextUtils.isEmpty(str)) {
            throw new RuntimeException("Name value is null");
        }
        if (str.length() < 4) {
            throw new RuntimeException("Name length error");
        }

        String tag = str.substring(0, 2);
        String lengthByteHex = str.substring(2, 4);
        int lengthByte = Integer.parseInt((lengthByteHex), 16);

        if (lengthByte * 2 <= str.length() - 4) {

            String name = str.substring(4, 4 + lengthByte * 2);
            if ("80".equals(tag)) {

                return decodeUnicode(name);
            } else if ("81".equals(tag)) {
                return ASCII2String(name, false);

            } else {
                throw new RuntimeException("format error");
            }

        } else {
            throw new RuntimeException("length error");
        }

    }

    /**
     * 编码姓名得到TLV格式的数据 T:80-汉字 81-英文
     *
     * @param str
     * @param bLength 要求多长 不足 用FF填充
     * @return
     * @throws RuntimeException
     */
    public static String encodeName_TLV(String str, int bLength)
            throws RuntimeException {
        if (TextUtils.isEmpty(str)) {
            throw new RuntimeException("Name value is null");
        }

        String tag = "81";
        String name = "";
        for (char ch : str.toCharArray()) {

            if (ch > 128) {
                tag = "80";
                break;
            }

        }

        if ("80".equals(tag)) {
            name = encodeUnicode(str);
        }

        if ("81".equals(tag)) {
            name = String2ASCII(str);
        }

        name = tag + Dec2Hex(name.length() / 2, 1, false) + name;
        if (name.length() > bLength * 2) {
            name = name.substring(0, bLength * 2);
        } else {
            name = RPadFF(name, bLength);
        }
        return name;

    }

    /**
     * unic转String
     *
     * @param unic
     * @return
     */
    public static String decodeUnicode(String unic) {
        if (null == unic) {
            unic = "";
        }
        StringBuffer sbString = new StringBuffer();

        for (int offset = 0; offset < unic.length() - 1; offset = offset + 4) {
            String temp = unic.substring(offset, 4 + offset);
            if ("FFFF".equals(temp)) {
                break;

            }
            char c = (char) Integer.parseInt(temp, 16);

            sbString.append(c);
        }

        return sbString.toString();
    }

    /**
     * 80+unic转String
     *
     * @param unic
     * @return
     */
    public static String decode80VUnicode(String unic) throws RuntimeException {

        if (null == unic) {
            unic = "";
        }

        String fristChar = unic.substring(0, 2);
        if ("80".equals(fristChar)) {
            unic = unic.substring(2);
        } else {
            throw (new RuntimeException("非80开头"));
        }
        return decodeUnicode(unic);
    }

    /**
     * 十进制字符转16进制，用0左边补齐
     *
     * @param dec
     * @param bLength 字节长度
     * @param ifLV
     * @return
     * @throws RuntimeException
     */
    public static String Dec2Hex(int dec, int bLength, boolean ifLV)
            throws RuntimeException {

        String str = Integer.toHexString(dec);

        str = LPad00(str, bLength);

        if (ifLV) {
            str = Dec2Hex((str.length() / 2), 1, false) + str;
        }

        return str.toUpperCase();
    }

    /**
     * 十进制字符转16进制，用0左边补齐
     *
     * @param dec
     * @param bLength 字节数
     * @param ifLV
     * @return
     * @throws RuntimeException
     * @throws NumberFormatException
     */
    public static String Dec2Hex(String dec, int bLength, boolean ifLV)
            throws NumberFormatException, RuntimeException {

        if (TextUtils.isEmpty(dec)) {
            dec = "0";
        }
        return Dec2Hex(Integer.parseInt(dec), bLength, ifLV);
    }

    /**
     * 十进制字符转16进制
     *
     * @param dec //	 * @param bLength
     *            字节数
     *            //	 * @param ifLV
     * @return
     * @throws RuntimeException
     * @throws NumberFormatException
     */
    public static String Dec2Hex(int dec) throws NumberFormatException,
            RuntimeException {

        return Dec2Hex(dec, 0, false);
    }

    /**
     * 十进制字符转16进制
     *
     * @param dec //	 * @param bLength
     *            字节数
     *            //	 * @param ifLV
     * @return
     * @throws RuntimeException
     * @throws NumberFormatException
     */
    public static String Dec2Hex(String dec) throws NumberFormatException,
            RuntimeException {

        return Dec2Hex(dec, 0, false);
    }

    /**
     * String转ASC
     *
     * @param str
     * @param bLength
     * @param ifLV
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(String str, int bLength, boolean ifLV)
            throws RuntimeException {

        if (null == str) {
            str = "";
        }

        StringBuffer sAsc = new StringBuffer();
        for (char ch : str.toCharArray()) {
            if (ch > 128) {
                throw (new RuntimeException("非Asc字符"));
            } else {
                sAsc.append(Integer.toHexString(ch));
            }
        }
        String s = sAsc.toString();

        s = LPad00(s, bLength);
        if (ifLV) {
            s = Dec2Hex((s.length() / 2), 1, false) + s;
        }

        return s.toUpperCase();
    }

    /**
     * String转ASC
     *
     * @param i_num
     * @param bLength
     * @param ifLV
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(int i_num, int bLength, boolean ifLV)
            throws RuntimeException {
        return String2ASCII(Integer.toString(i_num), bLength, ifLV);
    }

    /**
     * String转ASC
     *
     * @param str
     * @param ifLV
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(String str, boolean ifLV)
            throws RuntimeException {

        if (null == str) {
            str = "";
        }

        int bLength = str.length();

        return String2ASCII(str, bLength, ifLV);
    }

    /**
     * String转ASC
     *
     * @param num
     * @param ifLV
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(int num, boolean ifLV)
            throws RuntimeException {
        return String2ASCII(Integer.toString(num), ifLV);
    }

    /**
     * String转ASC
     *
     * @param num
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(int num) throws RuntimeException {
        return String2ASCII(num, false);
    }

    /**
     * String转ASC
     *
     * @param str
     * @return
     * @throws RuntimeException
     */
    public static String String2ASCII(String str) throws RuntimeException {
        return String2ASCII(str, false);
    }

    /**
     * Asc转Str
     *
     * @param ascStr
     * @param ifLV   传入的值是否是LV形式
     * @return
     * @throws RuntimeException
     */
    public static String ASCII2String(String ascStr, boolean ifLV)
            throws RuntimeException {

        if ((ascStr == null) || (ascStr.length() % 2 != 0))
            throw new RuntimeException(
                    "String is null OR  String's length must be divide exactly by 2");

        if (ifLV) {
            String length = ascStr.substring(0, 2);

            int ilength = Integer.parseInt(length, 16);
            ascStr = ascStr.substring(2, 2 + ilength * 2);
        }

        StringBuilder sb = new StringBuilder();

        StringReader reader = new StringReader(ascStr);
        char[] cbuf = new char[2];
        try {
            while (reader.read(cbuf) != -1) {
                int v = Integer.parseInt(new String(cbuf), 16);
                // 遇到FF自动终止
                if (v == 255)
                    break;

                if ((v >= 32) && (v <= 126)) {
                    char c = (char) v;
                    sb.append(c);
                } else {
                    throw new RuntimeException(
                            "String contains charactor no-ASCII OR is invisible ");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();

    }

    /**
     * 按指定直接长度左边填充00
     *
     * @param str
     * @param bLength 字节长度
     * @return
     * @throws RuntimeException
     */
    public static String LPad00(String str, int bLength)
            throws RuntimeException {

        if (null == str) {
            str = "";
        }

        int num0 = bLength * 2 - str.length();
        StringBuffer sb = new StringBuffer();
        if (bLength == 0) {
            bLength = str.length() / 2;
            if (str.length() % 2 == 1) {
                bLength++;
            }
        } else {

            if (num0 < 0) {
                throw new RuntimeException("长度输入有误");
            }
        }

        num0 = bLength * 2 - str.length();
        for (int i = 0; i < num0; i++) {
            sb.append("0");
        }

        return sb.toString() + str;
    }

    /**
     * 按指定长度右填充FF
     *
     * @param str
     * @param bLength
     * @return
     * @throws RuntimeException
     */
    public static String RPadFF(String str, int bLength)
            throws RuntimeException {

        if (null == str) {
            str = "";
        }
        // 字符数必须为偶数
        if (str.length() % 2 == 1) {
            throw new RuntimeException("传入参数长度不能为奇数");
        }

        StringBuffer sb = new StringBuffer();
        int num0 = bLength - str.length() / 2;
        if (bLength == 0) {
            return str;

        } else {
            if (num0 < 0) {
                throw new RuntimeException("传入参数长度错误");
            }

        }

        num0 = bLength - str.length() / 2;
        for (int i = 0; i < num0; i++) {
            sb.append("FF");
        }
        return str + sb.toString();
    }

    /**
     * 把16进制的金额 转成以元为单位的10进制数
     *
     * @param moneyHex
     * @return
     */
    public static String money4View(String moneyHex) {
        int money = Integer.parseInt(moneyHex, 16);
        return "" + money / 100.00;
    }

    /**
     * 把以元为单位（有两位小数）的金额变成16进制的金额存入卡中
     * <p/>
     * //	 * @param moneyHex
     *
     * @return
     * @throws RuntimeException
     */
    public static String money4Card(String money) throws RuntimeException {
        if (TextUtils.isEmpty(money)) {
            throw new RuntimeException("money value is null");
        }
        double d = Double.parseDouble(money);
        int moneyDec = (int) (d * 100);

        return Dec2Hex(moneyDec, 4, false);
    }

    /**
     * 取非的值
     *
     * @param stringHex （长度在0-8字符之间,如果输入长度小于4，返回4字符；输入长度大于4返回8个字符）
     * @return
     * @throws RuntimeException
     */
    public static String getNotValue(String stringHex) throws RuntimeException {
        if (stringHex.length() > 8) {
            throw new RuntimeException("length wrong");

        } else if (stringHex.length() < 5) {

            int j = ~Integer.parseInt(stringHex, 16);

            return Dec2Hex(j).substring(4, 8);
        } else {

            return Dec2Hex(~Integer.parseInt(stringHex.substring(0, 4), 16))
                    .substring(4, 8)
                    + Dec2Hex(~Integer.parseInt(stringHex.substring(4), 16))
                    .substring(4, 8);
        }
    }

}
