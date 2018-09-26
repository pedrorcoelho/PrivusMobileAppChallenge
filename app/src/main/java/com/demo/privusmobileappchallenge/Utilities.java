package com.demo.privusmobileappchallenge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Utilities {

    /**
     * Returns the current date as a String
     *
     * @return
     */
    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return df.format(c);
    }


    /**
     * Adds dummy data for testing. Only adds this data once. Does not duplicate data.
     *
     * @param context
     */
    public static void addDummyData(Context context) {
        Contact contact = new Contact("John Smith", "934 957 222", "934 957 222", null, context);
        List<Contact> contacts = DataManager.loadContacts(context);
        contacts.add(contact);
        contact = new Contact("John Anderson", "800 232 421", "135135235", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Mike Johnson", "123 234 234", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Bill Williams", "434 343 232", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("George Miller", "623 235 234", "1253535", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("David Jones", "545 324 243", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Oscar Brown", "239 939 443", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Pamela Wilson", "525 355 455", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Kate Jones", "443 443 444", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Jane Jackson", "123 233 741", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Mary Davis", "177 521 686", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("James Martinez", "525 555 123", "525 555 123", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Mike Ross", "512 512 555", "512 512 555", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("John Cooper", "981 332 151", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Rachel Harris", "555 252 123", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("John Lee Walker", "123 333 324", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Maynard Butler", "616 611 234", "616 611 234", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Emily Gray", "900 123 444", "900 123 444", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Sophia Harris", "151 123 123", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Adam Foster", "142 123 444", "144 444 123", null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("Margaret Taylor", "444 123 323", null, null, context);
        contacts = addIfNotPresent(contacts, contact);
        contact = new Contact("William Adams", "333 197 829", "333 197 829", null, context);
        contacts = addIfNotPresent(contacts, contact);
        DataManager.saveContacts(context, contacts);
    }

    /**
     * Checks if the contact already exists before adding. Only adds new contacts. This is only used for dummy data as well as contacts synchronization.
     * Manually is possible to add the same contact more than once.
     *
     * @param contacts
     * @param contact
     * @return
     */
    public static List<Contact> addIfNotPresent(List<Contact> contacts, Contact contact) {
        for (Contact saved_contact : contacts) {
            if ((contact.get_number() != null) && (saved_contact.get_number() != null) && saved_contact.get_number().equals(contact.get_number())
                    || (contact.get_voip_number() != null) && (saved_contact.get_voip_number() != null) && saved_contact.get_voip_number().equals(contact.get_voip_number())) {
                return contacts;
            }
        }
        contacts.add(contact);
        return contacts;
    }

    /**
     * Used if the user has no profile picture.
     * If the user name has more than one name, it uses the first letter of the first name and the first letter of the last name
     * to put in the given textview
     *
     * @param contact
     * @param avatar_circle
     * @param avatar
     */
    public static void setAvatar(Contact contact, TextView avatar_circle, ImageView avatar) {
        if (contact.get_photo_uri() != null) {
            Picasso.get().load(Uri.parse(contact.get_photo_uri())).transform(new CircleTransform()).into(avatar);
            avatar.setVisibility(View.VISIBLE);
            avatar_circle.setVisibility(View.GONE);
        } else {
            if (contact.get_name().contains(" ")) {
                String[] arr = contact.get_name().split(" ");
                String circle_letters;
                if (arr.length > 1) {
                    circle_letters = String.valueOf(arr[0].charAt(0)) + String.valueOf(arr[arr.length - 1].charAt(0));
                } else {
                    circle_letters = String.valueOf(contact.get_name().charAt(0));
                }
                avatar_circle.setText(circle_letters.toUpperCase());
            } else {
                avatar_circle.setText(String.valueOf(contact.get_name().charAt(0)).toUpperCase());
            }
            avatar.setVisibility(View.GONE);
            avatar_circle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sorts a Contact's list by alphabetical order
     *
     * @param list
     * @return
     */
    public static List<Contact> sortList(List<Contact> list) {
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Contact>() {
                public int compare(Contact c1, Contact c2) {
                    return c1.get_name().toLowerCase().compareTo(c2.get_name().toLowerCase());
                }
            });
        }
        return list;
    }

    /**
     * Fetch device's contacts in background.
     *
     * @param activity
     */

    public void getPhoneContacts(Activity activity) {
        new ContactsSync(activity).execute("");
    }

    public class ContactsSync extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        private Activity activity;

        public ContactsSync(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
            dialog.setMessage(activity.getApplicationContext().getResources().getString(R.string.synchronizing));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            List<Contact> contacts = DataManager.loadContacts(activity.getApplicationContext());
            Cursor phones = activity.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (phones != null && phones.getCount() > 0) {
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    Contact contact = new Contact(name, phoneNo, null, photoUri, activity.getApplicationContext().getApplicationContext());
                    contacts = Utilities.addIfNotPresent(contacts, contact);
                }
                DataManager.saveContacts(activity.getApplicationContext(), contacts);
                phones.close();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            DataListFragment.refreshContacts();
        }
    }
}
