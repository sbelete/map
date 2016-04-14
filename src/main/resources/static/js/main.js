var latitude = 41.8268;
var longitude = -71.4025;
var size = .001; // number of shown latitude

var MAX_SIZE = .01;
var MIN_SIZE = .0005;
var canvas = $("#map")[0];
var canvasSize = canvas.height;
var submit = $("#submit")[0];

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
	
	if(nodesObject.id != ""){
		start_lat = nodesObject.lat;
		start_lng = nodesObject.lng;
		start_id  = nodesObject.id;
	} else {
		start_id = null;
		start_lng = null;
		start_lat  = null;
	}
};

function setLocationFinish(nodesJSON){
	var nodesObject = JSON.parse(nodesJSON);
	
	if(nodesObject.id != ""){
		finish_lat = nodesObject.lat;
		finish_lng = nodesObject.lng;
		finish_id  = nodesObject.id;
	} else {
		finish_lat = null;
		finish_lng = null;
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
	if(start_id == null){
		$.post("/nearestNeighbor", postParameters, setLocationStart);
	} else {
		$.post("/nearestNeighbor", postParameters, setLocationFinish);
	}
};

function textEnter1(){
	var postParameters = {first_street : input1[0].value, second_street : input2[0].value};
	$.post("/findIntersection", postParameters, setLocationStart);
};

function clearText1() {
	   // Get the first form with the name
		input1[0].value = "";
		input2[0].value = "";
	 
	   $.post("/clear", {}, paint);
	   start_id = null;
	   start_lat = null;
	   start_lng = null;
	   repaint();
};

function textEnter2(){
	var postParameters = {first_street :input3[0].value, second_street : input4[0].value};
	$.post("/findIntersection", postParameters, setLocationFinish);
};

function clearText2() {
	   // Get the first form with the name
		input3[0].value = "";
		input4[0].value = "";
	 
	   $.post("/clear", {}, paint);
	   finish = null;
	   finish_lat = null;
	   finish_lng = null;
	   repaint();
};


function shortestPath(){
	if(start_id != null && finish_id != null){
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
	
	if(start_id != null){
		var c = canvas
		var ctx = c.getContext("2d");
		ctx.beginPath();
		var x = (start_lng - latitude   + size/2) * (canvasSize/size);
		var y = (start_lat - latitude   + size/2) * (canvasSize/size);
		ctx.arc(x,y, 5,0,2*Math.PI);
		context.fillStyle = 'blue';
		context.fill();
		ctx.stroke();
	}
	if(finish_id != null){
		var c2 = canvas
		var ctx2 = c2.getContext("2d");
		ctx2.beginPath();
		var x2 = (finish_lng - latitude   + size/2) * (canvasSize/size);
		var y2 = (finish_lat - latitude   + size/2) * (canvasSize/size);
		ctx2.arc(x2,x2, 5,0,2*Math.PI);
		ctx2.fillStyle = 'blue';
		ctx2.fill();
		ctx2.stroke();
	}
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

canvas.addEventListener('mousewheel', function(event){
	var temp = size - 0.00000833333*event.wheelDelta;
	
	if(temp < MAX_SIZE && temp > MIN_SIZE){
		size = temp;
	}
	
	repaint();
	return false;
}, false);


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

// ========================= AUTO CORRECCT ================================
//Input textbox
var input1 = $("#street1"); 

// Suggestion textboxes
var boxes1 =
    [$("#sug11"),
    $("#sug12"),
    $("#sug13"),
    $("#sug14"),
    $("#sug15")];

// Input textbox
var input2 = $("#street2");
// Suggestion textboxes
var boxes2 =
    [$("#sug21"),
    $("#sug22"),
    $("#sug23"),
    $("#sug24"),
    $("#sug25")];

    // Input textbox
    var input3 = $("#street3");
    // Suggestion textboxes
    var boxes3 =
        [$("#sug31"),
        $("#sug32"),
        $("#sug33"),
        $("#sug34"),
        $("#sug35")];

    // Input textbox
    var input4 = $("#street4");
    // Suggestion textboxes
    var boxes4 =
        [$("#sug41"),
        $("#sug42"),
        $("#sug43"),
        $("#sug44"),
        $("#sug45")];

// Color of a suggestion box that has a suggestion
var suggestionColor = "white";
// Color of a suggestion box that doesn't have a suggestion
var noSuggestionColor = "rgb(200, 200, 200)";
// Color of a suggestion box that has a suggestion and is moused-over
var highlightedColor = "orange";

// When the input text box is updated, get suggestions
input1.on("keyup", getSuggestions1);
input1.keyup(function(e){
    console.log(e);
    $('#autocomplete1').val($(this).val() + 'asdf')
});
// Makes a post request to get suggestions for input
function getSuggestions1() {
    var postParameters = {input: input1[0].value, on: true};
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
            input1[0].value = box[0].value;
            getSuggestions1();
        }
    });
});

// When the input text box is updated, get suggestions
input2.on("keyup", getSuggestions2);

// Makes a post request to get suggestions for input
function getSuggestions2() {
    var postParameters = {input: input2[0].value, on: true};
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
            input2[0].value = box[0].value;
            getSuggestions2();
        }
    });
});

// When the input text box is updated, get suggestions
input3.on("keyup", getSuggestions3);

// Makes a post request to get suggestions for input
function getSuggestions3() {
    var postParameters = {input: input3[0].value, on: true};
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
            input3[0].value = box[0].value;
            getSuggestions3();
        }
    });
});

// When the input text box is updated, get suggestions
input4.on("keyup", getSuggestions4);

// Makes a post request to get suggestions for input
function getSuggestions4() {
    var postParameters = {input: input4[0].value, on: true};
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
            input4[0].value = box[0].value;
            getSuggestions4();
        }
    });
});
