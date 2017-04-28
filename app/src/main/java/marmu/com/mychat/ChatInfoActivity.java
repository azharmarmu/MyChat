package marmu.com.mychat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import marmu.com.mychat.common.Common;

public class ChatInfoActivity extends AppCompatActivity {

    QBChatDialog qbChatDialog;

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    Toolbar mToolBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(qbChatDialog.getName());

        mToolBarView = (Toolbar) findViewById(R.id.view);
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(qbChatDialog.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        dynamicToolbarColor();

        toolbarTextAppearance();
    }


    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimary)));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(getResources().getColor(R.color.colorPrimary)));
            }
        });
    }


    private void toolbarTextAppearance() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (qbChatDialog.getType() == QBDialogType.GROUP || qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP) {
            getMenuInflater().inflate(R.menu.chat_message_group_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_name:

                editGroupName();

                break;
            case R.id.action_add_member:

                addMemberToGroup();

                break;
            case R.id.action_remove_member:

                removeMemberFromGroup();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMemberToGroup() {
        Intent listUserActivity = new Intent(ChatInfoActivity.this, ListUsersActivity.class);
        listUserActivity.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        listUserActivity.putExtra(Common.UPDATE_MODE, Common.UPDATE_ADD_MODE);
        startActivity(listUserActivity);
    }

    private void removeMemberFromGroup() {
        Intent listUserActivity = new Intent(ChatInfoActivity.this, ListUsersActivity.class);
        listUserActivity.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        listUserActivity.putExtra(Common.UPDATE_MODE, Common.UPDATE_REMOVE_MODE);
        startActivity(listUserActivity);
    }

    private void editGroupName() {
        LayoutInflater inflater = LayoutInflater.from(ChatInfoActivity.this);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_edit_group_layout, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatInfoActivity.this);
        alertBuilder.setView(view);
        final EditText newName = (EditText) view.findViewById(R.id.edt_new_group_name);

        //set Dialog message
        alertBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        qbChatDialog.setName(newName.getText().toString());

                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        Toast.makeText(getApplicationContext(), "Group name changed", Toast.LENGTH_SHORT).show();

                                        collapsingToolbarLayout.setTitle(qbChatDialog.getName());

                                        setSupportActionBar(mToolBarView);
                                        if (getSupportActionBar() != null) {
                                            getSupportActionBar().setTitle(qbChatDialog.getName());
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        Log.e("Error", e.getMessage());
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

}
