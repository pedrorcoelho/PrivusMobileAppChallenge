package com.demo.privusmobileappchallenge;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private List<Contact> m_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkAndRequestPermission();

        initialization();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        DataAdapter adapter = new DataAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DataManager.setIntPreference(DataManager.MODE, tab.getPosition(), getApplicationContext());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        findViewById(R.id.btn_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sync:
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        new Utilities().getPhoneContacts(MainActivity.this);
                                    }
                                };
                                String title = getResources().getString(R.string.import_contacts);
                                String message = getResources().getString(R.string.are_you_sure_import);
                                CustomDialog dialog = new CustomDialog(MainActivity.this, title, message, runnable);
                                dialog.show();
                                return true;

                            case R.id.only_voip:
                                DataManager.setOnlyVoip(!DataManager.isOnlyVoip(getApplicationContext()), getApplicationContext());
                                DataListFragment.refreshContacts();
                                DataListFragment.refreshHistory();
                                return true;

                            case R.id.show_all:
                                DataManager.setOnlyVoip(!DataManager.isOnlyVoip(getApplicationContext()), getApplicationContext());
                                DataListFragment.refreshContacts();
                                DataListFragment.refreshHistory();
                                return true;

                            case R.id.delete_contacts:
                                Runnable delete_contacts = new Runnable() {
                                    @Override
                                    public void run() {
                                        DataManager.saveContacts(getApplicationContext(), new ArrayList<Contact>());
                                        DataListFragment.refreshContacts();
                                    }
                                };
                                title = getResources().getString(R.string.delete);
                                message = getResources().getString(R.string.are_you_sure_delete_all_contacts);
                                dialog = new CustomDialog(MainActivity.this, title, message, delete_contacts);
                                dialog.show();
                                return true;

                            case R.id.delete_history:
                                Runnable delete_history = new Runnable() {
                                    @Override
                                    public void run() {
                                        DataManager.saveHistory(getApplicationContext(), new ArrayList<History>());
                                        DataListFragment.refreshHistory();
                                    }
                                };
                                title = getResources().getString(R.string.delete);
                                message = getResources().getString(R.string.are_you_sure_delete_all_history);
                                dialog = new CustomDialog(MainActivity.this, title, message, delete_history);
                                dialog.show();
                                return true;

                            case R.id.delete_all:
                                Runnable delete_all = new Runnable() {
                                    @Override
                                    public void run() {
                                        DataManager.saveContacts(getApplicationContext(), new ArrayList<Contact>());
                                        DataManager.saveHistory(getApplicationContext(), new ArrayList<History>());
                                        DataListFragment.refreshHistory();
                                        DataListFragment.refreshContacts();
                                    }
                                };
                                title = getResources().getString(R.string.delete);
                                message = getResources().getString(R.string.are_you_sure_delete_all_data);
                                dialog = new CustomDialog(MainActivity.this, title, message, delete_all);
                                dialog.show();
                                return true;

                            case R.id.add_dummy_data:
                                DataListFragment.addDummyDataDialog(MainActivity.this, getApplicationContext());
                                return true;

                            case R.id.enable_auto_sync:
                                DataManager.setAutoSync(true, getApplicationContext());
                                return true;

                            case R.id.disable_auto_sync:
                                DataManager.setAutoSync(false, getApplicationContext());
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.menu);
                if (DataManager.isOnlyVoip(getApplicationContext())) {
                    popup.getMenu().findItem(R.id.only_voip).setVisible(false);
                } else {
                    popup.getMenu().findItem(R.id.show_all).setVisible(false);
                }
                if (DataManager.isAutoSync(getApplicationContext())) {
                    popup.getMenu().findItem(R.id.enable_auto_sync).setVisible(false);
                } else {
                    popup.getMenu().findItem(R.id.disable_auto_sync).setVisible(false);
                }
                popup.show();
            }
        });

    }

    private void initialization() {
        if (!DataManager.getBooleanPreference(DataManager.IS_CONFIG, this)) {
            List<History> histories = new ArrayList<History>();
            DataManager.saveContacts(this, m_contacts);
            DataManager.saveHistory(this, histories);
            DataManager.setAutoSync(true, this);
            DataManager.setBooleanPreference(DataManager.IS_CONFIG, true, this);
        } else {
            if (DataManager.isAutoSync(getApplicationContext()))
                if (checkPermission()) {
                    new Utilities().getPhoneContacts(MainActivity.this);
                }
        }

    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

    }

    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Utilities().getPhoneContacts(MainActivity.this);
                }
                return;
            }
        }
    }

    public class DataAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public DataAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            if (position == 0) {
                bundle.putInt(DataManager.MODE, DataListFragment.MODE_CONTACTS);
            } else {
                bundle.putInt(DataManager.MODE, DataListFragment.MODE_HISTORY);
            }
            DataListFragment fragment = new DataListFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return 2;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return mContext.getString(R.string.title_contacts);
                case 1:
                    return mContext.getString(R.string.title_history);
                default:
                    return null;
            }
        }
    }

}



