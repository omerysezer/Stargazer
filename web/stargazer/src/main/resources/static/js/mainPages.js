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
    let objectType = objectTypeChecker.getObjectType();
    pointToObject(input, "UNKNOWN", objectType);
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
        let objectType = objectTypeChecker.getObjectType();
        let objectName = document.getElementById(id).innerText;

        loadPopUp(objectName);
        let objectId = document.getElementById(id.replace("pointButton", "objectId")).innerText;
        pointToObject(objectName, objectId, objectType);
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