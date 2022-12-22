package com.sport.starnotes.create;

import static com.sport.starnotes.Account.clicked_note_id;
import static com.sport.starnotes.Account.created_or_edited;
import static com.sport.starnotes.Account.tapped_on_item;
import static com.sport.starnotes.Account.updateUuidCounter;
import static com.sport.starnotes.app.toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sport.starnotes.Account;
import com.sport.starnotes.AppDatabase;
import com.sport.starnotes.MainActivity;
import com.sport.starnotes.MyApplication;
import com.sport.starnotes.Note;
import com.sport.starnotes.NoteDao;
import com.sport.starnotes.R;
import com.sport.starnotes.SendWatch;
import com.sport.starnotes.settings.backup.RetriveandSaveJSONdatafromfile;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    NoteDao noteDao;
    EditText editTextTitle;
    EditText editTextNote;
    public static boolean isStar;
    ImageView imageViewStar;
    TextView textViewExport;

    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_create);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "note").allowMainThreadQueries().build();

        noteDao = db.noteDao();
        editTextNote = findViewById(R.id.editTextNote);
        editTextTitle = findViewById(R.id.editTextTextPersonName);
        textViewExport = findViewById(R.id.textViewExport);
        TextView textViewDelete = findViewById(R.id.textViewDelete);
        if (tapped_on_item) {
            textViewDelete.setVisibility(View.VISIBLE);
            Note note = noteDao.getByID(clicked_note_id);
            editTextTitle.setText(note.name);
            editTextNote.setText(note.note);
        } else {
            isStar = false;
            textViewDelete.setVisibility(View.GONE);
            textViewExport.setVisibility(View.GONE);
        }

        setStar();
    }

    private void setStar() {
        imageViewStar = findViewById(R.id.imageViewStar);

        if (!isStar) {
            imageViewStar.setImageDrawable(ContextCompat.getDrawable(CreateActivity.this, R.drawable.star_viewed));
        } else {
            imageViewStar.setImageDrawable(ContextCompat.getDrawable(CreateActivity.this, R.drawable.star_fresh));
        }
    }

    public void saveNote(View view) {
        storeNote();
    }

    private void storeNote() {
        Note note = new Note();
        note.name = editTextTitle.getText().toString();
        note.note = editTextNote.getText().toString();
        note.created = System.currentTimeMillis() / 1000L;
        note.uid = Account.uuidCounter();
        note.last_edited = System.currentTimeMillis() / 1000L;
        note.synced = false;
        note.star = isStar;

        if (tapped_on_item) {
            noteDao.updateNote(note.name,note.note,note.last_edited,isStar, clicked_note_id);
        } else {
            noteDao.insertAll(note);
            updateUuidCounter();
        }
        created_or_edited = true;
        if (Account.syncWatch()) {
            SendWatch.syncNote(note.note);
        }
        Intent i = new Intent(MyApplication.getAppContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(i);
        finish();
    }

    public void starNote(View view) {
        isStar = !isStar;
        setStar();
    }

    public void deleteNote(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Delete note");
        builder1.setMessage("U sure u want to delete this note? :(");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes, delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        created_or_edited = true;
                        noteDao.deleteNote(clicked_note_id);
                        Intent i = new Intent(MyApplication.getAppContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(i);
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();



    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Are you sure you don't want to save your note?");
        builder1.setMessage("Choose between discarding or saving your changes.");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Discard Changes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        finish();
                    }
                });

        builder1.setNegativeButton(
                "Save Changes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storeNote();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void exportNote(View view) throws IOException {
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
        Note note = noteDao.getByID(clicked_note_id);

        Gson gson = new Gson();

        RetriveandSaveJSONdatafromfile.objectToFile("["+gson.toJson(note)+"]", false);

        toast("Exported to default path: "+ RetriveandSaveJSONdatafromfile.path+".txt");
    }
}