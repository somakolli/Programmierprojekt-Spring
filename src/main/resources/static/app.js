var stompClient = null;
var mymap = null;
var src = null;
var trgt = null;

function connect() {
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

function switchView() {
    $("#loading").hide();
    $("#main-content").show();
}

function checkGraphStatus() {
    $.get("graphStatus", function (graphStatus) {
        if(graphStatus){
            switchView();
        }else{
            connect();
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



function onMapClick(e) {
    var lon = e.latlng.lng;
    var lat = e.latlng.lat;
    $.get("closestNode", {lon: lon, lat: lat}, function(data){
        if($("#srcRadio").is(":checked")){
            L.marker([data.lat, data.lon]).addTo(mymap)
                .bindPopup('Source')
                .openPopup();
            src = data.id;
            $("#srcLabel").html("Source: " +  data.id);
        }else{
            L.marker([data.lat, data.lon]).addTo(mymap)
                .bindPopup('Target')
                .openPopup();
            $("#trgtLabel").html("Target: " + data.id);
            trgt = data.id;
        }
    });
}

function drawRoute() {
    var line = null;
    $.get("path", {src: src, trgt: trgt}, function(data){
        line = {
            "type": "LineString",
            "coordinates": data
        };

        var myStyle = {
            "color": "#ff7800",
            "weight": 5,
            "opacity": 0.65
        };
        L.geoJSON(line, {
            style: myStyle
        }).addTo(mymap);
    });

}

$(function () {
    checkGraphStatus();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#getDistance" ).click(function() { getDistance(); });
    $("#getClosestNode").click(function(){ getClosestNode();});
    $("#drawRoute").click(function(){drawRoute();});

    mymap = L.map('mapid').setView([51.505, -0.09], 13);

    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1IjoicDRjaDFuMCIsImEiOiJjamRtMzN0Mm4wYWkwMnFucHE2bTFtZnh0In0.RQcP1SPjgfYAbir206CWmg'
    }).addTo(mymap);
    mymap.on('click', onMapClick);
    $('#setSource').click(function(){setSource();});
});
