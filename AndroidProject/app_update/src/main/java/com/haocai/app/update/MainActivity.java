package com.haocai.app.update;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.Toast;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import java.io.File;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_STORAGE = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "您正在进行省流量更新", Toast.LENGTH_SHORT).show();
                    ApkUtils.installApk(MainActivity.this, Constants.NEW_APK_PATH);
                    break;
            }
        }
    };
    private NumberFormat numberFormat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("简单文件下载");

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        checkSDCardPermission();

        /**
         * 因为后台没有写版本判断语句
         * 在高版本下暂时先注释fileDownload(); 否则一直下载安装
         *
         * 低版本下运行fileDownload();
         */
        //  fileDownload();


    }


    /**
     * 检查SD卡权限
     */
    protected void checkSDCardPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取权限
                fileDownload();
            } else {
                Toast.makeText(getApplicationContext(), "权限被禁止，无法下载文件！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }


    public void fileDownload() {

        OkGo.<File>get(Constants.URL_PATCH_DOWNLOAD)//
                .tag(this)//
                .execute(new FileCallback(Constants.SD_CARD, Constants.PATCH_FILE) {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                    }

                    @Override
                    public void onSuccess(Response<File> response) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    //      File patchFile = new File(Constants.SD_CARD, Constants.PATCH_FILE);
                                    String oldfile = ApkUtils.getSourceApkPath(MainActivity.this, getPackageName());
                                    String newfile = Constants.NEW_APK_PATH;
                                    String patchfile = Constants.SD_CARD + File.separator + Constants.PATCH_FILE;
                                    BsPatch.patch(oldfile, newfile, patchfile);

                                    mHandler.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                    }

                    @Override
                    public void onError(Response<File> response) {

                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        System.out.println(progress);

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        System.out.println(downloadLength);
                    }
                });
    }

}
