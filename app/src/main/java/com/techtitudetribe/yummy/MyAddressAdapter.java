package com.techtitudetribe.yummy;

public class MyAddressAdapter {
    String address, count;

    public MyAddressAdapter()
    {

    }

    public MyAddressAdapter(String address, String count) {
        this.address = address;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
