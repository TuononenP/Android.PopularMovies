package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class IOUtils {
    public String ReadFileFromAssetsFolder(Context context, String fileName, String encoding) throws IOException {
        AssetManager am = context.getAssets();
        InputStream is = am.open(fileName);
        return ConvertInputStreamToString(is, encoding);
    }

    public String ConvertInputStreamToString(InputStream is, String encoding) throws IOException {
        int size = is.available();
        byte buffer[] = new byte[size];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(encoding);
    }
}
