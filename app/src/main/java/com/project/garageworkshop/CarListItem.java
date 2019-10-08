package com.project.garageworkshop;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class CarListItem implements Parcelable {
    private String make, model, carNumb;
    private int imageId;

    public CarListItem() {

    }

    public CarListItem(String make, String model, String carNumb, int imageId) {
        this.make = make;
        this.model = model;
        this.imageId = imageId;
        this.carNumb = carNumb;
    }

    public CarListItem(String make, String model, String carNumb) {
        this.make = make;
        this.model = model;
        this.carNumb = carNumb;
    }
    public CarListItem(Parcel source) {
        make = source.readString();
        model = source.readString();
        carNumb = source.readString();
        imageId = source.readInt();

    }
    public String getMake() {
        return make;
    }
    public String getModel() {
        return model;
    }
    public int getImageId() {
        return imageId;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCarNumb() {
        return carNumb;
    }

    public void setCarNumb(String carNumb) {
        this.carNumb = carNumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(carNumb);
        dest.writeInt(imageId);
    }

    public static final Creator<CarListItem> CREATOR = new Creator<CarListItem>() {
        @Override
        public CarListItem createFromParcel(Parcel source) {
            return new CarListItem(source);
        }

        @Override
        public CarListItem[] newArray(int size) {
            return new CarListItem[0];
        }
    };
    public static class CustomComparator implements Comparator<CarListItem> {

        @Override
        public int compare(CarListItem o1, CarListItem o2) {
            return o1.getCarNumb().compareTo(o2.getCarNumb());
        }
    }
}
