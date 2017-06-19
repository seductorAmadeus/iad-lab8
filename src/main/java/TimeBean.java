import java.util.Date;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(
        name = "timeProvider"
)
@ApplicationScoped
public class TimeBean {
    private String time = null;

    public TimeBean() {
    }

    public String getTime() {
        if (this.time == null) {
            this.update();
        }

        return this.time;
    }

    public void update() {
        this.time = (new Date()).toString();
    }
}
