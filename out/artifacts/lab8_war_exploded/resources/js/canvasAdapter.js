var divCanvas, canvasGraph, rInput, canvasPoints;
var x_center, y_center, x_transform, y_transform, r, canvasHeight, canvasWidth;
var xVals, yVals;

function init() {
    xVals = [];
    yVals = [];
    divCanvas = document.getElementById('canvasContainer');
    canvasGraph = document.getElementById("graph");
    canvasPoints = document.getElementById("graph");

    rInput = document.getElementById('radius');
    window.addEventListener("resize", onResize);
    onResize();
}

function initialPoints() {
    data = JSON.stringify({
        type: "G"
    });
    console.log(data);
    Socket.send(data);
}

function onResize() {
    r = document.getElementById("myForm:valueR").value;
    // if (rInput.value === null) {
    //     r = 3;
    // } else {
    //     r = rInput.value;
    // }
    // // r = rInput.value;
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
    if (xVals.length > 0) {
        sendPoints(xVals, yVals, 0);
    }
}

function deletePoints() {
    Socket.send(JSON.stringify({
        type: "D",
        rval: parseFloat(r)
    }));
}

function addNewPoint() {
    var xValue = document.getElementById("myForm:valueX").value;
    var yValue = document.getElementById("myForm:valueY").value;
    xVals.push(parseFloat(xValue));
    yVals.push(parseFloat(yValue));
    sendPoint(parseFloat(xValue), parseFloat(yValue), xVals.length - 1);
}

function validateForm() {
    var text = "";
    var y = document.getElementById("myForm:valueY").value;
    if (!isNaN(y)) {
        alert("Input not valid! ");
        //document.getElementById("answerValid").innerHTML = text;
        return false;
    }
    document.getElementById("answerValid").innerHTML = text;
    return true;
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
        drawFigure(context);
    }
    drawCoordinates(context);
}

function setPoint(event) {
    rect = canvasGraph.getBoundingClientRect();
    offset = (rect.width - canvasGraph.width) / 2 + 1;
    x = event.clientX - rect.left - offset;
    y = event.clientY - rect.top - offset;
    if (r == 0) {
        alert("Установите радиус сначала");
    } else {
        real_x = r * (x - x_center) / x_transform;
        real_y = -r * (y - y_center) / y_transform;
        xVals.push(real_x);
        yVals.push(real_y);
        sendPoint(real_x, real_y, xVals.length - 1);
    }
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

function drawFigure(context) {
    context.beginPath();
    drawEllipse(context, x_center + 1, y_center + 1, x_transform, y_transform);
    drawTriangle(context, x_center - 1, y_center - 1);
    //context.rect(x_center + 1, y_center - 1 - y_transform, x_transform, y_transform);
    context.closePath();
    context.fillStyle = "#5c99ED";
    context.fill();
}

function drawEllipse(context, x, y, a, b) {
    context.save();
    context.translate(x, y);
    context.scale(a / b, 1);
    context.arc(0, 0, 0, -Math.PI * 0.5,0);
    context.lineTo(0, 0);
    context.lineTo(b, 0);
    context.restore();
}

function drawTriangle(context, x, y) {
    context.save();
    context.translate(x, y);
    context.moveTo(0, 0);
    context.lineTo(0, y_transform/2);
    context.lineTo(x_transform, 0)
    context.lineTo(0, 0);
    context.restore();
}
