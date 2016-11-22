package marmu.com.mychat.Controller.Activity.MainActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import marmu.com.mychat.CommonStuffs.ClickListener;
import marmu.com.mychat.CommonStuffs.Common;
import marmu.com.mychat.CommonStuffs.RecyclerTouchListener;
import marmu.com.mychat.Controller.Activity.IndexActivity.IndexActivity;
import marmu.com.mychat.Controller.Adapter.ContactListAdapter;
import marmu.com.mychat.Controller.Adapter.ViewPagerAdapter;
import marmu.com.mychat.Controller.Fragment.ChatFragment;
import marmu.com.mychat.Controller.Fragment.ContactFragment;
import marmu.com.mychat.Controller.Model.ContactList;
import marmu.com.mychat.R;
import marmu.com.mychat.Controller.Activity.UserSettingsActivity.Activity.UserSettings;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Let'sChat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Tab View-Pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Recycler-View
        //populateRecyclerView();

        //PrepareContactList
        //prepareContactLists();

        //AuthStateListener
        authStateListener();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "CHATS");
        adapter.addFragment(new ContactFragment(), "CONTACTS");
        viewPager.setAdapter(adapter);
    }

    private void authStateListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.v(TAG, "onAuthStateChanged ---> signed_in : " + firebaseUser.getUid());
                } else {
                    Log.v(TAG, "onAuthStateChanged ---> signed_out");
                    Intent indexActivity = new Intent(getApplicationContext(), IndexActivity.class);
                    indexActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(indexActivity);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                Common.saveUserData("cachedImage", "", getApplicationContext());
                mAuth.signOut();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, UserSettings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
