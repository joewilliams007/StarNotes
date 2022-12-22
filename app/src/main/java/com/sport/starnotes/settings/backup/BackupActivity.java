package com.sport.starnotes.settings.backup;

import static com.sport.starnotes.Account.created_or_edited;
import static com.sport.starnotes.Account.updateUuidCounter;
import static com.sport.starnotes.app.toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.sport.starnotes.Account;
import com.sport.starnotes.AppDatabase;
import com.sport.starnotes.Note;
import com.sport.starnotes.NoteDao;
import com.sport.starnotes.R;
import com.sport.starnotes.app;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BackupActivity extends AppCompatActivity {
    private static final int PICKFILE_REQUEST_CODE = 0;
    NoteDao noteDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Account.theme().equals("normal")) {
            if (Account.theme().equals("purple")) {
                setTheme(R.style.Theme_Purple);
            } else if (Account.theme().equals("blue")) {
                setTheme(R.style.Theme_Blue);
            } else if (Account.theme().equals("amoled")) {
                setTheme(R.style.Theme_Amoled);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "note").allowMainThreadQueries().build();

        noteDao = db.noteDao();

        try {
            getRowCount();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        app.verifyStoragePermissions(BackupActivity.this);
    }

    private void getRowCount() throws InterruptedException {
        final AtomicInteger fcount = new AtomicInteger();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int num = getNumFiles();
                fcount.set(num);
            }
        });
        t.setPriority(10);
        t.start();
        t.join();

        TextView textViewCount = findViewById(R.id.textViewCount);
        textViewCount.setText("You have "+fcount+" notes");
        // use as fcount.get()
    }

    int getNumFiles() {
        return noteDao.getRowCount();
    }

    public void export(View view) throws IOException {
        getPermissions();
        exportDB();
    }

    private void getPermissions() {
        //Storage Permission

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


    private void exportDB() throws IOException {
        List<Note> notes = noteDao.getAll();

        int exported = 0;
        StringBuilder text = new StringBuilder();
        text.append("exported:\n\n");
        for (Note note: notes) {
            text.append(note.name).append("\n");
            exported++;
        }
        text.append("\ntotal notes exported: ").append(exported);

        Gson gson = new Gson();

        TextView textViewCount = findViewById(R.id.textViewExport);
        textViewCount.setText(text);

        RetriveandSaveJSONdatafromfile.objectToFile(gson.toJson(notes), true);

        toast("Exported to default path: "+ RetriveandSaveJSONdatafromfile.path+".txt");
    }

    public void importdb(View view) throws IOException, ClassNotFoundException, JSONException {
        start(true);
    }

    Boolean addNotes;
    private void start(boolean b) {
        //  importDB();
        addNotes = b;
        toast("Choose file. Default path: "+ RetriveandSaveJSONdatafromfile.path+".txt");
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        // Ask specifically for something that can be opened:
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/plain");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose file. Default path: "+ RetriveandSaveJSONdatafromfile.path+".txt"),
                PICKFILE_REQUEST_CODE);
    }

    public void restoredb(View view) {
        start(false);
    }


    // And then somewhere, in your activity:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK){
            Uri content_describer = data.getData();
            BufferedReader reader = null;
            try {
                // open the user-picked file for reading:
                InputStream in = getContentResolver().openInputStream(content_describer);
                // now read the content:
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
                // Do something with the content in

                insertNotes(builder.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                toast("file not found");
            } catch (IOException e) {
                e.printStackTrace();
                toast("choose a valid star-notes file");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        toast("choose a valid star-notes file");
                    }
                }
            }
        }
    }

    private void insertNotes(String jsonNotes) throws IOException {
        TextView textViewImport = findViewById(R.id.textViewImport);
        TextView textViewRestore = findViewById(R.id.textViewRestore);
        ObjectMapper mapper = new ObjectMapper();
        int count = 0;

            List<JsonNote> list = mapper.readValue(jsonNotes,
                    TypeFactory.defaultInstance().constructCollectionType(List.class,JsonNote.class));

            StringBuilder text = new StringBuilder();

            if (addNotes) {
                for (JsonNote jsonNote: list) {
                    count ++;
                    Note note = new Note();
                    note.name = jsonNote.name;
                    note.note = jsonNote.note;
                    note.created = jsonNote.created;
                    // note.uid = jsonNote.uid;
                    note.uid = Account.uuidCounter();
                    note.last_edited = jsonNote.last_edited;
                    note.synced = jsonNote.synced;
                    note.star = jsonNote.star;

                    noteDao.insertAll(note);
                    updateUuidCounter();

                    created_or_edited = true;

                    text.append(jsonNote.name);
                    text.append("\n");
                }
            } else {
                for (JsonNote jsonNote: list) {
                    count ++;
                    Note note = new Note();
                    note.name = jsonNote.name;
                    note.note = jsonNote.note;
                    note.created = jsonNote.created;
                    note.uid = jsonNote.uid;
                    // note.uid = Account.uuidCounter();
                    note.last_edited = jsonNote.last_edited;
                    note.synced = jsonNote.synced;
                    note.star = jsonNote.star;

                    noteDao.insertAll(note);
                    updateUuidCounter();

                    created_or_edited = true;

                    text.append(jsonNote.name);
                    text.append("\n");
                }
            }

            text.append("\n");


            if (addNotes) {
                text.append("total notes imported: ").append(count);
                textViewImport.setText(text);
            } else {
                text.append("total notes restored: ").append(count);
                textViewRestore.setText(text);
            }

            try {
                getRowCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


}