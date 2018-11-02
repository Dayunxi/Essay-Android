package com.adamyt.essay.essay;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.adamyt.essay.struct.EssayInfo;
import com.adamyt.essay.utils.EssayUtils;
import com.google.gson.Gson;

public class EditActivity extends AppCompatActivity {

    private Toolbar reviewBar, modifyBar;
    private EditText editTextContent;
    private EditText editTextTitle;
    private Switch editPrivateSwitch;

    private String originContent = null;
    private String originTitle = null;
    private EssayInfo originEssayInfo = null;
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
        editTextContent = findViewById(R.id.editEssay);
        editTextTitle = findViewById(R.id.editTitle);
        editPrivateSwitch = findViewById(R.id.editPrivateSwitch);
        textViewBack = findViewById(R.id.editor_activity_back);
        textViewModify = findViewById(R.id.editor_activity_modify);
        textViewCancel = findViewById(R.id.editor_activity_cancel);
        textViewDone = findViewById(R.id.editor_activity_done);

        Intent intent  = getIntent();
        isNew = intent.getBooleanExtra(MainActivity.IS_NEW, false);
        String json = intent.getStringExtra(MainActivity.EDIT_ESSAY);
        if(json != null) originEssayInfo = (new Gson()).fromJson(json, EssayInfo.class);

        if(originEssayInfo!=null){
            originContent = EssayUtils.getEssayContent(this, originEssayInfo);
            originTitle = originEssayInfo.title;
            editTextContent.setText(originContent);
            editTextTitle.setText(originTitle);
            editPrivateSwitch.setChecked(originEssayInfo.isPrivate);

            if(originEssayInfo.isPrivate && !EssayUtils.isAuthorized){
                Toast.makeText(this, "Not Authorize!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if(isNew){
            switchToModify();
        }
        else{
//            editTextContent.setText(intent.getStringExtra(MainActivity.ESSAY_URL));
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
                String currentContent = editTextContent.getText().toString();
                String currentTitle = editTextTitle.getText().toString();
                boolean isEmpty = currentContent.equals("") || currentTitle.equals("");
                boolean isModified = !currentContent.equals(originContent) || !currentTitle.equals(originTitle);
                if(isNew&&!isEmpty || !isNew&&isModified){
                    draftConfirm();
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
                String content = editTextContent.getText().toString();
                String title = editTextTitle.getText().toString();
                EssayInfo newEssay;
                if(isNew){
                    newEssay = new EssayInfo();
                    newEssay.uid = EssayUtils.CurrentUser.uid;
                    newEssay.type = "text";
                    newEssay.createTime = System.currentTimeMillis();
                    newEssay.title = title;
                    newEssay.isPrivate = editPrivateSwitch.isChecked();

                    String typeDir = newEssay.isPrivate? "private/text/" : "public/text/";
                    newEssay.url = typeDir + newEssay.createTime.toString() + ".md";
                }
                else{
                    newEssay = originEssayInfo;
                    newEssay.lastModifyTime = System.currentTimeMillis();
                    newEssay.isPrivate = editPrivateSwitch.isChecked();
                    newEssay.title = title;
                }

                if(!EssayUtils.hasLoggedIn){
                    Toast.makeText(EditActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }
                else if(newEssay.isPrivate && !EssayUtils.isAuthorized){
                    Toast.makeText(EditActivity.this, "Please authorize to save private essay", Toast.LENGTH_SHORT).show();
                }
                else if(EssayUtils.saveEssay(EditActivity.this, content, newEssay, isNew)){
                    originEssayInfo = newEssay;     // newEssay could change in saveEssay()
                    originContent = content;
                    originTitle = title;
                    isNew = false;
                    switchToReview();
                    Toast.makeText(EditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(EditActivity.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });

        ScrollView scrollView = findViewById(R.id.edit_activity_scroll);
        scrollView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editTextContent.requestFocus();
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
        editTextContent.setFocusable(false);
        editTextContent.setFocusableInTouchMode(false);
        editTextContent.setCursorVisible(false);
        editTextContent.setEnabled(false);
        editTextContent.setTextColor(Color.BLACK);
        editTextTitle.setFocusable(false);
        editTextTitle.setFocusableInTouchMode(false);
        editTextTitle.setCursorVisible(false);
        editTextTitle.setEnabled(false);
        editTextTitle.setTextColor(Color.BLACK);
        editPrivateSwitch.setClickable(false);

        reviewBar.setVisibility(View.VISIBLE);
        modifyBar.setVisibility(View.GONE);
    }
    private void switchToModify(){
        editTextContent.setFocusable(true);
        editTextContent.setFocusableInTouchMode(true);
        editTextContent.setCursorVisible(true);
        editTextContent.setEnabled(true);
        editTextTitle.setFocusable(true);
        editTextTitle.setFocusableInTouchMode(true);
        editTextTitle.setCursorVisible(true);
        editTextTitle.setEnabled(true);
        editPrivateSwitch.setClickable(true);

        modifyBar.setVisibility(View.VISIBLE);
        reviewBar.setVisibility(View.GONE);
        editTextContent.requestFocus();
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
                    editTextContent.setText(originContent);
                    editTextTitle.setText(originTitle);
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
                    editTextContent.setText(originContent);
                    editTextTitle.setText(originTitle);
                    switchToReview();
                    Toast.makeText(EditActivity.this, "switch to review mode", Toast.LENGTH_SHORT).show();
                }
                else finish();
            }
        });
        dialog.show();
    }
}
