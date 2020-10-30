package com.tang.chinesepoemv2;

public class PoemsItem {
    private String poemid;
    private String poemtitle;
    private String poemcontent;

    public PoemsItem() {
        super();
        poemtitle = "";
        poemcontent = "";
    }

    public PoemsItem(String poemid, String poemtitle, String poemcontent) {
        super();
        this.poemid = poemid;
        this.poemtitle = poemtitle;
        this.poemcontent = poemcontent;
    }

    public String getPoemid() {
        return poemid;
    }

    public void setPoemid(String poemid) {
        this.poemid = poemid;
    }

    public String getPoemtitle() {
        return poemtitle;
    }

    public void setPoemtitle(String poemtitle) {
        this.poemtitle = poemtitle;
    }

    public String getPoemcontent() {
        return poemcontent;
    }

    public void setPoemcontent(String poemcontent) {
        this.poemcontent = poemcontent;
    }
}
