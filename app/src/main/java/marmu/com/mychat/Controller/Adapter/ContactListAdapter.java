package marmu.com.mychat.Controller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import marmu.com.mychat.Controller.Model.ContactList;
import marmu.com.mychat.R;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private List<ContactList> mContactLists;

    public ContactListAdapter(List<ContactList> ContactLists) {
        this.mContactLists = ContactLists;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName, mStatus;

        private MyViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.tvName);
            mStatus = (TextView) view.findViewById(R.id.tvStatus);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contacts, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactList contactList = mContactLists.get(position);
        holder.mName.setText(contactList.getContactName());
        holder.mStatus.setText(contactList.getStatus());
    }

    @Override
    public int getItemCount() {
        return mContactLists.size();
    }
}

