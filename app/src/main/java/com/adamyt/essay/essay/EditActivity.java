package com.adamyt.essay.essay;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // set status bar's to colorPrimaryDark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        final EditText editText = findViewById(R.id.editEssay);
        editText.requestFocus();

        TextView textViewCancel = findViewById(R.id.editor_activity_cancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.getText().toString().equals("")) draftConfirm();
                else finish();
            }
        });
        TextView textViewSave = findViewById(R.id.editor_activity_save);
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                finish();
            }
        });
    }

    private void draftConfirm(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Save to draft box?");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                finish();
            }
        });
        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                //
                Toast.makeText(EditActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
