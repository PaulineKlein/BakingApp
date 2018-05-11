package com.pklein.bakingapp.data;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pklein.bakingapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class recipe implements Parcelable {

    private static final String TAG = recipe.class.getSimpleName();
    private int mId;
    private String mName;
    private int mServings;
    private String mImage;
    private List<ingredient> mIngredients;
    private List<step> mStep;

    public recipe(){
        mIngredients = new ArrayList<ingredient>();
        mStep = new ArrayList<step>();
    }

    public recipe(int mId, String mName, int mServings, String mImage, List<ingredient> mIngredients, List<step> mStep) {
        this.mId = mId;
        this.mName = mName;
        this.mServings = mServings;
        this.mImage = mImage;
        this.mIngredients = mIngredients;
        this.mStep = mStep;
    }

    public recipe(Parcel in) {

        this();

        mId = in.readInt();
        mServings = in.readInt();
        mName = in.readString();
        mImage = in.readString();

        in.readTypedList(mIngredients, ingredient.CREATOR);
        in.readTypedList(mStep, step.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mServings);
        dest.writeString(mName);
        dest.writeString(mImage);
        dest.writeTypedList(mIngredients);
        dest.writeTypedList(mStep);
    }

    public static final Parcelable.Creator<recipe> CREATOR = new Parcelable.Creator<recipe>() {

        public recipe createFromParcel(Parcel in) {
            return new recipe(in);
        }
        public recipe[] newArray(int size) {
            return new recipe[size];
        }
    };

    /* GETTER and SETTER */

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmServings() {
        return mServings;
    }

    public void setmServings(int mServings) {
        this.mServings = mServings;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public List<ingredient> getmIngredients() {
        return mIngredients;
    }

    public void setmIngredients(List<ingredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public List<step> getmStep() {
        return mStep;
    }

    public void setmStep(List<step> mStep) {
        this.mStep = mStep;
    }
}
