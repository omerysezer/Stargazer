window.addEventListener("load", function(){
    setTimeout(
        function open(event){
            document.querySelector(".popup").style.display = "block";
        },
        500
    )
});

function pair() {
    let submitButton = document.getElementById("submitButton");
    submitButton.disabled = true;

    let output = document.getElementById("pairingResultOutput");
    let sidewaysTurns = document.getElementById("slider1").value;
    let verticalTurns = document.getElementById("slider2").value;
    let laserBlinks = document.getElementById("slider3").value;

    let piId = String(sidewaysTurns) + String(verticalTurns) + String(laserBlinks);

    let oldSessionId = socketHandler.getSessionId();
    let newSessionId = oldSessionId + piId;

    let message = JSON.stringify({"piSessionId": piId});
    let destination = "/api/pair";

    socketHandler.sendMessage(message, destination, handleResponse);

    function handleResponse(response) {
        if (String(response.status) !== "SUCCESS") {
            output.innerHTML = "<b>Failed to pair with the Raspberry Pi.<br>Server Response: \"" + response.message +"\"</b>";
        }
        else{
            let promise = new Promise(function(resolve, reject){
                try{
                    socketHandler.changeSubscription(newSessionId);
                    resolve();
                }
                catch(err){
                    socketHandler.changeSubscription(oldSessionId);
                    reject();
                }
            });

            promise.then(function(){
                output.innerHTML = "<b>Successfully paired with the Raspberry Pi! Press the continue button.</b>";
                submitButton.innerHTML = "Continue";
                submitButton.onclick = () => document.location = 'location.html';
            }).catch(function(){
                output.innerHTML = "<b>Successfully paired to Raspberry Pi but could not reestablish connection to server.</b>";
            });

            submitButton.disabled = false;
        }
    }
}