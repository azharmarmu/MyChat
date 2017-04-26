package marmu.com.mychat.holder;

import android.os.Bundle;

/**
 * Created by azharuddin on 25/4/17.
 */

public class QBUnreadMessageHolder {
    private static QBUnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance() {
        QBUnreadMessageHolder qbUnreadMessageHolder;
        synchronized (QBUnreadMessageHolder.class) {
            if (instance == null) {
                instance = new QBUnreadMessageHolder();
            }

            qbUnreadMessageHolder = instance;
        }
        return qbUnreadMessageHolder;

    }

    private QBUnreadMessageHolder() {
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public int getUnreadMessageByDialogId(String id){
        return this.bundle.getInt(id);
    }


}
