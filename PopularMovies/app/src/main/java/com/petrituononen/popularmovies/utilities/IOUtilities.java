package com.petrituononen.popularmovies.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Petri Tuononen on 20.1.2017.
 */
public class IOUtilities {
    public String readFileFromAssetsFolder(Context context, String fileName, String encoding)
            throws IOException {
        AssetManager am = context.getAssets();
        InputStream is = am.open(fileName);
        return convertInputStreamToString(is, encoding);
    }

    public String convertInputStreamToString(InputStream is, String encoding) throws IOException {
        int size = is.available();
        byte buffer[] = new byte[size];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(encoding);
    }

    public byte[] GetBytesFromUrl(URL url) throws NetworkOnMainThreadException {
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), baos);

            return baos.toByteArray();
        }
        catch (IOException e)
        {
            Log.w("IOUtilities", e.getMessage());
            e.printStackTrace();
            return null;
        }
//        catch (NetworkOnMainThreadException e) {
//            Log.w("IOUtilities", e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
    }
}
