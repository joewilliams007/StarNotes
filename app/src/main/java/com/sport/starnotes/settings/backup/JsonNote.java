package com.sport.starnotes.settings.backup;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonNote {
    @JsonProperty("uid") int uid;
    @JsonProperty("name") String name;
    @JsonProperty("note") String note;
    @JsonProperty("synced") Boolean synced;
    @JsonProperty("star") Boolean star;
    @JsonProperty("last_edited") long last_edited;
    @JsonProperty("created") long created;

    public JsonNote(int uid, String name, String note, Boolean synced, Boolean star, long last_edited, long created) {
        this.uid = uid;
        this.name = name;
        this.note = note;
        this.synced = synced;
        this.star = star;
        this.last_edited = last_edited;
        this.created = created;
    }

    public JsonNote() {
        super();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public long getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(long last_edited) {
        this.last_edited = last_edited;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
