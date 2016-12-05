package com.example.yaroslav.gdekacheli;

/**
 * Created by chipodeil on 05.12.2016.
 */
import java.io.File;

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}

