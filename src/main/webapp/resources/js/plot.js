function setR(value){ 

    document.getElementById("mainform:r").value=value;
    document.getElementById("rtext").innerHTML=value;
    console.log("SetR:" + value);
    draw_plot([]);

}

function check()
{

	error_msg = document.getElementById("error_msg");

        x = document.getElementById('mainform:x_hidden').value;
        y = document.getElementById('mainform:y').value;
        r = document.getElementById('mainform:r').value;

	if( x === '' ) { error_msg.innerHTML = "Please, Choose X value first"; return false; }
	if( y === '' ) { error_msg.innerHTML = "Please, Type Y value first"; return false; }
	if( r === '' ) { error_msg.innerHTML = "Please, Choose R value first"; return false; }

        return true;
}

/* PLOT FUNCS */

g_dots = [];
sent=false;

function draw_plot(dots)
{
    if (dots.length === 0) dots = g_dots;
    else g_dots = dots;
       
    console.log("Drawing plot");
    console.log("Dots:");
    dots.forEach(function(dot){ console.log(dot[0] + " " + dot[1] + " " + dot[2] + " " |+ dot[3]);});
    
    var c = document.getElementById("plot");
    var ctx = c.getContext("2d");
    
    var r = 200;
    var xc = c.width/2;
    var yc = c.height/2;
    var tick_len = 10;
    var arrow_sz = 10;

    c.addEventListener('click', function(e)
    {
        if(sent) return;
        console.log("Plot listener");
        R = document.getElementById('mainform:radius').value;
        if (!R) { document.getElementById("error_msg").innerHTML = "Please, Choose R value first"; return; }

        var rect = c.getBoundingClientRect();
        console.log(c)
                console.log(e)

        x = e.clientX - rect.left;
        y = e.clientY - rect.top;

        x = R*(x-xc)/r;
        y = R*(yc-y)/r;

        document.getElementById('mainform:x_hidden').value = x*100;
        document.getElementById('mainform:y').value = y;
        
        console.log(document.getElementById('mainform:submit_button'))

        sent=true
        document.getElementById('mainform:submit_button').click();
        
    },
    false);

    ctx.clearRect(0, 0, c.width, c.height);
    // draw functions boundaries
    ctx.fillStyle="#4782c9";

    ctx.beginPath();
    ctx.fillRect(xc-r/2, yc-r, r/2, r);
    ctx.arc(xc, yc, r/2, 0.5*Math.PI, 1*Math.PI);
    ctx.lineTo(xc, yc);
    ctx.lineTo(xc+r, yc);
    ctx.lineTo(xc, yc+r/2);
    ctx.fill();


    // reset path for function drawing
    ctx.beginPath();

    ctx.strokeStyle="#4f4f4f";
    ctx.fillStyle="#4f4f4f";
    ctx.lineWidth = 4;

    // axes
    ctx.moveTo(0, yc); ctx.lineTo(2*xc, yc); ctx.stroke();
    ctx.moveTo(xc, 0); ctx.lineTo(xc, 2*yc); ctx.stroke();

    // axes arrows
    ctx.beginPath();
    x = 2*xc; ctx.moveTo(x - arrow_sz, yc+arrow_sz); ctx.lineTo(x, yc); ctx.lineTo(x - arrow_sz, yc - arrow_sz);
    ctx.moveTo(xc - arrow_sz, arrow_sz); ctx.lineTo(xc, 0); ctx.lineTo(xc+arrow_sz, arrow_sz);

    //ticks
    ctx.lineWidth = 2;
    ctx.fillStyle=ctx.strokeStyle;

    ctx.textAlign = "center";
    ctx.beginPath();
    y0 = yc-tick_len/2; y1 = yc+tick_len/2; text_pos = y1+tick_len;
    x = [xc-r, xc-r/2, xc+r/2, xc+r];
    x.forEach(function(x) { ctx.moveTo(x, y0); ctx.lineTo(x, y1); ctx.fillText((x-xc)/r + "R", x, text_pos);} );

    ctx.textAlign = "right";
    x0 = xc - tick_len/2; x1 = xc + tick_len/2; text_pos = x0-tick_len/2;
    y = [yc-r, yc-r/2, yc+r/2, yc+r];
    y.forEach(function(y) { ctx.moveTo(x0, y); ctx.lineTo(x1, y); ctx.fillText((yc-y)/r + "R", text_pos, y);} );

    ctx.stroke();
    
    r_elem = document.getElementById('mainform:radius');
    
    if( !r_elem ) return;
    
    R = document.getElementById('mainform:radius').value;
    console.log("[Plot] R=" + R);
    dots.forEach(function(dot) {
        console.log(dot);
        ctx.beginPath();
        ctx.fillStyle = dot[2]?"#ff80ff":"#303030";
        ctx.arc(xc + r * dot[0]/R, yc - r * dot[1]/R, 7, 0, 2*Math.PI);
        ctx.fill();
    });
    
    ctx.beginPath();
    
    sent=false;
 }
