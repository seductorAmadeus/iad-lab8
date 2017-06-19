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
public class DatabasePointsBean implements Serializable {
    private static Connection db = null;
    private String x = "0";
    private String y;
    private String r;

    public DatabasePointsBean() {
    }

    @PostConstruct
    public void init() {
        r = "3";
    } // initial radius value

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

    public void changeRadius(float x, float y, float r, boolean check) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE RESULTS SET R=(?), INSIDE=(?) WHERE X=(?) AND Y=(?)");
            statement.setFloat(1, r);
            statement.setInt(2, check ? 1 : 0);
            statement.setFloat(3, x);
            statement.setFloat(4, y);
            System.out.println("Sending db update: " + statement);
            statement.executeUpdate();
            statement.close();
            connection.close();
            db = null;
        } catch (SQLException var4) {
            System.err.println("Error: SQL exception " + var4.getMessage());
            var4.printStackTrace();
        }

    }

    public void addPoint(Point point) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO RESULTS VALUES(?,?,?,?)");
            statement.setFloat(1, point.getX());
            statement.setFloat(2, point.getY());
            statement.setFloat(3, point.getR());
            statement.setInt(4, point.getInside() ? 1 : 0);
            System.out.println("Sending db update: " + statement);
            statement.executeUpdate();
            statement.close();
            connection.close();
            db = null;
        } catch (SQLException var4) {
            System.err.println("Error: SQL exception " + var4.getMessage());
            var4.printStackTrace();
        }
    }

    public ArrayList<Point> getPoints() {
        ArrayList points = new ArrayList();

        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT X, Y, R, INSIDE FROM RESULTS");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Point point = new Point(rs.getFloat(1), rs.getFloat(2), rs.getFloat(3), rs.getInt(4) != 0);
                points.add(point);
            }
            statement.close();
            connection.close();
            db = null;
        } catch (SQLException var6) {
            System.err.println("Error: SQL exception" + var6.getMessage());
        }

        return points;
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

    public boolean check(double x, double y, double r) {
        return ((x <= 0) && (x >= -r / 2) && (y >= 0) && (y <= r)) ||
                ((x >= 0) && (y <= 0) && (y >= x / 2 - r / 2)) ||
                ((x >= 0) && (y >= 0) && (x * x + y * y <= r * r / 4));
    }

    public String addPoint() throws Exception {
        System.out.println("Request: " + this.x + " " + this.y + " " + this.r);

        try {
            float x = Float.parseFloat(this.x);
            float y = Float.parseFloat(this.y);
            float r = Float.parseFloat(this.r);
            Point point = new Point(x, y, r, this.check((double) x, (double) y, (double) r));
            this.addPoint(point);
        } catch (NumberFormatException var5) {
            ;
        } catch (Exception var6) {
            System.out.println("Oops!: " + var6);
        }

        return "main";
    }
}