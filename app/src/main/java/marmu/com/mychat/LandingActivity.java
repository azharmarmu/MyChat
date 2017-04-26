package marmu.com.mychat;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import marmu.com.mychat.adapter.ViewPagerAdapter;
import marmu.com.mychat.fragment.CallFragment;
import marmu.com.mychat.fragment.ChatFragment;


public class LandingActivity extends AppCompatActivity {

    private String user, password;

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

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

}
