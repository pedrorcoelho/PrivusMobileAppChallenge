package com.demo.privusmobileappchallenge;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DataListFragment extends Fragment {

    final static int MODE_CONTACTS = 0;
    final static int MODE_HISTORY = 1;
    private RecyclerView m_recyclerView;
    private DataAdapter m_adapter;
    private int m_mode;
    private int pressed_position = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        m_mode = getArguments().getInt(DataManager.MODE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_list, container, false);

        m_recyclerView = view.findViewById(R.id.recycler_view);

        m_adapter = new DataAdapter(view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        m_recyclerView.setLayoutManager(mLayoutManager);
        m_recyclerView.setItemAnimator(new DefaultItemAnimator());
        m_recyclerView.setAdapter(m_adapter);

        if (m_mode == MODE_CONTACTS) {
            m_adapter.setContactsData(DataManager.loadContacts(getContext()));
            view.findViewById(R.id.buttons_panel).setVisibility(View.VISIBLE);
            hookButtonsPanel(view);
        } else {
            List<History> histories = DataManager.loadHistory(getContext());
            if (histories == null) histories = new ArrayList<History>();
            m_adapter.setHistoryData(histories);
        }

        return view;
    }

    void hookButtonsPanel(View view) {
        view.findViewById(R.id.add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactOptionsDialog dialog = new ContactOptionsDialog(getActivity(), null, ContactOptionsDialog.MODE_CREATE);
                dialog.show();
            }
        });
    }

    public static void addDummyDataDialog(final Activity activity, final Context context) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Utilities.addDummyData(context);
                refreshContacts();
            }
        };
        String title = activity.getResources().getString(R.string.add_dummy_data_short);
        String message = activity.getResources().getString(R.string.add_dummy_data);
        CustomDialog dialog = new CustomDialog(activity, title, message, runnable);
        dialog.show();
    }

    @Subscribe
    public void onMessageEvent(EventBusMessage event) {
        if (m_mode == MODE_HISTORY) {
            if (event.getMessage().equals(EventBusMessage.COMMAND_REFRESH_HISTORY)) {
                m_adapter.setHistoryData(DataManager.loadHistory(getContext()));
                m_adapter.notifyDataSetChanged();
            }
        } else {
            if (event.getMessage().equals(EventBusMessage.COMMAND_REFRESH_CONTACTS)) {
                m_adapter.setContactsData(DataManager.loadContacts(getContext()));
                m_adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public static void refreshHistory() {
        EventBus.getDefault().post(new EventBusMessage(EventBusMessage.COMMAND_REFRESH_HISTORY));
    }

    public static void refreshContacts() {
        EventBus.getDefault().post(new EventBusMessage(EventBusMessage.COMMAND_REFRESH_CONTACTS));
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.VH> {

        private Context mContext;
        private List<Contact> contacts = new ArrayList<Contact>();
        private List<History> histories = new ArrayList<History>();
        private View view;

        public class VH extends RecyclerView.ViewHolder {

            TextView line1, line2, voip_txt, avatar_circle;
            ImageView avatar;
            FrameLayout avatar_container;

            public VH(View view) {
                super(view);
                line1 = view.findViewById(R.id.line1);
                line2 = view.findViewById(R.id.line2);
                voip_txt = view.findViewById(R.id.voip_txt);
                avatar = view.findViewById(R.id.avatar);
                avatar_circle = view.findViewById(R.id.circle);
                avatar_container = view.findViewById(R.id.avatar_container);
            }
        }

        public DataAdapter(View view) {
            this.view = view;
        }

        /**
         * Enables or disables the "no data warning" textview.
         * Also, if the "Only voip" filter is active, changes text to "No data (only voip)" to warn the user.
         *
         * @param isVisible
         */
        private void setNoDataWarningState(boolean isVisible) {
            TextView no_data = view.findViewById(R.id.no_data_warning);
            no_data.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            no_data.setText(DataManager.isOnlyVoip(getContext()) ? getResources().getString(R.string.no_data_voip) : getResources().getString(R.string.no_data));
        }

        public void setContactsData(List<Contact> contacts) {
            resetContacts();
            if (contacts == null || contacts.isEmpty()) {
                setNoDataWarningState(true);
                this.contacts = new ArrayList<Contact>();
                notifyDataSetChanged();
                return;
            }

            for (Contact contact : contacts) {
                if (DataManager.isOnlyVoip(getContext())) {
                    if (contact.is_is_active() && contact.get_voip_number() != null) {
                        this.contacts.add(contact);
                    }
                } else {
                    if (contact.is_is_active()) {
                        this.contacts.add(contact);
                    }
                }
            }
            contacts = Utilities.sortList(contacts);
            setNoDataWarningState(this.contacts.isEmpty());
            notifyDataSetChanged();
        }

        public void setHistoryData(List<History> histories) {
            resetHistory();
            this.histories = histories;
            setNoDataWarningState(this.histories.isEmpty());
            Collections.reverse(this.histories);
            notifyDataSetChanged();
        }

        public void resetContacts() {
            m_adapter.contacts = new ArrayList<Contact>();
        }

        public void resetHistory() {
            m_adapter.histories = new ArrayList<History>();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_contact_record, parent, false);

            final VH vh = new VH(itemView);

            if (m_mode == MODE_CONTACTS) {
                final View parent_view = view;
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Contact contact = contacts.get(vh.getAdapterPosition());
                        pressed_position = vh.getAdapterPosition();
                        Runnable remove_contact = new Runnable() {
                            @Override
                            public void run() {
                                contacts.remove(contact);
                                notifyItemRemoved(pressed_position);
                                notifyItemRangeChanged(pressed_position, contacts.size());
                                parent_view.findViewById(R.id.no_data_warning).setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
                            }
                        };
                        ContactOptionsDialog dialog = new ContactOptionsDialog(getActivity(), contact, ContactOptionsDialog.MODE_VIEW, remove_contact);
                        dialog.show();
                    }
                });
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (getItemCount() == 0) return;
            if (m_mode == MODE_CONTACTS) {
                Contact contact = contacts.get(position);

                if (contact == null) {
                    return;
                }

                Utilities.setAvatar(contact, holder.avatar_circle, holder.avatar);

                holder.line1.setText(contact.get_name());
                holder.line2.setText(contact.get_number());
                holder.voip_txt.setVisibility(contact.get_voip_number() == null ? View.GONE : View.VISIBLE);

            } else {
                History history = histories.get(position);

                if (history == null) {
                    return;
                }

                Utilities.setAvatar(history.get_contact(), holder.avatar_circle, holder.avatar);

                String line1_txt = (history.get_contact().get_name() + " (" + history.get_contact().get_voip_number() + ")");
                holder.line1.setText(line1_txt);
                holder.line2.setText(history.get_date().toString());
                holder.voip_txt.setVisibility(history.get_contact().get_voip_number() == null ? View.GONE : View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            if (m_mode == MODE_HISTORY) {
                return histories.size();
            } else {
                return contacts.size();
            }
        }
    }
}
