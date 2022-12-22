package com.sport.starnotes;

public class NoteItem {
    private final int mUid;
    private final String mName;
    private final String mNote;
    private final Boolean mSynced;
    private final Boolean mStar;
    private final long mLast_edited;
    private final long mCreated;

    public NoteItem(int uid, String name, String note, Boolean synced, boolean star, long last_edited, long created) {
        mUid = uid;
        mName = name;
        mNote = note;
        mSynced = synced;
        mStar = star;
        mLast_edited = last_edited;
        mCreated = created;
    }

    public int getUid() {
        return mUid;
    }

    public String getName() {
        return mName;
    }

    public String getNote() {
        return mNote;
    }

    public Boolean getSynced() {
        return mSynced;
    }

    public Boolean getStar() {
        return mStar;
    }

    public long getLast_edited() {
        return mLast_edited;
    }

    public long getCreated() {
        return mCreated;
    }
}


