import java.util.*;
import java.io.*;

public class MainSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager um = new UserManager();
        DataList<Club> clubData = new DataList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        loadAllData(clubData, reviews);

        while (true) {
            UI.header("MFU Event & Club Management System");
            System.out.println("[1] Login \n[2] Register \n[0] Exit");
            UI.prompt("Choose");
            String startChoice = sc.nextLine();
            
            if (startChoice.equals("0")) {
                UI.success("Goodbye!");
                System.exit(0);
            } else if (startChoice.equals("2")) {
                um.register(sc);
                continue;
            }

            User current = um.login(sc);
            if (current == null) {
                UI.error("Invalid credentials.");
                continue;
            }

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n" + UI.BOLD + UI.PURPLE + "── SESSION: " + current.username + " (" + current.role + ") ──" + UI.RESET);
                displayMenu(current);
                UI.prompt("Action");
                int choice;
                try { choice = Integer.parseInt(sc.nextLine()); } catch (Exception e) { choice = -1; }

                if (choice == 0) System.exit(0);
                if (choice == 9) { loggedIn = false; break; }

                handleChoice(current, choice, clubData, reviews, sc);
                saveAllData(clubData, reviews);
            }
        }
    }

    private static void displayMenu(User current) {
        if (current.role == Role.ADMIN)
            System.out.println("[1] Create Club [2] View Clubs \n[3] Delete Club [4] Dashboard");
        else if (current.role == Role.LEADER)
            System.out.println("[1] Post Announcement [2] View Announcements \n[3] Review Apps [4] Manage Members [6] My Club");
        else
            System.out.println("[1] View & Apply Club [2] View & Apply Announcement \n[3] Write Review [4] Search Club [5] Track Apps");
        System.out.println(UI.RED + "[9] Logout [0] Exit" + UI.RESET);
    }

   private static void handleChoice(User current, int choice, DataList<Club> clubData, ArrayList<Review> reviews,
            Scanner sc) {
        if (current.role == Role.ADMIN) {
            switch (choice) {
                case 1: {
                    UI.prompt("Club name");
                    String n = sc.nextLine();
                    UI.prompt("Required Skill");
                    String s = sc.nextLine();
                    UI.prompt("Leader Username");
                    String l = sc.nextLine();
                    clubData.getList().add(new Club(n, l, s));
                    UI.success("Club created.");
                }
                case 2: {
                    for (Club c : clubData.getList()) {
                        UI.header("CLUB: " + c.name);
                        System.out.println("Leader: " + c.leader + " | Skill: " + c.requiredSkill);
                    }
                }
                case 3: {
                    UI.prompt("Club name to delete");
                    String n = sc.nextLine();
                    UI.warning("Delete '" + n + "'? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) {
                        clubData.getList().removeIf(c -> c.name.equalsIgnoreCase(n));
                        UI.success("Deleted.");
                    }
                }
                case 4: {
                    UI.header("SYSTEM DASHBOARD");
                    System.out.println(
                            "Total Clubs: " + clubData.getList().size() + "\nTotal Reviews: " + reviews.size());
                }
            }
        } else if (current.role == Role.LEADER) {
            Club myClub = null;
            for (Club c : clubData.getList())
                if (c.leader.equals(current.username))
                    myClub = c;
            if (myClub == null) {
                UI.error("No club found.");
                return;
            }

            switch (choice) {
                case 1: {
                    UI.prompt("Title");
                    String t = sc.nextLine();
                    UI.prompt("Type [1] Event [2] Staff");
                    int ty = Integer.parseInt(sc.nextLine());
                    UI.prompt("Date");
                    String d = sc.nextLine();
                    UI.prompt("Description");
                    String desc = sc.nextLine();
                    UI.prompt("Capacity");
                    int cap = Integer.parseInt(sc.nextLine());
                    myClub.announcements.add(new Announcement(t, d, desc,
                            ty == 1 ? AnnouncementType.EVENT : AnnouncementType.STAFF_CALLING, cap));
                    UI.success("Posted.");
                }
                case 2: {
                    UI.header("ANNOUNCEMENTS: " + myClub.name);
                    UI.tableHeader("Index", "Title", "Applicants/Cap", "Type");
                    for (int i = 0; i < myClub.announcements.size(); i++) {
                        Announcement a = myClub.announcements.get(i);
                        UI.tableRow(String.valueOf(i), a.title, a.applications.size() + "/" + a.capacity,
                                a.type.toString());
                    }
                }
                case 3: {
                    UI.header("REVIEW APPLICATIONS");
                    System.out
                            .println("[1] Club Memberships (" + myClub.clubApplications.size() + ") [2] Announcements");
                    if (sc.nextLine().equals("1")) {
                        for (AnnApplication a : myClub.clubApplications) {
                            if (a.status != Status.PENDING)
                                continue;
                            a.displaySummary();
                            UI.prompt("[1] Approve [2] Reject [3] Skip");
                            String dec = sc.nextLine();
                            if (dec.equals("1")) {
                                a.status = Status.APPROVED;
                                myClub.members.add(a.username);
                            } else if (dec.equals("2"))
                                a.status = Status.REJECTED;
                        }
                    } else {
                        for (Announcement an : myClub.announcements) {
                            System.out.println(UI.YELLOW + "\nCATEGORY: " + an.title + UI.RESET);
                            for (AnnApplication a : an.applications) {
                                if (a.status != Status.PENDING)
                                    continue;
                                a.displaySummary();
                                UI.prompt("[1] Approve [2] Reject");
                                String dec = sc.nextLine();
                                if (dec.equals("1"))
                                    a.status = Status.APPROVED;
                                else if (dec.equals("2"))
                                    a.status = Status.REJECTED;
                            }
                        }
                    }
                }
                case 5: {
                    UI.header("MEMBERS");
                    System.out.println("Members: " + myClub.members);
                    UI.prompt("[1] Add [2] Remove");
                    String op = sc.nextLine();
                    UI.prompt("Usernames (comma separated)");
                    String[] users = sc.nextLine().split(",");
                    for (String u : users) {
                        if (op.equals("1"))
                            myClub.members.add(u.trim());
                        else
                            myClub.members.remove(u.trim());
                    }
                }
                case 6: {
                    UI.header("MY CLUB");
                    System.out.println("Name: " + myClub.name + "\nSkill: " + myClub.requiredSkill + "\nMembers: "
                            + myClub.members.size());
                }
            }
        } else if (current.role == Role.STUDENT) {
            switch (choice) {
                case 1: {
                    UI.header("JOIN CLUB");
                    for (int i = 0; i < clubData.getList().size(); i++)
                        UI.tableRow(String.valueOf(i), clubData.getList().get(i).name);
                    UI.prompt("Index");
                    int idx = Integer.parseInt(sc.nextLine());
                    Club c = clubData.getList().get(idx);
                    AnnApplication f = getStudentForm(sc, current.username, "Member");
                    f.displaySummary();
                    UI.prompt("Confirm? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) {
                        c.clubApplications.add(f);
                        UI.success("Applied!");
                    }
                }
                case 2: {
                    UI.header("REGISTRATION");
                    ArrayList<Announcement> all = new ArrayList<>();
                    for (Club c : clubData.getList()) {
                        for (Announcement a : c.announcements) {
                            all.add(a);
                            UI.tableRow(String.valueOf(all.size() - 1), a.title, c.name);
                        }
                    }
                    UI.prompt("Index");
                    int idx = Integer.parseInt(sc.nextLine());
                    Announcement t = all.get(idx);
                    AnnApplication f = getStudentForm(sc, current.username,
                            t.type == AnnouncementType.STAFF_CALLING ? "Staff" : "Part");
                    f.displaySummary();
                    UI.prompt("Confirm? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) {
                        t.applications.add(f);
                        UI.success("Done!");
                    }
                }
                case 4: {
                    UI.prompt("Club name to search");
                    String query = sc.nextLine().toLowerCase();
                    boolean found = false;
                    for (Club c : clubData.getList()) {
                        if (c.name.toLowerCase().contains(query)) {
                            found = true;
                            UI.header("CLUB PROFILE: " + c.name);
                            System.out.println(UI.BOLD + "Leader: " + UI.RESET + c.leader);
                            System.out.println(UI.BOLD + "Required Skill: " + UI.RESET + c.requiredSkill);
                            System.out.println(UI.BOLD + "Total Members: " + UI.RESET + c.members.size());
                            if (c.announcements.isEmpty())
                                System.out.println(UI.RED + "No active announcements." + UI.RESET);
                            else {
                                System.out.println(UI.PURPLE + "\n--- ACTIVE ANNOUNCEMENTS ---" + UI.RESET);
                                for (Announcement a : c.announcements)
                                    System.out.println("• " + a.title + " (" + a.type + ") on " + a.date + "\n  Desc: "
                                            + a.description);
                            }
                        }
                    }
                    if (!found)
                        UI.error("No clubs found.");
                }
                case 5: {
                    for (Club c : clubData.getList()) {
                        c.clubApplications.stream().filter(a -> a.username.equals(current.username))
                                .forEach(a -> System.out.println("Club [" + c.name + "]: " + a.status));
                        for (Announcement an : c.announcements)
                            an.applications.stream().filter(a -> a.username.equals(current.username))
                                    .forEach(a -> System.out.println("Announce [" + an.title + "]: " + a.status));
                    }
                }
            }
        }
    }

    private static AnnApplication getStudentForm(Scanner sc, String u, String pos) {
        UI.header("FILL FORM");
        UI.prompt("Name");
        String fn = sc.nextLine();
        UI.prompt("ID");
        String id = sc.nextLine();
        UI.prompt("Email");
        String em = sc.nextLine();
        UI.prompt("School");
        String scN = sc.nextLine();
        UI.prompt("Year");
        String yr = sc.nextLine();
        UI.prompt("Motivation");
        String mot = sc.nextLine();
        return new AnnApplication(u, fn, id, em, scN, yr, pos, mot);
    }

    private static void loadAllData(DataList<Club> clubData, ArrayList<Review> reviews) {
        try {
            File f = new File("data.txt");
            if (!f.exists())
                return;
            Scanner fsc = new Scanner(f);
            while (fsc.hasNextLine()) {
                String line = fsc.nextLine();
                if (line.equals("REVIEWS"))
                    break;
                if (!line.isEmpty())
                    clubData.getList().add(Club.fromFile(fsc, line));
            }
            while (fsc.hasNextLine()) {
                String line = fsc.nextLine();
                if (!line.trim().isEmpty())
                    reviews.add(Review.fromFile(line));
            }
        } catch (Exception e) {
        }
    }

    private static void saveAllData(DataList<Club> clubData, ArrayList<Review> reviews) {
        try (PrintWriter pw = new PrintWriter("data.txt")) {
            for (Club c : clubData.getList())
                pw.print(c.toFile());
            pw.println("REVIEWS");
            for (Review r : reviews)
                pw.println(r.toFile());
        } catch (Exception e) {
        }
    }
}
