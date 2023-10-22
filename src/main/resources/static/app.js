var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS("/my-websocket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log("Connected: " + frame);
        stompClient.subscribe("/topic/messages", function (message) {
            showMessage(
                JSON.parse(message.body).content,
                JSON.parse(message.body).username
            );
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showMessage(message, username) {
    $("#messages").append(
        "<tr><td>" + username + " -- " + message + "</td></tr>"
    );
}

$(function () {
    connect(); // Automatically connect when the page loads

    $("form").on("submit", function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
});
