package com.sport.starnotes.settings.backup;

import static com.sport.starnotes.MainActivity.finalPath;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RetriveandSaveJSONdatafromfile {

    @SuppressLint("SimpleDateFormat")
    static
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm");

    // on below line we are creating a variable
    // for current date and time and calling a simple date format in it.
    public static String currentDateAndTime = sdf.format(new Date());


    public static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/starnotes-"+currentDateAndTime;

    public static String objectToFile(Object object, Boolean multiple) throws IOException {

        if (!multiple) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/starnote-"+currentDateAndTime;
        }
        File dir = new File(path);

        if (!dir.getParentFile().exists())
            dir.getParentFile().mkdirs();
        if (!dir.exists())
            dir.createNewFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        path += ".txt";

        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }
        finalPath = path;


        Writer output = null;
        String text = object.toString();
        File file = new File(path);
        output = new BufferedWriter(new FileWriter(file));
        output.write(text);
        output.close();

        return path;
    }

    public static Object objectFromFile() throws IOException, ClassNotFoundException {
        Object object = null;
        File data = new File(path);
        if(data.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
            object = objectInputStream.readObject();
            objectInputStream.close();
        }
        return object;
    }
}
