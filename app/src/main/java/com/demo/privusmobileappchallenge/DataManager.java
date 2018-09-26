package com.demo.privusmobileappchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    // Strings constants for data storage

    public static final String MODE = "MODE";
    public static final String IS_CONFIG = "IS_CONFIG";
    private static final String CONTACTS = "CONTACTS";
    private static final String HISTORY = "HISTORY";
    private static final String ID = "ID";
    private static final String VOIP = "VOIP";
    private static final String AUTO_SYNC = "AUTO_SYNC";

    /**
     * Saves the contacts list
     *
     * @param context
     * @param contacts
     */
    public static void saveContacts(Context context, List<Contact> contacts) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        prefsEditor.putString(CONTACTS, json);
        prefsEditor.apply();
    }

    /**
     * Loads the contacts list. If there are no contacts, loads an empty list.
     *
     * @param context
     * @return
     */
    public static List<Contact> loadContacts(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(CONTACTS, "");
        Type type = new TypeToken<List<Contact>>() {
        }.getType();
        List<Contact> list = new ArrayList<Contact>();
        if (gson.fromJson(json, type) != null) {
            list = gson.fromJson(json, type);
            list = Utilities.sortList(list);
        }
        return list;
    }


    /**
     * Saves call log history
     *
     * @param context
     * @param histories
     */
    public static void saveHistory(Context context, List<History> histories) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(histories);
        prefsEditor.putString(HISTORY, json);
        prefsEditor.apply();
    }

    /**
     * Loads the call log history. If empty returns an empty list.
     *
     * @param context
     * @return
     */
    public static List<History> loadHistory(Context context) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(HISTORY, "");
        Type type = new TypeToken<List<History>>() {
        }.getType();
        List<History> list = new ArrayList<History>();
        if (gson.fromJson(json, type) != null) {
            list = gson.fromJson(json, type);
        }
        return list;
    }

    /**
     * Stores an int value in the preferences
     *
     * @param string
     * @param context
     * @return
     */
    public static int getIntPreference(String string, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(string, 0);
    }

    /**
     * Retrieves an int value stored in the preferences
     *
     * @param string
     * @param value
     * @param context
     */
    public static void setIntPreference(String string, int value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(string, value);
        editor.apply();
    }

    /**
     * Stores a boolean value in the preferences
     *
     * @param string
     * @param context
     * @return
     */
    public static Boolean getBooleanPreference(String string, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(string, false);
    }

    /**
     * Retrieves a boolean value stored in the preferences
     *
     * @param string
     * @param value
     * @param context
     */
    public static void setBooleanPreference(String string, Boolean value, Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(string, value);
        editor.apply();
    }

    /**
     * Generates a new ID for an added contact. All IDs are unique.
     *
     * @param context
     * @return
     */
    public static int getNewContactId(Context context) {
        int id = getIntPreference(ID, context);
        id++;
        setIntPreference(ID, id, context);
        return id;
    }

    /**
     * Sets a contact as inactive so that it will not be displayed.
     * This prevents an user to delete a contact and then upon syncing retrieving the same deleted contact.
     *
     * @param id
     * @param context
     */
    public static void disableContactByID(Long id, Context context) {
        List<Contact> contacts = loadContacts(context);
        for (Contact contact : contacts) {
            if (contact.getId() == id) {
                contact.set_is_active(false);
                saveContacts(context, contacts);
                break;
            }
        }
    }

    /**
     * Returns the state of the "only voip" filter.
     *
     * @param context
     * @return
     */
    public static boolean isOnlyVoip(Context context) {
        return getBooleanPreference(VOIP, context);
    }

    /**
     * Sets the "only voip" filter.
     *
     * @param value
     * @param context
     */
    public static void setOnlyVoip(boolean value, Context context) {
        setBooleanPreference(VOIP, value, context);
    }

    /**
     * Returns the state of the "auto sync" feature.
     *
     * @param context
     * @return
     */
    public static boolean isAutoSync(Context context) {
        return getBooleanPreference(AUTO_SYNC, context);
    }

    /**
     * Sets the "auto sync" feature.
     *
     * @param value
     * @param context
     */
    public static void setAutoSync(boolean value, Context context) {
        setBooleanPreference(AUTO_SYNC, value, context);
    }
}
