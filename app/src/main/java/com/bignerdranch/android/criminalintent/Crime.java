package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by djn on 18-8-13.
 * A Crime Object describing an office crime
 */

public class Crime {

    private UUID mId; // Universal Unique ID
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspect() {

        return mSuspect;
    }


    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
