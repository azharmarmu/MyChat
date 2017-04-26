package marmu.com.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "56912";
    static final String AUTH_KEY = "BCOPyJN629Jm4XZ";
    static final String AUTH_SECRET = "Rh6ZRTpWyS6s43t";
    static final String ACCOUNT_KEY = "fNyZ8DK7DGQ1X899MHyP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set-up quick-blox framework
        initializeQBFrameWork();

        //register session
        registerQBSession();
    }

    private void initializeQBFrameWork() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }

    private void registerQBSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    public void Login(View view) {
        EditText edtUser = (EditText) findViewById(R.id.main_editName);
        EditText edtPassword = (EditText) findViewById(R.id.main_editPassword);

        final String user = edtUser.getText().toString();
        final String password = edtPassword.getText().toString();

        QBUser qbUser = new QBUser(user, password);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toast.makeText(getApplicationContext(), "Logged in Successfully ", Toast.LENGTH_SHORT).show();

                Intent landingActivity = new Intent(MainActivity.this, LandingActivity.class);
                landingActivity.putExtra("user", user);
                landingActivity.putExtra("password", password);
                startActivity(landingActivity);
                finish(); //Close Login activity after logged

            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void SignUp(View view) {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
}
