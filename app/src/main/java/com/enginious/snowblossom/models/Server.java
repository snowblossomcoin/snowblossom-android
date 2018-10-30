package com.enginious.snowblossom.models;

/**
 * Created by waleed on 10/24/18.
 */

public class Server {

    String url;
    String port;

    Boolean connected;

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Server() {
        this.url = "";
        this.port = "";
        connected = false;
    }

    public Server(String url, String port, Boolean connected) {
        this.url = url;
        this.port = port;
        this.connected = connected;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }





}
