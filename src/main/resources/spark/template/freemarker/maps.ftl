<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<div id= "word_maps">
	<p><b>MAPS</b></p>
</div>

<div id= "forms">

<div id= "div1">
Start Street 1: <input list="autoS1" type="text"  id ="streetS1" name="streetS1" value=""/><br><br>
<datalist id="autoS1">
    <option id ="suggestS11" value=""/>
    <option id ="suggestS12" value=""/>
    <option id ="suggestS13" value=""/>
    <option id ="suggestS14" value=""/>
    <option id ="suggestS15" value=""/>
</datalist>
</div>

<div id= "div2">
Start Street 2: <input list="autoS2" type="text"  id ="streetS1" name="streetS2" value=""/><br><br>
<datalist id="autoS2">
    <option id ="suggestS21" value=""/>
    <option id ="suggestS22" value=""/>
    <option id ="suggestS23" value=""/>
    <option id ="suggestS24" value=""/>
    <option id ="suggestS25" value=""/>
</datalist>
<div id/>

<button id="submitS" onclick="textEnterS()"> Submit </button>
<button id="clearS" onclick="clearTextS()"> Clear </button>

<br><br>
<div id= "div3">
Destination Street 1: <input list="autoF1" type="text"  id ="streetF1" name="streetF1" value=""/><br><br>
<datalist id="autoF1">
    <option id ="suggestS31" value=""/>
    <option id ="suggestS32" value=""/>
    <option id ="suggestS33" value=""/>
    <option id ="suggestS34" value=""/>
    <option id ="suggestS35" value=""/>
</datalist>
<div/>

<div id= "div4">
Destination Street 2: <input list="autoF2" type="text"  id ="streetF2" name="streetF2" value=""/><br><br>
<datalist id="autoF2">
    <option id ="suggestS41" value=""/>
    <option id ="suggestS42" value=""/>
    <option id ="suggestS43" value=""/>
    <option id ="suggestS44" value=""/>
    <option id ="suggestS45" value=""/>
</datalist>
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
