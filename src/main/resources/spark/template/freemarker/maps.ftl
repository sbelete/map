<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<div id= "word_maps">
	<p><b>MAPS</b></p>
</div>

<div id= "forms">

<form id = "start" class="start">
  Start Street 1: 
  <input type="text" name="start_street_1">
  <br><br>
  Start Street 2:
  <input type="text" name="start_street_1"><br><br>
  <input type="button" value="Submit" id="btnsubmit" onclick="submitStart()">
  <input type="button" value="Clear" id="btnsubmit" onclick="clearForm()">
</form>

<br><br>
<form id = "destination" class="destination">
   Start Street 1: 
   <input type="text" name="finish_street_1">
  <br><br>
  Start Street 2:
  <input type="text" name="finish_street_1"><br><br>
  <input type="button" value="Submit" id="btnsubmit" onclick="submitFinish()">
  <input type="button" value="Clear" id="btnsubmit" onclick="clearForm()">
</form>
</div>

<div id= "enter">
<input type = "button" value="Enter" onclick="shortestPath()">
</div>


</div>
</#assign>
<#include "main.ftl">
