package com.umpay.nfcandnet.apdu;

/**
 * @ClassName: NfcApduExecutorListener
 * @Description: apdu执行器状态监听对象
 * @author yangningbo
 * @date 2015-4-9 上午10:25:14
 */
public interface ApduExecutorListener {

	void onStart(int reqId);

	/**
	 * ******************************************** method name : onConnected
	 * description : apdu执行器成功绑定SEService远程服务监听
	 * 
	 * @return : void
	 * @param : @param executor modified : yangningbo , 2013-10-12 下午4:18:10
	 * @see : *******************************************
	 */
	void onConnected(int reqId);

	/**
	 * ******************************************** method name : onCompleted
	 * description : apdu执行器执行指令成功状态监听
	 * 
	 * @return : void
	 * @param : @param rsp modified : yangningbo , 2013-10-12 下午4:19:04
	 * @see : *******************************************
	 */
	void onCompleted(ApduResponse rsp);

	/**
	 * ******************************************** method name : onFinished
	 * description : apdu执行器执行指令结束监听
	 * 
	 * @return : void
	 * @param : modified : yangningbo , 2013-10-12 下午4:19:48
	 * @see : *******************************************
	 */
	void onFinished();

	/**
	 * ******************************************** method name : onFailed
	 * description : apdu执行器执行指令失败监听
	 * 
	 * @return : void
	 * @param : @param e modified : yangningbo , 2013-10-12 下午4:20:14
	 * @see : *******************************************
	 */
	void onFailed(Exception e);

}
