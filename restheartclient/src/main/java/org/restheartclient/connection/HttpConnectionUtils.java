package org.restheartclient.connection;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.restheartclient.utils.GsonUtils;

/**
 * Created by aeirew on 7/12/2017.
 */
public class HttpConnectionUtils implements IHttpConnectionsUtil, Closeable {

    private static final Logger LOGGER = Logger.getLogger(HttpConnectionUtils.class.getName());

    private IHttpClientFactory httpClientFactory;

    public HttpConnectionUtils() {
        this.httpClientFactory = new HttpClientBasicFactory();
    }

    public HttpConnectionUtils(IHttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    public <REQ> CloseableHttpResponse sendHttpPost(String url, REQ request) {
        CloseableHttpResponse execute = null;
        String requestJson = GsonUtils.toJson(request);

        try {
            LOGGER.log(Level.FINER, "Send POST request:" + requestJson + " to url-" + url);
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(requestJson, "UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            execute = this.httpClientFactory.getHttpClient().execute(httpPost);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "was unable to send POST request:" + requestJson
                + " (displaying first 1000 chars) from url-" + url, e);
        }

        return execute;
    }

    @Override
    public <REQ> CloseableHttpResponse sendHttpPut(String url, REQ request) {
        CloseableHttpResponse execute = null;
        String requestJson = GsonUtils.toJson(request);

        try {
            LOGGER.log(Level.FINER, "Send PUT request:" + requestJson + " to url-" + url);
            HttpPut httpPut = new HttpPut(url);
            StringEntity entity = new StringEntity(requestJson, "UTF-8");
            entity.setContentType("application/json");
            httpPut.setEntity(entity);
            execute = this.httpClientFactory.getHttpClient().execute(httpPut);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to send PUT request:" + requestJson
                + " (displaying first 1000 chars) from url-" + url, e);
        }

        return execute;
    }

    @Override
    public CloseableHttpResponse sendHttpGet(String url) {
        CloseableHttpResponse execute = null;
        try {
            LOGGER.info("Sending GET request to url-" + url);
            CloseableHttpClient httpClient = this.httpClientFactory.getHttpClient();
            HttpGet httpGet = new HttpGet(url);
            execute = httpClient.execute(httpGet);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to send Get request to url-" + url, e);
        }

        return execute;
    }

    @Override
    public CloseableHttpResponse sendHttpDelete(String url, List<Header> headers) {
        CloseableHttpResponse execute = null;
        try {
            LOGGER.info("Sending GET request to url-" + url);
            CloseableHttpClient httpClient = this.httpClientFactory.getHttpClient();
            HttpDelete httpDelete = new HttpDelete(url);
            if (headers != null && !headers.isEmpty()) {
                for (Header header : headers) {
                    httpDelete.addHeader(header);
                }
            }
            execute = httpClient.execute(httpDelete);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Was unable to send Get request to url-" + url, e);
        }

        return execute;
    }

    @Override
    public void close() throws IOException {
        this.httpClientFactory.close();
    }
}
