<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<form id="start">
  Start Street 1:<br>
  <input type="text" name="start_street_1">
  <br>
  Start Street 2:<br>
  <input type="text" name="start_street_1">
  <input type="submit" value="Submit">
  <input type="button" value="Clear" id="btnsubmit" onclick="clearForm()">
</form>

<form id="destination">
  Destination Street 1:<br>
  <input type="text" name="finish_street_1">
  <br>
  Destination Street 2:<br>
  <input type="text" name="finish_street_2">
  <input type="submit" value="Submit">
  <input type="button" value="Clear" id="btnsubmit" onclick="clearForm()">
</form>
</div>
</#assign>
<#include "main.ftl">
