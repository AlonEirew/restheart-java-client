package org.restheartclient.data;

import org.apache.http.Header;
import org.apache.http.StatusLine;

/**
 * Created by aeirew on 7/12/2017.
 */
public class RestHeartClientResponse {

    private static final String ETAG_LABEL = "ETag";

    private final StatusLine statusLine;
    private final String body;
    private final Header[] headers;

    public RestHeartClientResponse(final StatusLine statusLine, final Header[] headers,
        final String body) {
        this.statusLine = statusLine;
        this.body = body;
        this.headers = headers;
    }

    public RestHeartClientResponse(final StatusLine statusLine, final Header[] headers) {
        this(statusLine, headers, null);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public int getStatusCode() {
        if (statusLine != null) {
            return statusLine.getStatusCode();
        }

        return -1;
    }

    public String getEtag() {
        String etag = null;
        if (headers != null) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase(ETAG_LABEL)) {
                    etag = header.getValue();
                }
            }
        }

        return etag;
    }

    public String getBody() {
        return body;
    }

    public Header[] getHeaders() {
        return headers;
    }
}
