// Input textbox
var input1 = $("#input1");
// Suggestion textboxes
var boxes1 =
    [$("#sug11"),
    $("#sug12"),
    $("#sug13"),
    $("#sug14"),
    $("#sug15")];

// Input textbox
var input2 = $("#input2");
// Suggestion textboxes
var boxes2 =
    [$("#sug21"),
    $("#sug22"),
    $("#sug23"),
    $("#sug24"),
    $("#sug25")];

// Color of a suggestion box that has a suggestion
var suggestionColor = "white";
// Color of a suggestion box that doesn't have a suggestion
var noSuggestionColor = "rgb(200, 200, 200)";
// Color of a suggestion box that has a suggestion and is moused-over
var highlightedColor = "orange";

// When the input text box is updated, get suggestions
input1.on("keyup", getSuggestions1);

// Makes a post request to get suggestions for input
function getSuggestions1() {
    var postParameters = {input: input1[0].value, on: true};
    $.post("/auto1", postParameters, showSuggestions1);
}

// Displays a list of suggestions
function showSuggestions1(suggestionsJSON) {
    // Get the suggestions list
    var suggestionsObject = JSON.parse(suggestionsJSON);
    var suggestions = $.map(suggestionsObject, function(e) {return e;});
    
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