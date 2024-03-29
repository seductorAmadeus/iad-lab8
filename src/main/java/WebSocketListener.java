import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@ServerEndpoint("/echo")
public class WebSocketListener {

    DatabasePointsBean databasePointsBean;
    private String type;
    private Double r;

    @OnMessage
    public void onMessage(Session session, String message) {
        Point curPoint = new Point();
        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject data = jsonReader.readObject();
        type = data.getString("type");
        r = data.getJsonNumber("rval").doubleValue();
        switch (type) {
            case "C": {
                JsonArrayBuilder result = Json.createArrayBuilder();
                // изменяем значения радиусов в БД на указанное и возвращаем обновленный список вхождений;
                ArrayList<Boolean> listOfInsideInformation = databasePointsBean.changeRadius((float) (double) r);
                for (int i = 0; i <= listOfInsideInformation.size(); i++) {
                    result.add(listOfInsideInformation.get(i));
                }
                JsonArray res = result.build();
                JsonObject retData = Json.createObjectBuilder().add("type", "C").add("points", res).add("first", data.getInt("begin")).build();
                try {
                    session.getBasicRemote().sendText(retData.toString());
                } catch (Throwable e) {

                }

                break;
            }
            case "A": {
                Double xval, yval;
                xval = data.getJsonNumber("xval").doubleValue();
                yval = data.getJsonNumber("yval").doubleValue();
                curPoint.setR((float) (double) (r));
                curPoint.setX((float) (double) xval);
                curPoint.setY((float) (double) yval);
                curPoint.setInside(databasePointsBean.check((double) curPoint.getX(), (double) curPoint.getY(), (double) curPoint.getR()));
                try {
                    databasePointsBean.addPoint(curPoint);
                } catch (Exception e) {

                }
                JsonObject retData = Json.createObjectBuilder()
                        .add("type", "A")
                        .add("point", curPoint.getInside())
                        .add("r", r)
                        .add("id", data.getInt("id"))
                        .build();
                try {
                    session.getBasicRemote().sendText(retData.toString());
                } catch (Throwable e) {

                }

                break;
            }
            case "G": {
                JsonArray xvals, yvals, points;
                try {
                    List<Point> pointList = databasePointsBean.getPoints();
                    JsonArrayBuilder xBuild = Json.createArrayBuilder();
                    JsonArrayBuilder yBuild = Json.createArrayBuilder();
                    JsonArrayBuilder pBuild = Json.createArrayBuilder();
                    for (int i = 0; i < pointList.size(); ++i) {
                        curPoint = pointList.get(i);
                        curPoint.setX(Float.parseFloat(String.valueOf(pointList.get(i).getX())));
                        curPoint.setY(Float.parseFloat(String.valueOf(pointList.get(i).getY())));
                        curPoint.setR(Float.parseFloat(String.valueOf(pointList.get(i).getR())));
                        curPoint.setInside((pointList.get(i).getInside())); // добавляем старые данные???
                        xBuild.add(curPoint.getX());
                        yBuild.add(curPoint.getY());
                        pBuild.add(curPoint.getInside());
                    }
                    xvals = xBuild.build();
                    yvals = yBuild.build();
                    points = pBuild.build();
                    JsonObject retData = Json.createObjectBuilder().add("type", "G")
                            .add("xvals", xvals)
                            .add("yvals", yvals)
                            .add("points", points).build();
                    try {
                        session.getBasicRemote().sendText(retData.toString());
                    } catch (Throwable e) {

                    }
                } catch (Exception e) {

                }
                break;
            }
        }
    }

    public void init() {
        databasePointsBean = new DatabasePointsBean();
    }

    @OnOpen
    public void onOpen(Session session) {
        init();
    }


}
