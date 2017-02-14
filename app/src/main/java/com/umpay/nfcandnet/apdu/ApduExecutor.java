package com.umpay.nfcandnet.apdu;


import com.umpay.nfcandnet.exception.OpenChannelException;

import java.io.Serializable;


/**
 * 这个类继承与ApduExecutor，只是为了更容易地对接。可以使用ApduExecutor 来接收该对象
 *
 * @author wangzhonggang
 * @version 0.1
 * @description
 * @since 2015-6-19
 */
public interface ApduExecutor extends Serializable {


    boolean isConnect();

    /**
     * connect
     *
     * @param @return
     * @param @throws OpenChannelException    设定文件
     * @return boolean    返回类型
     * @throws
     * @Title: connect
     * @Description: 建立通道的连接
     */
    boolean connect(int reqId) throws OpenChannelException;

    /**
     * ********************************************
     * method name   : execute
     * description   : 执行apdu指令，并返回响应数据
     *
     * @param : @param channel 逻辑通道
     * @param : @param apdu apdu指令
     * @param : @return 卡片返回的响应字节
     * @param : @throws Exception
     *          modified      : yangningbo ,  2013-10-12  下午4:08:51
     * @return : byte[] 卡片返回的响应字节
     * @see :
     * *******************************************
     */
    byte[] execute(String apdu) throws Exception;


    /**
     * ********************************************
     * 在进行互联互通卡片检测时使用，其他情况下使用excute方法
     * method name   : executeHtCard
     * description   : 执行apdu指令，并返回响应数据
     *
     * @param : @param channel 逻辑通道
     * @param : @param apdu apdu指令
     * @param : @return 卡片返回的响应字节
     * @param : @throws Exception
     *          modified      : yangningbo ,  2013-10-12  下午4:08:51
     * @return : byte[] 卡片返回的响应字节
     * @see :
     * *******************************************
     */
    byte[] executeNotCheck(String apdu) throws Exception;

    /**
     * runApdu
     *
     * @param @param req    设定文件
     * @return void    返回类型
     * @throws
     * @Title: runApdu
     * @Description: 执行apdu的业务
     */
    void runApdu(ApduRequest req);

    /**
     * shutdown
     *
     * @param @param 设定文件
     * @return void    返回类型
     * @throws
     * @Title: shutdown
     * @Description: 关闭通道
     */
    void shutdown();

}
