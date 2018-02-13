var stompClient = null;


function connect() {
    var socket = new SockJS('/pp-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/graphStatus', function (graphStatus) {
            switchView();
            disconnect();
        });
    });
}

function switchView() {
    $("#loading").hide();
    $("#distance-form").show();
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

$(function () {
    checkGraphStatus();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#getDistance" ).click(function() { getDistance(); });
});
