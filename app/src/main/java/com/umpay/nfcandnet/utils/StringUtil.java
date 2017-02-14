package com.umpay.nfcandnet.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {
    private static final String TAG = StringUtil.class.getSimpleName();

    public static final String sRegPhoneNumber = "^[0-9]*$";
    public static final String sRegMatcherNumber = "^[A-Za-z0-9]+$";
    public static final String EMAIL_PATTERN_CODE = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String sRegEx = "[`~!@#$%^&*()+=\\-\\s*|\t|\r|\n|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    public static final String sRegEx1 = "\\+|(?<=\\d)-|\\*|/|&|=|(>=)|(<=)";

    // 设置错误提示内容的文本颜色
    public static CharSequence setTextColor(String text, int color, int start, int end) {
        final ForegroundColorSpan fc = new ForegroundColorSpan(color);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(fc, start, end, 0);
        return ssb;
    }

    //设置本文字体大小与颜色
    public static SpannableString setTextSizeAndColor(String text, int color, int size, int start, int end) {
        SpannableString msp = new SpannableString(text);
        msp.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    // 设置错误提示内容的文本颜色
    public static CharSequence setTextColor(String text, int color) {
        return setTextColor(text, color, 0, text.length());
    }

    /*
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 生成6字节交易流水号
     *
     * @return
     */
    public static String generateSeq() {
        return generateRandom(6);
    }

    /**
     * 生成指定字节的随机数
     *
     * @param byteSize
     * @return
     */
    public static String generateRandom(int byteSize) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[byteSize];
        random.nextBytes(bytes);
        // return new String(bytes);
        return bytesToHexString(bytes);
    }

    /**
     * String转MD5
     *
     * @param inStr
     * @return
     */
    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 获取字节数组对应的字节的长度
     *
     * @return
     */
    public static int getBALength(byte[] bytes) {
        if ((bytes.length % 8) == 0) {
            return bytes.length / 8;
        } else {
            return bytes.length / 8 + 1;
        }
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }

    public static String bigIntegerAddOne(String seq) {
        BigInteger seqBI = new BigInteger(seq, 16);
        BigInteger oneHex = new BigInteger("1", 16);
        return seqBI.add(BigInteger.ONE).toString(16);
//        return seqBI.add(oneHex).toString(16);
    }

    public static String getSeidFormat(String seid) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] seidBytes = hexStringToBytes(seid);
        byte[] tempBytes = md.digest(seidBytes);
        byte[] resultBytes = new byte[7];
        System.arraycopy(tempBytes, 5, resultBytes, 0, resultBytes.length);
        return "e0" + bytesToHexString(resultBytes);

//        String temp = null;
//        try {
//            temp = md5(seid, 32);
//            Log.i(TAG, "temp:" + temp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] tempBytes = hexStringToBytes(temp);
//        byte[] resultBytes = new byte[7];
//        System.arraycopy(tempBytes, 5, resultBytes, 0, resultBytes.length);
//        return "E0" + bytesToHexString(resultBytes);
    }

    /**
     * @param input
     * @param bit   表示希望得到的字符串的长度
     *              * @return
     * @throws Exception
     */
    public static String md5(String input, int bit) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
            if (bit == 16)
                return bytesToHexString(md.digest(input.getBytes("utf-8"))).substring(8, 24);
            return bytesToHexString(md.digest(input.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Could not found MD5 algorithm.", e);
        }
    }


    public static boolean checkPhoneNumber(String phoneNumber) {
        String regEx = "^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phoneNumber);
        // 正则匹配，匹配成功返回true，否则false
        return matcher.matches();
    }

    public static boolean checkPwd(String pwd) {
        String regEx = "^[a-zA-Z][~!@#\\$%\\^&\\*\\?a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(pwd);
        // 正则匹配，匹配成功返回true，否则false
        return matcher.matches();
    }

    /**
     * 判断字符数是否由字母，数字组成
     *
     * @param str
     * @return
     */
    public static boolean isPw(String str) {
        String pipeRegex = "^[0-9A-Za-z]{6,10}$";
        Pattern pipePattern = Pattern.compile(pipeRegex, Pattern.CASE_INSENSITIVE);
        return pipePattern.matcher(str).matches();
    }

    /**
     * 判断字符串数是否url
     *
     * @param str
     * @return
     */
    public static boolean isURL(String str) {
        String pipeRegex = "(http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>;]*)?|([a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>;]*)?)";
        Pattern pipePattern = Pattern.compile(pipeRegex, Pattern.CASE_INSENSITIVE);
        return pipePattern.matcher(str).matches();
    }

    /**
     * 是否是空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 是否是非空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    /**
     * 获得汉语拼音首字母
     *
     * @param str
     * @return
     */
    public static String getAlpha(String str) {
        if (str == null) {
            return "#";
        }

        if (str.trim().length() == 0) {
            return "#";
        }

        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    /**
     * 根据日期解析出当前星期几
     *
     * @param data
     * @return
     */
    public static String dayForWeek(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String dayForWeek = null;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                dayForWeek = "日";
                break;
            case 2:
                dayForWeek = "一";
                break;
            case 3:
                dayForWeek = "二";
                break;
            case 4:
                dayForWeek = "三";
                break;
            case 5:
                dayForWeek = "四";
                break;
            case 6:
                dayForWeek = "五";
                break;
            case 7:
                dayForWeek = "六";
                break;
            default:
                break;
        }
        return dayForWeek;
    }

    /**
     * 截取日期
     *
     * @param time   日期格式:yyyy-MM-dd HH:mm:ss
     * @param length 从左截取个数
     * @return
     */
    public static String getSubTime(String time, int length) {
        String newSubTime = "";
        if (StringUtil.isNotEmpty(time)) {
            newSubTime = time.substring(0, length);
        }
        return newSubTime;
    }

    /**
     * 日期转换成字符串
     *
     * @param date 日期
     * @return str 转换的格式
     */
    public static String DateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String str = sdf.format(date);
        return str;
    }

    /**
     * 把date类型转换成yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String DateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 把long类型转换成yyyy-MM-dd HH:mm:ss格式的字符串
     *
     * @param date
     * @return
     */
    public static String longToStr(long date) {
        return DateToStr(new Date(date));
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static final SimpleDateFormat sdf_ = new SimpleDateFormat("HHmmss");

    public static String formatTime(String str) {
        try {
            Date date = sdf_.parse(str);
            return DateToStr(date, "HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static String formatDate(String str) {
        try {
            Date date = sdf.parse(str);
            return DateToStr(date, "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 打电话
     *
     * @param context
     * @param tel
     */
    public static void call(Context context, String tel) {
        if (StringUtil.isNotEmpty(tel)) {
            String sz = "tel:" + tel;
            Uri uri = Uri.parse(sz);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);
        }
    }

    /**
     * 格式化价格
     *
     * @return
     */
    public static String formatPrice(String price) {
        String newprice = "0.00";
        if (StringUtil.isNotEmpty(price)) {
            DecimalFormat df = new DecimalFormat("#######0.00");
            newprice = df.format(Double.valueOf(price));
        }
        return newprice;
    }

    /**
     * 格式化价格
     *
     * @return
     */
    public static String formatPrice(Double price) {
        String newprice = "0.00";
        DecimalFormat df = new DecimalFormat("#######0.00");
        newprice = df.format(price);
        return newprice;
    }

    /**
     * 字符串转换成日期 (默认格式：yyyy-MM-dd HH:mm:ss)
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {
        if (StringUtil.isNotEmpty(str)) {
            return strToDate(str, "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }

    /**
     * 将时间格式化
     *
     * @param opdt
     * @return
     */
    public static String getFormatOpdt(String opdt) {
        return StringUtil.DateToStr(StringUtil.strToDate2(opdt));
    }

    /**
     * 字符串转换成日期 (默认格式：yyyyMMddHHmmss)
     *
     * @param str
     * @return date
     */
    public static Date strToDate2(String str) {
        if (StringUtil.isNotEmpty(str)) {
            return strToDate(str, "yyyyMMddHHmmss");
        }
        return null;
    }

    /**
     * 比较时间大小
     *
     * @param firsttime
     * @param secondtime
     * @return true:fristtime>secondtime,false:fristtime<=secondtime
     */
    public static Boolean isgt(String firsttime, String secondtime)// is greater
    // than
    {
        Boolean isFlag = false;
        Date firstdate = strToDate(firsttime);
        Date seconddate = strToDate(secondtime);
        if (firstdate.getTime() > seconddate.getTime()) {
            isFlag = true;
        }
        return isFlag;

    }

    /**
     * 截取字符串（超过一定长度加...）
     *
     * @param text
     * @param len  个数
     * @return
     */
    public static String getEllipsisString(String text, int len) {
        String newString = text;
        if (StringUtil.isNotEmpty(text) && text.length() > len) {
            newString = text.subSequence(0, len - 1) + "...";
        }
        return newString;
    }

    /**
     * 不够2位用0补齐
     *
     * @param str
     * @return
     */
    public static String LeftPad_Tow_Zero(int str) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(str);

    }

    /**
     * 转换大小写
     *
     * @param str
     * @return
     */
    public static String getCnString(String str) {
        Double n = StringToDouble(str);
        String fraction[] = {"角", "分"};
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String unit[][] = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if (s.length() < 1) {
            s = "整";
        }
        int integerPart = (int) Math.floor(n);

        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart = integerPart / 10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }

    /**
     * String转Double
     *
     * @param str
     * @return
     */
    public static Double StringToDouble(String str) {

        if (StringUtil.isEmpty(str)) {
            return 0.0;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * 摘自commons-lang.jar
     *
     * @return String
     */
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one
        // major goal)
        // let me know if there are performance requests, we can create a
        // harness to measure

        if (text == null || text.length() == 0 || searchList == null || searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - " + "output of one loop is the input of another");
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have
        // to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their
        // corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].length() == 0 || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    /**
     * 去除json串里面的符号
     *
     * @param json
     * @return
     */
    public static String jsonToString(String json) {
        if (StringUtil.isNotEmpty(json)) {
            return json.replace("[", "").replace("]", "").replace("\"", "");
        }
        return null;
    }


    /**
     * MD5加密字符串
     *
     * @param str
     * @return
     */
    public static String encodeMD5(String str) {
        return encodePassword(str, "MD5");
    }

    /**
     * SHA加密字符串
     *
     * @param str
     * @return
     */
    public static String encodeSHA(String str) {
        return encodePassword(str, "SHA");
    }

    /**
     * 字符编码加密
     *
     * @param password  字符明文
     * @param algorithm 加密算法：SHA 或 MD5
     * @return
     */
    public static String encodePassword(String password, String algorithm) {
        byte[] unencodedPassword = password.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {

            return password;
        }
        md.reset();
        md.update(unencodedPassword);
        byte[] encodedPassword = md.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * 手机号验证
     *
     * @return
     */
    public static boolean validatePhoneNumb(String phoneNumb) {
        if (!TextUtils.isEmpty(phoneNumb) && phoneNumb.length() == 11) {
            String regExp = "^((1\\d{10})|(\\d{7,8})|((\\d{4}|\\d{3})-(\\d{7,8}))|((\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))|((\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})))$";
            Pattern pattern = Pattern.compile(regExp);
            Matcher m = pattern.matcher(phoneNumb);
            return m.find();
        } else {
            return false;
        }

    }

    /**
     * 将卡号格式化的方法
     * XXXXXXXXXXXXXXXX-->XXXX XXXX XXXX XXXX
     *
     * @param cardAsn
     * @return
     */
    private String formatCardAsn(String cardAsn) {
        StringBuilder sb = new StringBuilder();
        int length = 0;
        while (cardAsn.length() / 4 > 0) {
            length = cardAsn.length();
            sb.append(cardAsn.substring(0, 4) + " ");
            cardAsn = cardAsn.substring(4, length);
        }
        if (cardAsn.length() != 0) {
            sb.append(cardAsn.substring(0, cardAsn.length()));
        }
        return sb.toString().trim();
    }

    /**
     * 将textview中的字符全角化,解决排版问题
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    // 替换、过滤特殊字符
    public static String StringFilter(String str) throws PatternSyntaxException {
        str=str.replaceAll("【","[").replaceAll("】","]").replaceAll("！","!");//替换中文标号
        String regEx="[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
