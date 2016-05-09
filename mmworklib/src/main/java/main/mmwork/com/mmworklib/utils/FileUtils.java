package main.mmwork.com.mmworklib.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by anzhuo on 2016/1/29.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    // bit字节参考量
    public static final long SIZE_BT = 1024L;
    // KB字节参考量
    public static final long SIZE_KB = SIZE_BT * 1024L;
    // MB字节参考量
    public static final long SIZE_MB = SIZE_KB * 1024L;
    // GB字节参考量
    public static final long SIZE_GB = SIZE_MB * 1024L;
    // TB字节参考量
    public static final long SIZE_TB = SIZE_GB * 1024L;


    private static final int IMG_QUALITY = 60;
    public static String ImageSdPath;

    public static void initImageSdPath(String imageSdPath) {
        ImageSdPath = imageSdPath;
    }

    /**
     * 将String数据存为文件
     */
    public static Observable<String> saveFileObservable(final String content, final String path, final String name) {
        return Observable.just(name)
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        saveFile(content, path, name);
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return path + name;
                    }
                });
    }

    /**
     * 将String数据存为文件
     */
    public static boolean saveFile(String content, String path, String name) {
        boolean isSuccess = false;
        byte[] b = content.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path, name);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return isSuccess;
    }


    //根据名字读取文件
    public static String readFile(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(new FileReader(file));
                String content = "";
                StringBuilder sb = new StringBuilder();
                while (content != null) {
                    content = bf.readLine();
                    if (content == null) {
                        break;
                    }
                    sb.append(content.trim());
                }
                bf.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean exists(String path) {
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
        }
        return false;
    }

    //删除文件
    public static boolean deleteFile(String filePath) {
        boolean ret = false;
        try {
            ret = false;
            File f = new File(Environment.getExternalStorageDirectory(), filePath);
            if (f.exists()) {
                ret = f.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteFile", e);
        }
        return ret;
    }

    public static DecimalFormat df = new DecimalFormat("#.00");

    /**
     * 获取文件大小，自动转换文件单位大小
     *
     * @param fileS
     * @return
     */
    public static String getFileSize(long fileS) {// 转换文件大小

        String fileSizeString = "";
        if (fileS < SIZE_BT) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < SIZE_KB) {
            fileSizeString = df.format((double) fileS / SIZE_BT) + "KB";
        } else if (fileS < SIZE_MB) {
            fileSizeString = df.format((double) fileS / SIZE_KB) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / SIZE_MB) + "GB";
        }
        return fileSizeString;
    }

    public static String getName(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        if (!TextUtils.isEmpty(file.getName())) {
            String[] split = name.split("\\.");
            if (split != null) {
                name = split[0];
            }
        }
        return name;
    }

    /**
     * 保存图片到临时目录
     */
    public static File saveBitmap(Bitmap bm, String picName) {
        File file = save(bm, picName, null);
        return file;
    }

    private static File save(Bitmap bm, String picName, String path) {
        FileOutputStream fos = null;
        File file = null;
        try {
            if (!isFileExist("")) {
                createSDDir("");
            }
            if (!TextUtils.isEmpty(path)) {
                file = new File(path, picName + ".JPEG");
            } else {
                file = new File(ImageSdPath, picName + ".JPEG");
            }
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY, fos);
            Log.e("", "已经保存");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException", e);
        } finally {

            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(ImageSdPath + fileName);
        file.isFile();
        return file.exists();
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(ImageSdPath + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

        }
        return dir;
    }

}
