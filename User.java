public class User {
    public int userID;
    public String userName;
    public String password;
    public String displayName;
    public boolean isManager;

    public String toString() {
        String result = "  UserID: " + this.userID
                + "\nUsername: " + this.userName
                + "\nPassword: " + this.password
                + "\n    Name: " + this.displayName
                + "\n   Admin: " + this.isManager;
        return result;
    }
}

