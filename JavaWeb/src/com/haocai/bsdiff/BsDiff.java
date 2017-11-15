package com.haocai.bsdiff;

/**
 * Created by Administrator on 2017/11/14.
 */
public class BsDiff {
    /**
     * 差分
     * @param oldfile
     * @param newfile
     * @param patchfile
     */
    public native static void diff(String oldfile,String newfile,String patchfile);

    static {
        System.loadLibrary("bsdiff");
    }
}
