package marmu.com.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import marmu.com.mychat.ChatMessageActivity;
import marmu.com.mychat.ListUsersActivity;
import marmu.com.mychat.R;
import marmu.com.mychat.adapter.ChatFragmentAdapter;
import marmu.com.mychat.common.Common;
import marmu.com.mychat.holder.QBChatDialogHolder;
import marmu.com.mychat.holder.QBUnreadMessageHolder;
import marmu.com.mychat.holder.QBUsersHolder;


public class ChatFragment extends Fragment {

    String user, password;

    private ListView lstChatDialogs;


    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getString("user");
        password = getArguments().getString("password");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //create session to chat
        createQBSessionForChat();

        //load chat-list
        loadChatList();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        lstChatDialogs = (ListView) view.findViewById(R.id.lstChatDialogs);

        lstChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog) lstChatDialogs.getAdapter().getItem(position);
                Intent chatMessageActivity = new Intent(getActivity(), ChatMessageActivity.class);
                chatMessageActivity.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                startActivity(chatMessageActivity);
            }
        });


        FloatingActionButton chatDialogAddUser = (FloatingActionButton) view.findViewById(R.id.chatdialog_adduser);
        chatDialogAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listUsersActivity = new Intent(getActivity(), ListUsersActivity.class);
                startActivity(listUsersActivity);
            }
        });

        return view;
    }

    private void createQBSessionForChat() {
        Common.showProgressDialog(getContext());

        // Load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @SuppressWarnings("deprecation")
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                try {
                    qbUser.setId(qbSession.getUserId());
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Common.dismissProgressDialog();

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBSystemMessageListener qbSystemMessageListener = new QBSystemMessageListener() {
                            @Override
                            public void processMessage(QBChatMessage qbChatMessage) {
                                //Put dialog to cache
                                //Because we send System message with content is DialogId
                                //So we can get dialog by dialogId
                                QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        //Put to cache
                                        QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                                        ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllChatDialogs();
                                        ChatFragmentAdapter adapter = new ChatFragmentAdapter(getActivity(), adapterSource);
                                        lstChatDialogs.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        Log.e("Error", e.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void processError(QBChatException e, QBChatMessage qbChatMessage) {
                                Log.e("Error", e.getMessage());
                            }
                        };

                        qbSystemMessagesManager.addSystemMessageListener(qbSystemMessageListener);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        QBChatDialogMessageListener qbChatDialogMessageListener = new QBChatDialogMessageListener() {
                            @Override
                            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                                loadChatList();
                            }

                            @Override
                            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                                Log.e("Error", e.getMessage());
                            }
                        };

                        qbIncomingMessagesManager.addDialogMessageListener(qbChatDialogMessageListener);

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    private void loadChatList() {
        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
        requestGetBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

                //Put all dialogs to cache
                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                //Unread Settings
                Set<String> setIds = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs) {
                    setIds.add(chatDialog.getDialogId());
                }

                //get Message unread
                QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle())
                        .performAsync(new QBEntityCallback<Integer>() {
                            @Override
                            public void onSuccess(Integer integer, Bundle bundle) {
                                //Save to cache
                                QBUnreadMessageHolder.getInstance().setBundle(bundle);

                                //Refresh List Dialog
                                ChatFragmentAdapter adapter = new ChatFragmentAdapter(getContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                                lstChatDialogs.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e("Error", e.getMessage());
                            }
                        });
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error", e.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadChatList();
    }
}
