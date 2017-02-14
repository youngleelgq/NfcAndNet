package com.umpay.nfcandnet.crypto;

import android.util.Base64;

import com.umpay.nfcandnet.common.Const;
import com.umpay.nfcandnet.exception.MyHttpException;
import com.umpay.nfcandnet.utils.CharUtils;
import com.umpay.nfcandnet.utils.LogUtils;
import com.umpay.nfcandnet.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class SafeUtil {
    /**
     * @param obj
     * @return
     * @throws Exception String
     * @Description：对象序列化成字符串，并对该字符串进行Base64编码 <p>
     * 创建人：yangningbo , 2013-9-4
     * 下午3:50:28
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-9-4
     * 下午3:50:28
     * </p>
     */
    public static String obj2Str(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            throw e;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param str
     * @return
     * @throws Exception Object
     * @Description：对字符串进行Base64解码，并对该字符串反序列化成对象 <p>
     * 创建人：yangningbo , 2013-9-4
     * 下午3:49:14
     * </p>
     * <p>
     * 修改人：yangningbo , 2013-9-4
     * 下午3:49:14
     * </p>
     */
    public static Object str2Obj(String str) throws Exception {
        if (str == null) {
            throw new IllegalArgumentException("str can not null.");
        }

        ObjectInputStream ois = null;
        try {
            byte[] data = Base64.decode(str.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param obj
     * @return
     * @throws Exception String
     * @Description：对象序列化成字符串，并对该字符串DES加密 <p>
     * 创建人：baichenxi , 2014-4-4 下午3:49:14
     * </p>
     * <p>
     * 修改人：baichenxi , 2014-4-4 下午3:49:14
     * </p>
     */
    public static String encObj2Str(Object obj, String key) throws Exception {
        if (obj == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] encStr = DesAlaorithm.symmetricEncrypto(baos.toByteArray(),
                    key);
            // return new String(Base64.encode(encStr, Base64.DEFAULT));
            return CharUtils.bytesToHexString(encStr);
        } catch (Exception e) {
            throw e;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param str
     * @return
     * @throws Exception Object
     * @Description：对字符串进行Base64解码,DES解密，并对该字符串反序列化成对象 <p>
     * 创建人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     * <p>
     * 修改人：baichenxi , 2014-4-4
     * 下午3:49:14
     * </p>
     */
    public static Object decStr2Obj(String str, String key) throws Exception {
        if (str == null) {
            throw new IllegalArgumentException("str can not null.");
        }

        ObjectInputStream ois = null;
        try {

            // byte[] data = Base64.decode(str.getBytes(), Base64.DEFAULT);
            byte[] decoder = DesAlaorithm.symmetricDecrypto(
                    CharUtils.hexStringToBytes(str), key);

            ByteArrayInputStream bais = new ByteArrayInputStream(decoder);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param @param  req
     * @param @return 设定文件
     * @return String 返回类型
     * @throws MyHttpException toEncData
     * @throws
     * @Title: toEncData
     * @Description: 对json串序列化成字符串，做des加密，再对对称秘钥做非对称加密，秘钥放在报文头中
     */
    public static String getEncData(String jsonStr) throws MyHttpException {
        try {
            // 生成对称秘钥
            String key = UUID.randomUUID().toString();
            key = key.replace("-", "");

            return "umpay" + toEncKey(key) + SafeUtil.encObj2Str(jsonStr, key);

        } catch (Exception e) {
            e.printStackTrace();
            throw new MyHttpException(MyHttpException.CODE_ENC_EXCEPTION);
        }
    }

    private static String toEncKey(String desKey) throws Exception {

        byte[] data = desKey.getBytes();

        byte[] encodedData = RSACoder.encryptByPublicKey(data, Tag.getTag(Const.Config.DEBUG));

        String encodedDataStr = CharUtils.bytesToHexString(encodedData);

        return encodedDataStr;
    }

    /**
     * getDecData
     *
     * @param @param  encData
     * @param @return
     * @param @throws MyHttpException 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getDecData
     * @Description: 解密，从256字符中解出对称秘钥；然后对后续报文做des解密
     */
    public static String getDecData(String encData) throws MyHttpException {
        try {
            String key = encData.substring(0, 256);
            key = decKey(key);
            LogUtils.e("对称密钥:" + key);
            String content = encData.substring(256);
            return (String) decStr2Obj(content, key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyHttpException(MyHttpException.CODE_UNKNOWN_EXCEPTION);
        }

    }

    /**
     * getKey
     *
     * @param @param  desKey
     * @param @return
     * @param @throws Exception 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getKey
     * @Description: 解析得到对称秘钥
     */
    private static String decKey(String desKey) throws Exception {

        byte[] data = Utils.hexStringToBytes(desKey);

        byte[] decodedData = RSACoder.decryptByPublicKey(data,
                Tag.getTag(Const.Config.DEBUG));

//        decodedData = trimKey(decodedData);
        String decodedDataStr = new String(decodedData);

        return decodedDataStr;
    }
}