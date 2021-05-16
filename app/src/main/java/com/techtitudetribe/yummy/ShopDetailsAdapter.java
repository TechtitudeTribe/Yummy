package com.techtitudetribe.yummy;

public class ShopDetailsAdapter {
    String shopName, shopSchedule, shopContactNumber, shopWhatsappNumber, shopStatus, shopFrontImage, shopId, userId, shopContact;

    public ShopDetailsAdapter() {

    }

    public ShopDetailsAdapter(String shopName, String shopSchedule, String shopContactNumber, String shopWhatsappNumber, String shopStatus, String shopFrontImage, String shopId, String userId, String shopContact) {
        this.shopName = shopName;
        this.shopSchedule = shopSchedule;
        this.shopContactNumber = shopContactNumber;
        this.shopWhatsappNumber = shopWhatsappNumber;
        this.shopStatus = shopStatus;
        this.shopFrontImage = shopFrontImage;
        this.shopId = shopId;
        this.userId = userId;
        this.shopContact = shopContact;
    }

    public String getShopContact() {
        return shopContact;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getShopFrontImage() {
        return shopFrontImage;
    }

    public void setShopFrontImage(String shopFrontImage) {
        this.shopFrontImage = shopFrontImage;
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


}
