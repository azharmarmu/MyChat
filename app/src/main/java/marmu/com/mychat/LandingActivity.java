package marmu.com.mychat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

import marmu.com.mychat.adapter.ViewPagerAdapter;
import marmu.com.mychat.fragment.CallFragment;
import marmu.com.mychat.fragment.ChatFragment;


public class LandingActivity extends AppCompatActivity {

    private String user, password;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Oye");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //add chat fragment
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("password", password);
        chatFragment.setArguments(bundle);
        adapter.addFragment(chatFragment, "CHATS");

        //add contact fragment
        CallFragment contactFragment = new CallFragment();
        adapter.addFragment(contactFragment, "CALLS");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(LandingActivity.this, UserProfileActivity.class));
                break;
            case R.id.action_exit:
                QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                Toast.makeText(LandingActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(LandingActivity.this, MainActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainActivity);
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e("Error", e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", e.getMessage());
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
