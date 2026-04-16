// User.java
public abstract class User {
    String username, password, email, skill;
    Role role;

    public User(String u, String p, Role r, String e, String s) {
        username = u; password = p; role = r; email = e; skill = s;
    }

    public boolean login(String u, String p) {
        return username.equals(u) && password.equals(Utils.hash(p));
    }

    public String toFile() {
        return username + "|" + password + "|" + role + "|" + email + "|" + skill;
    }
}





