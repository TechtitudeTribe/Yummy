package com.techtitudetribe.yummy;

public class MyOffersAdapter {
    String amount, discount;

    public MyOffersAdapter() {

    }

    public MyOffersAdapter(String amount, String discount) {
        this.amount = amount;
        this.discount = discount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
