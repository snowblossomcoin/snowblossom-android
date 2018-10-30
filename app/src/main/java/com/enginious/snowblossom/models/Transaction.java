package com.enginious.snowblossom.models;

/**
 * Created by waleed on 10/23/18.
 */

public class Transaction {


    private String address;
    private long value;
    private Boolean confirmed;

    public Transaction() {
        this.address = "";
        this.value = 0;
        this.confirmed = false;
    }

    public Transaction(String address, long value, Boolean confirmed) {
        this.address = address;
        this.value = value;
        this.confirmed = confirmed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
