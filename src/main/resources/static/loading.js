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
