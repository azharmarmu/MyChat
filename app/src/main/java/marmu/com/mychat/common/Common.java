package marmu.com.mychat.common;

import android.app.ProgressDialog;
import android.content.Context;

import com.quickblox.users.model.QBUser;

import java.util.List;

import marmu.com.mychat.holder.QBUsersHolder;

/**
 * Created by azharuddin on 24/4/17.
 */

public class Common {

    private static ProgressDialog progressDialog;

    public static final String DIALOG_EXTRA = "Dialogs";

    public static String createChatDialogName(List<Integer> qbUsers) {

        List<QBUser> qbUserList = QBUsersHolder.getInstance().getUsersById(qbUsers);
        StringBuilder name = new StringBuilder();
        for (QBUser user : qbUserList) {
            name.append(user.getFullName()).append(" ");
        }

        if (name.length() > 30) {
            name = name.replace(30, name.length() - 1, "...");
        }

        return name.toString();
    }

    public static void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
