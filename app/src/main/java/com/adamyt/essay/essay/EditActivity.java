package com.adamyt.essay.essay;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.adamyt.essay.utils.EssayUtils;

import java.io.File;

public class EditActivity extends AppCompatActivity {

    private Toolbar reviewBar, modifyBar;
    private EditText editText;
    private String originText;
    
    // review or create a new essay
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView textViewBack, textViewModify, textViewCancel, textViewDone;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // set status bar's to colorPrimaryDark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        reviewBar = findViewById(R.id.review_toolbar);
        modifyBar = findViewById(R.id.modify_toolbar);
        editText = findViewById(R.id.editEssay);
        textViewBack = findViewById(R.id.editor_activity_back);
        textViewModify = findViewById(R.id.editor_activity_modify);
        textViewCancel = findViewById(R.id.editor_activity_cancel);
        textViewDone = findViewById(R.id.editor_activity_done);

        Intent intent  = getIntent();
        isNew = intent.getBooleanExtra(MainActivity.IS_NEW, false);
        originText = intent.getStringExtra(MainActivity.ESSAY_URL);

        if(isNew){
            switchToModify();
        }
        else{
            editText.setText(intent.getStringExtra(MainActivity.ESSAY_URL));
            switchToReview();
        }


        // review toolbar 
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textViewModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToModify();
                Toast.makeText(EditActivity.this, "switch to modify mode!", Toast.LENGTH_SHORT).show();
            }
        });

        // modify toolbar
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if not new and not modify?
                String currentText = editText.getText().toString();
                if(isNew&&!currentText.equals("") || !isNew&&!currentText.equals(originText)){
                    draftConfirm();
//                    draftConfirm();
                }
                else{
                    if(!isNew){
                        Toast.makeText(EditActivity.this, "switch to review mode!", Toast.LENGTH_SHORT).show();
                        switchToReview();
                    }
                    else finish();
                }
            }
        });
        textViewDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(EssayUtils.needRequestWrite(EditActivity.this)) return;
                //save to local as cipher text or plain text

                File dir = new File(EssayUtils.RelativeUserDir);
                if(!dir.exists()){
                    if(!dir.mkdir())
                        Toast.makeText(EditActivity.this, "Can't mkdir", Toast.LENGTH_SHORT).show();
                    else{   //TODO: ./user/username/essay.json
                        Toast.makeText(EditActivity.this, EssayUtils.RelativeUserDir, Toast.LENGTH_SHORT).show();

                    }
                }
                Toast.makeText(EditActivity.this, "saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if(requestCode == EssayUtils.REQUEST_WRITE_SOTRAGE){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void switchToReview(){
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
        editText.setEnabled(false);
        editText.setTextColor(Color.BLACK);
        reviewBar.setVisibility(View.VISIBLE);
        modifyBar.setVisibility(View.GONE);
    }
    private void switchToModify(){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        editText.setEnabled(true);
        modifyBar.setVisibility(View.VISIBLE);
        reviewBar.setVisibility(View.GONE);
        editText.requestFocus();
    }

    private void saveAsDraft(){
        String draft = ((EditText) findViewById(R.id.editEssay)).getText().toString();
        // String title = "";
    }

//    private void draftAlertDialog(){
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(R.string.confirm_save_draft);
//        dialog.setNeutralButton(R.string.confirm_cancel, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which){
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    private void draftConfirm() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_confirm, null);
        TextView cancel = view.findViewById(R.id.confirm_no);
        TextView confirm = view.findViewById(R.id.confirm_yes);
        dialog.setContentView(view);
        //点击对话框外部消失
        dialog.setCanceledOnTouchOutside(true);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!isNew){
                    editText.setText(originText);
                    switchToReview();
                }
                else finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsDraft();
                dialog.dismiss();
                if(!isNew){
                    editText.setText(originText);
                    switchToReview();
                    Toast.makeText(EditActivity.this, "switch to review mode", Toast.LENGTH_SHORT).show();
                }
                else finish();
            }
        });
        dialog.show();
    }
}
