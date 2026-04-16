// Admin.java
public class Admin extends User {
    public Admin(String u, String p, String e, String s) { super(u, Utils.hash(p), Role.ADMIN, e, s); }
}