var stompClient = null;
var mymap = null;
var src = null;
var trgt = null;
var srcMarker = null;
var trgtMarker = null;
var path = null;


function setSource(id, lat, lon) {
    if(srcMarker!==null){
        mymap.removeLayer(srcMarker);
    }
    srcMarker = new L.Marker([lat, lon]);
    mymap.addLayer(srcMarker);
    srcMarker.bindPopup('Source')
        .openPopup();
    src = id;
    $("#srcLabel").html('Source(id:' + id + ';lat: ' + lat + ';lon: ' + lon + ')');
}
function setTarget(id, lat, lon) {
    if(trgtMarker!==null){
        mymap.removeLayer(srcMarker);
    }
    trgtMarker = new L.Marker([lat, lon]);
    mymap.addLayer(trgtMarker);
    trgtMarker.bindPopup('Target')
        .openPopup();
    trgt = id;
    $("#trgtLabel").html('Target(id:' + id + ';lat: ' + lat + ';lon: ' + lon + ')');
}



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
    setTimeout(function(){ mymap.invalidateSize()}, 400);
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
    var src = parseInt($("#src").val());
    var trgt = parseInt($("#trgt").val());
    var ids = [src, trgt];

    $.get("coordinates?ids=" + src + "&" + "ids=" + trgt, function(data){
        setSource(src, data[0][1], data[0][0]);
        setTarget(trgt, data[1][1], data[1][0]);
        drawRoute();
    });
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
        if(path!==null) mymap.removeLayer(path);
        if($("#srcRadio").is(":checked")){
            setSource(data.id, data.lat, data.lon)
        }else{
            setTarget(data.id, data.lat, data.lon)
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
        if(data.path[0] === null){
            trgtMarker.bindPopup('No Path')
                .openPopup();
        }
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
    setTimeout(function(){ mymap.invalidateSize()}, 400);

    $( "#getDistance" ).click(function() { getDistance(); });
    $("#getClosestNode").click(function(){ getClosestNode();});
    $("#drawRoute").click(function(){ drawRoute(); showDistance();});


    mymap.on('click', onMapClick);
    $('#setSource').click(function(){setSource();});
});
