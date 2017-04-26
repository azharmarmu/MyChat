package marmu.com.mychat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void SignUp(View view) {
        EditText edtUser = (EditText) findViewById(R.id.signup_editName);
        EditText edtFullName = (EditText) findViewById(R.id.signup_editFullName);
        EditText edtPassword = (EditText) findViewById(R.id.signup_editPassword);

        String fullName = edtFullName.getText().toString();
        String user = edtUser.getText().toString();
        String password = edtPassword.getText().toString();

        QBUser qbUser = new QBUser(user, password);

        qbUser.setFullName(fullName);

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toast.makeText(getApplicationContext(), "Sign Up successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void Cancel(View view) {
        finish();
    }
}
