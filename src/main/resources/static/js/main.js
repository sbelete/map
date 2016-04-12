var latitude = 41.8268;
var longtitude = -71.4025;
var size = .001; // number of shown latitude
var shownNodes;
var canvas = $("#map");
var canvasSize = $("#map").height;
var submit = $("#submit");
udpdateShownNodes();

var mouseDownX;
var mouseDonwY;

var start;
var finish;

function updateShownNodes() {
	var postParameters = {lat : latiude, lon : longitude, s : size}
		$.post("/getNodes", postParameters, setNodes);
};

function setNdoes(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	shownNodes = $.map(nodesObject, function(e) {return e;});
};

map.addEventListener("mousedown", function(event){
	mouseDownX = event.clientX;
	mouseDownY = event.clientY;
});

map.addEventListener('mousewheel', function(event){
	size = size + 0.00000833333*event.originalEvent.wheelDelta;
	updateShownNodes();
	repaint();
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
	locationArr = $.map(nodesObject, function(e){return e;});
	start = locaitonArr[0];
};

function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	locationArr = $.map(nodesObject, function(e){return e;});
	finish = locationArr[0];
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
		var postParameters = {s : start, f : finish};
		$.post("/shortestPath", postParameters, route);
	}
};

function route(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	var optimalRouteArr = $.map(nodesObject, function(e){return e;});
	// How are we going to display the route 
	// routeRepaint();???
};


function repaint(){
	var postParameters = {lat : latiude, lon : longitude, s : size, nodes : shownNodes};
	$.post("/getEdges", postParameters, paint);
};

function paint(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	var edgeArr = $.map(nodesObject, function(e){return e;});
	var endpoints;
	var vertex1;
	var vertex2;
	for (i = 0; i < edgeArr.length; i++){
		endpoints = edgeArr[i].getEndpoints();
		vertex1 = endpoints.s().getLat();
		vertex2 = endpoints.t().getLng;
		
		paint_helper(vertex1.getLat() * (canvasSize/size)/latitude, 
				vertex1.getLng() * (canvasSize/size)/longitude, 
				vertex2.getLat() * (canvasSize/size)/latitude,
				vertex2.getLng() * (canvasSize/size)/longitude, 
				edgeArr[i].getWeight()
				);
		}	
	}
};

function paint_helper(x1, y1, x2, y2, weight){
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

