import java.util.*;
import java.io.*;

public class MainSystem {
    public static void main(String[] args) {
        UI.clearScreen();
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
                UI.success("Goodbye!. See you again......");
                System.exit(0);
            } else if (startChoice.equals("2")) {
                um.register(sc);
                continue;
            } else if (startChoice.equals("1")) {
                User current = um.login(sc);
                if (current == null) {
                    UI.error("Invalid credentials.");
                    continue;
                }

                boolean loggedIn = true;
                while (loggedIn) {
                    System.out.println("--------------------------------------------------------------");
                    System.out.println("\n" + UI.BOLD + UI.PURPLE + "── SESSION: " + current.username + " (" + current.role + ") ──" + UI.RESET);
                    displayMenu(current);
                    UI.prompt("Action");
                    
                    int choice = getValidatedInt(sc, 0, 9);
                    
                    if (choice == -1) continue; 
                    if (choice == 0) System.exit(0);
                    if (choice == 9) { loggedIn = false; break; }

                    handleChoice(current, choice, clubData, reviews, sc);
                    saveAllData(clubData, reviews);
                }
            } else {
                UI.error("Invalid input. Input must only be 0, 1, or 2.");
            }
        }
    }

    private static int getValidatedInt(Scanner sc, int min, int max) {
        try {
            String input = sc.nextLine();
            int val = Integer.parseInt(input);
            if (val < min || val > max) {
                UI.error("Invalid input. Input must only be " + min + "-" + max);
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            UI.error("Invalid input. Please enter a valid number.");
            return -1;
        }
    }

    private static void displayMenu(User current) {
        if (current.role == Role.ADMIN)
            System.out.println("[1] Create Club [2] View Clubs \n[3] Delete Club [4] Dashboard");
        else if (current.role == Role.LEADER)
            System.out.println("[1] Post Announcement [2] View Announcements \n[3] Review Applications [4] Manage Members [5] My Club");
        else
            System.out.println("[1] View & Apply Club [2] View & Apply Announcement \n[3] Write Review [4] Search Club [5] Track Apps");
        System.out.println(UI.RED + "[9] Logout [0] Exit" + UI.RESET);
    }

    private static void handleChoice(User current, int choice, DataList<Club> clubData, ArrayList<Review> reviews, Scanner sc) {
        if (current.role == Role.ADMIN) {
            switch (choice) {
                case 1: {
                    UI.clearScreen();
                    UI.header("CREATE CLUB");
                    UI.prompt("Club name"); String n = sc.nextLine();
                    UI.prompt("Required Skill"); String s = sc.nextLine();
                    UI.prompt("Leader Username"); String l = sc.nextLine();
                    clubData.getList().add(new Club(n, l, s));
                    UI.success("Club successfully created.");
                    break;
                }
                case 2: {
                    UI.clearScreen();
                    UI.header("VIEW CLUBS");
                    for (Club c : clubData.getList()) {
                        UI.header("CLUB: " + c.name);
                        System.out.println("Leader: " + c.leader + " | Skill: " + c.requiredSkill);
                    }
                    break;
                }
                case 3: {
                    UI.clearScreen();
                    UI.header("DELETE CLUB");
                    UI.prompt("Club name to delete");
                    String n = sc.nextLine();
                    UI.warning("Delete " + n + "? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) {
                        clubData.getList().removeIf(c -> c.name.equalsIgnoreCase(n));
                        UI.success("Club " + n + " Deleted.");
                    }
                    break;
                }
                case 4: {
                    UI.clearScreen();
                    UI.header("SYSTEM DASHBOARD");
                    System.out.println("Total Clubs: " + clubData.getList().size() + "\nTotal Reviews: " + reviews.size());
                    break;
                }
                default: UI.error("Invalid input. Input must only be 1-4");
            }
        } else if (current.role == Role.LEADER) {
            Club myClub = null;
            for (Club c : clubData.getList()) if (c.leader.equals(current.username)) myClub = c;
            if (myClub == null) { UI.error("No club found."); return; }

            switch (choice) {
                case 1: {
                    UI.header("CREATE ANNOUNCEMENT");
                    UI.prompt("Title"); String t = sc.nextLine();
                    UI.prompt("Type [1] Event [2] Staff");
                    int ty = getValidatedInt(sc, 1, 2);
                    if (ty == -1) break;
                    UI.prompt("Date"); String d = sc.nextLine();
                    UI.prompt("Description"); String desc = sc.nextLine();
                    UI.prompt("Capacity");
                    int cap = getValidatedInt(sc, 1, 1000);
                    if (cap == -1) break;
                    myClub.announcements.add(new Announcement(t, d, desc, ty == 1 ? AnnouncementType.EVENT : AnnouncementType.STAFF_CALLING, cap));
                    UI.success("Announcement Posted.");
                    break;
                }
                case 2: {
                    UI.header("MY ANNOUNCEMENTS");
                    UI.tableHeader("Index", "Title", "Applicants/Cap", "Type");
                    for (int i = 0; i < myClub.announcements.size(); i++) {
                        Announcement a = myClub.announcements.get(i);
                        UI.tableRow(String.valueOf(i), a.title, a.applications.size() + "/" + a.capacity, a.type.toString());
                    }
                    break;
                }
                case 3: {
                    UI.header("REVIEW APPLICATIONS");
                    System.out.println("[1] Club Memberships [2] Announcements");
                    int dec = getValidatedInt(sc, 1, 2);
                    if (dec == 1) {
                        for (AnnApplication a : myClub.clubApplications) {
                            if (a.status != Status.PENDING) continue;
                            a.displaySummary();
                            UI.prompt("[1] Approve [2] Reject [3] Skip");
                            int res = getValidatedInt(sc, 1, 3);
                            if (res == 1) { a.status = Status.APPROVED; myClub.members.add(a.username); }
                            else if (res == 2) a.status = Status.REJECTED;
                        }
                    }
                    break;
                }
                case 4: {
                    UI.header("MEMBERS");
                    System.out.println("Members: " + myClub.members);
                    UI.prompt("[1] Add [2] Remove");
                    int op = getValidatedInt(sc, 1, 2);
                    UI.prompt("Username");
                    String u = sc.nextLine();
                    if (op == 1) myClub.members.add(u.trim());
                    else myClub.members.remove(u.trim());
                    break;
                }
                case 5: {
                    UI.header("MY CLUB");
                    System.out.println("Name: " + myClub.name + "\nSkill: " + myClub.requiredSkill + "\nMembers: " + myClub.members.size());
                    break;
                }
                default: UI.error("Invalid input. Input must only be 1-5");
            }
        } else if (current.role == Role.STUDENT) {
            switch (choice) {
                case 1: {
                    UI.header("JOIN CLUB");
                    int size = clubData.getList().size();
                    for (int i = 0; i < size; i++) UI.tableRow(String.valueOf(i + 1), clubData.getList().get(i).name);
                    UI.prompt("Enter Index (0 to back)");
                    int idx = getValidatedInt(sc, 0, size);
                    if (idx <= 0) break;
                    Club c = clubData.getList().get(idx - 1);
                    AnnApplication f = getStudentForm(sc, current.username, "Member");
                    UI.prompt("Confirm? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) { c.clubApplications.add(f); UI.success("Applied!"); }
                    break;
                }
                case 2: {
                    UI.header("REGISTRATION");
                    ArrayList<Announcement> all = new ArrayList<>();
                    for (Club c : clubData.getList()) {
                        for (Announcement a : c.announcements) {
                            all.add(a);
                            UI.tableRow(String.valueOf(all.size()), a.title, c.name);
                        }
                    }
                    UI.prompt("Enter Index (0 to back)");
                    int idx = getValidatedInt(sc, 0, all.size());
                    if (idx <= 0) break;
                    Announcement t = all.get(idx - 1);
                    AnnApplication f = getStudentForm(sc, current.username, t.type == AnnouncementType.STAFF_CALLING ? "Staff" : "Part");
                    UI.prompt("Confirm? (y/n)");
                    if (sc.nextLine().equalsIgnoreCase("y")) { t.applications.add(f); UI.success("Registered!"); }
                    break;
                }
                case 3: {
                    UI.header("WRITE A REVIEW");
                    UI.prompt("Club/Event Name"); String target = sc.nextLine();
                    UI.prompt("Rating (1-5)");
                    int r = getValidatedInt(sc, 1, 5);
                    if (r == -1) break;
                    UI.prompt("Comment"); String com = sc.nextLine();
                    reviews.add(new Review(target, current.username, r, com));
                    UI.success("Review submitted!");
                    break;
                }
                case 4: {
                    UI.header("SEARCH CLUB");
                    UI.prompt("Name"); String q = sc.nextLine().toLowerCase();
                    for (Club c : clubData.getList()) if (c.name.toLowerCase().contains(q)) UI.header("Found: " + c.name);
                    break;
                }
                case 5: {
                    UI.header("TRACK APPS");
                    for (Club c : clubData.getList()) {
                        c.clubApplications.stream().filter(a -> a.username.equals(current.username))
                                .forEach(a -> System.out.println("Club [" + c.name + "]: " + a.status));
                    }
                    break;
                }
                default: UI.error("Invalid input. Input must only be 1-5");
            }
        }
    }

    private static AnnApplication getStudentForm(Scanner sc, String u, String pos) {
        UI.header("FILL FORM");
        UI.prompt("Full Name"); String fn = sc.nextLine();
        UI.prompt("Student ID"); String id = sc.nextLine();
        UI.prompt("Email"); String em = sc.nextLine();
        UI.prompt("School"); String scN = sc.nextLine();
        UI.prompt("Year"); String yr = sc.nextLine();
        UI.prompt("Motivation"); String mot = sc.nextLine();
        return new AnnApplication(u, fn, id, em, scN, yr, pos, mot);
    }

    private static void loadAllData(DataList<Club> clubData, ArrayList<Review> reviews) {
        try {
            File f = new File("data.txt");
            if (!f.exists()) return;
            Scanner fsc = new Scanner(f);
            while (fsc.hasNextLine()) {
                String line = fsc.nextLine();
                if (line.equals("REVIEWS")) break;
                if (!line.isEmpty()) clubData.getList().add(Club.fromFile(fsc, line));
            }
            while (fsc.hasNextLine()) {
                String line = fsc.nextLine();
                if (!line.trim().isEmpty()) reviews.add(Review.fromFile(line));
            }
            fsc.close();
        } catch (Exception e) {}
    }

    private static void saveAllData(DataList<Club> clubData, ArrayList<Review> reviews) {
        try (PrintWriter pw = new PrintWriter("data.txt")) {
            for (Club c : clubData.getList()) pw.print(c.toFile());
            pw.println("REVIEWS");
            for (Review r : reviews) pw.println(r.toFile());
        } catch (Exception e) {}
    }
}