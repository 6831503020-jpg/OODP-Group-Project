// AnnApplication.java

/**
 * CONCEPT: DATA TRANSFER OBJECT (DTO)
 * Why: This class only cares about carrying data between the Student 
 * and the Club Leader, keeping the communication protocol clean.
 */

public class AnnApplication {
    String username, fullName, studentId, email, school, year, position, motivation;
    Status status = Status.PENDING;

    public AnnApplication(String un, String fn, String id, String em, String sc, String yr, String pos, String mot) {
        this.username = un; this.fullName = fn; this.studentId = id; this.email = em;
        this.school = sc; this.year = yr; this.position = pos; this.motivation = mot;
    }

    public void displaySummary() {
        System.out.println(UI.PURPLE + "--- APPLICATION DETAILS ---" + UI.RESET);
        System.out.println("Name: " + fullName + " (" + username + ")\nID: " + studentId + " | Year: " + year);
        System.out.println("School: " + school + " | Email: " + email + "\nPosition: " + position + " | Motivation: " + motivation);
        System.out.println("---------------------------");
    }

    public String toFile() {
        return username + "!" + fullName + "!" + studentId + "!" + email + "!" + school + "!" + year + "!" + position + "!" + motivation + "!" + status;
    }

    public static AnnApplication fromFile(String raw) {
        String[] p = raw.split("!");
        AnnApplication a = new AnnApplication(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
        a.status = Status.valueOf(p[8]);
        return a;
    }
}






