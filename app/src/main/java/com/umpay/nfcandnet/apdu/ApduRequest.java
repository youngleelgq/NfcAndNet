package com.umpay.nfcandnet.apdu;

import com.umpay.nfcandnet.exception.OpenCardException;
import com.umpay.nfcandnet.utils.DataSwitch;
import com.umpay.nfcandnet.utils.TransportUtils;
import com.umpay.nfcandnet.utils.Utils;

public abstract class ApduRequest {

    public abstract ApduResponse run(ApduExecutor executor)
            throws Exception, OpenCardException;

    protected void checkError(byte[] rsp) throws Exception {
        if (!(TransportUtils.getSW1SW2(rsp) == 0x9000)
                && !(TransportUtils.getSW1(rsp) == 0x61)) {
            throw new Exception(TransportUtils.bytesToHexString(rsp));
        }
    }

    protected boolean checkSW1SW2(byte[] rsp) throws Exception {
        if ((TransportUtils.getSW1SW2(rsp) == 0x9000)
                || (TransportUtils.getSW1(rsp) == 0x61)) {
            return true;
        }
        return false;
    }

    /**
     * 访问cap的aid
     */
    private String mAid;

    /**
     * ********************************************
     * method name   : getAid
     * description   : 获取访问aid
     *
     * @param : @return
     *          modified      : yangningbo ,  2013-10-12  下午4:30:15
     * @return : String 访问cap的aid
     * @see :
     * *******************************************
     */
    public String getAid() {
        return mAid;
    }

    /**
     * ********************************************
     * method name   : setAid
     * description   : 设置访问aid
     *
     * @param : @param aid 访问cap的aid
     *          modified      : yangningbo ,  2013-10-12  下午4:30:49
     * @return : void
     * @see :
     * *******************************************
     */
    public void setAid(String aid) {
        mAid = aid;
    }

    /**
     * ********************************************
     * method name   : toBalance
     * description   : 从响应数据中解析出字符串型的余额
     *
     * @param : @param rsp 响应数据
     * @param : @return 余额
     *          modified      : yangningbo ,  2013-10-12  下午4:40:09
     * @return : String 余额
     * @see :
     * *******************************************
     */
    protected String toBalance(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        int n = Utils.bytesToInt(data);
//        if (n > 100000 || n < -100000) {
//            n -= 0x80000000;
//        }
        return toAmount(n);
    }

    /**
     * ********************************************
     * method name   : toAmount
     * description   : 将int型金额转为String型金额
     *
     * @param : @param value int型金额
     * @param : @return 金额
     *          modified      : yangningbo ,  2013-10-12  下午4:40:56
     * @return : String String型金额
     * @see :
     * *******************************************
     */
    protected String toAmount(int value) {
        return String.valueOf(value);
    }

    /**
     * ********************************************
     * method name   : toUserName
     * description   : 从响应数据中解析出用户名
     *
     * @param : @param rsp 响应数据
     * @param : @return 用户名
     *          modified      : yangningbo ,  2013-10-12  下午4:35:26
     * @return : String 用户名
     * @see :
     * *******************************************
     */
    protected String toUserName(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return DataSwitch.decodeName_TLV(Utils.bytesToHexString(data)
                .substring(4, 44));
    }

    /**
     * ********************************************
     * method name   : toCardAsn
     * description   : 从响应数据中解析出卡号
     *
     * @param : @param rsp 响应数据
     * @param : @return 卡号
     *          modified      : yangningbo ,  2013-10-12  下午4:34:44
     * @return : String 卡号
     * @see :
     * *******************************************
     */
    protected String toCardAsn(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return Utils.bytesToHexString(data).substring(34, 44);
    }

    /**
     * 获取卡芯片号
     *
     * @param rsp
     * @return
     */
    protected String toCardSerial(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return Utils.bytesToHexString(data);
    }

    protected String toBusCode(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return Utils.bytesToHexString(data).substring(18, 22);
    }

    protected String toCardKind(byte[] rsp) {
        byte[] data = Utils.stripSW1SW2(rsp);
        return Utils.bytesToHexString(data).substring(22, 24);
    }

    /**
     * ********************************************
     * method name   : toApdu
     * description   : 根据文件索引和p2参数组装apdu指令
     *
     * @param : @param index 文件索引
     * @param : @param p2 p2参数
     * @param : @return apdu指令
     *          modified      : yangningbo ,  2013-10-12  下午4:42:37
     * @return : String apdu指令
     * @see :
     * *******************************************
     */
    protected String toApdu(int index, byte p2) {
        byte[] apdu = {(byte) 0x00, (byte) 0xB2, (byte) index, p2, (byte) 0x17};
        return Utils.bytesToHexString(apdu);
    }

    /**
     * ********************************************
     * method name   : toTradeNo
     * description   : 从响应数据中解析出交易号
     *
     * @param : @param rsp 响应数据
     * @param : @return 交易号
     *          modified      : yangningbo ,  2013-10-12  下午4:44:56
     * @return : String 交易号
     * @see :
     * *******************************************
     */
    protected String toTradeNo(byte[] rsp) {
        return String.valueOf(Utils.bytesToInt(rsp, 0, 2));
    }

    /**
     * ********************************************
     * method name   : toOverdraft
     * description   : 从响应中解析出超支金额
     *
     * @param : @param rsp 响应数据
     * @param : @return 超支金额
     *          modified      : yangningbo ,  2013-10-12  下午4:45:56
     * @return : String 超支金额
     * @see :
     * *******************************************
     */
    protected String toOverdraft(byte[] rsp) {
        return String.valueOf(Utils.bytesToInt(rsp, 2, 3));
    }

    /**
     * ********************************************
     * method name   : toTradeType
     * description   : 从响应数据中解析出交易类型
     *
     * @param : @param rsp 响应数据
     * @param : @return 交易类型
     *          modified      : yangningbo ,  2013-10-12  下午4:46:40
     * @return : String 交易类型
     * @see :
     * *******************************************
     */
//    protected String toTradeType(byte[] rsp) {
//        return (rsp[9] == 0x01 || rsp[9] == 0x02) ? Const.ADDED : Const.USED;
//    }

    /**
     * ********************************************
     * method name   : toTerminalNo
     * description   : 从响应数据中解析出终端号码
     *
     * @param : @param rsp 响应数据
     * @param : @return 终端号码
     *          modified      : yangningbo ,  2013-10-12  下午4:47:56
     * @return : String 终端号码
     * @see :
     * *******************************************
     */
    protected String toTerminalNo(byte[] rsp) {
        return Utils.bytesToHexString(rsp, 10, 6);
    }

    /**
     * ********************************************
     * method name   : toDate
     * description   : 从响应数据中解析出交易日期
     *
     * @param : @param rsp 响应数据
     * @param : @return 交易日期
     *          modified      : yangningbo ,  2013-10-12  下午4:51:20
     * @return : String 交易日期
     * @see :
     * *******************************************
     */
    protected String toDate(byte[] rsp) {
        return String.format("%02X%02X%02X%02X", rsp[16], rsp[17], rsp[18],
                rsp[19]);
    }

    /**
     * ********************************************
     * method name   : toTime
     * description   : 从响应数据中解析出交易时间
     *
     * @param : @param rsp 响应数据
     * @param : @return 交易时间
     *          modified      : yangningbo ,  2013-10-12  下午4:51:20
     * @return : String 交易时间
     * @see :
     * *******************************************
     */
    protected String toTime(byte[] rsp) {
        return String.format("%02X%02X%02X", rsp[20], rsp[21], rsp[22]);
    }

    protected String toDateWithoutYear(byte[] rsp) {
        return String.format("%02X%02X", rsp[18],
                rsp[19]);
    }

    protected String toTimeWithoutSecond(byte[] rsp) {
        return String.format("%02X%02X", rsp[20], rsp[21]);
    }

}
