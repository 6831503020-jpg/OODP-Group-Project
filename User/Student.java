// Student.java
public class Student extends User {
    public Student(String u, String p, String e, String s) { super(u, Utils.hash(p), Role.STUDENT, e, s); }
}