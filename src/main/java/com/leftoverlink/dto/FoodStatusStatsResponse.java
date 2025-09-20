package com.leftoverlink.dto;

public class FoodStatusStatsResponse {
    private long posted;
    private long accepted;
    private long completed;
    private long expired;  // ✅ new field

    public FoodStatusStatsResponse(long posted, long accepted, long completed, long expired) {
        this.posted = posted;
        this.accepted = accepted;
        this.completed = completed;
        this.expired = expired;
    }

    public long getPosted() {
        return posted;
    }

    public void setPosted(long posted) {
        this.posted = posted;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }
}
