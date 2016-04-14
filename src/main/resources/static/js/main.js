var latitude = 41.8268;   // Latitude of the center of the board
var longitude = -71.4025; // Longitude of the center of the board

var MAX_SIZE = .01;    // Maximum size the map will show
var MIN_SIZE = .0005;  // Minimum size the map will show
var size = .001;       // Size of Latitude and Longitude shown

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
		drawEndpoint(start);
	}

	if(finish[0] != null){
		drawEndpoint(finish);
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
var boxes1 =
    [$("#sug11"),
    $("#sug12"),
    $("#sug13"),
    $("#sug14"),
    $("#sug15")];

// Input textbox
var inputS2 = $("#streetS2");
// Suggestion textboxes
var boxes2 =
    [$("#sug21"),
    $("#sug22"),
    $("#sug23"),
    $("#sug24"),
    $("#sug25")];

    // Input textbox
    var inputF1 = $("#streetF1");
    // Suggestion textboxes
    var boxes3 =
        [$("#sug31"),
        $("#sug32"),
        $("#sug33"),
        $("#sug34"),
        $("#sug35")];

    // Input textbox
    var inputF2 = $("#streetF2");
    // Suggestion textboxes
    var boxes4 =
        [$("#sug41"),
        $("#sug42"),
        $("#sug43"),
        $("#sug44"),
        $("#sug45")];

/*
// Color of a suggestion box that has a suggestion
var suggestionColor = "white";
// Color of a suggestion box that doesn't have a suggestion
var noSuggestionColor = "rgb(200, 200, 200)";
// Color of a suggestion box that has a suggestion and is moused-over
var highlightedColor = "orange";

// When the input text box is updated, get suggestions
inputS1.on("keyup", getSuggestions1);
inputS1.keyup(function(e){
    console.log(e);
    $('#autocomplete1').val($(this).val() + 'asdf')
});
// Makes a post request to get suggestions for input
function getSuggestions1() {
    var postParameters = {input: inputS1[0].value, on: true};
    $.post("/auto", postParameters, showSuggestions1);
}

// Displays a list of suggestions
function showSuggestions1(suggestionsJSON) {
    // Get the suggestions list
    var suggestionsObject = JSON.parse(suggestionsJSON);
    var suggestions = suggestionsObject.list;

    // First, disable and clear all boxes
    clearSuggestions1();

    // Then, fill in the suggestions
    for (var i = 0; i < suggestions.length; i++) {
        var box = boxes1[i][0];
        box.style.backgroundColor = suggestionColor;
        box.value = suggestions[i];
        // Make sure the user can see the end of the suggestion
        box.scrollLeft = box.scrollWidth;
    }
}

// Clears all suggestions
function clearSuggestions1() {
    for (var i = 0; i < boxes1.length; i++) {
        var box = boxes1[i][0];
        box.style.backgroundColor = noSuggestionColor;
        box.value = "";
    }
}

// Events for each suggestion box
boxes1.forEach(function(box) {
    // At the beginning, the boxes have no suggestions
    box[0].style.backgroundColor = noSuggestionColor;

    // When a box with a suggestion is moused-over, highlight it
    box.on("mouseover", function() {
       if (box[0].style.backgroundColor === suggestionColor) {
           box[0].style.backgroundColor = highlightedColor;
       }
    });

    // When the mouse leaves a highlighted box, un-highlight it
    box.on("mouseout", function() {
        if (box[0].style.backgroundColor === highlightedColor) {
            box[0].style.backgroundColor = suggestionColor;
        }
    });

    // When a box with a suggestion is clicked, replace the input text with
    // the suggestion and display new suggestions
    box.on("click", function() {
        if (box[0].style.backgroundColor === suggestionColor
                || box[0].style.backgroundColor === highlightedColor) {
            inputS1[0].value = box[0].value;
            getSuggestions1();
        }
    });
});

// When the input text box is updated, get suggestions
inputS2.on("keyup", getSuggestions2);

// Makes a post request to get suggestions for input
function getSuggestions2() {
    var postParameters = {input: inputS2[0].value, on: true};
    $.post("/auto2", postParameters, showSuggestions2);
}

// Displays a list of suggestions
function showSuggestions2(suggestionsJSON) {
    // Get the suggestions list
    var suggestionsObject = JSON.parse(suggestionsJSON);
    var suggestions = $.map(suggestionsObject, function(e) {return e;});

    // First, disable and clear all boxes
    clearSuggestions2();

    // Then, fill in the suggestions
    for (var i = 0; i < suggestions.length; i++) {
        var box = boxes2[i][0];
        box.style.backgroundColor = suggestionColor;
        box.value = suggestions[i];
        // Make sure the user can see the end of the suggestion
        box.scrollLeft = box.scrollWidth;
    }
}

// Clears all suggestions
function clearSuggestions2() {
    for (var i = 0; i < boxes2.length; i++) {
        var box = boxes2[i][0];
        box.style.backgroundColor = noSuggestionColor;
        box.value = "";
    }
}

// Events for each suggestion box
boxes2.forEach(function(box) {
    // At the beginning, the boxes have no suggestions
    box[0].style.backgroundColor = noSuggestionColor;

    // When a box with a suggestion is moused-over, highlight it
    box.on("mouseover", function() {
       if (box[0].style.backgroundColor === suggestionColor) {
           box[0].style.backgroundColor = highlightedColor;
       }
    });

    // When the mouse leaves a highlighted box, un-highlight it
    box.on("mouseout", function() {
        if (box[0].style.backgroundColor === highlightedColor) {
            box[0].style.backgroundColor = suggestionColor;
        }
    });

    // When a box with a suggestion is clicked, replace the input text with
    // the suggestion and display new suggestions
    box.on("click", function() {
        if (box[0].style.backgroundColor === suggestionColor
                || box[0].style.backgroundColor === highlightedColor) {
            inputS2[0].value = box[0].value;
            getSuggestions2();
        }
    });
});

// When the input text box is updated, get suggestions
inputF1.on("keyup", getSuggestions3);

// Makes a post request to get suggestions for input
function getSuggestions3() {
    var postParameters = {input: inputF1[0].value, on: true};
    $.post("/auto", postParameters, showSuggestions3);
}

// Displays a list of suggestions
function showSuggestions3(suggestionsJSON) {
    // Get the suggestions list
    var suggestionsObject = JSON.parse(suggestionsJSON);
    var suggestions = $.map(suggestionsObject, function(e) {return e;});

    // First, disable and clear all boxes
    clearSuggestions3();

    // Then, fill in the suggestions
    for (var i = 0; i < suggestions.length; i++) {
        var box = boxes3[i][0];
        box.style.backgroundColor = suggestionColor;
        box.value = suggestions[i];
        // Make sure the user can see the end of the suggestion
        box.scrollLeft = box.scrollWidth;
    }
}

// Clears all suggestions
function clearSuggestions3() {
    for (var i = 0; i < boxes3.length; i++) {
        var box = boxes3[i][0];
        box.style.backgroundColor = noSuggestionColor;
        box.value = "";
    }
}

// Events for each suggestion box
boxes3.forEach(function(box) {
    // At the beginning, the boxes have no suggestions
    box[0].style.backgroundColor = noSuggestionColor;

    // When a box with a suggestion is moused-over, highlight it
    box.on("mouseover", function() {
       if (box[0].style.backgroundColor === suggestionColor) {
           box[0].style.backgroundColor = highlightedColor;
       }
    });

    // When the mouse leaves a highlighted box, un-highlight it
    box.on("mouseout", function() {
        if (box[0].style.backgroundColor === highlightedColor) {
            box[0].style.backgroundColor = suggestionColor;
        }
    });

    // When a box with a suggestion is clicked, replace the input text with
    // the suggestion and display new suggestions
    box.on("click", function() {
        if (box[0].style.backgroundColor === suggestionColor
                || box[0].style.backgroundColor === highlightedColor) {
            inputF1[0].value = box[0].value;
            getSuggestions3();
        }
    });
});

// When the input text box is updated, get suggestions
inputF2.on("keyup", getSuggestions4);

// Makes a post request to get suggestions for input
function getSuggestions4() {
    var postParameters = {input: inputF2[0].value, on: true};
    $.post("/auto", postParameters, showSuggestions4);
}

// Displays a list of suggestions
function showSuggestions4(suggestionsJSON) {
    // Get the suggestions list
    var suggestionsObject = JSON.parse(suggestionsJSON);
    var suggestions = $.map(suggestionsObject, function(e) {return e;});

    // First, disable and clear all boxes
    clearSuggestions4();

    // Then, fill in the suggestions
    for (var i = 0; i < suggestions.length; i++) {
        var box = boxes4[i][0];
        box.style.backgroundColor = suggestionColor;
        box.value = suggestions[i];
        // Make sure the user can see the end of the suggestion
        box.scrollLeft = box.scrollWidth;
    }
}

// Clears all suggestions
function clearSuggestions4() {
    for (var i = 0; i < boxes4.length; i++) {
        var box = boxes4[i][0];
        box.style.backgroundColor = noSuggestionColor;
        box.value = "";
    }
}



// Events for each suggestion box
boxes4.forEach(function(box) {
    // At the beginning, the boxes have no suggestions
    box[0].style.backgroundColor = noSuggestionColor;

    // When a box with a suggestion is moused-over, highlight it
    box.on("mouseover", function() {
       if (box[0].style.backgroundColor === suggestionColor) {
           box[0].style.backgroundColor = highlightedColor;
       }
    });

    // When the mouse leaves a highlighted box, un-highlight it
    box.on("mouseout", function() {
        if (box[0].style.backgroundColor === highlightedColor) {
            box[0].style.backgroundColor = suggestionColor;
        }
    });

    // When a box with a suggestion is clicked, replace the input text with
    // the suggestion and display new suggestions
    box.on("click", function() {
        if (box[0].style.backgroundColor === suggestionColor
                || box[0].style.backgroundColor === highlightedColor) {
            inputF2[0].value = box[0].value;
            getSuggestions4();
        }
    });
});
*/
