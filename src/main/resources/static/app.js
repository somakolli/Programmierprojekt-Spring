var stompClient = null;
var mymap = null;
var src = null;
var trgt = null;
var srcMarker = null;
var trgtMarker = null;
var path = null;

//subscribe to get messages about the loading status of the graph
function subscribeToGraphStatus() {
    var socket = new SockJS('/pp-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/graphStatus', function (graphStatus){
            if(graphStatus.body==="true"){
                switchView();
                disconnect();
            }
            else{
                $("#graphStatus").text(graphStatus.body);
            }
        });
    });
}

//switch view if the graph is loaded
function switchView() {
    $("#loading").hide();
    $("#main-content").show();
}

//if the graph is already loaded switch the view else
function checkGraphStatus() {
    $.get("graphStatus", function (graphStatus) {
        if(graphStatus){
            switchView();
        }else{
            subscribeToGraphStatus();
        }
    })
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function getDistance(){
    var src = $("#src").val();
    var trgt = $("#trgt").val();
    $.get("distance", {src: src, trgt: trgt}, function(data){
        $("#distance-form").append("<p>Distance from " + src + " to " + trgt + ": " + data + "</p>");
        $.get("path", {src: src, trgt: trgt}, function(data){
            $("#distance-form").append("<p>Path from " + src + " to " + trgt + ": " + data + "</p>");
        })
    })
}

function getClosestNode() {
    var lon = $("#lon").val();
    var lat = $("#lat").val();
    $.get("closestNode", {lon: lon, lat: lat}, function(data){
        $("#distance-form").append("<p>Closest node from (" + lon + "," + lat + "): " + JSON.stringify(data) + "</p>");
    });
}


//handles the mapclick event
//if the source radio button is selected it changes the source
//if the target radio button ist selected it changes the target
function onMapClick(e) {
    var lon = e.latlng.lng;
    var lat = e.latlng.lat;
    $.get("closestNode", {lon: lon, lat: lat}, function(data){
        if($("#srcRadio").is(":checked")){
            if(srcMarker!==null){
                mymap.removeLayer(srcMarker);
            }
            srcMarker = new L.Marker([data.lat, data.lon]);
            mymap.addLayer(srcMarker);
            srcMarker.bindPopup('Source')
                .openPopup();
            src = data.id;
            $("#srcLabel").html('Source(id:' + data.id + ';Lat:' + data.lat + ';Lon: ' + data.lon + ')');
        }else{
            if(trgtMarker!==null){
                mymap.removeLayer(trgtMarker);
            }
            trgtMarker = new L.Marker([data.lat, data.lon],{color: 'green'});
            mymap.addLayer(trgtMarker);
            trgtMarker.bindPopup('Target')
                .openPopup();
            $("#trgtLabel").html('Target(id:' + data.id + ';Lat:' + data.lat + ';Lon: ' + data.lon + ')');
            trgt = data.id;
        }
    });
}

//requests the route given a source and a target and draws it
function drawRoute() {
    //remove already drawn path
    if(path!==null) mymap.removeLayer(path);
    var line = null;
    //http get request to '/path?src={src}&trgt={trgt}' returns array with coordinates
    $.get("path", {src: src, trgt: trgt}, function(data){
        //geoJSON line
        line = {
            "type": "LineString",
            "coordinates": data.path
        };
        var myStyle = {
            "color": "#ff7800",
            "weight": 5,
            "opacity": 0.65
        };
        path = L.geoJSON(line, {
            style: myStyle
        });
        path.addTo(mymap);
        trgtMarker.bindPopup('Distance: ' + data.distance)
            .openPopup();
    });
}

function showDistance() {

}

//'Main' function
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    checkGraphStatus();

    mymap = L.map('mapid').setView([50.708,9.799], 6);

    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1IjoicDRjaDFuMCIsImEiOiJjamRtMzN0Mm4wYWkwMnFucHE2bTFtZnh0In0.RQcP1SPjgfYAbir206CWmg'
    }).addTo(mymap);


    $( "#getDistance" ).click(function() { getDistance(); });
    $("#getClosestNode").click(function(){ getClosestNode();});
    $("#drawRoute").click(function(){ drawRoute(); showDistance();});


    mymap.on('click', onMapClick);
    $('#setSource').click(function(){setSource();});
});
