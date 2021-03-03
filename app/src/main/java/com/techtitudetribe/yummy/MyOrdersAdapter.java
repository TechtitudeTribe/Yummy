package com.techtitudetribe.yummy;

public class MyOrdersAdapter {
    String itemNames, itemDescription, itemPlacedDate, itemTotalAmount,count;

    public MyOrdersAdapter()
    {

    }

    public MyOrdersAdapter(String itemNames, String itemDescription, String itemPlacedDate, String itemTotalAmount, String count) {
        this.itemNames = itemNames;
        this.itemDescription = itemDescription;
        this.itemPlacedDate = itemPlacedDate;
        this.itemTotalAmount = itemTotalAmount;
        this.count = count;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getItemPlacedDate() {
        return itemPlacedDate;
    }

    public void setItemPlacedDate(String itemPlacedDate) {
        this.itemPlacedDate = itemPlacedDate;
    }

    public String getItemTotalAmount() {
        return itemTotalAmount;
    }

    public void setItemTotalAmount(String itemTotalAmount) {
        this.itemTotalAmount = itemTotalAmount;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
