const socketHandler = function () {
    let sessionId = null;
    let client = null;
    let awaitingResponseQueue = {};
    let subscription = null;
    let warningHandler = getWarningHandler();

    initializeSession();
    client.activate();
    waitForConnection();
    return {sendMessage, getSessionId, changeSubscription};

    function getSessionId() {
        if (!document.cookie.includes("sessionId")) {
            let httpRequest = new XMLHttpRequest();
            httpRequest.open("GET", "https://stargazer.ninja/getSessionId", false);
            httpRequest.send();
            httpRequest.onload = () => {
            };
        }

        return document.cookie.split("=")[1];
    }

    function sendMessage(message, destination, responseHandler){
        let messageId = String(window.performance.now());
        awaitingResponseQueue[messageId] = responseHandler;
        client.publish({
            destination: destination,
            body: message,
            headers: {
                sessionId: sessionId,
                messageId: messageId
            }
        });
    }

    function changeSubscription(endpoint){
        sessionId = endpoint;
        document.cookie = "sessionId=" + endpoint + ";";
        subscription.unsubscribe();
        subscription = client.subscribe("/user/queue/session-" + endpoint, incomingMessageHandler);
        waitForConnection();
    }

    function incomingMessageHandler(message) {
        if(!message.body){
            return;
        }

        let messageBody = JSON.parse(message.body);
        let messageId = messageBody["messageId"];
        if(messageId !== '' && Object.keys(awaitingResponseQueue).includes(messageId)){
            awaitingResponseQueue[messageId](messageBody);
            delete awaitingResponseQueue[messageId];
            return;
        }

        switch (String(messageBody.status)){
            case "CALIBRATION_WARNING":
                warningHandler.raiseCalibrationWarning();
                break;
            case "ORIENTATION_WARNING":
                warningHandler.raiseOrientationWarning();
                break;
            case "LEVEL_WARNING":
                warningHandler.raiseLevelWarning();
                break;
            case "CALIBRATION_OK":
                warningHandler.clearCalibrationWarning();
                break;
            case "ORIENTATION_OK":
                warningHandler.clearOrientationWarning();
                break;
            case "LEVEL_OK":
                warningHandler.clearLevelWarning();
                break;
        }
    }

    function initializeSession() {
        sessionId = getSessionId();

        client = new StompJs.Client({
            brokerURL: "wss://stargazer.ninja/connect/",
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
        }
    }

    function waitForConnection() {
        if (!client.connected) {
            setTimeout(waitForConnection, 500);
        }
    }
}();
