package com.pklein.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class step implements Parcelable {
    private int mPos;
    private int mId;
    private String mShortDescription;
    private String mDescription;
    private String mVideoURL;
    private String mThumbnailURL;

    public step(){ }

    public step(int mPos,int mId, String mShortDescription, String mDescription, String mVideoURL, String mThumbnailURL) {
        this.mPos = mPos;
        this.mId = mId;
        this.mShortDescription = mShortDescription;
        this.mDescription = mDescription;
        this.mVideoURL = mVideoURL;
        this.mThumbnailURL = mThumbnailURL;
    }

    public step(Parcel in) {
        mPos = in.readInt();
        mId = in.readInt();
        mShortDescription = in.readString();
        mDescription = in.readString();
        mVideoURL = in.readString();
        mThumbnailURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPos);
        dest.writeInt(mId);
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeString(mVideoURL);
        dest.writeString(mThumbnailURL);
    }

    public static final Parcelable.Creator<step> CREATOR = new Parcelable.Creator<step>() {

        public step createFromParcel(Parcel in) {
            return new step(in);
        }
        public step[] newArray(int size) {
            return new step[size];
        }
    };


     /* GETTER and SETTER */

    public int getmPos() {
        return mPos;
    }
    public void setmPos(int mPos) {
        this.mPos = mPos;
    }

    public int getmId() {
        return mId;
    }
    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmShortDescription() {
        return mShortDescription;
    }
    public void setmShortDescription(String mShortDescription) { this.mShortDescription = mShortDescription; }

    public String getmDescription() {
        return mDescription;
    }
    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmVideoURL() {
        return mVideoURL;
    }
    public void setmVideoURL(String mVideoURL) {
        this.mVideoURL = mVideoURL;
    }

    public String getmThumbnailURL() {
        return mThumbnailURL;
    }
    public void setmThumbnailURL(String mThumbnailURL) {
        this.mThumbnailURL = mThumbnailURL;
    }
}
