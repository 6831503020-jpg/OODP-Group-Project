import java.util.ArrayList;

/**
 * CONCEPT: ENCAPSULATION
 * Why: Keeping the Announcement model separate ensures that changes to how 
 * events are structured won't accidentally break the User or Manager logic.
 */
public class Announcement {
    String title, date, description;
    AnnouncementType type;
    int capacity;
    ArrayList<AnnApplication> applications = new ArrayList<>();

    public Announcement(String t, String d, String desc, AnnouncementType type, int capacity) {
        this.title = t;
        this.date = d;
        this.description = desc;
        this.type = type;
        this.capacity = capacity;
    }

    public String toFile() {
        String cleanDesc = description.replace("\n", "[BR]");
        StringBuilder sb = new StringBuilder();
        for (AnnApplication app : applications)
            sb.append(app.toFile()).append(",");
        String appStr = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "NONE";
        return title + "|" + date + "|" + cleanDesc + "|" + type + "|" + capacity + "|" + appStr;
    }

    public static Announcement fromFile(String line) {
        String[] p = line.split("\\|");
        Announcement a = new Announcement(p[0], p[1], p[2].replace("[BR]", "\n"), 
                                        AnnouncementType.valueOf(p[3]),
                                        Integer.parseInt(p[4]));
        if (p.length > 5 && !p[5].equals("NONE")) {
            for (String s : p[5].split(","))
                a.applications.add(AnnApplication.fromFile(s));
        }
        return a;
    }
}