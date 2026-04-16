// DataList.java (Parametric Polymorphism)
import java.util.ArrayList;
public class DataList<T> {
    private ArrayList<T> list = new ArrayList<>();
    public void add(T item) { list.add(item); }
    public ArrayList<T> getList() { return list; }
}