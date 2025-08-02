
package com.asmaa.rag;

public class Document {
    private String id;
    private String content;

    public Document(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Document{id='" + id + "', content='" + content + "'}";
    }
}
