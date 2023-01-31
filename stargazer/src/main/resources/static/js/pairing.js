function pair(numSideways, numVertical, numOnOff) {
    let piId = "" + numSideways + "" + numVertical + "" + numOnOff;
    let oldHeader = structuredClone(standardHeader);
    let oldSessionId = oldHeader.sessionId;
    let newSubscriptionId = sessionId + piId;

    client.publish({
        destination: "/api/pair",
        body: piId,
        headers: oldHeader
    });

    changeSessions(newSubscriptionId);
    awaitingResponseQueue.push(processResponse);

    function processResponse(response) {
        console.log(response);
        if (!String(response.body.status) === "SUCCESS") {
            alert("Pairing Was Unsuccessful: " + response.body.reason);
            changeSessions(oldSessionId);
        }
    }

    function changeSessions(newSessionId){
        document.cookie = "sessionId=" + newSessionId;
        sessionId = newSessionId;
        standardHeader.sessionId = newSessionId;
        subscription.unsubscribe();
        subscription = client.subscribe("/user/queue/session-" + newSessionId, incomingMessageHandler);

        waitForConnection(() => {});
    }
}