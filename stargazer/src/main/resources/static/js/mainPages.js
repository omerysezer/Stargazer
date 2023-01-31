function loadPopUp(input) {
    var x = document.getElementById('myDIV');
    document.getElementById("tempText1").innerHTML = input.toUpperCase();
    x.style.display = 'block';

}

document.querySelector("#close").addEventListener("click", function(){
    document.querySelector(".popup").style.display = "none";
});

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
  