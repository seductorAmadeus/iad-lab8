import java.util.Date;
import javax.faces.bean.ManagedBean;

@ManagedBean(
        name = "timeProvider"
)
public class TimeProvider {
    private String value = null;

    public TimeProvider() {
    }

    public String getValue() {
        if (this.value == null) {
            this.update();
        }

        return this.value;
    }

    public void update() {
        this.value = (new Date()).toString();
    }
}
