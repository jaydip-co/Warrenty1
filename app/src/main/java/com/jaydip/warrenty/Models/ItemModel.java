package com.jaydip.warrenty.Models;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "Items")
public class ItemModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int key;
    private  String Iname;
    private String Category;
    private String purchaseDate;
    private int DurationMonth;
    private String Detail;
    private String ExpireDate;
    private byte[] ItemImage;
    private byte[] billImage;
    private String ItemImageUri;
    private String billUri;
    private boolean isBillPdf;
    private String lastUpdateDate;

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getIname() {
        return Iname;
    }

    public void setIname(String iname) {
        Iname = iname;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getDurationMonth() {
        return DurationMonth;
    }

    public void setDurationMonth(int durationMonth) {
        DurationMonth = durationMonth;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public byte[] getItemImage() {
        return ItemImage;
    }

    public void setItemImage(byte[] itemImage) {
        ItemImage = itemImage;
    }

    public byte[] getBillImage() {
        return billImage;
    }

    public void setBillImage(byte[] billImage) {
        this.billImage = billImage;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getItemImageUri() {
        return ItemImageUri;
    }

    public void setItemImageUri(String itemImageUri) {
        ItemImageUri = itemImageUri;
    }

    public String getBillUri() {
        return billUri;
    }

    public void setBillUri(String billUri) {
        this.billUri = billUri;
    }

    public boolean isBillPdf() {
        return isBillPdf;
    }

    public void setBillPdf(boolean billPdf) {
        isBillPdf = billPdf;
    }
}
