package marmu.com.mychat.holder;


import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azharuddin on 24/4/17.
 */

public class QBUsersHolder {
    private static QBUsersHolder instance;

    private SparseArray<QBUser> qbUserSparseArray;

    public static synchronized QBUsersHolder getInstance() {
        if (instance == null) {
            instance = new QBUsersHolder();
        }
        return instance;
    }

    private QBUsersHolder() {
        qbUserSparseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> users) {
        for (QBUser user : users) {
            putUsers(user);
        }
    }

    private void putUsers(QBUser user) {
        qbUserSparseArray.put(user.getId(), user);
    }

    public QBUser getUserById(int id) {
        return qbUserSparseArray.get(id);
    }

    public List<QBUser> getUsersById(List<Integer> ids) {
        List<QBUser> qbUser = new ArrayList<>();
        for (Integer id : ids) {
            QBUser user = getUserById(id);
            if (user != null) {
                qbUser.add(user);
            }
        }
        return qbUser;
    }

    public ArrayList<QBUser> getAllUsers() {
        ArrayList<QBUser> allUsers = new ArrayList<>();

        for (int i = 0; i < qbUserSparseArray.size(); i++) {
            allUsers.add(qbUserSparseArray.valueAt(i));
        }

        return allUsers;
    }
}
