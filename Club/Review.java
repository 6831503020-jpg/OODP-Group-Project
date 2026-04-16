// Review.java

/**
 * CONCEPT: MODULAR STORAGE
 * Why: Separating Review allows the system to load feedback independently 
 * of club data, optimizing memory usage.
 */

public class Review {
    String event, student, comment;
    int rating;

    public Review(String e, String s, int r, String c) {
        event = e; student = s; rating = r; comment = c;
    }

    public String toFile() { return event + "|" + student + "|" + rating + "|" + comment; }

    public static Review fromFile(String line) {
        String[] p = line.split("\\|");
        return new Review(p[0], p[1], Integer.parseInt(p[2]), p[3]);
    }
}