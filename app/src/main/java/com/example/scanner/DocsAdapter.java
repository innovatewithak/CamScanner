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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.MainHolder> {

    private Context context;
    private List<Document> documents = new ArrayList<>();
    Bitmap bitmap;
    String path;

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.docs_layout, parent, false);

        context = parent.getContext();
        return new MainHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        final Document document = documents.get(position);

        holder.pageCount.setText(document.getName());

        if (document.getCategory().equals("1") || document.getCategory().equals("2")) {
            path = document.getPath();
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.pageImage.setImageBitmap(bitmap);
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v) {
                    BitmapHelper.getInstance().setBitmap(bitmap);

                    if (BitmapHelper.getInstance().getBitmap() == null) {
                        Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(context, FullviewActivity.class);
                        intent.putExtra("documentId", document.getDocumentId());
                        intent.putExtra("path", document.getPath());
                        context.startActivity(intent);
                    }
                }
            });
        }

        if (document.getCategory().equals("3")) {
            holder.pageImage.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf));

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, PdfActivity.class);
//                    intent.putExtra("path", document.getPath());
//                    context.startActivity(intent);

                    Toast.makeText(context, "Use PDF Reader", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        private ImageView pageImage;
        private TextView pageCount;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            pageImage = (ImageView) itemView.findViewById(R.id.pageImage);
            pageCount = (TextView) itemView.findViewById(R.id.pageNumber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v);
        }
    }
}
