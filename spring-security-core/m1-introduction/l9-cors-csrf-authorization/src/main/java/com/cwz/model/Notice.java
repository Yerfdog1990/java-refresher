package com.cwz.model;

public class Notice {

    String noticeId;
    String noticeTitle;

    public Notice() {
    }

    public Notice(String noticeId, String noticeTitle) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }
}
