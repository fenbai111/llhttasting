package com.jlt.vote.bis.wx.sdk.jsapis;

import com.jlt.vote.bis.wx.sdk.common.decrypt.AesException;
import com.jlt.vote.bis.wx.sdk.common.decrypt.SHA1;
import com.jlt.vote.bis.wx.sdk.common.jsapi.JsAPISignature;
import com.jlt.vote.bis.wx.sdk.common.util.RandomStringGenerator;
import com.jlt.vote.exception.VoteRuntimeException;
import com.jlt.vote.util.CommonConstants;
import com.xcrm.common.http.*;
import com.xcrm.common.http.parser.ResponseParser;
import com.xcrm.common.http.utils.SafeUtils;
import com.xcrm.common.util.InputStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class JsAPIs {

    private static final Logger log = LoggerFactory.getLogger(JsAPIs.class);
    private ServiceClient serviceClient;
    private String wxAccessTokenUrl;
    private String jsSdkTicketUrl;

    public static JsAPIs with(String wxAccessTokenUrl, String jsSdkTicketUrl) {
        return new JsAPIs(wxAccessTokenUrl, jsSdkTicketUrl);
    }

    private JsAPIs(String wxAccessTokenUrl, String jsSdkTicketUrl) {
        this.wxAccessTokenUrl = wxAccessTokenUrl;
        this.jsSdkTicketUrl = jsSdkTicketUrl;
        ClientConfiguration config = new ClientConfiguration();
        serviceClient = new DefaultServiceClient(config);
    }

    /**
     * 获取微信公众号 access_token 有效期7200秒
     *
     * @param appId
     * @param appSecret
     */
    public Map getWxAccessToken(String appId, String appSecret) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("grant_type", "client_credential");
        RequestMessage request = new RequestMessage();
        request.setEndpoint(new URI(wxAccessTokenUrl));
        request.setParameters(params);
        request.setMethod(HttpMethod.GET);

        ExecutionContext context = createDefaultContext(request.getMethod());
        ResponseMessage response = null;
        String json = "";
        try {
            response = send(request, context, true);
            json = InputStreamUtils.InputStreamTOString(response.getContent(), "UTF-8");
            log.info("get weixin api server response ={}", json);
            Map result = ResponseParser.unmarshaller(json, Map.class);
            if (result.containsKey(CommonConstants.ACCESS_TOKEN)) {
                return result;
            } else {
                log.warn("get weixin getWxAccessToken failed errcode={},errmsg={}"
                        , result.get("errcode"), result.get("errmsg"));
                return null;
            }
        } catch (Exception e) {
            log.error("get weixin getWxAccessToken failed ", e);
        } finally {
            if (response != null)
                SafeUtils.safeCloseResponse(response);
        }
        return null;
    }

    /**
     * 获取微信 js-sdk jsapi_ticket 有效期7200秒
     *
     * @param access_token
     */
    public Map getWxJsSdkTicket(String access_token) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", access_token);
        params.put("type", "jsapi");
        RequestMessage request = new RequestMessage();
        request.setEndpoint(new URI(jsSdkTicketUrl));
        request.setParameters(params);
        request.setMethod(HttpMethod.GET);

        ExecutionContext context = createDefaultContext(request.getMethod());
        ResponseMessage response = null;
        String json = "";
        try {
            response = send(request, context, true);
            json = InputStreamUtils.InputStreamTOString(response.getContent(), "UTF-8");
            log.debug("get weixin api server response ={}", json);
            Map result = ResponseParser.unmarshaller(json, Map.class);
            if (result.containsKey("ticket")) {
                return result;
            } else {
                log.warn("get weixin getWxJsSdkTicket failed errcode={},errmsg={}"
                        , result.get("errcode"), result.get("errmsg"));
                return null;
            }
        } catch (Exception e) {
            log.error("get weixin getWxJsSdkTicket failed ", e);
        } finally {
            if (response != null)
                SafeUtils.safeCloseResponse(response);
        }

        return null;
    }

    protected ResponseMessage send(RequestMessage request, ExecutionContext context, boolean keepResponseOpen)
            throws ServiceException, ClientException {
        ResponseMessage response = serviceClient.sendRequest(request, context);
        if (!keepResponseOpen) {
            SafeUtils.safeCloseResponse(response);
        }
        return response;
    }

    private ExecutionContext createDefaultContext(HttpMethod method) {
        ExecutionContext context = new ExecutionContext();
        context.setCharset("utf-8");
        return context;
    }
}
