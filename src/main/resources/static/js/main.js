var latitude = 41.8268;
var longtitude = -71.4025;
var size = .001; // number of shown latitude
var pathEdges;
var canvas = $("#map");
var canvasSize = $("#map").height;
var submit = $("#submit");
udpdateShownNodes();

var mouseDownX;
var mouseDonwY;

var start_lat;
var start_lng;
var start_id;
var finish_lat;
var finish_lng;
var finish_id;

function updateShownNodes() {
	var postParameters = {lat : latiude, lon : longitude, s : size}
		$.post("/getNodes", postParameters, paint);
};

map.addEventListener("mousedown", function(event){
	mouseDownX = event.clientX;
	mouseDownY = event.clientY;
});

map.addEventListener('mousewheel', function(event){
	size = size + 0.00000833333*event.originalEvent.wheelDelta;
	updateShownNodes();
	return false;
}, false);

submit.addEventListener("click", shortestPath);

map.addEventListener("mouseup", function(event){
	var deltaX = event.clientX - mouseDownX;
	var deltaY = mouseDownY - event.clientY;
	if(deltaX == 0 && deltaY == 0){
		nearestNode(mouseDownX, mouseDownY);
	} else{
		mouseDrag(deltaX, deltaY);
	}
	
});

function setLocationStart(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	start_lat = nodesObject.lat;
	start_lng = nodesObject.lng;
	start_id  = nodesObject.id;
};

function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	finish_lat = nodesObject.lat;
	finsih_lng = nodesObject.lng;
	finish_id  = nodesObject.id;
};


function mouseDrag(deltaX, deltaY){
	latitude = latitude + (deltaY/canvasSize) * size;
	var degreeLong = size * 110.574 / (111.320 * cos(latitude));
	longitude = longtiude + (deltaX/canvasSize) * degreeLong;
	
	updateShownNodes();
	repaint();
};


function nearestNode(x, y){
	var lat = size*latitude*(x/canvasSize);
	var lon = size*longitude*(y/canvasSize);
	
	var postParameters = {lat : latiude, lon : longitude};
	s
	if(start != null){
		$.post("/nearestNeighbor", postParameters, setLocationStart);
	} else {
		$.post("/nearestNeighbor", postParameters, setLocationFinish);
	}
};

setInterval(repaint, 5000);

function shortestPath(){
	if(start == null || finish == null){
		var postParameters = {start_id : start_id, finish_id : finish_id};
		$.post("/shortestPath", postParameters, paint);
	}
};

function route(nodesJSON){
	pathEdges = JSON.parse(nodesJSON);
	// How are we going to display the route 
	// routeRepaint();???
};


function repaint(){
	var postParameters = {lat : latiude, lon : longitude, s : size};
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

function paint_helper(x1, y2, x1, y2, weight){
	var c = map.getElementById("myCanvas");
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	if(weight > 40){
		ctx.fillStyle="#FF0000";
	} else if(weight > 20){
		ctx.fillStyle="#FFFF00";
	} else{
		ctx.fillStyle="#00FF00";
	}
	ctx.stroke();
};

function paint_helper_path(x1, y2, x1, y2, weight){
	var c = map.getElementById("myCanvas");
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.moveTo(x1, y1);
	ctx.lineTo(x2, y2);
	ctx.
	if(weight > 5){
		ctx.fillStyle="#FF0000";
	} else if(weight > 2){
		ctx.fillStyle="#FFFF00";
	} else{
		ctx.fillStyle="#00FF00";
	}
	ctx.stroke();
};

