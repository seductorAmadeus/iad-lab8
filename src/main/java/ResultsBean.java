import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(
        name = "data",
        eager = true
)
@ApplicationScoped
public class ResultsBean implements Serializable {
    private static Connection db = null;
    private String x = "0";
    private String y;
    private String r;

    public ResultsBean() {
        System.out.println("Heya! New ResultsBean!");
    }

    @PostConstruct
    public void init() {
        r = "3";
    }

    public String testBean() {
        return "Привет всем!";
    }

    private Connection getConnection() {
        if (db != null) {
            return db;
        } else {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                String URL = "jdbc:oracle:thin:@localhost:1521:xe";
                String USER = "martin";
                String PASS = "0797";
                db = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Got connection");
            } catch (Exception var4) {
                System.err.println("Error at DB connect: " + var4.getMessage());
                var4.printStackTrace();
            }

            return db;
        }
    }

    public void addResult(Result result) {
        System.out.println("Trying to add result [" + result + "]");

        try {
            Connection conn = this.getConnection();
            PreparedStatement st = conn.prepareStatement("INSERT INTO RESULTS VALUES(?,?,?,?)");
            st.setFloat(1, result.getX());
            st.setFloat(2, result.getY());
            st.setFloat(3, result.getR());
            st.setInt(4, result.isInside() ? 1 : 0);
            System.out.println("Sending db update: " + st);
            st.executeUpdate();
        } catch (SQLException var4) {
            System.err.println("Error: SQL exception " + var4.getMessage());
            var4.printStackTrace();
        }

    }

    public ArrayList<Result> getResults() {
        ArrayList results = new ArrayList();

        try {
            Connection conn = this.getConnection();
            PreparedStatement st = conn.prepareStatement("select X, Y, R, INSIDE from RESULTS");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Result result = new Result(rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getInt(4) != 0);
                results.add(result);
            }
        } catch (SQLException var6) {
            System.err.println("Error: SQL exception" + var6.getMessage());
        }

        return results;
    }

    public String getX() {
        return this.x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return this.y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getR() {
        return this.r;
    }

    public void setR(String r) {
        this.r = r;
    }

    private boolean check(double x, double y, double r) {
        return x > 0.0D ? y <= 0.0D && -x + 2.0D * y + r >= 0.0D : (y >= 0.0D ? y <= r && x >= -r / 2.0D : x * x + y * y <= r * r);
    }

    public String action() throws Exception {
        System.out.println("Request: " + this.x + " " + this.y + " " + this.r);

        try {
            float x = Float.parseFloat(this.x);
            float y = Float.parseFloat(this.y);
            float r = Float.parseFloat(this.r);
            x /= 100.0F;
            Result result = new Result(x, y, r, this.check((double) x, (double) y, (double) r));
            this.addResult(result);
        } catch (NumberFormatException var5) {
            ;
        } catch (Exception var6) {
            System.out.println("Oops!: " + var6);
        }

        return "main";
    }
}
