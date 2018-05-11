package com.pklein.bakingapp.data;


import android.os.Parcel;
import android.os.Parcelable;

public class ingredient implements Parcelable {

    private String mQuantity;
    private String mMeasure;
    private String mIngredient;

    public ingredient(){
    }

    public ingredient(String mQuantity, String mMeasure, String mIngredient) {
        this.mQuantity = mQuantity;
        this.mMeasure = mMeasure;
        this.mIngredient = mIngredient;
    }

    public ingredient(Parcel in) {
        mQuantity = in.readString();
        mMeasure = in.readString();
        mIngredient = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mIngredient);
    }

    public static final Parcelable.Creator<ingredient> CREATOR = new Parcelable.Creator<ingredient>() {

        public ingredient createFromParcel(Parcel in) {
            return new ingredient(in);
        }
        public ingredient[] newArray(int size) {
            return new ingredient[size];
        }
    };

    /* GETTER and SETTER */

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmMeasure() {
        return mMeasure;
    }

    public void setmMeasure(String mMeasure) {
        this.mMeasure = mMeasure;
    }

    public String getmIngredient() {
        return mIngredient;
    }

    public void setmIngredient(String mIngredient) {
        this.mIngredient = mIngredient;
    }
}
