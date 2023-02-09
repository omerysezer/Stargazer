window.addEventListener("load", function(){
    setTimeout(
        function open(event){
            document.querySelector(".popup").style.display = "block";
        },
        500
    );
});

window.addEventListener("load", function() {
    document.getElementById("subbut").disabled=true;
})

function saveLocation() {
    let output = document.getElementById("out");
    let continueButton = document.getElementById("subbut");
    continueButton.disabled = true;

    if(!navigator.geolocation){
        output.innerHTML = "<p>Geolocation is not supported by your browser</p>";
        return;
    }

    output.innerHTML = "<p>Locating…</p>";


    let promise = new Promise(function(resolve, reject){
        let success = (position) => {
            output.innerHTML = "<p>Found coordinates.<br>" +
                "Longitude: " + position.coords.longitude + "<br>" +
                "Latitiude: " + position.coords.latitude + "</p>";

            resolve({longitude: position.coords.longitude, latitude: position.coords.latitude});
        }
        let error = () => reject();
        navigator.geolocation.getCurrentPosition(success, error);
    });

    promise.then(function(coords){
        let latitude = coords.latitude;
        let longitude = coords.longitude;

        if(longitude == null || latitude == null){
            output.innerHTML = "<p>Failed to get location coordinates.</p>";
            return;
        }

        output.innerHTML += "<p>Sending coordinates to server...</p>";
        let message = JSON.stringify(coords);
        let destination = "/api/saveLocation";
        socketHandler.sendMessage(message, destination, responseHandler);
    }).catch(function (){
        output.innerHTML = '<p>Could not get location data.</p>';
    });

    function responseHandler(response) {
        if (String(response.status) !== "SUCCESS") {
            output.innerHTML = "<p>Failed to save user coordinates. Server Response: \"" + response.message + "\"</p>";
            continueButton.disabled = true;
        } else {
            output.innerHTML = '<p>Coordinates saved. Press "Continue" to begin stargazing!</p>';
            continueButton.disabled = false;
        }
    }
}


