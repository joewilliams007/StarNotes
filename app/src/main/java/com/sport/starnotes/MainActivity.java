package com.sport.starnotes;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;
import static com.sport.starnotes.Account.clicked_note_id;
import static com.sport.starnotes.Account.created_or_edited;
import static com.sport.starnotes.Account.tapped_on_item;
import static com.sport.starnotes.app.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.sport.starnotes.settings.SettingsActivity;
import com.sport.starnotes.settings.backup.BackupActivity;
import com.sport.starnotes.create.CreateActivity;
import com.sport.starnotes.create.NoteAdapter;
import com.sport.starnotes.settings.backup.RetriveandSaveJSONdatafromfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    NoteDao noteDao;
    public static ArrayList<Integer> selectedItems = new ArrayList<>();
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
        setContentView(R.layout.activity_main);

        selectItems = false;
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "note").allowMainThreadQueries().build();

        Account.setOrder_type("edited");

        noteDao = db.noteDao();
        ConstraintLayout constraintLayoutSelected = findViewById(R.id.constraintLayoutSelected);
        constraintLayoutSelected.setVisibility(View.GONE);
        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        constraintLayoutOrder.setVisibility(View.GONE);

        setNotes();
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (created_or_edited) {
            setNotes();
            created_or_edited = false;
        }
    }

    private void setNotes() {
        List<Note> notes = null;
        if (Account.order_type().equals("edited")) {
            notes = noteDao.getAll();
        } else if (Account.order_type().equals("star")) {
            notes = noteDao.getAllStar();
        } else if (Account.order_type().equals("created")) {
            notes = noteDao.getAllCreated();
        }
       

        createList(notes);
        buildRecyclerView();
    }

    private ArrayList<NoteItem> mList;
    public void createList(List<Note> notes){
        mList = new ArrayList<>();

        for (Note note : notes){
            try {
                // boolean image = user.image == 1;
                mList.add(new NoteItem(note.uid, note.name, note.note, note.synced, note.star,note.last_edited,note.created));
            } catch (Exception ignored) {

            }
        }
    }
    public static NoteAdapter mAdapter;
    public void buildRecyclerView(){
        RecyclerView mRecyclerView = findViewById(R.id.note_recycler_view);
        mRecyclerView.setHasFixedSize(false);
        //  mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mAdapter = new NoteAdapter(mList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void createNote(View view) {
        tapped_on_item = false;
        Intent i = new Intent(MainActivity.this, CreateActivity.class);
        startActivity(i);
    }

    public void openSettings(View view) {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }


    public static Boolean selectItems = false;
    @SuppressLint("NotifyDataSetChanged")
    public void selectItems(View view) {
        selectItems = !selectItems;
        selectedItems = new ArrayList<>();
        ConstraintLayout constraintLayoutSelected = findViewById(R.id.constraintLayoutSelected);
        if (selectItems) {
            constraintLayoutSelected.setVisibility(View.VISIBLE);
        } else {
            constraintLayoutSelected.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
      //  setNotes();
        
        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        constraintLayoutOrder.setVisibility(View.GONE);
    }

    public void deleteSelected(View view) {
        if (selectedItems.size()<1) {
            toast("select items to delete");
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Delete "+selectedItems.size()+" notes");
        builder1.setMessage("U sure u want to delete "+selectedItems.size()+" selected notes? :(");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes, delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        for (Integer uuid : selectedItems){
                            try {
                                noteDao.deleteNote(uuid);
                            } catch (Exception ignored) {

                            }
                        }
                        setNotes();

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

    public static String finalPath;
    public void exportSelected(View view) {
        if (selectedItems.size()<1) {
            toast("select items to export");
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Export "+selectedItems.size()+" notes");
        builder1.setMessage("Export "+selectedItems.size()+" selected notes?");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes, export",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        getPermissions();
                        Gson gson = new Gson();
                        StringBuilder stringBuilder = new StringBuilder();
                        int exported = 0;
                        for (Integer uuid : selectedItems){
                            try {
                                Note note = noteDao.getByID(uuid);
                                if (!(exported == 0)) {
                                    stringBuilder.append(",");
                                }

                                exported++;
                                stringBuilder.append(gson.toJson(note));
                            } catch (Exception ignored) {

                            }
                        }

                        try {

                            RetriveandSaveJSONdatafromfile.objectToFile("["+stringBuilder+"]", false);
                            toast("Exported to default path: "+ finalPath);

                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            File fileWithinMyDir = new File(finalPath);

                            if(fileWithinMyDir.exists()) {
                                intentShareFile.setType("text/plain");
                                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(finalPath));

                                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                        "Sharing File...");
                                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                startActivity(Intent.createChooser(intentShareFile, "Share File"));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            toast("error");
                        }

                        setNotes();

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


    private void getPermissions() {
        //Storage Permission

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    public void starNote(View view) {
        if (selectedItems.size()<1) {
            toast("select items to star");
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Star "+selectedItems.size()+" notes");
        builder1.setMessage("Star "+selectedItems.size()+" selected notes?");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes, star",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        for (Integer uuid : selectedItems){
                            try {
                                Note note = noteDao.getByID(uuid);
                                noteDao.updateNote(note.name,note.note,note.last_edited,true, uuid);
                            } catch (Exception ignored) {

                            }
                        }
                        setNotes();

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

    public void unstarNote(View view) {
        if (selectedItems.size()<1) {
            toast("select items to star");
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Unstar "+selectedItems.size()+" notes");
        builder1.setMessage("Unstar "+selectedItems.size()+" selected notes? :(");
        builder1.setIcon(R.drawable.logo);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes, unstar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        for (Integer uuid : selectedItems){
                            try {
                                Note note = noteDao.getByID(uuid);
                                noteDao.updateNote(note.name,note.note,note.last_edited,false, uuid);
                            } catch (Exception ignored) {

                            }
                        }
                        setNotes();

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

    public void selectOrder(View view) {
        ConstraintLayout constraintLayoutSelected = findViewById(R.id.constraintLayoutSelected);
        constraintLayoutSelected.setVisibility(View.GONE);

        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        if (constraintLayoutOrder.getVisibility()==View.GONE) {
            constraintLayoutOrder.setVisibility(View.VISIBLE);
        } else {
            constraintLayoutOrder.setVisibility(View.GONE);
        }
    }

    public void orderFav(View view) {
        Account.setOrder_type("star");
        setNotes();
        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        constraintLayoutOrder.setVisibility(View.GONE);
    }

    public void orderCreated(View view) {
        Account.setOrder_type("created");
        setNotes();
        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        constraintLayoutOrder.setVisibility(View.GONE);
    }

    public void orderEdited(View view) {
        Account.setOrder_type("edited");
        setNotes();
        ConstraintLayout constraintLayoutOrder = findViewById(R.id.constraintLayoutOrder);
        constraintLayoutOrder.setVisibility(View.GONE);
    }


    public void expandItems(View view) {
        ImageView imageViewExpand = findViewById(R.id.imageViewExpand);
        ImageView imageViewSettings = findViewById(R.id.imageViewSettings);
        ConstraintLayout constraintLayoutExpand = findViewById(R.id.constraintLayoutExpand);
        if (constraintLayoutExpand.getVisibility()==View.GONE) {
            constraintLayoutExpand.setVisibility(View.VISIBLE);
            imageViewExpand.setRotation(180);
            imageViewSettings.setVisibility(View.GONE);
        } else {
            constraintLayoutExpand.setVisibility(View.GONE);
            imageViewExpand.setRotation(0);
            imageViewSettings.setVisibility(View.VISIBLE);
        }
    }
}