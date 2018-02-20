/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */

var src = null;
var trgt = null;
var mymap = null;
var srcMarker = null;
var trgtMarker = null;
var path = null;

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    checkGraphStatus();
    mymap = L.map('mapid').setView([50.708,9.799], 6);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png ', {
        maxZoom: 18
    }).addTo(mymap);
    setTimeout(function(){ mymap.invalidateSize()}, 400);

    $("#drawRoute").click(function(){ drawRoute(src, trgt)});
    $("#getClosestNode").click(function(){ getClosestNode();});

    mymap.on('click', onMapClick);
});


function getClosestNode() {
    var lon = $("#lon").val();
    var lat = $("#lat").val();
    $.get("closestNode", {lon: lon, lat: lat}, function(data){
        $("#closestNodeForm").append("<p>Closest node from (" + lon + "," + lat + "): " + JSON.stringify(data) + "</p>");
    });
}
//requests the route given a source and a target and draws it
function drawRoute(src, trgt) {
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

        if(data.path.length === 0){
            trgtMarker.bindPopup('No Path')
                .openPopup();
        } else {
            trgtMarker.bindPopup('Distance: ' + data.distance)
                .openPopup();
        }
    });
}

function getCoordinatesFromId(id) {
    $.get("coordinates?ids=" + id, function(data){
        var coordinates = getCoordinatesFromId(id);
        lat = coordinates[0][0];
        lon = coordinates[0][1];
        trgtMarker = new L.Marker([lat, lon]);
        mymap.addLayer(trgtMarker);
        trgtMarker.bindPopup('Target')
            .openPopup();
        trgt = id;
        $("#trgtLabel").html('Target(id:' + id + ';lat: ' + lat + ';lon: ' + lon + ')');
    });
}

function setSource(id) {
    if(path!==null) mymap.removeLayer(path);
    if(srcMarker!==null){
        mymap.removeLayer(srcMarker);
    }
    $('#src').val(id);
    $.get("coordinates?ids=" + id, function(data){
        if(data ===null || data[0] === null) {
            $("#srcLabel").html('Source');
            src = null;
            srcMarker = null;
            return null;
        }
        lon = data[0][0];
        lat = data[0][1];
        srcMarker = new L.Marker([lat, lon]);
        mymap.addLayer(srcMarker);
        srcMarker.bindPopup('Source')
            .openPopup();
        src = id;

        $("#srcLabel").html('Source(id:' + id + ';lat: ' + lat + ';lon: ' + lon + ')');
    });
}
function setTarget(id) {
    if(path!==null) mymap.removeLayer(path);
    trgt = id;
    $('#trgt').val(id);
    if(trgtMarker!==null){
        mymap.removeLayer(trgtMarker);
    }
    $.get("coordinates?ids=" + id, function(data){
        if(data ===null || data[0] === null) {
            $("#trgtLabel").html('Target');
            trgt = null;
            trgtMarker = null;
            return null;
        }
        lon = data[0][0];
        lat = data[0][1];
        trgtMarker = new L.Marker([lat, lon]);
        mymap.addLayer(trgtMarker);
        trgtMarker.bindPopup('Target')
            .openPopup();
        $("#trgtLabel").html('Target(id:' + id + ';lat: ' + lat + ';lon: ' + lon + ')');
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
            setSource(data.id, data.lat, data.lon)
        }else{
            setTarget(data.id, data.lat, data.lon)
        }
    });

}

function updateTarget() {
    setTarget($('#trgt').val());
}

function updateSource() {
    setSource($('#src').val());
}