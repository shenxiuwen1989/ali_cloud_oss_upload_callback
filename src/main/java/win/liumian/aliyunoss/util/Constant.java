package win.liumian.aliyunoss.util;


public class Constant {

	//阿里云OSS-KEYID
    public static final String accessKeyId = "LTAIiCfgAfk5cWNt";
    //阿里云OSS-SECRET
    public static final String accessKeySecret = "sSLtqpqk025xxWcc3CxZiXpXub1hXZ";
    //阿里云OSS-区链接
    public static final String endPoint = "http://oss-cn-shenzhen.aliyuncs.com";
    //阿里云OSS-私有块
    public static final String bucket = "okodm-private";
    /**
     * bucket + endpoint
     */
    public static final String HOST =  "http://" + bucket + "." + endPoint;

}
