package cn.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
//import org.springframework.core.io.ClassPathResource;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;

public class WXPayConfigImpl extends WXPayConfig {
    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;

    public final static String NOTIFY_URL = "http://***/wx-pay/callback";

    private WXPayConfigImpl() throws Exception {
        //获取证书
//        File file = new ClassPathResource("apiclient_cert.p12").getFile();
    	File file = null;
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static WXPayConfigImpl getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }
    
    public String getAppID() {
        return "***";
    }

    public String getMchID() {
        return "***";
    }

    public String getKey() {
        return "***";
    }


    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }
    protected IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return WXPayConstants.DOMAIN_API;
    }

    public String getAlternateDomain() {
        return WXPayConstants.DOMAIN_API2;
    }
}
