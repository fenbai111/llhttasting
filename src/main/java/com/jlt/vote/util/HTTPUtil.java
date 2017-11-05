package com.jlt.vote.util;

import com.alibaba.fastjson.JSON;
import com.xcrm.log.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 发送http请求
 *
 * @author tangyong
 */
public class HTTPUtil {

    private static Logger logger = Logger.getLogger(HTTPUtil.class);

    /**
     * 发送Get请求
     */
    public static String sendGetHttp(String action) {
        String result = "";
        int status = 0;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(action);
            HttpResponse response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                if (status != 200) {
                    logger.error("----请求失败----status:" + status + "，action:" + action + "，line:" + line);
                }
                result = line;
            }
        } catch (Exception e) {
            logger.error("----请求失败----status:" + status + "，action:" + action + "，result:" + result,e);
        }
        return result;
    }

    /**
     * 发送Get请求
     */
    public static String sendGetHttp(String action, Map<String, Object> parmas) {
        String result = "";
        int status = 0;
        BufferedReader rd = null;
        try {
            if (!StringUtils.contains(action, "?")) {
                action += "?";
            }
            HttpClient client = new DefaultHttpClient();
            Iterator it = parmas.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                action += key + "=" + value + "&";
            }

            HttpGet request = new HttpGet(action);
            HttpResponse response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                if (status != 200) {
                    logger.error("----请求失败----status:" + status + "，action:" + action + "，line:" + line);
                }
                result += line;
            }

        } catch (Exception e) {
            logger.error("----请求失败----status:" + status + "，action:" + action + "，result:" + result,e);
        }
        return result;
    }

    public static Map<String, Object> sendGet(String url, Map<String, Object> params) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(1000).setConnectionRequestTimeout(1000).build();

            if (!StringUtils.contains(url, "?")) {
                url += "?";
            }
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                url += key + "=" + value + "&";
            }

            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            ResponseHandler<Map<String, Object>> responseHandler = new ResponseHandler<Map<String, Object>>() {
                @Override
                public Map<String, Object> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return Json2Map(IOUtils.toString(response.getEntity().getContent(), "utf-8"));
                }
            };
            return httpclient.execute(get, responseHandler);
        } catch (IOException e) {
            logger.error("----sendGet fail :  parmas:" + params,e);
            return null;
        }
    }

    public static Map<String, Object> sendGet(String url, Map<String, Object> params, int timeout) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(1000).build();

            if (!StringUtils.contains(url, "?")) {
                url += "?";
            }
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                url += key + "=" + value + "&";
            }

            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            ResponseHandler<Map<String, Object>> responseHandler = new ResponseHandler<Map<String, Object>>() {
                @Override
                public Map<String, Object> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return Json2Map(IOUtils.toString(response.getEntity().getContent(), "utf-8"));
                }
            };
            return httpclient.execute(get, responseHandler);
        } catch (IOException e) {
            logger.error("----sendGet fail :  parmas:" + params,e);
            return null;
        }
    }

    public static String sendGetString(String url, Map<String, Object> params) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(1000).setConnectionRequestTimeout(1000).build();

            if (!StringUtils.contains(url, "?")) {
                url += "?";
            }
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                url += key + "=" + value + "&";
            }

            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return (IOUtils.toString(response.getEntity().getContent(), "utf-8"));
                }
            };
            return httpclient.execute(get, responseHandler);
        } catch (IOException e) {
            logger.error("----sendGet fail :  parmas:" + params,e);
            return null;
        }
    }

    /**
     * Convert Json to Map
     *
     * @throws IOException
     * @throws org.codehaus.jackson.JsonParseException
     */
    public static Map<String, Object> Json2Map(String jsonStr) {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            // convert JSON string to Map
            map = mapper.readValue(jsonStr, Map.class);

        } catch (Exception e) {
            logger.error("----Json2Map error :  parmas:" + jsonStr,e);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 发送Post请求
     */
    public static String sendPostHttp(String url, Map params_map) {
        int status = 0;
        String result = "";
        try {
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Iterator it = params_map.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                try {
                    params.add(new BasicNameValuePair(key.toString(), value.toString()));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }

            httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(httppost);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {// 如果状态码为200,就是正常返回
                result = EntityUtils.toString(response.getEntity());
//				logger.info("----请求成功----result:" + result + "，params_map:" + params_map + "，url:" + url);
                return result;
            } else {
                logger.error("----请求失败----status:" + status + "，params_map:" + params_map + "，url:" + url);
            }
        } catch (Exception e) {
            logger.error("----请求失败----status:" + status + "，params_map:" + params_map + "，url:" + url + "，result:" + result,e);
        }
        return "";
    }

    /**
     * 发送PUT请求
     */
    public static String sendPutHttp(String url, Map params_map) {
        int status = 0;
        String result = "";
        try {
            HttpPut httpPut = new HttpPut(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Iterator it = params_map.entrySet().iterator();
            while (it.hasNext()) {// 添加参数
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                params.add(new BasicNameValuePair(key.toString(), value.toString()));
            }

            httpPut.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(httpPut);
            status = response.getStatusLine().getStatusCode();
            if (status == 200) {// 如果状态码为200,就是正常返回
                result = EntityUtils.toString(response.getEntity());
//				logger.info("----请求成功----result:" + result + "，params_map:" + params_map + "，url:" + url);
                return result;
            } else {
                logger.error("----请求失败----status:" + status + "，params_map:" + params_map + "，url:" + url);
            }
        } catch (Exception e) {
            logger.error("----请求失败----status:" + status + "，params_map:" + params_map + "，url:" + url + "，result:" + result,e);
        }
        return "";
    }

    public static String sendPostJsonHttp(String url, Map params_map) {
        int status = 0;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-Type", "application/json");
        String result = "";
        try {
            StringEntity s = new StringEntity(JSON.toJSON(params_map).toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            status = httpResponse.getStatusLine().getStatusCode();
            if (status == 200) {// 如果状态码为200,就是正常返回
                result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                logger.error("----请求失败----status:" + status + "，params_json:" + params_map + "，url:" + url);
            }
        } catch (Exception e) {
            logger.error("----请求失败----status:" + status + "，params_json:" + params_map + "，url:" + url + "，result:" + result,e);
        }
        return result;
    }

    public static String parseUrlPara(Map<String,Object> paraMap){
        StringBuilder paraUrl = new StringBuilder("?");
        Iterator it = paraMap.entrySet().iterator();
        while (it.hasNext()) {// 添加参数
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            paraUrl.append(key + "=" + value + "&") ;
        }
        return paraUrl.substring(0,paraUrl.length() - 1);
    }



}