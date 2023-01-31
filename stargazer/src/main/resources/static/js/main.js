function pointToObject(objectName, isInsideSolarSystem) {
    let objectWrapper = {"objectName": objectName, "isInsideSolarSystem": isInsideSolarSystem};

    client.publish(
        {
            destination: "/api/pointToObject",
            body: JSON.stringify(objectWrapper),
            headers: standardHeader
        }
    );

    awaitingResponseQueue.push(processResponse);

    function processResponse(response) {
        console.log(response);
        if (!response.body.status === "SUCCESS") {
            // TODO
        }
    }
}

function turnOnLaser() {
    client.publish({
        destination: "/api/turnOnLaser",
        headers: standardHeader
    });

    awaitingResponseQueue.push(processResponse);

    function processResponse(response) {
        console.log(response);
        if (!response.body.status === "SUCCESS") {
            // TODO
        }
    }
}

function turnOffLaser() {
    client.publish({
        destination: "/api/turnOffLaser",
        headers: standardHeader
    });

    awaitingResponseQueue.push(processResponse);

    function processResponse(response) {
        console.log(response);
        if (!String(response.body.status) === "SUCCESS") {
            alert("Could Not Turn Off Laser");
            // TODO
        }
    }
}
