package io.freedriver.showingtime.model;

import java.util.Objects;

public class ShowNotification {
    private long id;
    private int typeEnum;
    private int statusEnum;
    private int itemType;
    private String readWhen;
    private String madeWhen;
    private String type;
    private String status;
    private String date;
    private String addedDate;
    private long activityId;
    private String activityEndTime;
    private String token;
    private long listingId;
    private String mlsListingId;
    private String mlsCode;
    private String mlsOfficeId;
    private String addressShortNoUnitWord;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(int typeEnum) {
        this.typeEnum = typeEnum;
    }

    public int getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(int statusEnum) {
        this.statusEnum = statusEnum;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getReadWhen() {
        return readWhen;
    }

    public void setReadWhen(String readWhen) {
        this.readWhen = readWhen;
    }

    public String getMadeWhen() {
        return madeWhen;
    }

    public void setMadeWhen(String madeWhen) {
        this.madeWhen = madeWhen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(String activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getListingId() {
        return listingId;
    }

    public void setListingId(long listingId) {
        this.listingId = listingId;
    }

    public String getMlsListingId() {
        return mlsListingId;
    }

    public void setMlsListingId(String mlsListingId) {
        this.mlsListingId = mlsListingId;
    }

    public String getMlsCode() {
        return mlsCode;
    }

    public void setMlsCode(String mlsCode) {
        this.mlsCode = mlsCode;
    }

    public String getMlsOfficeId() {
        return mlsOfficeId;
    }

    public void setMlsOfficeId(String mlsOfficeId) {
        this.mlsOfficeId = mlsOfficeId;
    }

    public String getAddressShortNoUnitWord() {
        return addressShortNoUnitWord;
    }

    public void setAddressShortNoUnitWord(String addressShortNoUnitWord) {
        this.addressShortNoUnitWord = addressShortNoUnitWord;
    }

    @Override
    public String toString() {
        return "ShowNotification{" +
                "id=" + id +
                ", typeEnum=" + typeEnum +
                ", statusEnum=" + statusEnum +
                ", itemType=" + itemType +
                ", readWhen=" + readWhen +
                ", madeWhen=" + madeWhen +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", addedDate='" + addedDate + '\'' +
                ", activityId=" + activityId +
                ", activityEndTime=" + activityEndTime +
                ", token='" + token + '\'' +
                ", listingId=" + listingId +
                ", mlsListingId='" + mlsListingId + '\'' +
                ", mlsCode='" + mlsCode + '\'' +
                ", mlsOfficeId='" + mlsOfficeId + '\'' +
                ", addressShortNoUnitWord='" + addressShortNoUnitWord + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowNotification that = (ShowNotification) o;
        return
                activityId == that.activityId &&
                listingId == that.listingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, listingId);
    }
}
