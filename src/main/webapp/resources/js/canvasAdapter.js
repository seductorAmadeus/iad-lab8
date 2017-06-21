var divCanvas, canvasGraph, rInput, canvasPoints;
var x_center, y_center, x_transform, y_transform, r, canvasHeight, canvasWidth;
var xVals, yVals;

function init() {
    xVals = [];
    yVals = [];
    divCanvas = document.getElementById('canvasContainer');
    canvasGraph = document.getElementById("graph");
    canvasPoints = document.getElementById("graph");
    window.addEventListener("resize", onResize);
    window.addEventListener("load", updateGraph());
    Socket.send(JSON.stringify({
            type: "G",
            rval: parseFloat(3)
        })
    );
    resizeWithoutRadius();
    // onResize();
}

function updateGraph() {
    Socket.send(JSON.stringify({
            type: "G",
            rval: parseFloat(r)
        })
    );
}

function resizeWithoutRadius() {
    canvasWidth = divCanvas.clientWidth;
    widthMod = canvasWidth % 8;
    if (widthMod != 0) {
        canvasWidth -= widthMod;
    }
    canvasHeight = divCanvas.clientHeight;
    heightMod = canvasHeight % 8;
    if (heightMod != 0) {
        canvasHeight -= heightMod;
    }
    x_center = Math.floor(canvasWidth / 2);
    y_center = Math.floor(canvasHeight / 2);
    x_transform = Math.floor(3 * (canvasWidth - 32) / 8);
    y_transform = Math.floor(3 * (canvasHeight - 32) / 8);
    canvasGraph.width = canvasWidth;
    canvasGraph.height = canvasHeight;

    /* canvas fill block */
    context = canvasGraph.getContext("2d");
    drawShapes(context, 3);
    drawCoordinates(context);
    /* end of canvas fill block */

    data = JSON.stringify({
        type: "C",
        xvals: xVals,
        yvals: yVals,
        begin: 0,
        rval: parseFloat(3)
    });
    console.log(data);
    Socket.send(data);
    //updateGraph(); // set points
    // if (xVals.length > 0) {
    //     sendPoints(xVals, yVals, 0);
    // }
    updateGraph()
}

function onResize() {
    r = document.getElementById("myForm:valueR").value;

    canvasWidth = divCanvas.clientWidth;
    widthMod = canvasWidth % 8;
    if (widthMod != 0) {
        canvasWidth -= widthMod;
    }
    canvasHeight = divCanvas.clientHeight;
    heightMod = canvasHeight % 8;
    if (heightMod != 0) {
        canvasHeight -= heightMod;
    }
    x_center = Math.floor(canvasWidth / 2);
    y_center = Math.floor(canvasHeight / 2);
    x_transform = Math.floor(r * (canvasWidth - 32) / 8);
    y_transform = Math.floor(r * (canvasHeight - 32) / 8);
    canvasGraph.width = canvasWidth;
    canvasGraph.height = canvasHeight;
    canvasFill();
    //updateGraph(); // set points
    if (xVals.length > 0) {
        sendPoints(xVals, yVals, 0);
    }
    updateGraph()
}

function deletePoints() {
    Socket.send(JSON.stringify({
        type: "D",
        rval: parseFloat(r)
    }));
}

function saveX(value) {
    document.getElementById("myForm:valueX").value = value;
}

function saveR(value) {
    document.getElementById("myForm:valueR").value = value;
    onResize();
}

function canvasFill() {
    context = canvasGraph.getContext("2d");
    if (r > 0) {
        drawShapes(context);
    }
    drawCoordinates(context);
}

function setPoint(event) {
    rect = canvasGraph.getBoundingClientRect();
    offset = (rect.width - canvasGraph.width) / 2 + 1;
    x = event.clientX - rect.left - offset;
    y = event.clientY - rect.top - offset;
    real_x = r * (x - x_center) / x_transform;
    real_y = -r * (y - y_center) / y_transform;
    xVals.push(real_x);
    yVals.push(real_y);
    sendPoint(real_x, real_y, xVals.length - 1);
    updateGraph();
}

function sendPoints(xargs, yargs, first) {
    data = JSON.stringify({
        type: "C",
        xvals: xargs,
        yvals: yargs,
        begin: first,
        rval: parseFloat(r)
    });
    console.log(data);
    Socket.send(data);
}

function sendPoint(xarg, yarg, id) {
    data = JSON.stringify({
        type: "A",
        xval: xarg,
        yval: yarg,
        id: id,
        rval: parseFloat(r)
    });
    console.log(data);
    Socket.send(data);
}


function drawPoint(context, real_x, real_y, isInside) {
    x = real_x / r * x_transform + x_center;
    y = real_y / -r * y_transform + y_center;
    context.beginPath();
    if (isInside) {
        context.fillStyle = "Green";
    } else {
        context.fillStyle = "Red";
    }
    context.arc(x, y, 3, 0 * Math.PI, 2 * Math.PI);
    context.fill();
}

function drawCoordinates(context) {
    context.beginPath();
    /*Draw coordianates*/
    context.moveTo(x_center, canvasHeight);
    context.lineTo(x_center, 0);
    context.lineTo(x_center + 5, 5);
    context.moveTo(x_center, 0);
    context.lineTo(x_center - 5, 5);
    context.moveTo(0, y_center);
    context.lineTo(canvasWidth, y_center);
    context.lineTo(canvasWidth - 5, y_center + 5);
    context.moveTo(canvasWidth, y_center);
    context.lineTo(canvasWidth - 5, y_center - 5);
    if (r > 0) {
        x_offset = x_center - x_transform;
        y_offset = y_center - y_transform;
        /*Draw measures*/
        for (i = 0; i < 5; ++i) {
            context.moveTo(x_center - 5, y_offset);
            context.lineTo(x_center + 5, y_offset);
            context.moveTo(x_offset, y_center - 5);
            context.lineTo(x_offset, y_center + 5);
            x_offset += x_transform / 2;
            y_offset += y_transform / 2;
        }
    }
    context.strokeStyle = "black";
    context.stroke();
    /*Draw coordinates text*/
    context.font = "16px Georgia";
    context.textBaseline = "top";
    context.textAlign = "left";
    context.fillStyle = "black";
    context.fillText("Y", x_center + 10, 0);
    context.textAlign = "right";
    context.textBaseline = "bottom";
    context.fillText("X", canvasWidth, y_center - 10);
}

function drawShapes(context) {
    if (arguments.length === 2) {
        alert("2 аргумента: " + arguments[1]);
        r = "3";
    }
    context.beginPath();
    drawTriangle(context, x_center - 1, y_center - 1);
    switch (r) {
        case "1":
            context.rect(x_center - y_transform + 6, y_center - y_transform, y_transform - 6, x_transform - 22);
            drawEllipse(context, x_center + 1, y_center + 1, x_transform - 28, y_transform - 14);
            break;
        case "1.5":
            context.rect(x_center - y_transform + 9, y_center - y_transform, y_transform - 9, x_transform - 32);
            drawEllipse(context, x_center + 1, y_center + 1, x_transform - 41, y_transform - 24);
            break;
        case "2":
            context.rect(x_center - y_transform + 12, y_center - y_transform, y_transform - 12, x_transform - 45);
            drawEllipse(context, x_center + 1, y_center + 1, x_transform - 56, y_transform - 33);
            break;
        case "2.5":
            context.rect(x_center - y_transform + 15, y_center - y_transform, y_transform - 15, x_transform - 55);
            drawEllipse(context, x_center + 1, y_center + 1, x_transform - 70, y_transform - 40);
            break;
        case "3":
            context.rect(x_center - y_transform + 19, y_center - y_transform, y_transform - 19, x_transform - 65);
            drawEllipse(context, x_center + 1, y_center + 1, x_transform - 85, y_transform - 50);
            break;
    }
    context.closePath();
    context.fillStyle = "#5c99ED";
    context.fill();
}

function drawEllipse(context, x, y, a, b) {
    context.save();
    context.translate(x, y); // The translate(x,y) method remaps the (0,0) position on the canvas.
    context.scale(a / b, 1); // изменяет масштаб фигуры
    context.arc(0, 0, b, 1.5 * Math.PI, 0);
    context.lineTo(0, 0);
    context.restore();
}

function drawTriangle(context, x, y) {
    context.save();
    context.translate(x, y);
    context.moveTo(0, 0);
    context.lineTo(0, y_transform / 2);
    context.lineTo(x_transform, 0);
    context.lineTo(0, 0);
    context.restore();
}
