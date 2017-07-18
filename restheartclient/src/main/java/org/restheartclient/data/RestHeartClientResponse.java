package org.restheartclient.data;

import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.StatusLine;

/**
 * Created by aeirew on 7/12/2017.
 */
public class RestHeartClientResponse {

    private static final String ETAG_LABEL = "ETag";
    private static final String LOCATION_LABEL = "Location";

    private final StatusLine statusLine;
    private final JsonObject responseObject;
    private final Header[] headers;

    public RestHeartClientResponse(final StatusLine statusLine, final Header[] headers,
        final JsonObject responseObject) {
        this.statusLine = statusLine;
        this.responseObject = responseObject;
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
        return getHeaderByName(ETAG_LABEL);
    }

    public String getDocumentUrlLocation() {
        return getHeaderByName(LOCATION_LABEL);
    }

    public String getHeaderByName(String headerName) {
        String value = null;
        if (this.headers != null) {
            for (Header header : this.headers) {
                if (header.getName().equalsIgnoreCase(headerName)) {
                    value = header.getValue();
                }
            }
        }

        return value;
    }

    public JsonObject getResponseObject() {
        return responseObject;
    }

    public Header[] getHeaders() {
        return headers;
    }
}
