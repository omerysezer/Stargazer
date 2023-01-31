function loadPopUp(input) {
    var x = document.getElementById('myDIV');
    document.getElementById("tempText1").innerHTML = input.toUpperCase();
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

window.addEventListener("load", function() {
    document.getElementById("error").style.display = "none";
})

document.addEventListener("click", function (event){
    let id = String(event.target.id);
    alert(id);
    if(id === "close"){
        document.querySelector(".popup").style.display = "none";
        return;
    }
    if(id.startsWith("pointButton")){
        let isInsideSolarSystem = insideSolarSystemChecker.checkIfInsideSolarSystem();
        let objectName = document.getElementById(id).innerText;
        pointToObject(objectName, isInsideSolarSystem);
    }
});
  