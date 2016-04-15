<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<div id= "word_maps">
	<p><b>MAPS</b></p>
</div>

<div id= "forms">

<div id= "div1">
Start Street 1: <input list="autoS1" type="text"  id ="streetS1" name="streetS1" value=""/><br>
<datalist id="autoS1">
    <option id ="suggestS11" type="text" value=""/>
    <option id ="suggestS12" type="text" value=""/>
    <option id ="suggestS13" type="text" value=""/>
    <option id ="suggestS14" type="text" value=""/>
    <option id ="suggestS15" type="text" value=""/>
</datalist>
</div>

<div id= "div2">
Start Street 2: <input list="autoS2" type="text"  id ="streetS2" name="streetS2" value=""/><br><br>
<datalist id="autoS2">
    <option id ="suggestS21" value=""/>
    <option id ="suggestS22" value=""/>
    <option id ="suggestS23" value=""/>
    <option id ="suggestS24" value=""/>
    <option id ="suggestS25" value=""/>
</datalist>
<div/>

<button id="submitS" onclick="textEnterS()"> Submit </button>
<button id="clearS" onclick="clearTextS()"> Clear </button>
<br><br>
<button id="gts" style= "color:red" onclick="goToStart()"/> Go to Start </button>
<br>
<div id= "div3">
Destination Street 1: <input list="autoF1" type="text"  id ="streetF1" name="streetF1" value=""/><br>
<datalist id="autoF1">
    <option id ="suggestF11" value=""/>
    <option id ="suggestF12" value=""/>
    <option id ="suggestF13" value=""/>
    <option id ="suggestF14" value=""/>
    <option id ="suggestF15" value=""/>
</datalist>
<div/>

<div id= "div4">
Destination Street 2: <input list="autoF2" type="text"  id ="streetF2" name="streetF2" value=""/><br><br>
<datalist id="autoF2">
    <option id ="suggestF21" value=""/>
    <option id ="suggestF22" value=""/>
    <option id ="suggestF23" value=""/>
    <option id ="suggestF24" value=""/>
    <option id ="suggestF25" value=""/>
</datalist>
<div/>

<button id="submitF" onclick="textEnterF()"> Submit </button>
<button  id="clearF" onclick="clearTextF()"> Clear </button>
<br><br>
<button id="gtd" style= "color:red" onclick="goToDest()"/> Go to Destination </button>
<br><br>

<div id= "enter">
<button id = "enterButton" style= "color:red" onclick="shortestPath()"/>Enter</button>
</div>
<p id="enterMessages"></p>

</div>

</div>
</#assign>
<#include "main.ftl">
