var latitude = 41.8268;
var longtitude = -71.4025;
var size = .001; // number of shown latitude

var canvas = $("#map");
var canvasSize = $("#map").height;
var submit = $("#submit");
var clearText1 = $("#clearText1");
var clearText2 = $("#clearText2");
var enetrText1 = $("#enterText1");
var enterText2 = $("#enterText2");

var input1 = $("#input1");
var input2 = $("#input2");
var input3 = $("#input3");
var input4 = $("#input4");

var mouseDownX;
var mouseDonwY;

var start_lat;
var start_lng;
var start_id;
var finish_lat;
var finish_lng;
var finish_id;

updateNodes();
setInterval(repaint, 5000);


function updateNodes() {
	var postParameters = {lat : latiude, lon : longitude, s : size}
		$.post("/getNodes", postParameters, paint);
};

function setLocationStart(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	
	if(nodesObject.id != null){
		start_lat = nodesObject.lat;
		start_lng = nodesObject.lng;
		start_id  = nodesObject.id;
	} else {
		finish_lat = null;
		finsih_lng = null;
		finish_id  = null;
	}
};

function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	
	if(nodesObject.id != null){
		finish_lat = nodesObject.lat;
		finsih_lng = nodesObject.lng;
		finish_id  = nodesObject.id;
	} else {
		finish_lat = null;
		finsih_lng = null;
		finish_id  = null;
	}
};

function mouseDrag(deltaX, deltaY){
	latitude = latitude + (deltaY/canvasSize) * size;
	var degreeLong = size * 110.574 / (111.320 * cos(latitude));
	longitude = longtiude + (deltaX/canvasSize) * degreeLong;
	
	updateNodes();
};

function nearestNode(x, y){
	var lon = x*(size/canvasSize) - size/2 + longitude;
	var lat = y*(size/canvasSize) - size/2 + latitude;
	
	var postParameters = {lat : latiude, lon: longitude};
	s
	if(start != null){
		$.post("/nearestNeighbor", postParameters, setLocationStart);
	} else {
		$.post("/nearestNeighbor", postParameters, setLocationFinish);
	}
};

function textEnter1(){
	var postParameters = {first_street : input1, second_street : input2};
	$.post("/findIntersection", postParameters, setLocationStart);
};

function textEnter2(){
	var postParameters = {first_street :input3, second_street : input4};
	$.post("/findIntersection", postParameters, setLocationFinish);
};

function shortestPath(){
	if(start == null || finish == null){
		var postParameters = {start_id : start_id, finish_id : finish_id};
		$.post("/shortestPath", postParameters, paint);
	}
};

function repaint(){
	var postParameters = {lat : latitude, lon : longitude, s : size};
	$.post("/getEdges", postParameters, paint);
};

function paint(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	var edgeArr = nodesObject.shownEdges;
	var pathArr = nodesObject.pathEdges;
	
	for (i = 0; i < edgeArr.length; i++){
		paint_helper(
				(edgeArr[i][0] - latitude   + size/2) * (canvasSize/size), 
				(edgeArr[i][1] - longitutde + size/2) * (canvasSize/size), 
				(edgeArr[i][2] - latitude   + size/2) * (canvasSize/size), 
				(edgeArr[i][3] - longitudue + size/2) * (canvasSize/size), 
				 edgeArr[i][4]);
	}
	
	for(j = 0; j < pathArr.length; j++){
		paint_helper_path(
				(pathArr[i][0] - latitude   + size/2) * (canvasSize/size), 
				(pathArr[i][1] - longitutde + size/2) * (canvasSize/size), 
				(pathArr[i][2] - latitude   + size/2) * (canvasSize/size), 
				(pathArr[i][3] - longitudue + size/2) * (canvasSize/size), 
				 pathArr[i][4]);
	}
};

function paint_helper(y1, x1, y2, x2, weight){
	var c = map.getElementById("myCanvas");
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=1;
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	if(weight > 5){
		ctx.fillStyle="#FF0000";
	} else if(weight > 2){
		ctx.fillStyle="#FFFF00";
	} else{
		ctx.fillStyle="#00FF00";
	}
	ctx.stroke();
};

function paint_helper(y1, x1, y2, x2, weight){
	var c = map.getElementById("myCanvas");
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=3;
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	if(weight > 5){
		ctx.fillStyle="#FF0000";
	} else if(weight > 2){
		ctx.fillStyle="#FFFF00";
	} else{
		ctx.fillStyle="#00FF00";
	}
	ctx.stroke();
};

canvas.addEventListener('mousewheel', function(event){
	size = size + 0.00000833333*event.originalEvent.wheelDelta;
	updateNodes();
	return false;
}, false);

submit.addEventListener("click", shortestPath);

clear.addEventListener("click", function {
	$.post("/clear", {}, paint);
});

clearText1.addEventListener("click", textClear1);
clearText2.addEventListener("click", textClear2);

enetrText1.addEventListener("click", textEnter1);
enetrText1.addEventListener("click", textEnter2);


canvas.addEventListener("mousedown", function(event){
	mouseDownX = event.clientX;
	mouseDownY = event.clientY;
});

canvas.addEventListener("mouseup", function(event){
	var deltaX = event.clientX - mouseDownX;
	var deltaY = mouseDownY - event.clientY;
	if(deltaX == 0 && deltaY == 0){
		nearestNode(mouseDownX, mouseDownY);
	} else{
		mouseDrag(deltaX, deltaY);
	}
	
});