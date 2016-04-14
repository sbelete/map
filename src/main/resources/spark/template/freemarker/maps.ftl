<#assign content>
<div id="content">
<canvas id="map" width="600" height="600"></canvas>

<div id= "word_maps">
	<p><b>MAPS</b></p>
</div>

<div id= "forms">

Start Street 1: <input type="text" id ="street1" name="street1" value=""/><br><br>
<input id="autocomplete1" type="text" disabled="disabled" />
Start Street 2: <input type="text" id = "street2" name="street2" value=""/><br><br>
<input id="autocomplete2" type="text" disabled="disabled" />
<input type="button" value="Submit" id="submit1" onclick="textEnter1()"/>
<input type="button" value="Clear" id="clear1" onclick="clearText1()"/>

<br><br>
Destination Street 1: <input type="text" id ="street3" name="street3" value=""/><br><br>
<input id="autocomplete3" type="text" disabled="disabled" />
Destination Street 2: <input type="text" id = "street4" name="street4" value=""/><br><br>
<input id="autocomplete4" type="text" disabled="disabled" />
<input type="button" value="Submit" id="submit1" onclick="textEnter2()"/>
<input type="button" value="Clear" id="clear2" onclick="clearText2()"/>

</div>

<div id= "enter">
<input type = "button" value="Enter" onclick="shortestPath()">
</div>


</div>
</#assign>
<#include "main.ftl">
