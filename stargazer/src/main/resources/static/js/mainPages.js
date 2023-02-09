function loadPopUp(input) {
    var x = document.getElementById('popup');
    document.getElementById("popupTitle").innerHTML = input;
    x.style.display = 'block';
    $(".fullscreen-container").fadeTo(200, 1);
}

function search() {
    let input = document.getElementById('search').value.toUpperCase();

    if(!input){
        return;
    }

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
    if(id === "searchBarSubmit"){
        search();
    }
    if(id.startsWith("pointButton")){
        let popup = document.getElementById("popup");
        let warning = document.getElementById("warningPopup");
        let isInsideSolarSystem = insideSolarSystemChecker.checkIfInsideSolarSystem();
        let objectName = document.getElementById(id).innerText;

        loadPopUp(objectName);
        let objectId = document.getElementById(id.replace("pointButton", "objectId")).innerText;
        pointToObject(objectName, objectId, isInsideSolarSystem);
    }
});

document.addEventListener("keypress", function (event){
   if(event.code === 'Enter' && document.activeElement.id === 'search'){
       search();
   }
});

function reset(){
    $(".fullscreen-container").fadeOut(200);
}