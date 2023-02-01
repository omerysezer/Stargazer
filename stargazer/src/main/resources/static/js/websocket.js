const socketHandler = function () {
    let sessionId = null;
    let client = null;
    let standardHeader = {};
    let awaitingResponseQueue = [];
    let subscription = null;

    initializeSession();
    client.activate();

    let returnObject = {sendMessage, getSessionId, changeSubscription};
    waitForConnection();
    return returnObject;

    function getSessionId() {
        if (!document.cookie.includes("sessionId")) {
            let httpRequest = new XMLHttpRequest();
            httpRequest.open("GET", "http://localhost:443/getSessionId", false);
            httpRequest.send();
            httpRequest.onload = () => {
            };
        }

        return document.cookie.split("=")[1];
    }

    function sendMessage(message, destination, responseHandler){
        client.publish({
            destination: destination,
            body: message,
            headers: standardHeader
        });
        awaitingResponseQueue.push(responseHandler);
    }

    function changeSubscription(endpoint){
        sessionId = endpoint;
        standardHeader.sessionId = endpoint;
        document.cookie = "sessionId=" + endpoint + ";";
        subscription.unsubscribe();
        subscription = client.subscribe("/user/queue/session-" + endpoint, incomingMessageHandler);
        waitForConnection();
    }

    function incomingMessageHandler(message) {
        if(!message.body){
            return;
        }

        let content = JSON.parse(message.body);

        switch (String(content.status)){
            case "CALIBRATION_WARNING":
                alert("Calibration Warning");
                break;
            case "ORIENTATION_WARNING":
                alert("ORIENTATION_WARNING");
                break;
            default:
                if(awaitingResponseQueue.length > 0){
                    let handler = awaitingResponseQueue.pop();
                    handler(content);
                }
                break;
        }
    }

    function initializeSession() {
        sessionId = getSessionId();
        standardHeader = {"sessionId": sessionId};

        client = new StompJs.Client({
            brokerURL: "ws://localhost:443/connect",
            debug: (str) => console.log(str)
        });

        client.onStompError = function (frame) {
            // Will be invoked in case of error encountered at Broker
            // Bad login/passcode typically will cause an error
            // Complaint brokers will set `message` header with a brief message. Body may contain details.
            // Compliant brokers will terminate the connection after any error
            console.log('Broker reported error: ' + frame.headers['message']);
            console.log('Additional details: ' + frame.body);
        };

        client.onConnect = function (frame) {
            subscription = client.subscribe("/user/queue/session-" + sessionId, incomingMessageHandler)
            waitForConnection();
        }
    }

    function waitForConnection() {
        if (!client.connected) {
            setTimeout(waitForConnection, 500);
        }
    }
}();
