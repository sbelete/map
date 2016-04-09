var latitude = 41.8268;
var longtitude = -71.4025;
var size = .001; // number of shown latitude
var shownNodes;
var canvas = $("#map");
var canvasSize = $("#map").height;
udpdateShownNodes();

var mouseDownX;
var mouseDonwY;

var start;
var finish;

function updateShownNodes() {
	var postParameters = {lat : latiude, lon : longitude, s : size}
		$.post("/getNodes", postParameters, setNodes;
};

function setNdoes(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	shownNodes = $.map(nodesObject, function(e) {return e;});
}

map.addEventListener("mousedown", function(event){
	mouseDownX = event.clientX;
	mouseDownY = event.clientY;
});
map.addEventListener("mouseup", function(event){
	var deltaX = event.clientX - mouseDownX;
	var deltaY = mouseDownY - event.clientY;
	if(deltaX == 0 && deltaY == 0){
		nearestNode();
	} else{
		mouseDrag(deltaX, deltaY);
	}
	
});

function mouseDrag(deltaX, deltaY){
	latitude = latitude + (deltaY/canvasSize) * size;
	var degreeLong = size * 110.574 / (111.320 * cos(latitude));
	longitude = longtiude + (deltaX/canvasSize) * degreeLong;
	
	updateShownNodes();
	repaint();
};


function nearestNode(){
	if(start != null){
		set start
	} else {
		set finsish
	}
	
};

setInterval(repaint, 5000);

function repaint(){
	
};
