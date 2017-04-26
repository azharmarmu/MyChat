package marmu.com.mychat.holder;

import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by azharuddin on 25/4/17.
 */

public class QBChatDialogHolder {

    private static QBChatDialogHolder instance;
    private HashMap<String, QBChatDialog> qbChatDialogHashMap;

    public static synchronized QBChatDialogHolder getInstance() {
        QBChatDialogHolder qbChatDialogHolder;
        synchronized (QBChatDialogHolder.class) {
            if (instance == null) {
                instance = new QBChatDialogHolder();
            }

            qbChatDialogHolder = instance;

            return qbChatDialogHolder;
        }
    }

    public QBChatDialogHolder() {
        this.qbChatDialogHashMap = new HashMap<>();
    }

    public void putDialogs(List<QBChatDialog> dialogs) {
        for (QBChatDialog qbChatDialog : dialogs) {
            putDialog(qbChatDialog);
        }
    }

    public void putDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialogHashMap.put(qbChatDialog.getDialogId(), qbChatDialog);
    }

    public QBChatDialog getChatDialogById(String dialogId) {
        return qbChatDialogHashMap.get(dialogId);
    }

    public List<QBChatDialog> getChatDialogsById(List<String> dialogIds) {
        List<QBChatDialog> chatDialogs = new ArrayList<>();
        for (String id : dialogIds) {
            QBChatDialog chatDialog = getChatDialogById(id);
            if (chatDialog != null) {
                chatDialogs.add(chatDialog);
            }
        }

        return chatDialogs;
    }

    public ArrayList<QBChatDialog> getAllChatDialogs() {
        ArrayList<QBChatDialog> chatDialogs = new ArrayList<>();
        for (String key : qbChatDialogHashMap.keySet()) {
            chatDialogs.add(qbChatDialogHashMap.get(key));
        }

        return chatDialogs;
    }

}
