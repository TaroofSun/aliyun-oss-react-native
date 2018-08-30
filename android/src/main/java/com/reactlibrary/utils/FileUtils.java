package com.reactlibrary.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    /**
     * copy file
     * @param context
     * @param srcUri
     * @param dstFile
     */
    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream is = context.getContentResolver().openInputStream(srcUri);
            if (is == null) return;
            OutputStream fos = new FileOutputStream(dstFile);
            int ch = 0;
            try {
                while((ch=is.read()) != -1){
                    fos.write(ch);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally{
                // close inputstream
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getFileName
     * @param uri
     * @return
     */
    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    /**
     * getFilePathFromURI
     * @param context
     * @param contentUri
     * @return
     */
    public static String getFilePathFromURI(Activity context, Uri contentUri) {
        //copy file and send new file path
//        String fileName = getFileName(contentUri);
//        if (!TextUtils.isEmpty(fileName)) {
//            File copyFile = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName);
//            FileUtils.copy(context, contentUri, copyFile);
//            return copyFile.getAbsolutePath();
//        }
//        return null;

        return uriToFile(contentUri, context).getAbsolutePath();
    }


    /**
     * 将uri转换为file
     * @param uri
     * @param activity
     * @return
     */
    public static File uriToFile(Uri uri, Activity activity) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            return null;
        }
        return null;
    }
}