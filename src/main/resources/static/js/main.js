var latitude = 41.8268;   // Latitude of the center of the board
var longitude = -71.4025; // Longitude of the center of the board

var MAX_SIZE = .01;    // Maximum size the map will show
var MIN_SIZE = .0005;  // Minimum size the map will show
var size = .005;       // Size of Latitude and Longitude shown

var canvas = $("#map")[0];
var canvasSize = canvas.height;
var submit = $("#submit")[0];

var mouseDown = [null, null];  // Mouse Down X, Mouse Down Y


var start = [null, null, null];   // Start ID, Start Lat, Start Lng
var finish = [null, null, null];  // Finish ID, Finish Lat, Finish Lng

var cache = {};

setInterval(repaint, 5000);
repaint();

// Sets the endpoint to either start or finish
function setEndpoint(endpointObject, endpoint){
	if (endpointObject.id != "") {
		endpoint[0] = endpointObject.id;
		endpoint[1] = endpointObject.lat;
		endpoint[2] = endpointObject.lng;
	} else {
		endpoint[0] = null;
		endpoint[1] = null;
		endpoint[2] = null;
	}
	repaint();
};


function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);

	if(nodesObject.id != ""){
		finish[1] = nodesObject.lat;
		finish[2] = nodesObject.lng;
		finish[0]  = nodesObject.id;
	} else {
		finish[0] = null;
		finish[1] = null;
		finish[2]  = null;
	}
	repaint();
};


function mouseDrag(deltaX, deltaY){
	latitude = latitude + (-deltaY/canvasSize) * size;
	var degreeLong = size * 110.574 / (111.320 * Math.cos(latitude));
	longitude = longitude + (deltaX/canvasSize) * degreeLong;

	repaint();
};

function nearestNode(x, y){
	var lon = x*(size/canvasSize) - size/2 + longitude;
	var lat = (canvasSize - y)*(size/canvasSize) - size/2 + latitude;

	var postParameters = {lat : lat, lon: lon};
	if(start[0] == null){
		$.post("/nearestNeighbor", postParameters, function(nodesJSON){
			setEndpoint(JSON.parse(nodesJSON), start);
		});
	} else {
		$.post("/nearestNeighbor", postParameters, function(nodesJSON){
			setEndpoint(JSON.parse(nodesJSON), finish);
		});
	}
};

function textEnterS(){
	var postParameters = {street1 : inputS1[0].value, street2 : inputS2[0].value};
	$.post("/findIntersection", postParameters, function(nodesJSON){
			setEndpoint(JSON.parse(nodesJSON), start);
	});
};

function textEnterF(){
	var postParameters = {street1 : inputF1[0].value, street2 : inputF2[0].value};
	$.post("/findIntersection", postParameters, function(nodesJSON){
			setEndpoint(JSON.parse(nodesJSON), finish);
	});
};

function clearText(endpoint){
	endpoint[0] = null;
	endpoint[1] = null;
	endpoint[2] = null;

	$.post("/clear", {}, repainter);
};

function clearTextS() {
	inputS1[0].value = "";
	inputS2[0].value = "";
	clearText(start);
};

function clearTextF() {
	inputF1[0].value = "";
	inputF2[0].value = "";
	clearText(finish)
};


function shortestPath() {
	if(start[0] != null && finish[0] != null){
		var postParameters = {start_id : start[0], finish_id : finish[0]};
		$.post("/shortestPath", postParameters, repainter);
	}
};

function goToStart(){
	if(start[0] != null){
		latitude = start[1];
		longitude = start[2];
		repaint();
	}
};

function goToDest(){
	if(finish[0] != null){
		latitude = finish[1];
		longitude = finish[2];
		repaint();
	}
};

function repaint(){
	var postParameters = {lat : latitude, lon : longitude, s : size};
	$.post("/getEdges", postParameters, repainter);
};

function repainter(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	// Array of 2-element arrays: first is ID, second is traffic
	var oldEdges = nodesObject.oldEdges;
	// Array of 2-element arrays: first is ID, second is traffic
	var newEdges = nodesObject.newEdges;
	// Array of 4-element arrays: lat/lng of start/end
	var newCoords = nodesObject.newCoords;
	// Edges in the shortest path
	var pathEdges = nodesObject.pathEdges;
	var length = nodesObject.length;
	if(jQuery.isEmptyObject(pathEdges)){
		document.getElementById("enterMessages").innerHTML = 'There isn\'t a path';
	} else {
		console.log(length);
		var message = 'This path in km is '.concat(String(length));
		document.getElementById("enterMessages").innerHTML = message;
	}
	
	canvas.getContext("2d").clearRect(0, 0, canvasSize, canvasSize);

	for (i = 0; i < oldEdges.length; i++) {
		var coords = cache[oldEdges[i][0]];
		var traffic = oldEdges[i][1];
		if (pathEdges[oldEdges[i][0]] == null) {
			paintCoordsTraffic(coords, traffic, 1);
		} else {
			paintCoordsTraffic(coords, traffic, 5);
		}
	}
	for (j = 0; j < newEdges.length; j++) {
		var coords = newCoords[j];
		cache[newEdges[j][0]] = coords;
		var traffic = newEdges[j][1];
		if (pathEdges[newEdges[j][0]] == null) {
			paintCoordsTraffic(coords, traffic, 1);
		} else {
			paintCoordsTraffic(coords, traffic, 5);
		}
	}

	if(start[0] != null){
		document.getElementById("gts").style = "color:green";
		drawEndpoint(start);
	} else{
		document.getElementById("gts").style = "color:red";
	}

	if(finish[0] != null){
		document.getElementById("gtd").style = "color:green";
		drawEndpoint(finish);
	}else{
		document.getElementById("gtd").style = "color:red";
	}
	
	if(start[0] != null && finish[0] != null){
		document.getElementById("enterButton").style = "color:green";
	}else{
		document.getElementById("enterButton").style = "color:red";
	}
};

function drawEndpoint(endpoint) {
	var x = (endpoint[2] - longitude   + size/2) * (canvasSize/size);
	var y = (endpoint[1] - latitude   + size/2) * (canvasSize/size);

	var ctx = canvas.getContext("2d");
	ctx.beginPath();
	ctx.arc(x,canvasSize - y, 5,0,2*Math.PI);
	ctx.fillStyle = 'blue';
	ctx.fill();
	ctx.stroke();
};


// Paints an edge with the given coordinates array and traffic value
function paintCoordsTraffic(coords, traffic, lineWidth) {
	paint_helper(
		(coords[0] - latitude   + size/2) * (canvasSize/size),
		(coords[1] - longitude + size/2) * (canvasSize/size),
		(coords[2] - latitude   + size/2) * (canvasSize/size),
		(coords[3] - longitude + size/2) * (canvasSize/size),
		traffic, lineWidth);
};

function paint_helper(y1, x1, y2, x2, weight, lineWidth) {
	var c = canvas
	var ctx = c.getContext("2d");
	ctx.beginPath();
	ctx.lineWidth=lineWidth;
	ctx.moveTo(x1, canvasSize - y1);
	ctx.lineTo(x2, canvasSize - y2);
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
	var temp = size - 0.00000833333*event.wheelDelta;

	if(temp < MAX_SIZE && temp > MIN_SIZE){
		size = temp;
	}

	repaint();
	return false;
}, false);

canvas.addEventListener("mousedown", function(event){
	mouseDown[0] = event.clientX;
	mouseDown[1] = event.clientY;
});

canvas.addEventListener("mouseup", function(event){
	var deltaX = event.clientX - mouseDown[0];
	var deltaY = mouseDown[1] - event.clientY;

	if(deltaX == 0 && deltaY == 0){
		var canvasBox = canvas.getBoundingClientRect();
		nearestNode(mouseDown[0] - canvasBox.left, mouseDown[1] - canvasBox.top);

	} else{
		mouseDrag(deltaX, deltaY);
	}
});

// ========================= AUTO CORRECCT ================================

//Input textbox
var inputS1 = $("#streetS1");

// Suggestion textboxes
var correctionsS1 =
    [$("#suggestS11")[0],
    $("#suggestS12")[0],
    $("#suggestS13")[0],
    $("#suggestS14")[0],
    $("#suggestS15")[0]];

// Input textbox
var inputS2 = $("#streetS2");
// Suggestion textboxes
var correctionsS2 =
    [$("#suggestS21")[0],
    $("#suggestS22")[0],
    $("#suggestS23")[0],
    $("#suggestS24")[0],
    $("#suggestS25")[0]];

    // Input textbox
    var inputF1 = $("#streetF1");
    // Suggestion textboxes
    var correctionsF1 =
        [$("#suggestF11")[0],
        $("#suggestF12")[0],
        $("#suggestF13")[0],
        $("#suggestF14")[0],
        $("#suggestF15")[0]];

    // Input textbox
    var inputF2 = $("#streetF2");
    // Suggestion textboxes
    var correctionsF2 =
        [$("#suggestF21")[0],
        $("#suggestF22")[0],
        $("#suggestF23")[0],
        $("#suggestF24")[0],
        $("#suggestF25")[0]];
    
// When the input text box is updated, get suggestions
//inputS1.on("keyup", getSuggestions1);
inputS1.keyup(function(e){

	if(inputS1[0].value != ""){
    var postParameters = {street_name: inputS1[0].value};
	$.post("/auto", postParameters, function(suggestionsJSON){
		console.log(suggestionsJSON);
		setSuggestions(JSON.parse(suggestionsJSON)[0], correctionsS1);
	});
	}
});

// Events for each suggestion box

//When the input text box is updated, get suggestions
//inputS1.on("keyup", getSuggestions1);
inputS2.keyup(function(e){
	
if(inputS2[0].value != ""){
  var postParameters = {street_name: inputS2[0].value};
	$.post("/auto", postParameters, function(suggestionsJSON){
		console.log(suggestionsJSON);
		setSuggestions(JSON.parse(suggestionsJSON)[0], correctionsS2);
	});
}
});



//When the input text box is updated, get suggestions
//inputS1.on("keyup", getSuggestions1);
inputF1.keyup( function(e){

if(inputF1[0].value != ""){
  var postParameters = {street_name: inputF1[0].value};
	$.post("/auto", postParameters, function(suggestionsJSON){
		console.log(suggestionsJSON);
		setSuggestions(JSON.parse(suggestionsJSON)[0], correctionsF1);
	});
}
});


//When the input text box is updated, get suggestions
//inputS1.on("keyup", getSuggestions1);
inputF2.keyup(function(e){
if(inputF2[0].value != ""){
  var postParameters = {street_name: inputF2[0].value};
	$.post("/auto", postParameters, function(suggestionsJSON){
		console.log(suggestionsJSON);
		setSuggestions(JSON.parse(suggestionsJSON)[0], correctionsF2);
	});
}
});


function setSuggestions(listSuggest, suggestions) {
	
	console.log("In Suggestions");
	var i;
	for(i = 0; i < 5 && i < listSuggest.length; i++){
		console.log(listSuggest[i]);
		suggestions[i].value = listSuggest[i];
	}
	
	for(i; i < 5; i++){
		console.log("Empty");
		suggestions[i].value ="";
	}
};
