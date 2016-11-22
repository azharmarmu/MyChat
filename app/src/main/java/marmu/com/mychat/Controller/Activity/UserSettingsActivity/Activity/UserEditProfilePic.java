package marmu.com.mychat.Controller.Activity.UserSettingsActivity.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import marmu.com.mychat.CommonStuffs.Common;
import marmu.com.mychat.R;
import marmu.com.mychat.CommonStuffs.Utility;


public class UserEditProfilePic extends AppCompatActivity {

    private StorageReference mStorageReference;
    private DatabaseReference mDatabase;

    private ImageView mProfilePicHolder;

    private Uri mImgUri;

    private static int REQUEST_CAMERA = 1;
    private static int REQUEST_GALLERY = 2;

    private String userChoosenTask = "";

    private ProgressDialog mProgressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile_pic);

        Toolbar mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile photo");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mProgressDialog = new ProgressDialog(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        //noinspection ConstantConditions
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid());
        mDatabase.keepSynced(true);

        mStorageReference = FirebaseStorage.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("image");

        mProfilePicHolder = (ImageView) findViewById(R.id.ivProfilePicHolder);
        if(!Objects.equals(Common.getUserData("cachedImage", getApplicationContext()), "")) {
            Uri uri = Uri.parse(Common.getUserData("cachedImage", getApplicationContext()));
            showImageCacheorUpdate(uri);
        }

    }

    private void showImageCacheorUpdate(Uri uri){
        mImgUri = uri;
        Picasso.with(getApplicationContext())
                .load(mImgUri)
                .into(mProfilePicHolder);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        mImgUri = data.getData();
        Picasso.with(this).load(mImgUri).into(mProfilePicHolder);
        uploadImageToStorage();

    }

    private void onCaptureImageResult(Intent data) {
        mImgUri = data.getData();
        Picasso.with(this).load(mImgUri).into(mProfilePicHolder);
        uploadImageToStorage();
    }

    private void uploadImageToStorage() {
        if (mImgUri != null) {
            mProgressDialog.show();
            mStorageReference.putFile(mImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    assert downloadUri != null;
                    mDatabase.child("profile_pic").setValue(downloadUri.toString());
                    mProgressDialog.dismiss();
                }
            });
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
                    Toast.makeText(UserEditProfilePic.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_pic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent userSettings = new Intent(UserEditProfilePic.this, UserSettings.class);
                userSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(userSettings);
                return true;
            case R.id.action_edit:
                final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(UserEditProfilePic.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(UserEditProfilePic.this);
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
                return true;
            case R.id.action_share:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
