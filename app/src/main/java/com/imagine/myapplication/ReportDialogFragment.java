package com.imagine.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.imagine.myapplication.post_classes.Post;

public class ReportDialogFragment extends AlertDialog {

    public Post post;

    public ReportDialogFragment(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_dialog_fragment);
    }
}
