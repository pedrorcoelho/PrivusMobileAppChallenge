package com.demo.privusmobileappchallenge;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private TextView line1, line2;
    private ImageButton btn_yes, btn_no;
    private String title, message;
    private Runnable runnable;


    public CustomDialog(Activity activity, String title, String message, Runnable runnable) {
        super(activity);
        this.activity = activity;
        this.title = title;
        this.message = message;
        this.runnable = runnable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_custom_dialog);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

        btn_yes = findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener(this);

        line1.setText(title);
        line2.setText(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                new Handler().post(runnable);
                dismiss();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
