package marmu.com.mychat.Controller.Model;

public class ContactList {

    private String ContactName, Status;
    private int ImageId;

    public ContactList() {
    }

    public ContactList(String contactName, String status, int imageId) {
        this.ContactName = contactName;
        this.Status = status;
        this.ImageId = imageId;
    }

    public String getContactName() {
        return this.ContactName;
    }

    public void setContactName(String contactName) {
        this.ContactName = contactName;
    }

    public String getStatus() {
        return this.Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public int getImageId() {
        return this.ImageId;
    }

    public void setImageId(int imageId) {
        this.ImageId = imageId;
    }
}
