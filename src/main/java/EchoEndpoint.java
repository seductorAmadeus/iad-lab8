import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import javax.json.*;
import java.io.*;
import java.util.List;

@ApplicationScoped
@ServerEndpoint("/echo")
public class EchoEndpoint {

    ResultsBean resultsBean;

    @OnMessage
    public void onMessage(Session session, String msg) {
        String type;
        Double r;
        Point curPoint = new Point();
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject data = jsonReader.readObject();
        type = data.getString("type");
        r = data.getJsonNumber("rval").doubleValue();
        switch (type) {
            case "I": {
                break;
            }
            case "C": {
                JsonArray xvals, yvals;
                xvals = data.getJsonArray("xvals");
                yvals = data.getJsonArray("yvals");
                JsonArrayBuilder result = Json.createArrayBuilder();
                curPoint.setR(Float.parseFloat(r.toString()));
                for (int i = 0; i < xvals.size(); ++i) {
                    curPoint.setX(Float.parseFloat(String.valueOf((Double) xvals.getJsonNumber(i).doubleValue())));
                    curPoint.setY(Float.parseFloat(String.valueOf((Double) yvals.getJsonNumber(i).doubleValue())));
                    curPoint.setInside(resultsBean.check(curPoint.getX(),curPoint.getY(), curPoint.getR()));
                    resultsBean.changeRadius(curPoint.getX(), curPoint.getY(), curPoint.getR(), curPoint.isInside());
                    result.add(curPoint.isInside());
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
                curPoint.setInside(resultsBean.check((double) curPoint.getX(), (double) curPoint.getY(), (double) curPoint.getR()));
                try {
                    resultsBean.addResult(curPoint);
                } catch (Exception e) {

                }
                JsonObject retData = Json.createObjectBuilder()
                        .add("type", "A")
                        .add("point", curPoint.isInside())
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
                    List<Point> pointList = resultsBean.getResults();
                    JsonArrayBuilder xBuild = Json.createArrayBuilder();
                    JsonArrayBuilder yBuild = Json.createArrayBuilder();
                    JsonArrayBuilder pBuild = Json.createArrayBuilder();
                    for (int i = 0; i < pointList.size(); ++i) {
                        curPoint = pointList.get(i);
                        curPoint.setX(Float.parseFloat(String.valueOf(pointList.get(i).getX())));
                        curPoint.setY(Float.parseFloat(String.valueOf(pointList.get(i).getY())));
                        curPoint.setR(Float.parseFloat(String.valueOf(pointList.get(i).getR())));
                        curPoint.setInside((pointList.get(i).isInside())); // добавляем старые данные???
                        xBuild.add(curPoint.getX());
                        yBuild.add(curPoint.getY());
                        pBuild.add(curPoint.isInside());
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


//                JsonArray xvals, yvals, points;
//                try {
//                    List<Point> pointList = resultsBean.getResults();
//                    JsonArrayBuilder xBuild = Json.createArrayBuilder();
//                    JsonArrayBuilder yBuild = Json.createArrayBuilder();
//                    JsonArrayBuilder pBuild = Json.createArrayBuilder();
//                    for (int i = 0; i < pointList.size(); ++i) {
//                        curPoint = pointList.get(i);
//                        curPoint.setX(Float.parseFloat(String.valueOf(pointList.get(i).getX())));
//                        curPoint.setY(Float.parseFloat(String.valueOf(pointList.get(i).getY())));
//                        curPoint.setR(Float.parseFloat(String.valueOf(pointList.get(i).getR())));
//                        curPoint.setInside((pointList.get(i).isInside())); // добавляем старые данные???
//                        xBuild.add(curPoint.getX());
//                        yBuild.add(curPoint.getY());
//                        pBuild.add(curPoint.isInside());
//                    }
//                    xvals = xBuild.build();
//                    yvals = yBuild.build();
//                    points = pBuild.build();
//                    JsonObject retData = Json.createObjectBuilder().add("type", "G")
//                            .add("xvals", xvals)
//                            .add("yvals", yvals)
//                            .add("points", points).build();
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
            }

        }
    }

    public void init() {
        resultsBean = new ResultsBean();
    }

    @OnOpen
    public void onOpen(Session session) {
        init();
    }


}
