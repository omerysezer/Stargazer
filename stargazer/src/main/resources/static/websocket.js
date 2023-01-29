function getCookie(){
    if(!document.cookie.includes("sessionId")){
        var httpRequest = new XMLHttpRequest();
        httpRequest.open("GET", "http://localhost:443/getSessionId", false);
        httpRequest.send();
        httpRequest.onload = () => {};
    }
}

getCookie();

let ident = document.cookie.split("=")[1];
let standardHeader = {"sessionId": ident};

const client = new StompJs.Client({});
client.brokerURL = 'ws://localhost:443/connect';
client.debug = function (str) {
    console.log(str);
};

client.onConnect = function (frame) {
    let callback = function (message) {
        if(message.body){
            alert("received: " + message.body);
        }
        else{
            alert("empty message");
        }
    };

    let subscription = client.subscribe("/user/queue/chat-" + ident, callback, {});
};

client.onStompError = function (frame) {
    // Will be invoked in case of error encountered at Broker
    // Bad login/passcode typically will cause an error
    // Complaint brokers will set `message` header with a brief message. Body may contain details.
    // Compliant brokers will terminate the connection after any error
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
};

client.activate();

function saveLocation(long, lat){
    let coordinates = {"longitude": long, "latitude": lat};

    client.publish(
        {
            destination:"/api/saveLocation",
            body: JSON.stringify(coordinates),
            headers: standardHeader
        }
    );
}

function pointToObject(objectName, isInsideSolarSystem) {
    let objectWrapper = {"objectName": objectName, "isInsideSolarSystem": isInsideSolarSystem};

    client.publish(
        {
            destination: "/api/pointToObject",
            body: JSON.stringify(objectWrapper),
            headers: standardHeader
        }
    );
}

function turnOnLaser(){
    client.publish({
        destination: "/api/turnOnLaser",
        headers: standardHeader
    });
}

function turnOffLaser() {
    client.publish({
        destination: "/api/turnOffLaser",
        headers: standardHeader
    });
}

function pair(numSideways, numVertical, numOnOff){
    let piId = "" + numSideways + "" + numVertical + "" + numOnOff;
    client.publish({
        destination: "/api/pair",
        body: JSON.stringify({"piIdNumber": piId}),
        headers: standardHeader
    });
}