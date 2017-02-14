package com.umpay.nfcandnet.utils;

import java.text.DecimalFormat;

/**
 * Created by liluhe on 2016/6/26.
 */
public class MoneyUtils {
    public static double getMoneyDouble(String money) {
        return Double.parseDouble("0" + money);
    }

    public static int getFen(String money) {
        double yuan = getMoneyDouble(money);
        return getFen(yuan);
    }

    public static int getFen(double money) {
        return (int) (money * 100);
    }

    public static String getMoneyString(double money) {
        DecimalFormat df = new DecimalFormat("#######.00");
        String str = df.format(money);
        if (money < 1 && money >= 0) {
            str = "0" + str;
        }
        return str;
    }
    public static String getMoneyString(String money) {
        double moneyDouble = getMoneyDouble(money);
        return getMoneyString(moneyDouble);
    }
    public static String getfen2Yuan(String money) {
        double fen = (double) getFen(money);
        fen = fen / 10000;
        return getMoneyString(fen);
    }

}
