package marmu.com.mychat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;


public class UserProfileActivity extends AppCompatActivity {

    EditText fullName, emailAddress, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit your  profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fullName = (EditText) findViewById(R.id.update_editFullName);
        emailAddress = (EditText) findViewById(R.id.update_editEmail);
        phoneNumber = (EditText) findViewById(R.id.update_editPhone);

        loadUserDetails();

    }


    public void loadUserDetails() {
        QBUser currentUser = QBChatService.getInstance().getUser();
        fullName.setText(currentUser.getFullName());
        emailAddress.setText(currentUser.getEmail());
        fullName.setText(currentUser.getPhone());
    }


    public void Update(View view) {


        QBUser qbUser = new QBUser();
        qbUser.setId(QBChatService.getInstance().getUser().getId());

        if (!TextUtils.isEmpty(fullName.getText())) {
            qbUser.setFullName(fullName.getText().toString());
        }

        if (!TextUtils.isEmpty(emailAddress.getText())) {
            qbUser.setEmail(emailAddress.getText().toString());
        }

        if (!TextUtils.isEmpty(phoneNumber.getText())) {
            qbUser.setPhone(phoneNumber.getText().toString());
        }

        final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                progressDialog.hide();
                Toast.makeText(UserProfileActivity.this, "User: " + qbUser.getLogin() + " updated", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(UserProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void Cancel(View view) {
        finish();
    }
}
