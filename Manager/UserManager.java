import java.util.*;
import java.io.*;

public class UserManager {
    ArrayList<User> users = new ArrayList<>();

    public UserManager() { load(); }

    public void register(Scanner sc) {
        UI.header("Registration");
        UI.prompt("Username"); String u = sc.nextLine();
        UI.prompt("Password"); String p = sc.nextLine();
        UI.prompt("Email"); String e = sc.nextLine();
        UI.prompt("Skill"); String s = sc.nextLine();
        System.out.println(UI.PURPLE + "[1] Admin [2] Leader [3] Student" + UI.RESET);
        UI.prompt("Role Number");
        int r = Integer.parseInt(sc.nextLine());
        User newUser = (r == 1) ? new Admin(u, p, e, s) : (r == 2) ? new Leader(u, p, e, s) : new Student(u, p, e, s);
        users.add(newUser);
        save();
        UI.success("Registered!");
    }

    public User login(Scanner sc) {
        UI.prompt("Username"); String u = sc.nextLine();
        UI.prompt("Password"); String p = sc.nextLine();
        for (User user : users) if (user.login(u, p)) return user;
        return null;
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter("users.txt")) {
            for (User u : users) pw.println(u.toFile());
        } catch (Exception e) {}
    }

    public void load() {
        try {
            File f = new File("users.txt");
            if (!f.exists()) return;
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");
                Role role = Role.valueOf(p[2]);
                User u = (role == Role.ADMIN) ? new Admin(p[0], "pass", p[3], p[4])
                        : (role == Role.LEADER) ? new Leader(p[0], "pass", p[3], p[4])
                        : new Student(p[0], "pass", p[3], p[4]);
                u.password = p[1];
                users.add(u);
            }
        } catch (Exception e) {}
    }
}