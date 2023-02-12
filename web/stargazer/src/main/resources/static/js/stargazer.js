let objectTypeChecker = function(){
    let objectTypes = {
        "planet.html": "PLANET",
        "asteroid.html": "ASTEROID",
        "comet.html": "COMET",
        "star.html": "STAR",
        "galaxy.html": "GALAXY"
    };

    return {
        getObjectType: function(){
            let currentPage = window.location.pathname.split("/").pop();
            return objectTypes[currentPage] ? objectTypes[currentPage] : "";
        }
    };
}();

function pointToObject(objectName, objectId, objectType) {
    let output = document.getElementById("outputText");

    output.innerHTML = "Pointing to " + objectName.toUpperCase();

    let destination = "/api/pointToObject";
    let message = JSON.stringify({"objectName": objectName, "objectId": objectId, "objectType": objectType});
    socketHandler.sendMessage(message, destination, responseHandler);

    function responseHandler(response){
        if(String(response.status) === "SUCCESS"){
            output.innerHTML = "";
            return;
        }

        output.innerHTML = "Failed to point to " + objectName + ".<br>" +
            "Server Response: \"" + response.message + "\"";

    }
}

function turnOnLaser(){
    switchLaser(true);
}

function turnOffLaser(){
    switchLaser(false);
}

function switchLaser(on){
    let output = document.getElementById("outputText");

    let laserState = "off";
    let destination = "/api/turnOffLaser";
    let message = '';

    if(on){
        laserState = "on";
        destination = "/api/turnOnLaser";
    }

    socketHandler.sendMessage(message, destination, responseHandler);

    function responseHandler(response){
        if(String(response.status) === "SUCCESS"){
            return;
        }

        output.innerHTML = "Failed to turn laser " + laserState + ".<br>" +
            "Server Response: \"" + response.message + "\"";
    }
}
