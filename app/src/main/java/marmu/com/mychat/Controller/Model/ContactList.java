package marmu.com.mychat.Controller.Model;

import android.net.Uri;

public class ContactList {

    private String user_name, status;
    private Uri profile_pic;

    public ContactList() {
    }

    public ContactList(String user_name, String status, Uri profile_pic) {
        this.user_name = user_name;
        this.status = status;
        this.profile_pic = profile_pic;
    }

    public String getuser_name() {
        return this.user_name;
    }

    public void setuser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getstatus() {
        return this.status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public Uri getprofile_pic() {
        return this.profile_pic;
    }

    public void setprofile_pic(Uri profile_pic) {
        this.profile_pic = profile_pic;
    }
}
