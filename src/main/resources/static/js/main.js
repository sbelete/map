var latitude = 41.8268;
var longitude = -71.4025;
var size = .001; // number of shown latitude

var canvas = $("#map")[0];
var canvasSize = canvas.height;
var submit = $("#submit")[0];
var clearText1 = $("#clearText1")[0];
var clearText2 = $("#clearText2")[0];
var enterText1 = $("#enterText1")[0];
var enterText2 = $("#enterText2")[0];

var input1 = $("#input1")[0];
var input2 = $("#input2")[0];
var input3 = $("#input3")[0];
var input4 = $("#input4")[0];

var mouseDownX;
var mouseDonwY;

var start_lat;
var start_lng;
var start_id;
var finish_lat;
var finish_lng;
var finish_id;

setInterval(repaint, 5000);
repaint();

function setLocationStart(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	
	if(nodesObject.id != null){
		start_lat = nodesObject.lat;
		start_lng = nodesObject.lng;
		start_id  = nodesObject.id;
	} else {
		finish_lat = null;
		finish_lng = null;
		finish_id  = null;
	}
};

function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	
	if(nodesObject.id != null){
		finish_lat = nodesObject.lat;
		finish_lng = nodesObject.lng;
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
	longitude = longitude + (deltaX/canvasSize) * degreeLong;
	
	updateNodes();
};

function nearestNode(x, y){
	var lon = x*(size/canvasSize) - size/2 + longitude;
	var lat = y*(size/canvasSize) - size/2 + latitude;
	
	var postParameters = {lat : latitude, lon: longitude};
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
	canvas.getContext("2d").clearRect(0, 0, canvasSize, canvasSize);
	for (i = 0; i < edgeArr.length; i++){
		paint_helper(
				(edgeArr[i][0] - latitude   + size/2) * (canvasSize/size), 
				(edgeArr[i][1] - longitude + size/2) * (canvasSize/size), 
				(edgeArr[i][2] - latitude   + size/2) * (canvasSize/size), 
				(edgeArr[i][3] - longitude + size/2) * (canvasSize/size), 
				 edgeArr[i][4]);
	}
	
	for(j = 0; j < pathArr.length; j++){
		paint_helper_path(
				(pathArr[i][0] - latitude   + size/2) * (canvasSize/size), 
				(pathArr[i][1] - longitude + size/2) * (canvasSize/size), 
				(pathArr[i][2] - latitude   + size/2) * (canvasSize/size), 
				(pathArr[i][3] - longitude + size/2) * (canvasSize/size), 
				 pathArr[i][4]);
	}
};

function paint_helper(y1, x1, y2, x2, weight){
	var c = canvas
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=1;
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	if(weight > 5){
		ctx.strokeStyle="#FF0000";
	} else if(weight > 2){
		ctx.strokeStyle="#FFFF00";
	} else{
		ctx.strokeStyle="#00FF00";
	}
	ctx.stroke();
};

function paint_helper_path(y1, x1, y2, x2, weight){
	var c = canvas
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=3;
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	if(weight > 5){
		ctx.strokeStyle="#FF0000";
	} else if(weight > 2){
		ctx.strokeStyle="#FFFF00";
	} else{
		ctx.strokeStyle="#00FF00";
	}
	ctx.stroke();
};

canvas.addEventListener('mousewheel', function(event){
	size = size + 0.00000833333*event.originalEvent.wheelDelta;
	updateNodes();
	return false;
}, false);

function clearStart() {
	   // Get the first form with the name
	   var frm = document.getElementsByName('start')[0];
	   frm.reset();  // Reset
	   return false; // Prevent page refresh
};

function submitStart() {
	   // Get the first form with the name
	   var frm = document.getElementsByName('start')[0];
	   
	   frm.reset();  // Reset
	   return false; // Prevent page refresh
};

function clearDestination() {
	   // Get the first form with the name
	   var frm = document.getElementsByName('start')[0];
	   frm.reset();  // Reset
	   return false; // Prevent page refresh
};

function submitDestination() {
	   // Get the first form with the name
	   var frm = document.getElementsByName('start')[0];
	   
	   var postParameters = {first_street : input1, second_street : input2};
		$.post("/findIntersection", postParameters, setLocationStart);
	   frm.reset();  // Reset
	   return false; // Prevent page refresh
};

/*
submit.addEventListener("click", shortestPath);

clear.addEventListener("click", function () {
	$.post("/clear", {}, paint);
});

clearText1.addEventListener("click", textClear1);
clearText2.addEventListener("click", textClear2);

enterText1.addEventListener("click", textEnter1);
enterText1.addEventListener("click", textEnter2);
*/

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
