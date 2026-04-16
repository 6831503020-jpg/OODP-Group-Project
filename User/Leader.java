// Leader.java
public class Leader extends User {
    public Leader(String u, String p, String e, String s) { super(u, Utils.hash(p), Role.LEADER, e, s); }
}