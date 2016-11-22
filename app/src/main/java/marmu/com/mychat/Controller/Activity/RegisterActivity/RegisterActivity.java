package marmu.com.mychat.Controller.Activity.RegisterActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import marmu.com.mychat.Controller.Activity.LoginActivity.LoginActivity;
import marmu.com.mychat.Controller.Activity.MainActivity.MainActivity;
import marmu.com.mychat.CommonStuffs.Utility;
import marmu.com.mychat.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabase;

    private RelativeLayout mImagePickerLayout;
    private ImageView mProfilePicHolder;
    private EditText mName, mEmail, mPassword;

    private String userChoosenTask = "";

    private static int REQUEST_CAMERA = 1;
    private static int REQUEST_GALLERY = 2;

    private Uri mImgUri;

    private String TAG = "FireBase : ";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();


        mStorageReference = FirebaseStorage.getInstance().getReference()
                .child("users");

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users");
        mDatabase.keepSynced(true);

        mProgressDialog = new ProgressDialog(this);

        mImagePickerLayout = (RelativeLayout) findViewById(R.id.rlImagePickerLayout);
        mProfilePicHolder = (ImageView) findViewById(R.id.ivProfilePicHolder);
        ImageButton mProfilePicButton = (ImageButton) findViewById(R.id.lbtnProfilePic);
        mProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(RegisterActivity.this);
                        if (items[item].equals("Take Photo")) {
                            userChoosenTask = "Take Photo";
                            if (result) {
                                cameraIntent();
                            }
                        } else if (items[item].equals("Choose from Library")) {
                            userChoosenTask = "Choose from Library";
                            if (result) {
                                galleryIntent();
                            }
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        mName = (EditText) findViewById(R.id.etName);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    public void login(View view) {
        Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivity);
    }

    public void register(View view) {
        mProgressDialog.setMessage("Registering ...");
        final String name = mName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && mImgUri != null) {
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            mProgressDialog.dismiss();

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Sign up failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                StorageReference StorageReferenceUSer = mStorageReference.child(mAuth.getCurrentUser().getUid())
                                        .child("image");
                                StorageReferenceUSer.putFile(mImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                                        assert downloadUri != null;

                                        DatabaseReference DatabaseUser = mDatabase.child(mAuth.getCurrentUser().getUid());
                                        DatabaseUser.child("profile_pic").setValue(downloadUri.toString());
                                        DatabaseUser.child("user_name").setValue(name);
                                        DatabaseUser.child("email_id").setValue(email);
                                        DatabaseUser.child("password").setValue(password);

                                        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mainActivity);
                                    }
                                });
                            }
                        }
                    });

        } else {
            Toast.makeText(RegisterActivity.this, "Please fill all the Fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    Toast.makeText(RegisterActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            mImagePickerLayout.setVisibility(View.GONE);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        mImgUri = data.getData();
        Picasso.with(this).load(mImgUri).into(mProfilePicHolder);
    }

    private void onCaptureImageResult(Intent data) {
        mImgUri = data.getData();
        Picasso.with(this).load(mImgUri).into(mProfilePicHolder);
    }
}