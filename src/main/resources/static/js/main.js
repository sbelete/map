var latitude = 41.8268;
var longitude = -71.4025;
var size = .001; // number of shown latitude

var MAX_SIZE = .01;
var MIN_SIZE = .0005;
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

var cache = {};

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
	var degreeLong = size * 110.574 / (111.320 * Math.cos(latitude));
	longitude = longitude + (deltaX/canvasSize) * degreeLong;

	repaint();
};

function nearestNode(x, y){
	var lon = x*(size/canvasSize) - size/2 + longitude;
	var lat = y*(size/canvasSize) - size/2 + latitude;
	
	var postParameters = {lat : latitude, lon: longitude};
	if(start_id != null){
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
	if(start_id == null || finish_id == null){
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
	// Array of 2-element arrays: first is ID, second is traffic
	var oldEdges = nodesObject.oldEdges;
	// Array of 2-element arrays: first is ID, second is traffic
	var newEdges = nodesObject.newEdges;
	// Array of 4-element arrays: lat/lng of start/end
	var newCoords = nodesObject.newCoords;
	// Edges in the shortest path
	var pathEdges = nodesObject.pathEdges;
	canvas.getContext("2d").clearRect(0, 0, canvasSize, canvasSize);
	for (i = 0; i < oldEdges.length; i++) {
		var coords = cache[oldEdges[i][0]];
		var traffic = oldEdges[i][1];
		if (pathEdges[oldEdges[i][0]] == null) {
			paintCoordsTraffic(coords, traffic, 1);
		} else {
			paintCoordsTraffic(coords, traffic, 3);
		}
	}
	for (j = 0; j < newEdges.length; j++) {
		var coords = newCoords[j];
		cache[newEdges[j][0]] = coords;
		var traffic = newEdges[j][1];
		if (pathEdges[newEdges[j][0]] == null) {
			paintCoordsTraffic(coords, traffic, 1);
		} else {
			paintCoordsTraffic(coords, traffic, 3);
		}
	}

	/*
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
*/
};

// Paints an edge with the given coordinates array and traffic value
function paintCoordsTraffic(coords, traffic, lineWidth) {
	paint_helper(
		(coords[0] - latitude   + size/2) * (canvasSize/size),
		(coords[1] - longitude + size/2) * (canvasSize/size), 
		(coords[2] - latitude   + size/2) * (canvasSize/size), 
		(coords[3] - longitude + size/2) * (canvasSize/size), 
		traffic, lineWidth);
}

function paint_helper(y1, x1, y2, x2, weight, lineWidth) {
	var c = canvas
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=lineWidth;
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
}

/*
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
*/

canvas.addEventListener('mousewheel', function(event){
	var temp = size - 0.00000833333*event.wheelDelta;
	
	if(temp < MAX_SIZE || temp > MIN_SIZE){
		size = temp;
	}
	
	repaint();
	return false;
}, false);

function clearStart() {
	   // Get the first form with the name
	   var frm = document.getElementsByName('start')[0];
	   frm.reset();  // Reset
	   $.post("/clear", {}, paint);
	   finish_id = null;
	   finish_lat = null;
	   finihs_lng = null;
	   paintEnter(false);
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
	   $.post("/clear", {}, paint);
	   finish_id = null;
	   finish_lat = null;
	   finihs_lng = null;
	   paintEnter(false);
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
