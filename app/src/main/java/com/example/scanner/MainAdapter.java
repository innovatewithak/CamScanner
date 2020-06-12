package com.example.scanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {

    private Context context;
    private List<Document> documents = new ArrayList<>();

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_item_layout, parent, false);

        context = parent.getContext();
        return new MainHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        final Document document = documents.get(position);

        String text = document.getDate();
        String cat = document.getCategory();

        Log.d("TAG", "PathImg: " + document.getName());
        Log.d("TAG", "PathImg: " + document.getPath());

        holder.documentName.setText(document.getName());
        holder.documentDate.setText(document.getScanned());

        String path = document.getPath();
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            holder.documentImage.setImageBitmap(bitmap);
        }

        DocumentViewModel viewModel;
        viewModel = ViewModelProviders.of((FragmentActivity) context).get(DocumentViewModel.class);
        String count = String.valueOf(viewModel.documentCount(text, cat));
        holder.documentCount.setText(count);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DocsActivity.class);
                intent.putExtra("date", document.getDate());
                intent.putExtra("cat", document.getCategory());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    class MainHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView documentImage;
        private TextView documentName;
        private TextView documentDate;
        private TextView documentCount;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            documentImage = (ImageView) itemView.findViewById(R.id.documentImage);
            documentName = (TextView) itemView.findViewById(R.id.documentName);
            documentDate = (TextView) itemView.findViewById(R.id.documentDate);
            documentCount = (TextView) itemView.findViewById(R.id.documentCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }
}
