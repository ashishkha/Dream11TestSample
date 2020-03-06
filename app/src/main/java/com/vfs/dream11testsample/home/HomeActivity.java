package com.vfs.dream11testsample.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vfs.dream11testsample.R;
import com.vfs.dream11testsample.adapters.HomeAdapter;
import com.vfs.dream11testsample.db.ContactEntityModel;
import com.vfs.dream11testsample.db.DatabaseClient;
import com.vfs.dream11testsample.save.SaveActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton floatingButton;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //set action bar title
        setTitle(getString(R.string.contacts));

        initViews();

        setListeners();

        configRecyclerView();

        recyclerViewSetAdapter();
    }

    private void recyclerViewSetAdapter() {
        recyclerView.setAdapter(homeAdapter);
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListeners() {
        floatingButton.setOnClickListener(this);
    }

    private void initViews() {
        floatingButton = findViewById(R.id.floatingButton);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingButton:
                startSaveActivity();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start async task
        Contact contact=new Contact();
        contact.execute();
    }

    private void startSaveActivity() {
        startActivity(new Intent(this, SaveActivity.class));
    }

    class Contact extends AsyncTask<Void, Void, List<ContactEntityModel>> {

        @Override
        protected List<ContactEntityModel> doInBackground(Void... voids) {
            List<ContactEntityModel> taskList = DatabaseClient
                    .getInstance(getApplicationContext())
                    .getAppDatabase()
                    .contactDao()
                    .getAll();
            return taskList;
        }

        @Override
        protected void onPostExecute(List<ContactEntityModel> contactEntityModels) {
            super.onPostExecute(contactEntityModels);
            HomeAdapter adapter = new HomeAdapter(HomeActivity.this.getBaseContext(),contactEntityModels);
            recyclerView.setAdapter(adapter);
        }
    }
}
