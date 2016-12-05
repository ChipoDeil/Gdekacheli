package com.example.yaroslav.gdekacheli;

/**
 * Created by chipodeil on 05.12.2016.
 */

import java.io.File;

import android.os.Environment;

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}