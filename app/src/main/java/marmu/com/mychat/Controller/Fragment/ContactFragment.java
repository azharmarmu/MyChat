package marmu.com.mychat.Controller.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import marmu.com.mychat.CommonStuffs.ClickListener;
import marmu.com.mychat.CommonStuffs.RecyclerTouchListener;
import marmu.com.mychat.Controller.Adapter.ContactListAdapter;
import marmu.com.mychat.Controller.Model.ContactList;
import marmu.com.mychat.R;

public class ContactFragment extends Fragment {

    private DatabaseReference mDatabase;

    private String TAG = "ContactFragment";

    private List<ContactList> mContactLists = new ArrayList<>();
    private ContactListAdapter mAdapter;

    public ContactFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        populateRecyclerView(rootView);
        prepareContactLists();
        return rootView;
    }

    private void populateRecyclerView(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new ContactListAdapter(mContactLists, getContext());

        //Recycler-view LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Recycler-view Animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ContactList contactList = mContactLists.get(position);
                Toast.makeText(getContext(), contactList.getuser_name() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(mAdapter);
    }

    private void prepareContactLists() {
/*        contactList = new ContactList("Azhar", "Single", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Bharath", "Engaged", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Sanjay", "Single", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Niyaz", "Mingle", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Shiva", "Married", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Prabhakar", "Dead", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Mohan", "ALive", R.drawable.profile);
        mContactLists.add(contactList);
        contactList = new ContactList("Dinesh", "Don't Know", R.drawable.profile);
        mContactLists.add(contactList);*/
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users");
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ContactList contactList;
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    String key = ((DataSnapshot) i.next()).getKey();
                    String name = dataSnapshot.child(key).child("user_name").getValue().toString();
                    String status = dataSnapshot.child(key).child("status").getValue().toString();
                    String profile_pic = dataSnapshot.child(key).child("profile_pic").getValue().toString();

                    mContactLists.add(new ContactList(name,status, Uri.parse(profile_pic)));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAdapter.notifyDataSetChanged();
    }
}

