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
    let input = document.getElementById('search').value
    input=input.toLowerCase();
    if(input === 'mars'){
        document.getElementById('error').style.display = "block";
    }
    else{
        document.getElementById("error").style.display = "none";
        loadPopUp(input);
    }
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

        loadPopUp(objectName.toUpperCase());
        pointToObject(objectName, isInsideSolarSystem);
    }
});

document.addEventListener("keypress", function (event){
   if(event.code === 'Enter'){
       search();
   }
});
  