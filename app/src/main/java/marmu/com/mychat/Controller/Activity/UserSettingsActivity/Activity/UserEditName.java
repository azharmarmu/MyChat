package marmu.com.mychat.Controller.Activity.UserSettingsActivity.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import marmu.com.mychat.R;


public class UserEditName extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private EditText mEditName;
    private TextView mNameLength;
    private ImageView mSmileys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_name);

        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Enter your name");
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //noinspection ConstantConditions
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mDatabase.keepSynced(true);

        mEditName = (EditText) findViewById(R.id.etEditName);
        mNameLength = (TextView) findViewById(R.id.tvNameLength);
        mNameLength.setText("");

        mDatabase.child("user_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEditName.setText(dataSnapshot.getValue().toString());
                int len = 25 - mEditName.getText().toString().length();
                mNameLength.setText(String.valueOf(len));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = 25 - mEditName.getText().toString().length();
                if (len < 25)
                    mNameLength.setText(String.valueOf(len));
                else
                    mNameLength.setText("");
                mEditName.setSelection(mEditName.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSmileys = (ImageView) findViewById(R.id.ivSmileys);
    }


    public void cancel(View view) {
        finish();
    }

    public void ok(View view) {
        mDatabase.child("user_name").setValue(mEditName.getText().toString().trim());
        cancel(view);
    }
}
