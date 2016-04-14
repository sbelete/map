<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<div id= "word_maps">
	<p><b>MAPS</b></p>
</div>

<div id= "forms">

<div id= "div1">
Start Street 1: <input type="text" id ="streetS1" name="streetS1" value=""/><br><br>
<!-- input id="autocomplete1" type="text" disabled="disabled" -->
</div>

<div id= "div2">
Start Street 2: <input type="text" id = "streetS2" name="streetS2" value=""/><br><br>
<!--input id="autocomplete2" type="text" disabled="disabled" -->
<div id/>

<button id="submitS" onclick="textEnterS()"> Submit </button>
<button id="clearS" onclick="clearTextS()"> Clear </button>

<br><br>
<div id= "div3">
Destination Street 1: <input type="text" id ="streetF1" name="streetF1" value=""/><br><br>
<!-- input id="autocomplete3" type="text" disabled="disabled" -->
<div/>

<div id= "div4">
Destination Street 2: <input type="text" id = "streetF2" name="streetF2" value=""/><br><br>
<!-- input id="autocomplete4" type="text" disabled="disabled" -->
<div/>

<button id="submitF" onclick="textEnterF()"> Submit </button>
<button  id="clearF" onclick="clearTextF()"> Clear </button>
<br><br><br>
<div id= "enter">
<button id = "enter" style= "color:red" onclick="shortestPath()"/>Enter</button>
</div>
</div>
</div>
</#assign>
<#include "main.ftl">
