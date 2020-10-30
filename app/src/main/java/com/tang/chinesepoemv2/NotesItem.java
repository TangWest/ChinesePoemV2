package com.tang.chinesepoemv2;

public class NotesItem {
    private String poemid;
    private String notecontent;
    private String notetime;

    public NotesItem() {
        super();
        notecontent = notecontent;
        notetime = notetime;
    }

    public NotesItem(String poemid, String notecontent, String notetime) {
        super();
        this.poemid = poemid;
        this.notecontent = notecontent;
        this.notetime = notetime;
    }

    public String getPoemid() {
        return poemid;
    }

    public void setPoemid(String poemid) {
        this.poemid = poemid;
    }

    public String getNotecontent() {
        return notecontent;
    }

    public void setNotecontent(String notecontent) {
        this.notecontent = notecontent;
    }

    public String getNotetime() {
        return notetime;
    }

    public void setNotetime(String notetime) {
        this.notetime = notetime;
    }
}
