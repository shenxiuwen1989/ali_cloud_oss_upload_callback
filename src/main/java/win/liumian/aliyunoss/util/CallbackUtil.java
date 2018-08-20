package win.liumian.aliyunoss.util;

import com.aliyun.oss.common.utils.BinaryUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by liumian on 2016/9/27.
 */
public class CallbackUtil {

    /**
     * 检验是否是oss服务器发送过来的回调请求
     * @param authorizationInput base64编码后的签名
     * @param pubKeyInput base64编码手的公钥地址
     * @param requestBody 请求body体
     * @param queryString 请求的查询参数
     * @param uri 请求的URI
     * @return 如果校验成功则返回true，否则返回false。
     */
    public static final boolean verifyOSSCallbackRequest(String authorizationInput,
                                                         String pubKeyInput,
                                                         String requestBody,
                                                         String queryString,
                                                         String uri) {

        byte[] publicKeyBytes = BinaryUtil.fromBase64String(pubKeyInput);
        String publicKeyAddr = new String(publicKeyBytes);

        String publicKey = executeGet(publicKeyAddr);
        if (StringUtils.isEmpty(publicKey)) {
            return false;
        }

        publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKey = publicKey.replace("-----END PUBLIC KEY-----", "");

        StringBuffer requestContent = new StringBuffer();

        requestContent.append(uri).append("?").append(queryString).append("\n").append(requestBody);

        byte[] sign = BinaryUtil.fromBase64String(authorizationInput);

        return doCheck(requestContent.toString(),sign,publicKey);


    }

    /**
     * 检查公钥和数字签名是否正确
     *
     * @param content   需要检查的内容
     * @param sign      数字签名
     * @param publicKey 公钥
     * @return
     */
    public static final boolean doCheck(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes());
            boolean bverify = signature.verify(sign);
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 想AliyunOSS服务器发起请求，获得结果用于检查是否是服务器发起的Callback请求
     *
     * @param url AliyunOSS服务器路径
     * @return 请求结果
     */
    private static final String executeGet(String url) {
        BufferedReader in = null;

        String content = null;
        try {
            // 定义HttpClient
            @SuppressWarnings("resource")
            DefaultHttpClient client = new DefaultHttpClient();
            // 实例化HTTP方法
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            content = sb.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();// 最后要关闭BufferedReader
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return content;
        }
    }

}
