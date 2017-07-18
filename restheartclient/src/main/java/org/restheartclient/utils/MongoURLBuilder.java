package org.restheartclient.utils;

import java.lang.reflect.MalformedParametersException;

/**
 * Created by aeirew on 7/17/2017.
 */
public class MongoURLBuilder {
    private String baseURL;
    private String databaseName;
    private String collectionName;
    private String documentId;

    public MongoURLBuilder setBaseURL(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public MongoURLBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public MongoURLBuilder setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public MongoURLBuilder setDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public String build() throws MalformedParametersException {
        if(this.baseURL == null || this.baseURL.isEmpty()) {
            throw new MalformedParametersException("Cannot build URL with empty host");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.baseURL);
        if(databaseName != null && !databaseName.isEmpty()) {
            sb.append("/").append(databaseName);
            if(collectionName != null && !collectionName.isEmpty()) {
                sb.append("/").append(collectionName);
                if(documentId != null && !documentId.isEmpty()) {
                    sb.append("/").append(documentId);
                }
            }
        }

        return sb.toString();
    }
}
