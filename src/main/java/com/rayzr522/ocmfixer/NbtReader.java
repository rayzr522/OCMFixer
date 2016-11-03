
package com.rayzr522.ocmfixer;

import java.io.File;
import java.io.FileInputStream;

import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.StreamOptions;

public class NbtReader {

    @SuppressWarnings("resource")
    public static NbtCompound read(File file) {
        try {
            return NbtFactory.fromStream(new FileInputStream(file), StreamOptions.GZIP_COMPRESSION);
        } catch (Exception e) {
            System.err.println("Failed to read NBT file: " + file.getPath());
            e.printStackTrace();
        }
        return null;
    }

}
