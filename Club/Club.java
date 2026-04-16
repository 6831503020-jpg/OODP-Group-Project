// Club.java
import java.util.*;

/**
 * CONCEPT: COMPOSITION
 * Why: A Club contains Announcements and Members. By giving Club its own file, 
 * we can manage complex "one-to-many" relationships without cluttering the main logic.
 */

public class Club {
    String name, leader, requiredSkill;
    ArrayList<Announcement> announcements = new ArrayList<>();
    ArrayList<String> members = new ArrayList<>();
    ArrayList<AnnApplication> clubApplications = new ArrayList<>();

    public Club(String n, String l, String s) { name = n; leader = l; requiredSkill = s; }

    public String toFile() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("|").append(leader).append("|").append(requiredSkill).append("\n");
        sb.append("MEMBERS|").append(members.isEmpty() ? "NONE" : String.join(",", members)).append("\n");
        for (AnnApplication app : clubApplications) sb.append("CAPP|").append(app.toFile()).append("\n");
        for (Announcement a : announcements) sb.append("AN|").append(a.toFile()).append("\n");
        sb.append("END\n");
        return sb.toString();
    }

    public static Club fromFile(Scanner sc, String firstLine) {
        String[] p = firstLine.split("\\|");
        Club c = new Club(p[0], p[1], p[2]);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("END")) break;
            if (line.startsWith("MEMBERS|")) {
                String mData = line.substring(8);
                if (!mData.equals("NONE")) for (String m : mData.split(",")) c.members.add(m.trim());
            } else if (line.startsWith("CAPP|")) {
                c.clubApplications.add(AnnApplication.fromFile(line.substring(5)));
            } else if (line.startsWith("AN|")) {
                c.announcements.add(Announcement.fromFile(line.substring(3)));
            }
        }
        return c;
    }
}