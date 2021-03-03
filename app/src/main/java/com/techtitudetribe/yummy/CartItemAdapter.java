package com.techtitudetribe.yummy;

public class CartItemAdapter {
    String itemName, itemPrice, itemImage, itemDescription, itemCustomizedPrice, itemQuantity;

    public CartItemAdapter() {

    }

    public CartItemAdapter(String itemName, String itemPrice, String itemImage, String itemDescription, String itemCustomizedPrice, String itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemImage = itemImage;
        this.itemDescription = itemDescription;
        this.itemCustomizedPrice = itemCustomizedPrice;
        this.itemQuantity = itemQuantity;
    }

    public String getItemCustomizedPrice() {
        return itemCustomizedPrice;
    }

    public void setItemCustomizedPrice(String itemCustomizedPrice) {
        this.itemCustomizedPrice = itemCustomizedPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

}
