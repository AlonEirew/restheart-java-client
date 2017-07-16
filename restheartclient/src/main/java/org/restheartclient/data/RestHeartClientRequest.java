package org.restheartclient.data;

/**
 * Created by aeirew on 7/13/2017.
 */
public class RestHeartClientRequest {
    private final String dataBaseName;
    private final String collectionName;
    private final String description;
    private final String eTag;
    private final String id;
    private final Object requestObject;

    public RestHeartClientRequest(final String dataBaseName, final String collectionName,
        final String description, final String eTag, final String id, final Object requestObject) {
        this.dataBaseName = dataBaseName;
        this.collectionName = collectionName;
        this.description = description;
        this.eTag = eTag;
        this.id = id;
        this.requestObject = requestObject;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getDescription() {
        return description;
    }

    public String getETag() {
        return eTag;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public String getId() {
        return id;
    }
}
