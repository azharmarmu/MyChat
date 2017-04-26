package marmu.com.mychat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

import marmu.com.mychat.adapter.ListUsersAdapter;
import marmu.com.mychat.common.Common;
import marmu.com.mychat.holder.QBUsersHolder;

public class ListUsersActivity extends AppCompatActivity {

    private ListView lstUsers;
    private TextView noUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        retrieveAllUsers();

        noUsers = (TextView) findViewById(R.id.noUsers);

        lstUsers = (ListView) findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button btnCreateChat = (Button) findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lstUsers.getCheckedItemPositions().size() == 1) {
                    createPrivateChat(lstUsers.getCheckedItemPositions());
                } else if (lstUsers.getCheckedItemPositions().size() > 1) {
                    createGroupChat(lstUsers.getCheckedItemPositions());
                } else {
                    Toast.makeText(getBaseContext(), "Please select a friend to chat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

        Common.showProgressDialog(ListUsersActivity.this);

        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdList = new ArrayList<>();
        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions.get(i)) {
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                occupantIdList.add(user.getId());
            }
        }

        //create chat dialog
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                Common.dismissProgressDialog();
                Toast.makeText(getBaseContext(), "Created group chat dialog successfully", Toast.LENGTH_SHORT).show();

                //send system message to recipient id user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                for (int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));

                    try {
                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());
            }
        });

    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {

        Common.showProgressDialog(ListUsersActivity.this);

        int countChoice = lstUsers.getCount();
        for (int i = 0; i < countChoice; i++) {
            if (checkedItemPositions.get(i)) {
                final QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Common.dismissProgressDialog();
                        Toast.makeText(getBaseContext(), "Created private chat dialog successfully", Toast.LENGTH_SHORT).show();

                        //send system message to recipient id user
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(user.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());

                        try {
                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", e.getMessage());
                    }
                });
            }
        }

    }

    private void retrieveAllUsers() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                //Add to cache

                QBUsersHolder.getInstance().putUsers(qbUsers);

                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                if (qbUsers.size() > 0) {
                    for (QBUser user : qbUsers) {
                        try {
                            if (!user.getLogin().equalsIgnoreCase(QBChatService.getInstance().getUser().getLogin())) {
                                qbUserWithoutCurrent.add(user);
                            }
                        } catch (NullPointerException e) {
                            Log.e("Null Pointer-Exception", e.getMessage());
                        }
                    }

                    ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(), qbUserWithoutCurrent);
                    lstUsers.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    noUsers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

}
