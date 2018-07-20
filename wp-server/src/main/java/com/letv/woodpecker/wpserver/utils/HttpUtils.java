package com.letv.woodpecker.wpserver.utils;

import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUtils {

    public static final List<Header> HEADER_LIST = new ArrayList<Header>();

    private static final int HTTP_UTILS_CONNECTION_TIMEOUT = 3000;

    private static final int HTTP_UTILS_SOCKET_TIMEOUT = 3000;

    static {

        HEADER_LIST.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
        HEADER_LIST.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        HEADER_LIST.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36"));
    }

    public static String handleResponse(HttpResponse response, Charset charset) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode >= 300) {
            return null;
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }

        Header headerEncode = response.getLastHeader("Content-Encoding");
        if (null != headerEncode && "Content-Encoding: gzip".equalsIgnoreCase(headerEncode.toString())) {
            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
        }

        return EntityUtils.toString(entity, charset == null ? Charsets.UTF_8 : charset);
    }

    public static String get(String url, String host) throws Exception {
        return get(url, host, 0);
    }

    public static String get(String url, String host, int timeout) throws Exception {
        List<NameValuePair> nvps = null;
        return get(url, host, nvps, timeout);
    }

    public static String get(String url, String host, Map<String, String> paramMap, int timeout) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<Entry<String, String>> entrySet = paramMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (entry.getValue() != null) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return get(url, host, nvps, timeout);
    }

    public static String get(String url, String host, List<NameValuePair> nameValuePairList, int timeout)
            throws Exception {
        StringBuffer urlBuffer = new StringBuffer(url);
        if (nameValuePairList != null) {
            int paramCount = nameValuePairList.size();
            for (int i = 0; i < paramCount; i++) {
                NameValuePair nameValuePair = nameValuePairList.get(i);
                urlBuffer.append(i == 0 ? "?" : "&");
                urlBuffer.append(nameValuePair.getName()).append("=").append(nameValuePair.getValue() == null ? ""
                        : URLEncoder.encode(nameValuePair.getValue(), Charsets.UTF_8.name()));
            }
        }

        return get(urlBuffer.toString(), host, null, timeout, timeout, (Charset) null);
    }

    /**
     * 
     * @param url
     * @param host
     * @param headerList
     * @param socket_timeout
     * @param conn_timeout
     * @param charset
     * @return String
     * @throws Exception
     */
    public static String get(String url, String host, List<Header> headerList, int socket_timeout, int conn_timeout,
            Charset charset) throws Exception {

        HttpGet httpGet = new HttpGet(url);

        try {
            // 设置请求头
            List<Header> headers = headerList;
            if (headers == null) {
                headers = new ArrayList<Header>(HttpUtils.HEADER_LIST);
            }
            if (host != null) {
                headers.add(new BasicHeader("Host", host));
            }
            httpGet.setHeaders(headers.toArray(new Header[0]));

            Builder builder = RequestConfig.custom()
                    .setSocketTimeout(socket_timeout > 0 ? socket_timeout : HTTP_UTILS_SOCKET_TIMEOUT)
                    .setConnectTimeout(conn_timeout > 0 ? conn_timeout : HTTP_UTILS_CONNECTION_TIMEOUT);

            httpGet.setConfig(builder.build());

            CloseableHttpClient httpclient = HttpConnPoolManager.getInstance().build();
            CloseableHttpResponse response = httpclient.execute(httpGet);
            return handleResponse(response, charset);
        } finally {
            if (httpGet != null) {
                try {
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    ;
                }
            }
        }
    }

    /**
     * 
     * @param url
     * @param host
     * @param headerList
     * @param socket_timeout
     * @param conn_timeout
     * @param responseHandler
     * @return
     * @throws Exception
     */
    public static <T> T get(String url, String host, List<Header> headerList, int socket_timeout, int conn_timeout,
            ResponseHandler<T> responseHandler) throws Exception {

        HttpGet httpGet = new HttpGet(url);

        try {
            // 设置请求头
            List<Header> headers = headerList;
            if (headers == null) {
                headers = new ArrayList<Header>(HttpUtils.HEADER_LIST);
            }
            if (host != null) {
                headers.add(new BasicHeader("Host", host));
            }
            httpGet.setHeaders(headers.toArray(new Header[0]));

            Builder builder = RequestConfig.custom()
                    .setSocketTimeout(socket_timeout > 0 ? socket_timeout : HTTP_UTILS_SOCKET_TIMEOUT)
                    .setConnectTimeout(conn_timeout > 0 ? conn_timeout : HTTP_UTILS_CONNECTION_TIMEOUT);

            httpGet.setConfig(builder.build());

            CloseableHttpClient httpclient = HttpConnPoolManager.getInstance().build();

            T resp = httpclient.execute(httpGet, responseHandler);

            return resp;
        } finally {
            if (httpGet != null) {
                try {
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    ;
                }
            }
        }
    }

    public static String post(String url, String host, Map<String, String> paramMap, int timeout) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<Entry<String, String>> entrySet = paramMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (entry.getValue() != null) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        return post(url, host, null, null, nvps, timeout, timeout);
    }

    public static String post(String url, String host, List<NameValuePair> nameValuePairList, int timeout)
            throws Exception {
        return post(url, host, null, null, nameValuePairList, timeout, timeout);
    }

    public static String post(String url, String host, Charset charset, List<NameValuePair> nameValuePairList)
            throws Exception {
        return post(url, host, charset, null, nameValuePairList, 0, 0);
    }

    /**
     * 
     * @param url
     * @param host
     * @param charset
     * @param headerList
     * @param nameValuePairList
     * @param socket_timeout
     * @param conn_timeout
     * @return String
     * @throws Exception
     */
    public static String post(String url, String host, Charset charset, List<Header> headerList,
            List<NameValuePair> nameValuePairList, int socket_timeout, int conn_timeout) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (charset == null) {
                charset = Charsets.UTF_8;
            }

            // 设置请求头
            List<Header> headers = headerList;
            if (headers == null) {
                headers = new ArrayList<Header>(HttpUtils.HEADER_LIST);
            }
            if (host != null) {
                headers.add(new BasicHeader("Host", host));
            }
            httpPost.setHeaders(headers.toArray(new Header[0]));

            if (nameValuePairList != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, charset));
            }

            Builder builder = RequestConfig.custom()
                    .setSocketTimeout(socket_timeout > 0 ? socket_timeout : HTTP_UTILS_SOCKET_TIMEOUT)
                    .setConnectTimeout(conn_timeout > 0 ? conn_timeout : HTTP_UTILS_CONNECTION_TIMEOUT);

            httpPost.setConfig(builder.build());
            CloseableHttpClient httpclient = HttpConnPoolManager.getInstance().build();
            CloseableHttpResponse response = httpclient.execute(httpPost);
            return handleResponse(response, charset);
        } finally {
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    ;
                }
            }
        }
    }

    public static String post(String url, String host, String body, int timeout) throws Exception {
        return HttpUtils.post(url, host, null, null, body, timeout, timeout);
    }

    public static String post(String url, String host, Charset charset, List<Header> headerList, String body,
            int socket_timeout, int conn_timeout) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (charset == null) {
                charset = Charsets.UTF_8;
            }

            // 设置请求头
            List<Header> headers = headerList;
            if (headers == null) {
                headers = new ArrayList<Header>(HttpUtils.HEADER_LIST);
            }
            if (host != null) {
                headers.add(new BasicHeader("Host", host));
            }
            httpPost.setHeaders(headers.toArray(new Header[0]));

            // 设置请求体
            httpPost.addHeader("Content-Type", "text/xml");
            if (!StringUtils.isEmpty(body)) {
                httpPost.setEntity(new StringEntity(body, charset));
            }

            Builder builder = RequestConfig.custom()
                    .setSocketTimeout(socket_timeout > 0 ? socket_timeout : HTTP_UTILS_SOCKET_TIMEOUT)
                    .setConnectTimeout(conn_timeout > 0 ? conn_timeout : HTTP_UTILS_CONNECTION_TIMEOUT);

            httpPost.setConfig(builder.build());
            CloseableHttpClient httpclient = HttpConnPoolManager.getInstance().build();
            CloseableHttpResponse response = httpclient.execute(httpPost);
            return handleResponse(response, charset);
        } finally {
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    ;
                }
            }
        }
    }

    public static String createGetUrl(String url, Map<String, String> paramMap) throws Exception {
        StringBuffer urlBuffer = new StringBuffer(url);
        if (paramMap != null) {
            urlBuffer.append("?").append(toQueryString(paramMap));
        }

        return urlBuffer.toString();
    }

    public static String toQueryString(Map<String, String> paramMap) {
        StringBuffer buff = new StringBuffer();

        for (Entry<String, String> entry : paramMap.entrySet()) {
            try {
                if (buff.length() > 0) {
                    buff.append("&");
                }

                buff.append(entry.getKey()).append("=").append(
                        entry.getValue() == null ? "" : URLEncoder.encode(entry.getValue(), Charsets.UTF_8.name()));
            } catch (Exception e) {
                ;
            }
        }

        return buff.toString();
    }

}
