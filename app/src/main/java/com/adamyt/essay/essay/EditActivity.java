package com.adamyt.essay.essay;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
                //save to local as cipher text or plain text

            }
        });
    }
    
    private void switchToReview(){
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
        editText.setEnabled(false);
        editText.setTextColor(Color.BLACK);
        reviewBar.setVisibility(View.VISIBLE);
        modifyBar.setVisibility(View.GONE);
//        Toast.makeText(EditActivity.this, "Review Model!", Toast.LENGTH_SHORT).show();
    }
    private void switchToModify(){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        editText.setEnabled(true);
        modifyBar.setVisibility(View.VISIBLE);
        reviewBar.setVisibility(View.GONE);
        editText.requestFocus();
//        Toast.makeText(EditActivity.this, "Modify Model!", Toast.LENGTH_SHORT).show();
    }

    private void saveAsDraft(){
        String draft = ((EditText) findViewById(R.id.editEssay)).getText().toString();
        // String title = "";
    }

//    private void draftAlertDialog(){
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage(R.string.confirm_save_draft);
//        dialog.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which){
//                dialog.dismiss();
//                if(!isNew){
//                    editText.setText(originText);
//                    switchToReview();
//                }
//                else finish();
//            }
//        });
//        dialog.setNeutralButton(R.string.confirm_cancel, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which){
//                dialog.dismiss();
//            }
//        });
//        dialog.setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which){
//                //
//
//                dialog.dismiss();
//                if(!isNew){
//                    editText.setText(originText);
//                    switchToReview();
//                    Toast.makeText(EditActivity.this, "switch to review mode", Toast.LENGTH_SHORT).show();
//                }
//                else finish();
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
//        //设置对话框的大小
//        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(EditActivity.this, "No", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(EditActivity.this, "Yes", Toast.LENGTH_SHORT).show();
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
