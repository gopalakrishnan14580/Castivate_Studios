package com.sdi.castivate.utils;

import android.util.Log;

import com.sdi.castivate.model.fileUrlModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by twilightuser on 28/9/16.
 */
public class Compress {

    private static final int BUFFER = 2048;
    ArrayList<fileUrlModel> _files;
    private String _zipFile;

    public Compress(ArrayList<fileUrlModel> files, String zipFile) {
        _files = files;
        _zipFile = zipFile;
    }

    public void zip() {
        try  {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(_zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for(int i=0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i).getFileUrl());
                FileInputStream fi = new FileInputStream(String.valueOf(_files.get(i).getFileUrl()));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files.get(i).getFileUrl().substring(_files.get(i).getFileUrl().lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
