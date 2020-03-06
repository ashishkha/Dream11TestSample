package com.vfs.dream11testsample.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vfs.dream11testsample.AppClass;
import com.vfs.dream11testsample.R;
import com.vfs.dream11testsample.db.ContactEntityModel;
import com.vfs.dream11testsample.edit.EditActivity;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String TAG = HomeAdapter.class.getSimpleName();
    private List<ContactEntityModel> contactEntityModels;
    private Context context;

    public HomeAdapter(Context context, List<ContactEntityModel> contactEntityModels) {
        Log.d(TAG, "HomeAdapter: called ak check size:" + contactEntityModels.size());
        this.contactEntityModels = contactEntityModels;
        AppClass.getInstance().setContactList(contactEntityModels);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtViewUsername.setText(contactEntityModels.get(position).getUserName());

        Bitmap bitmap = BitmapFactory.decodeByteArray(contactEntityModels.get(position).getImage(), 0, contactEntityModels.get(position).getImage().length);
        holder.imgView.setImageBitmap(bitmap);

        holder.lnrLytParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick(position);
            }
        });

    }

    private void itemClick(int position) {
        Log.d(TAG, "itemClick: called ak posotion:" + position);
        Intent intent = new Intent(context, EditActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt("delete_contact_position", position);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return contactEntityModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewUsername;
        private ImageView imgView;
        private LinearLayout lnrLytParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewUsername = itemView.findViewById(R.id.txtViewUsername);
//            txtViewPhone = itemView.findViewById(R.id.txtViewPhone);
//            txtViewEmail = itemView.findViewById(R.id.txtViewEmail);
            imgView = itemView.findViewById(R.id.imgView);
            lnrLytParent = itemView.findViewById(R.id.lnrLytParent);
        }
    }
}
