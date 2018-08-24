package com.messi.languagehelper.meinv.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.messi.languagehelper.meinv.http.LanguagehelperHttpClient;
import com.messi.languagehelper.meinv.impl.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Response;

public class DownLoadUtil {

    public static void downloadFile(final Context mContext, final String url, final String path, final String fileName, final Handler mHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    LogUtil.DefalutLog("---url:" + url);
                    Response mResponse = LanguagehelperHttpClient.get(url);
                    if (mResponse != null) {
                        if (mResponse.isSuccessful()) {
                            saveFile(mContext, path, fileName, mResponse.body().bytes());
                            if (mHandler != null) {
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                                LogUtil.DefalutLog("send Handler:"+msg.what);
                            }
                        } else if (mResponse.code() == 404) {
                            if (mHandler != null) {
                                msg.what = 3;
                                mHandler.sendMessage(msg);
                            }
                        } else {
                            if (mHandler != null) {
                                msg.what = 2;
                                mHandler.sendMessage(msg);
                            }
                        }
                    } else {
                        if (mHandler != null) {
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    if (mHandler != null) {
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean downloadFile(final Context mContext, final String url, final String path, final String fileName) {
        try {
            LogUtil.DefalutLog("downloadFile---url:" + url);
            LogUtil.DefalutLog("downloadFile---localPath:" + (path+fileName));
            Response mResponse = LanguagehelperHttpClient.get(url);
            if (mResponse != null) {
                if (mResponse.isSuccessful()) {
                    return saveFile(mContext, path, fileName, mResponse.body().bytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean downloadFile(final Context mContext, final String url, final String path,
                                       final String fileName, final ProgressListener progressListener,
                                       String flag) {
        try {
            LogUtil.DefalutLog("downloadFile---url:" + url);
            LogUtil.DefalutLog("downloadFile---localPath:" + (path+fileName));
            Response mResponse = LanguagehelperHttpClient.get(url,progressListener);
            if (mResponse != null) {
                if (mResponse.isSuccessful()) {
                    return saveFile(mContext, path, fileName, mResponse.body().bytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveFile(Context mContext, String path, String fileName, byte[] binaryData) {
        try {
            FileOutputStream mFileOutputStream = getFile(mContext, path, fileName);
            if (mFileOutputStream != null) {
                mFileOutputStream.write(binaryData);
                mFileOutputStream.flush();
                mFileOutputStream.close();
                LogUtil.DefalutLog("SaveFile:"+path+fileName);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static FileOutputStream getFile(Context mContext, String dir, String fileName) throws IOException {
        String path = SDCardUtil.getDownloadPath(dir);
        if (!TextUtils.isEmpty(path)) {
            File sdDir = new File(path);
            if (!sdDir.exists()) {
                sdDir.mkdirs();
            }
            String filePath = path + fileName;
            File mFile = new File(filePath);
            mFile.createNewFile();
            LogUtil.DefalutLog("CreateFile:" + filePath);
            return new FileOutputStream(mFile);
        } else {
            return null;
        }
    }

    /**
     * 删除内部存储中之前下载的apk
     *
     * @param mContext
     */
    public static void deleteOldUpdateFile(Context mContext) {
        try {
            File file = mContext.getFilesDir();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                LogUtil.DefalutLog("----------logoutFiles:" + files[i].getName());
                if (files[i].isFile()) {
                    String name = files[i].getName();
                    if (name.startsWith("zcp_stand_")) {
                        SDCardUtil.deleteFile(files[i].getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
