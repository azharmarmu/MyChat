package marmu.com.mychat.Controller.Activity.IndexActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import marmu.com.mychat.Controller.Activity.LoginActivity.LoginActivity;
import marmu.com.mychat.Controller.Activity.RegisterActivity.RegisterActivity;
import marmu.com.mychat.R;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
    }

    public void login(View v){
        Intent loginActivity = new Intent(IndexActivity.this,LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivity);
    }
    public void register(View v){
        Intent registerActivity = new Intent(IndexActivity.this,RegisterActivity.class);
        registerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
    }
}
