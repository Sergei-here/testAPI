import java.util.ArrayList;
import java.util.List;

public class Student {

    public Student() {
    }

    public Student(Integer id, String name, List<Integer> marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }

    Integer id;
    String name;
    List<Integer> marks = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getMarks() {
        return marks;
    }

    public void setMarks(List<Integer> marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", marks=" + marks +
                '}';
    }
}
