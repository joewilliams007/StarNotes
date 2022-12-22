package com.sport.starnotes.create;

import static com.sport.starnotes.MainActivity.mAdapter;
import static com.sport.starnotes.MainActivity.selectItems;
import static com.sport.starnotes.MainActivity.selectedItems;
import static com.sport.starnotes.create.CreateActivity.isStar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sport.starnotes.Account;
import com.sport.starnotes.MainActivity;
import com.sport.starnotes.MyApplication;
import com.sport.starnotes.NoteItem;
import com.sport.starnotes.PreviewActivity;
import com.sport.starnotes.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final ArrayList<NoteItem> mList;

    public static class NoteViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewTitle;
        public ImageView imageViewStar;
        public TextView textViewTime;
        public CheckBox checkBox;
        public TextView textViewPreview;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewStar = itemView.findViewById(R.id.imageViewStar);
            textViewPreview = itemView.findViewById(R.id.textViewPreview);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public NoteAdapter(ArrayList<NoteItem> noteList){
        mList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent, false);
        return new NoteViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteItem currentItem = mList.get(position);

        holder.textViewTitle.setText(currentItem.getName());
        holder.textViewPreview.setText(currentItem.getNote());
        holder.checkBox.setChecked(selectedItems.contains(currentItem.getUid()));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yy HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(currentItem.getLast_edited()*1000);
        holder.textViewTime.setText(formattedDate);

        if (currentItem.getStar()) {
            holder.imageViewStar.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewStar.setVisibility(View.GONE);
        }

        if (selectItems) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account.clicked_note_id = currentItem.getUid();
                Account.tapped_on_item = true;
                isStar = currentItem.getStar();
                Intent i = new Intent(MyApplication.getAppContext(), PreviewActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getAppContext().startActivity(i);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.checkBox.isChecked()) {
                    try {
                        selectedItems.remove(currentItem.getUid());
                    } catch (Exception IndexOutOfBoundsException) {
                        selectedItems = new ArrayList<>();
                    }

                } else {
                    selectedItems.add(currentItem.getUid());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
