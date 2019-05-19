package com.example.acronyms;

import java.util.List;

public class LongForm {
    private String sf;
    private List<LongFormItem> lfs;

    public LongForm(String sf, List<LongFormItem> lfs) {
        this.sf = sf;
        this.lfs = lfs;
    }

    public String getSf() {
        return sf;
    }

    public List<LongFormItem> getLfs() {
        return lfs;
    }
}

class LongFormItem {
    private String lf;
    private int freq;
    private int since;

    public LongFormItem(String lf, int freq, int since) {
        this.lf = lf;
        this.freq = freq;
        this.since = since;
    }

    public String getLf() {
        return lf;
    }

    public int getFreq() {
        return freq;
    }

    public int getSince() {
        return since;
    }
}
