package win.liumian.aliyunoss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import win.liumian.aliyunoss.policy.PostObjectPolicy;
import win.liumian.aliyunoss.util.CallbackUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;

/**
 * 下发oss回调策略；
 * 响应oss回调。
 *
 * Created by liumian on 2016/9/27.
 */
@RestController
public class OssController {

    /**
     * 提供给oss服务器回调的地址
     * @param ossCallbackBody
     * @param authorization
     * @param callbackMethodName
     * @param publicKeyUrlBase64
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "oss/callback", method = {RequestMethod.POST,RequestMethod.GET})
    public String callback(@RequestBody String ossCallbackBody,
                           @RequestHeader("Authorization") String authorization,
                           @RequestParam("callback") String callbackMethodName,
                           @RequestHeader("x-oss-pub-key-url") String publicKeyUrlBase64,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        boolean isOssCallback = CallbackUtil.verifyOSSCallbackRequest(authorization
            , publicKeyUrlBase64
            , ossCallbackBody
            , request.getQueryString()
            , request.getRequestURI());

        if (isOssCallback) {
            response.setStatus(HttpServletResponse.SC_OK);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success",true);
            return jsonObject.toString();
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success",false);
            return jsonObject.toString();
        }
    }

    @RequestMapping(value = "oss/policy")
    public String createPolicy(){

        return PostObjectPolicy.createPolicy("avatar/").toJSONString();

    }

}
