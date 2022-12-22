package com.sport.starnotes;

import static com.sport.starnotes.Account.clicked_note_id;
import static com.sport.starnotes.app.toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sport.starnotes.create.CreateActivity;

public class PreviewActivity extends AppCompatActivity {
    NoteDao noteDao;
    TextView textViewPreview;
    AppDatabase db;
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
        setContentView(R.layout.activity_preview);

        textViewPreview = findViewById(R.id.textViewPreview);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "note").allowMainThreadQueries().build();

        getNote();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getNote() {
        noteDao = db.noteDao();
        Note note = noteDao.getByID(clicked_note_id);


        textViewPreview.setText(note.note);

    }

    public void editNote(View view) {
        Intent i = new Intent(MyApplication.getAppContext(), CreateActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(i);
    }
}