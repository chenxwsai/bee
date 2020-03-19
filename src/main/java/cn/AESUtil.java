package cn;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.github.wxpay.sdk.WXPayUtil;

public class AESUtil {
	 /** 
     * 密钥算法 
     */  
    private static final String ALGORITHM = "AES";  
    /** 
     * 加解密算法/工作模式/填充方式 
     */  
    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";   
    /** 
     * 商户key( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
     */ 
    private static final String busKey = "2IBtBXdrqC3kCBs4gaceL7nl2nnFadQv";
  
    /** 
     * AES加密 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static String encryptData(String data) throws Exception {  
        // 创建密码器  
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);  
        // 初始化  
        SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(busKey).toLowerCase().getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);  
        return new String(Base64.getMimeEncoder().encode(cipher.doFinal(data.getBytes())));  
    }  
  
    /** 
     * AES解密 
     *  
     * @param base64Data 
     * @return 
     * @throws Exception 
     */  
    public static String decryptData(String base64Data) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);  
        SecretKeySpec key = new SecretKeySpec(WXPayUtil.MD5(busKey).toLowerCase().getBytes(), ALGORITHM);
        return new String(cipher.doFinal(Base64.getMimeDecoder().decode(base64Data)));  
    }  
}
