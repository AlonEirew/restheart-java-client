package org.restheartclient.connection;

import java.io.Closeable;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by aeirew on 7/16/2017.
 */
public interface IHttpClientFactory extends Closeable {
    CloseableHttpClient getHttpClient();
}
