// UI.java
public class UI {
    public static final String RESET = "\u001B[0m", CYAN = "\u001B[36m", YELLOW = "\u001B[33m",
            GREEN = "\u001B[32m", RED = "\u001B[31m", PURPLE = "\u001B[35m", BOLD = "\u001B[1m";

    public static void line() {
        System.out.println(CYAN + "========================================================" + RESET);
    }

    public static void header(String title) {
        System.out.println();
        line();
        System.out.printf(BOLD + CYAN + "║ %-52s ║\n" + RESET, title.toUpperCase());
        line();
    }

    public static void tableHeader(String... cols) {
        System.out.print(BOLD + YELLOW + "│");
        for (String c : cols) System.out.printf(" %-15s │", c);
        System.out.println(RESET + "\n├──────────────────────────────────────────────────────┤");
    }

    public static void tableRow(String... data) {
        System.out.print("│");
        for (String d : data) {
            String val = (d != null && d.length() > 15) ? d.substring(0, 12) + "..." : (d == null ? "N/A" : d);
            System.out.printf(" %-15s │", val);
        }
        System.out.println();
    }

    public static void prompt(String text) {
        System.out.print(BOLD + YELLOW + "➤ " + text + ": " + RESET);
    }

    public static void success(String msg) {
        System.out.println(GREEN + "✔ " + msg + RESET);
    }

    public static void error(String msg) {
        System.out.println(RED + "✘ " + msg + RESET);
    }

    public static void warning(String msg) {
        System.out.println(RED + "⚠ " + msg + RESET);
    }
}