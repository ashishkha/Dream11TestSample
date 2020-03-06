package com.vfs.dream11testsample;

import android.app.Application;

import com.vfs.dream11testsample.db.ContactEntityModel;

import java.util.List;

public class AppClass extends Application {

    private static AppClass appClass;
    private List<ContactEntityModel> contactEntityModels;

    @Override
    public void onCreate() {
        super.onCreate();
        appClass = this;
    }

    public static AppClass getInstance(){
        return appClass;
    }

    public void setContactList(List<ContactEntityModel> contactEntityModels) {
        this.contactEntityModels = contactEntityModels;
    }

    public List<ContactEntityModel> getContactEntityModels() {
        return contactEntityModels;
    }

    public void setContactEntityModels(List<ContactEntityModel> contactEntityModels) {
        this.contactEntityModels = contactEntityModels;
    }
}
