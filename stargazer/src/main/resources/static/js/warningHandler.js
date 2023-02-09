function getWarningHandler() {
    let calibrationWarningActive = false;
    let levelWarningActive = false;
    let orientationWarningActive = false;
    let disconnectWarningActive = false;

    if(document.location.pathname.includes("pairing.html") || document.location.pathname.includes("location.html")){
        return {
            raiseCalibrationWarning: () => {},
            raiseLevelWarning: () => {},
            raiseOrientationWarning: () => {},
            handleDisconnect: () => raiseWarning("DISCONNECT"),
            clearCalibrationWarning: () => {},
            clearLevelWarning: () => {},
            clearOrientationWarning: () => {}
        }
    }

    return {
        raiseCalibrationWarning: () => raiseWarning("CALIBRATION"),
        raiseLevelWarning: () => raiseWarning("LEVEL"),
        raiseOrientationWarning: () => raiseWarning("ORIENTATION"),
        handleDisconnect: () => raiseWarning("DISCONNECT"),
        clearCalibrationWarning: () => clearWarning("CALIBRATION"),
        clearLevelWarning: () => clearWarning("LEVEL"),
        clearOrientationWarning: () => clearWarning("ORIENTATION")
    };

    async function raiseWarning(warningType) {
        let warningPopup = document.getElementById("warningPopup");
        if (warningPopup == null) {
            return;
        }

        let message = '';
        let elementName = '';
        switch (warningType) {
            case "DISCONNECT":
                message = "LOST CONNECTION TO SERVER AND RASPBERRY PI! SENDING YOU BACK TO PAIRING PAGE.<br>";
                elementName = "disconnectWarningText";
                document.cookie = "sessionId=; Max-Age=-99999999;";
                disconnectWarningActive = true;
                break;
            case "CALIBRATION":
                message = "WARNING: THE RASPBERRY PI IS NOT CALIBRATED! THIS WILL AFFECT ITS ACCURACY!<br>";
                elementName = "calibrationWarningText";
                calibrationWarningActive = true;
                break;
            case "LEVEL":
                message = "WARNING: THE RASPBERRY PI IS NOT LEVEL ON THE GROUND! THIS WILL AFFECT ITS ACCURACY!<br>";
                elementName = "levelWarningText";
                levelWarningActive = true;
                break;
            case "ORIENTATION":
                message = "WARNING: THE RASPBERRY PI IS NOT FACING NORTH! THIS WILL AFFECT ITS ACCURACY<br>";
                elementName = "orientationWarningText";
                orientationWarningActive = true;
                break;
        }

        displayWarningPopup(message, elementName);

        // forces js to wait 5 seconds
        if(warningType === "DISCONNECT"){
            clearWarning("CALIBRATION");
            clearWarning("ORIENTATION");
            clearWarning("LEVEL");
            await new Promise(resolve => setTimeout(() => document.location = 'pairing.html', 5000)).then();
        }
    }

    function clearWarning(warning){
        let warningPopup = document.getElementById("warningPopup");
        if(warningPopup == null){
            return;
        }

        if(warning === "CALIBRATION"){
            let calibrationWarning = document.getElementById("calibrationWarningText");
            calibrationWarning.innerHTML = '';
            calibrationWarningActive = false;
        }
        else if(warning === "LEVEL"){
            let levelWarning = document.getElementById("levelWarningText");
            levelWarning.innerHTML = '';
            levelWarningActive = false;
        }
        else if(warning === "ORIENTATION"){
            let orientationWarning = document.getElementById("orientationWarningText");
            orientationWarning.innerHTML = '';
            orientationWarningActive = false;
        }

        if(!calibrationWarningActive && !orientationWarningActive && !levelWarningActive && !disconnectWarningActive){
            takeDownWarningPopup();
        }
    }

    function displayWarningPopup(message, element){
        let warningPopup = document.getElementById("warningPopup");
        document.getElementById(element).innerHTML = message;
        // check if the warning popup is already being displayed
        if (warningPopup.style.display !== 'block') {
            warningPopup.style.display = 'block';
            $(".fullscreen-warning-container").fadeTo(200, 1);
        }
    }

    function takeDownWarningPopup(){
        let warningPopup = document.getElementById("warningPopup");

        if(warningPopup.style.display !== 'none'){
            warningPopup.style.display = 'none';
            $(".fullscreen-warning-container").fadeOut(200);
        }
    }
}

