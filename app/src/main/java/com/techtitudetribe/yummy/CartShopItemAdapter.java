package com.techtitudetribe.yummy;

public class CartShopItemAdapter {
    String itemNames, shopName;

    public CartShopItemAdapter()
    {

    }

    public CartShopItemAdapter(String itemNames, String shopName) {
        this.itemNames = itemNames;
        this.shopName = shopName;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
