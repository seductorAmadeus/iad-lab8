Socket = new WebSocket("ws://localhost:60233/lab8/echo");

Socket.onopen = function () {
    console.log("Connected");
    Socket.send(JSON.stringify({
            type: "G",
            rval: parseFloat(r)
        })
    );
};

Socket.onmessage = function (event) {
    console.log("HERE " + event.data);
    data = JSON.parse(event.data);
    switch (data.type) {
        case "C":
            console.log(data);
            context = canvasPoints.getContext("2d");
            first = parseInt(data.first);
            for (i in data.points) {
                pointer = parseInt(i) + first;
                drawPoint(context, xVals[pointer], yVals[pointer], data.points[i]);
            }
            break;

        case "A":
            console.log(data);
            body = document.getElementById("dataTable").getElementsByTagName("tbody")[0];
            id = parseInt(data.id);
            new_tr = document.createElement("tr");
            x_td = document.createElement("td");
            x_td.innerHTML = Math.round(xVals[id] * 100) / 100;

            y_td = document.createElement("td");
            y_td.innerHTML = Math.round(yVals[id] * 100) / 100;

            r_td = document.createElement("td");
            r_td.innerHTML = data.r;

            check_td = document.createElement("td");
            check_td.innerHTML = data.point;
            new_tr.appendChild(x_td);
            new_tr.appendChild(y_td);
            new_tr.appendChild(r_td);
            new_tr.appendChild(check_td);
            body.appendChild(new_tr);
            if (data.r == r) {
                drawPoint(canvasPoints.getContext("2d"), xVals[id], yVals[id], data.point);
            }
            break;

        case "G":
            console.log(data);
            xVals = data.xvals;
            yVals = data.yvals;
            for (i in data.points) {
                pointer = parseInt(i);
                drawPoint(canvasPoints.getContext("2d"), xVals[pointer], yVals[pointer], data.points[i]);
            }
            break;

        case "D":
            location.reload();
    }
};
Socket.onclose = function () {
    console.log('Connection Lost');
};