import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import javax.json.*;
import java.io.*;

@ApplicationScoped
@ServerEndpoint("/echo")
public class EchoEndpoint {

    ResultsBean pointDao;

    @OnMessage
    public void onMessage(Session session, String msg) {
        String type;
        Double r;
        Result curPoint = new Result();
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject data = jsonReader.readObject();
        type = data.getString("type");
        r = data.getJsonNumber("rval").doubleValue();
        switch (type) {
            case "C": {
                JsonArray xvals, yvals;
                System.out.println("1212512512512");
                xvals = data.getJsonArray("xvals");
                yvals = data.getJsonArray("yvals");
                JsonArrayBuilder result = Json.createArrayBuilder();
                curPoint.setR(Float.parseFloat(r.toString()));
                for (int i = 0; i < xvals.size(); ++i) {
                    curPoint.setX(Float.parseFloat(String.valueOf((Double) xvals.getJsonNumber(i).doubleValue())));
                    curPoint.setY(Float.parseFloat(String.valueOf((Double) yvals.getJsonNumber(i).doubleValue())));
                    curPoint.setInside(curPoint.isInside());
                    result.add(curPoint.toString()); // возвращаем значение точки
                }
                JsonArray res = result.build();
                JsonObject retData = Json.createObjectBuilder().add("type", "C").add("points", res).add("first", data.getInt("begin")).build();
                try {
                    session.getBasicRemote().sendText(retData.toString());
                } catch (Throwable e) {

                }

                break;
            }
//            case "A": {
//                Double xval, yval;
//                xval = (Double) data.getJsonNumber("xval").doubleValue();
//                yval = (Double) data.getJsonNumber("yval").doubleValue();
//                curPoint.setR(r);
//                curPoint.setX(xval);
//                curPoint.setY(yval);
//                curPoint.checkIsEntry();
//                try {
//                    pointDao.addResult(curPoint);
//                } catch (Exception e) {
//
//                }
//                JsonObject retData = Json.createObjectBuilder().add("type", "A").add("point", (boolean) curPoint.getIsEntry()).add("r", (double) r).add("id", data.getInt("id")).build();
//                try {
//                    session.getBasicRemote().sendText(retData.toString());
//                } catch (Throwable e) {
//
//                }
//
//                break;
//            }
//            case "G": {
//                JsonArray xvals, yvals, points;
//                try {
//                    List<Point> pointList = pointDao.getPoints();
//                    JsonArrayBuilder xBuild = Json.createArrayBuilder();
//                    JsonArrayBuilder yBuild = Json.createArrayBuilder();
//                    JsonArrayBuilder pBuild = Json.createArrayBuilder();
//                    for (int i = 0; i < pointList.size(); ++i) {
//                        curPoint = pointList.get(i);
//                        curPoint.setR(r);
//                        curPoint.checkIsEntry();
//                        xBuild.add((double) curPoint.getX());
//                        yBuild.add((double) curPoint.getY());
//                        pBuild.add((boolean) curPoint.getIsEntry());
//                    }
//                    xvals = xBuild.build();
//                    yvals = yBuild.build();
//                    points = pBuild.build();
//                    JsonObject retData = Json.createObjectBuilder().add("type", "G").add("xvals", xvals).add("yvals", yvals).add("points", points).build();
//                    try {
//                        session.getBasicRemote().sendText(retData.toString());
//                    } catch (Throwable e) {
//
//                    }
//                } catch (Exception e) {
//
//                }
//
//                break;
//            }

        }
    }

    public void init() {
        pointDao = new ResultsBean();
    }

    @OnOpen
    public void onOpen(Session session) {
        init();
    }


}
