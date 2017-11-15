package com.haocai.app.update;

/**
 * Created by Xionghu on 2017/11/14.
 * Desc:
 */

public class BsPatch {
    /**
     * 合并
     * @param oldfile
     * @param newfile
     * @param patchfile
     */
    public native static void patch(String oldfile,String newfile,String patchfile);

    static {
        System.loadLibrary("bspatch");
    }
}
