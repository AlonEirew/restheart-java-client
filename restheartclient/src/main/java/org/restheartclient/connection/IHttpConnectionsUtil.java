package org.restheartclient.connection;

import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Created by aeirew on 7/16/2017.
 */
public interface IHttpConnectionsUtil {
    <REQ> CloseableHttpResponse sendHttpPost(String url, REQ request);
    <REQ> CloseableHttpResponse sendHttpPut(String url, REQ request);
    CloseableHttpResponse sendHttpGet(String url);
    CloseableHttpResponse sendHttpDelete(String url, List<Header> headers);
}
