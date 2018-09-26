package com.demo.privusmobileappchallenge;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactOptionsDialog extends Dialog implements View.OnClickListener {

    public final static int MODE_VIEW = 0;
    public final static int MODE_EDIT = 1;
    public final static int MODE_CREATE = 2;

    private Activity activity;
    private TextView line1, line2, voip_txt, avatar_circle;
    private EditText line1_edit, line2_edit, voip_txt_edit;
    private ImageView avatar;
    private ImageButton btn_call, btn_edit, btn_delete, btn_confirm, btn_cancel;
    private Contact contact;
    private int mode;
    private Runnable remove_contact_action;

    public ContactOptionsDialog(Activity activity, Contact contact, int mode, final Runnable remove_contact_action) {
        super(activity);
        this.activity = activity;
        this.contact = contact;
        this.mode = mode;
        this.remove_contact_action = remove_contact_action;
    }

    public ContactOptionsDialog(Activity activity, Contact contact, int mode) {
        super(activity);
        this.activity = activity;
        this.contact = contact;
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_contact_options);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        voip_txt = findViewById(R.id.voip_txt);
        avatar = findViewById(R.id.avatar);
        avatar_circle = findViewById(R.id.circle);
        line1_edit = findViewById(R.id.line1_edit);
        line2_edit = findViewById(R.id.line2_edit);
        voip_txt_edit = findViewById(R.id.voip_txt_edit);

        btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(this);
        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);


        switch (mode) {
            case MODE_VIEW:
                findViewById(R.id.btns_panel_1).setVisibility(View.VISIBLE);
                findViewById(R.id.btns_panel_2).setVisibility(View.GONE);
                line1.setText(contact.get_name());
                line2.setText(contact.get_number());
                voip_txt.setVisibility(contact.get_voip_number() == null ? View.INVISIBLE : View.VISIBLE);
                btn_call.setVisibility(contact.get_voip_number() == null ? View.GONE : View.VISIBLE);
                voip_txt.setText(getContext().getResources().getString(R.string.voip) + " " + contact.get_voip_number());
                Utilities.setAvatar(contact, avatar_circle, avatar);
                break;
            case MODE_CREATE:
                changeUiToAddOrCreate();

                break;
            case MODE_EDIT:
                changeUiToAddOrCreate();
                line1_edit.setText(contact.get_name());
                line2_edit.setText(contact.get_number());
                voip_txt.setVisibility(contact.get_voip_number() != null ? View.INVISIBLE : View.VISIBLE);
                voip_txt_edit.setText(contact.get_voip_number());
                findViewById(R.id.avatar_container).setVisibility(View.GONE);
                break;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                String title = activity.getResources().getString(R.string.delete) + " " + contact.get_name();
                String message = activity.getResources().getString(R.string.are_you_sure_delete);
                DataManager.disableContactByID(contact.getId(), getContext());
                CustomDialog dialog = new CustomDialog(activity, title, message, remove_contact_action);
                dialog.show();
                dismiss();
                break;

            case R.id.btn_call:
                List<History> histories = DataManager.loadHistory(getContext());
                if (histories == null) histories = new ArrayList<History>();
                histories.add(new History(contact, Utilities.getCurrentDate(), History.Type.RECEIVED));
                DataManager.saveHistory(getContext(), histories);
                DataListFragment.refreshHistory();
                dismiss();
                break;

            case R.id.btn_confirm:
                if (line1_edit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.missing_name), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (line2_edit.getText().toString().isEmpty() && voip_txt_edit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.one_contact_requiered), Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = line1_edit.getText().toString();
                String number = line2_edit.getText().toString();
                String voip = voip_txt_edit.getText().toString();


                List<Contact> contacts = DataManager.loadContacts(getContext());
                if (mode == MODE_CREATE) {
                    contacts.add(new Contact(name,
                            number.equals("") ? null : number,
                            voip.equals("") ? null : voip,
                            null, getContext()));
                } else if (mode == MODE_EDIT) {
                    for (int i = 0; i < contacts.size(); i++) {
                        if (contacts.get(i).getId() == contact.getId()) {
                            contacts.get(i).editContact(name,
                                    number.equals("") ? null : number,
                                    voip.equals("") ? null : voip);
                        }
                    }
                    DataListFragment.refreshContacts();
                }
                DataManager.saveContacts(getContext(), contacts);
                DataListFragment.refreshContacts();
                dismiss();
                break;

            case R.id.btn_edit:
                ContactOptionsDialog new_dialog = new ContactOptionsDialog(activity, contact, ContactOptionsDialog.MODE_EDIT);
                new_dialog.show();
                dismiss();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;

            default:
                break;
        }
        dismiss();
    }

    private void changeUiToAddOrCreate() {
        findViewById(R.id.btns_panel_1).setVisibility(View.GONE);
        findViewById(R.id.btns_panel_2).setVisibility(View.VISIBLE);
        findViewById(R.id.avatar_container).setVisibility(View.GONE);
        line1_edit.setVisibility(View.VISIBLE);
        line2_edit.setVisibility(View.VISIBLE);
        voip_txt_edit.setVisibility(View.VISIBLE);
        line1.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.INVISIBLE);
        voip_txt.setVisibility(View.INVISIBLE);
        line1_edit.setHint(R.string.name);
        line2_edit.setHint(R.string.phone);
        voip_txt_edit.setHint(R.string.voip);
    }
}
