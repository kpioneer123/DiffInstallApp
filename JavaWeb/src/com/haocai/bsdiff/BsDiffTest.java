package com.haocai.bsdiff;

/**
 * Created by Administrator on 2017/11/14.
 */
public class BsDiffTest {
    public static void main(String[] args){
        //得到差分包
        BsDiff.diff(ConstantsWin.OLD_APK_PATH,ConstantsWin.NEW_APK_PATH,ConstantsWin.PATCH_PATH);
    }
}
