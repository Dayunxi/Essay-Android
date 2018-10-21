package com.adamyt.essay.essay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();

        EditText editText = findViewById(R.id.editEssay);
        editText.requestFocus();

        TextView textViewCancel = findViewById(R.id.editor_activity_cancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
//        TextView textView = findViewById(R.id.textView);
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//        textView.setText(message);
    }
}
