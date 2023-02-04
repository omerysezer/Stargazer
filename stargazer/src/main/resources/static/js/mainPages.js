if(window.location.pathname.split("/").pop() === "star.html"){
    window.addEventListener("load", function() {
        document.getElementById("error").style.display = "none";
    });
}


function loadPopUp(input) {
    var x = document.getElementById('popup');
    document.getElementById("popupTitle").innerHTML = input;
    x.style.display = 'block';
}

function search() {
    let input = document.getElementById('search').value.toUpperCase();

    loadPopUp(input);
    // no id for the object is specified besides the name
    // send the name as id in hopes the api can recognize that name
    pointToObject(input, input, false);
}

document.addEventListener("click", function (event){
    let id = String(event.target.id);
    if(id === "close"){
        document.querySelector(".popup").style.display = "none";
        return;
    }
    if(id.startsWith("pointButton")){
        let isInsideSolarSystem = insideSolarSystemChecker.checkIfInsideSolarSystem();
        let objectName = document.getElementById(id).innerText;

        loadPopUp(objectName);
        let objectId = document.getElementById(id.replace("pointButton", "objectId")).innerText;
        pointToObject(objectName, objectId, isInsideSolarSystem);
    }
});

document.addEventListener("keypress", function (event){
   if(event.code === 'Enter'){
       search();
   }
});
  