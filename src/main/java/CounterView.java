import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class CounterView implements Serializable {

    private int number;

    public int getNumber() {
        return number;
    }

    public void increment() {
        number++;
    }
}