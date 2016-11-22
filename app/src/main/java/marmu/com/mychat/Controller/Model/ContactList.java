package marmu.com.mychat.Controller.Model;

public class ContactList {

    private String ContactName, Status;

    public ContactList() {
    }

    public ContactList(String contactName, String status) {
        ContactName = contactName;
        Status = status;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
