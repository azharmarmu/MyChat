package marmu.com.mychat;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import marmu.com.mychat.adapter.ChatMessageAdapter;
import marmu.com.mychat.common.Common;
import marmu.com.mychat.holder.QBChatMessagesHolder;


public class ChatMessageActivity extends AppCompatActivity implements QBChatDialogMessageListener {

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton submitButton, emojiButton;
    EditText edtContent;

    ChatMessageAdapter adapter;

    //update online users
    LinearLayout dialogInfo;
    ImageView imgOnlineCount;
    TextView txtOnlineCount;

    //Variables for Edit/Delete message
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;

    Toolbar mToolBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);


        initializeViews();

        initializeChatDialogs();

        retrieveAllMessage();

        sendMessage();
    }

    private void sendMessage() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtContent.getText().toString();
                if (!isEditMode) {
                    if (!message.isEmpty()) {
                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(message);
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);

                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        //Fix private chat don't show msg
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            //cache Message
                            QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessageByDialogId(chatMessage.getDialogId());
                            adapter = new ChatMessageAdapter(getBaseContext(), messages);
                            lstChatMessages.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        //Remove text from edit text
                        edtContent.setText("");
                        edtContent.setFocusable(true);
                    }
                } else {
                    QBMessageUpdateBuilder qbMessageUpdateBuilder = new QBMessageUpdateBuilder();
                    qbMessageUpdateBuilder.updateText(message).markDelivered().markRead();

                    QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), qbMessageUpdateBuilder)
                            .performAsync(new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    retrieveAllMessage();
                                    isEditMode = false; //reset variable

                                    edtContent.setText("");
                                    edtContent.setFocusable(true);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Log.e("Error", e.getMessage());
                                }
                            });
                }
            }
        });
    }

    private void retrieveAllMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(5000);

        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                    //put messages to cache
                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);

                    adapter = new ChatMessageAdapter(getBaseContext(), qbChatMessages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    private void initializeChatDialogs() {
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());

        //Register Listener Incoming Message
        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });

        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
            dialogInfo.setVisibility(View.GONE);
        }

        //Add join group to enable group chat
        if (qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP) {
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error", e.getMessage());
                }
            });
        }

        //Show online users
        QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogId, QBPresence qbPresence) {
                if (Objects.equals(dialogId, qbChatDialog.getDialogId())) {
                    QBRestChatService.getChatDialogById(dialogId)
                            .performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    //Get online Users
                                    try {

                                        Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();
                                        TextDrawable.IBuilder builder = TextDrawable.builder()
                                                .beginConfig()
                                                .withBorder(4)
                                                .endConfig()
                                                .round();

                                        TextDrawable online = builder.build("", Color.GREEN);
                                        imgOnlineCount.setImageDrawable(online);

                                        txtOnlineCount.setText(String.format("%d/%d online", onlineList.size(), qbChatDialog.getOccupants().size()));

                                    } catch (XMPPException | SmackException.NotConnectedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Log.e("Error", e.getMessage());
                                }
                            });
                }
            }
        };

        qbChatDialog.addParticipantListener(participantListener);
        qbChatDialog.addMessageListener(this);


        //set title for toolbar
        setSupportActionBar(mToolBarView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(qbChatDialog.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void initializeViews() {

        dialogInfo = (LinearLayout) findViewById(R.id.dialog_info);
        imgOnlineCount = (ImageView) findViewById(R.id.img_online_count);
        txtOnlineCount = (TextView) findViewById(R.id.txt_online_count);

        lstChatMessages = (ListView) findViewById(R.id.lstChatDialogs);
        submitButton = (ImageButton) findViewById(R.id.send_button);
        emojiButton = (ImageButton) findViewById(R.id.emoji_button);
        edtContent = (EditText) findViewById(R.id.edt_content);


        //Add context menu
        registerForContextMenu(lstChatMessages);

        //Add Toolbar
        mToolBarView = (Toolbar) findViewById(R.id.view);
        mToolBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatInfoActivity = new Intent(ChatMessageActivity.this, ChatInfoActivity.class);
                chatInfoActivity.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                startActivity(chatInfoActivity);
            }
        });
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        //cache Message
        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(), qbChatMessage);
        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessageByDialogId(qbChatMessage.getDialogId());
        adapter = new ChatMessageAdapter(getBaseContext(), messages);
        lstChatMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("Error", e.getMessage());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_message_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        contextMenuIndexClicked = menuInfo.position;

        switch (item.getItemId()) {
            case R.id.action_update:
                editMessage = QBChatMessagesHolder.getInstance().getChatMessageByDialogId(qbChatDialog.getDialogId())
                        .get(contextMenuIndexClicked);
                edtContent.setText(editMessage.getBody());
                isEditMode = true; //Set Edit mode to true
                break;
            case R.id.action_delete:
                editMessage = QBChatMessagesHolder.getInstance().getChatMessageByDialogId(qbChatDialog.getDialogId())
                        .get(contextMenuIndexClicked);

                QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        retrieveAllMessage();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", e.getMessage());
                    }
                });

                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }
}
