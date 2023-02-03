function getWarningHandler() {
    let calibrationWarningActive = false;
    let levelWarningActive = false;
    let orientationWarningActive = false;

    if(document.location.pathname.includes("pairing.html") || document.location.pathname.includes("location.html")){
        return {
            raiseCalibrationWarning: () => {},
            raiseLevelWarning: () => {},
            raiseOrientationWarning: () => {},
            clearCalibrationWarning: () => {},
            clearLevelWarning: () => {},
            clearOrientationWarning: () => {}
        }
    }

    return {
        raiseCalibrationWarning: () => raiseWarning("CALIBRATION"),
        raiseLevelWarning: () => raiseWarning("LEVEL"),
        raiseOrientationWarning: () => raiseWarning("ORIENTATION"),
        clearCalibrationWarning: () => clearWarning("CALIBRATION"),
        clearLevelWarning: () => clearWarning("LEVEL"),
        clearOrientationWarning: () => clearWarning("ORIENTATION")
    };

    function raiseWarning(warningType) {
        let warningPopup = document.getElementById("warningPopup");
        if (warningPopup == null) {
            return;
        }

        // check if the warning popup is already being displayed
        if (warningPopup.style.display !== 'block') {
            warningPopup.style.display = 'block';
        }

        let message = '';
        let elementName = '';
        switch (warningType){
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

        document.getElementById(elementName).innerHTML = message;
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

        if(!calibrationWarningActive && !orientationWarningActive && !levelWarningActive){
            let popup = document.getElementById("warningPopup");
            popup.style.display = 'none';
        }
    }
}

