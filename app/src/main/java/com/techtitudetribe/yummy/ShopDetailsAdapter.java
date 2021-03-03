package com.techtitudetribe.yummy;

public class ShopDetailsAdapter {
    String shopName, shopSchedule, shopContactNumber, shopWhatsappNumber, shopStatus, shopImageUrl;

    public ShopDetailsAdapter() {

    }

    public ShopDetailsAdapter(String shopName, String shopSchedule, String shopContactNumber, String shopWhatsappNumber, String shopStatus, String shopImageUrl) {
        this.shopName = shopName;
        this.shopSchedule = shopSchedule;
        this.shopContactNumber = shopContactNumber;
        this.shopWhatsappNumber = shopWhatsappNumber;
        this.shopStatus = shopStatus;
        this.shopImageUrl = shopImageUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopSchedule() {
        return shopSchedule;
    }

    public void setShopSchedule(String shopSchedule) {
        this.shopSchedule = shopSchedule;
    }

    public String getShopContactNumber() {
        return shopContactNumber;
    }

    public void setShopContactNumber(String shopContactNumber) {
        this.shopContactNumber = shopContactNumber;
    }

    public String getShopWhatsappNumber() {
        return shopWhatsappNumber;
    }

    public void setShopWhatsappNumber(String shopWhatsappNumber) {
        this.shopWhatsappNumber = shopWhatsappNumber;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }
}
