package com.umpay.nfcandnet.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：时间工具类 <p>
 * 创建日期：2013-4-26
 * </p>
 * @see
 */
public class DateUtil {

    /**
     * @return String[]
     * @Description：产生一个日期和时间，String[0]为日期，String[1]为时间 <p>
     * 创建人：yangningbo ,
     * 2013-4-26 下午3:13:19
     * </p>
     * <p>
     * 修改人：yangningbo ,
     * 2013-4-26 下午3:13:19
     * </p>
     */
    public static String[] getDateAndTime() {
        String currentTime = new SimpleDateFormat("yyyyMMdd,HHmmss")
                .format(new Date(System.currentTimeMillis()));
        String[] format = currentTime.split(",");
        return format;
    }

    /**
     * @return int
     * @Description：产生[0,9]的随机数 <p>
     * 创建人：yangningbo , 2013-4-26 下午3:15:18
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-4-26 下午3:15:18
     * </p>
     */
    public static int getRandom() {
        return new Random().nextInt(10);
    }

    /**
     * 数字和字母随机字符（用于密码控件）32数字和大小写字母
     */
    public static String getRandomString(int iLength) {
        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for (int i = 0; i < 32; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * @return String
     * @Description：产生带有随机数的日期和时间 <p>
     * 创建人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     */
    public static String getDateWithRandom() {
        String[] date = getDateAndTime();
        int r = getRandom();
        // 20130409153012|9
        return date[0] + date[1] + "|" + r;
    }

    public static String getFullDate() {
        String[] date = getDateAndTime();
        return date[0] + date[1];
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(c.getTime());
    }

    public static String parseTime(String dateStr, String inPattern,
                                   String outPattern) throws ParseException {
        Date date = new SimpleDateFormat(inPattern).parse(dateStr);
        return new SimpleDateFormat(outPattern).format(date);
    }


    static int[] DAYS = {
            0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    /**
     * 是否是合法的日期
     *
     * @param date
     * @return
     */
    public static boolean isValidDate(String date) {
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            if (year <= 0)
                return false;
            int month = Integer.parseInt(date.substring(4, 6));
            if (month <= 0 || month > 12)
                return false;
            int day = Integer.parseInt(date.substring(6, 8));
            if (day <= 0 || day > DAYS[month])
                return false;
            if (month == 2 && day == 29 && !isGregorianLeapYear(year)) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 是否是闰年
     *
     * @param year
     * @return
     */
    public static final boolean isGregorianLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public static String getDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.CHINA);
        return sdf.format(new Date());
    }


    /**
     * @return String
     * @Description：日期时间转化 <p>
     * 创建人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     */
    public static String formateDateTime(String dt) {
        if (!TextUtils.isEmpty(dt)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat parse = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                return format.format(parse.parse(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @return String
     * @Description：产生带有随机数的日期和时间 <p>
     * 创建人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-4-26 下午3:16:03
     * </p>
     */
    public static String get12hTime() {
        SimpleDateFormat dfhh = new SimpleDateFormat("H");
        SimpleDateFormat dfmm = new SimpleDateFormat("mm");
        int hh = 0;
        if ((hh = Integer.parseInt(dfhh.format(new Date()))) <= 12) {
            return "上午".concat(hh + ":" + dfmm.format(new Date()));
        } else {
            return "下午".concat((hh - 12) + ":" + dfmm.format(new Date()));
        }
    }

    /**
     * XXXXXXXX-->XXXX.XX.XX
     *
     * @param tradeTime
     * @return
     */
    public static String getTradeTime(String tradeTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(tradeTime.substring(0, 4)).append(".").append(tradeTime.substring(4, 6))
                .append(".").append(tradeTime.substring(6, 8));
        return sb.toString();
    }

    public static String formateTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat parse = new SimpleDateFormat("HHmmss");
            try {
                return format.format(parse.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
