package org.restheartclient.data;

/**
 * Created by aeirew on 7/13/2017.
 */
public class RestHeartClientRequestBuilder {
    private String dataBaseName;
    private String collectionName;
    private String description;
    private String eTag;
    private String id;
    private Object requestObject;

    public RestHeartClientRequestBuilder setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
        return this;
    }

    public RestHeartClientRequestBuilder setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public RestHeartClientRequestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RestHeartClientRequestBuilder setETag(String eTag) {
        this.eTag = eTag;
        return this;
    }

    public RestHeartClientRequestBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public RestHeartClientRequestBuilder setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
        return this;
    }

    public RestHeartClientRequest build() {
        return new RestHeartClientRequest(dataBaseName, collectionName, description, eTag, id, requestObject);
    }
}
