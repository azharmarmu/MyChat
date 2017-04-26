package marmu.com.mychat.holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by azharuddin on 25/4/17.
 */

public class QBChatMessagesHolder {
    private static QBChatMessagesHolder instance;
    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessagesHolder getInstance() {
        QBChatMessagesHolder qbChatMessagesHolder;
        synchronized (QBChatMessagesHolder.class) {
            if (instance == null) {
                instance = new QBChatMessagesHolder();
            }
            qbChatMessagesHolder = instance;
        }
        return qbChatMessagesHolder;
    }

    private QBChatMessagesHolder() {
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putMessages(String dialogId, ArrayList<QBChatMessage> qbChatMessages) {
        this.qbChatMessageArray.put(dialogId, qbChatMessages);
    }

    public void putMessage(String diaogId, QBChatMessage qbChatMessage) {
        List<QBChatMessage> lstResult = this.qbChatMessageArray.get(diaogId);
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lstAdded = new ArrayList<>(lstResult.size());
        lstAdded.addAll(lstResult);
        putMessages(diaogId, lstAdded);
    }

    public ArrayList<QBChatMessage> getChatMessageByDialogId(String dialogId) {
        return this.qbChatMessageArray.get(dialogId);
    }

}
