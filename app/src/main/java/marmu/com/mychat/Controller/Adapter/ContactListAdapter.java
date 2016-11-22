package marmu.com.mychat.Controller.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import marmu.com.mychat.CommonStuffs.CircleTransform;
import marmu.com.mychat.Controller.Model.ContactList;
import marmu.com.mychat.R;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    private List<ContactList> mContactLists;
    private Context mContext;

    public ContactListAdapter(List<ContactList> ContactLists, Context context) {
        this.mContactLists = ContactLists;
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mName, mStatus;
        private ImageView mImageId;

        private MyViewHolder(View view) {
            super(view);
            mCardView = (CardView) view.findViewById(R.id.cardView);
            mName = (TextView) view.findViewById(R.id.tvName);
            mStatus = (TextView) view.findViewById(R.id.tvStatus);
            mImageId = (ImageView) view.findViewById(R.id.ivProfilePic);
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
        //holder.mImageId.setImageResource(contactList.getImageId());
        Picasso.with(mContext).load(contactList.getImageId()).transform(new CircleTransform()).into(holder.mImageId);
    }

    @Override
    public int getItemCount() {
        return mContactLists.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, ContactList contactList) {
        mContactLists.add(position, contactList);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(ContactList contactList) {
        int position = mContactLists.indexOf(contactList);
        mContactLists.remove(position);
        notifyItemRemoved(position);
    }
}

