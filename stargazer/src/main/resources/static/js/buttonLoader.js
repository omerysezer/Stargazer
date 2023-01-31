let json = {
    "planet.html": [
        {objectName: "Mercury", objectImage:"../images/planets/mercury.jpg"},
        {objectName: "Venus", objectImage: "../images/planets/venus.jpg"},
        {objectName: "Moon", objectImage: "../images/planets/moon.jpg"},
        {objectName: "Mars", objectImage: "../images/planets/mars.jpg"},

    ],
    "star.html": [

    ],
    "galaxy.html": [

    ]
};

loadButtons(json);

function loadButtons(json){
    let buttonHolder = document.getElementById("buttonContainer");
    let standardButtonHtml = '' +
        '<div class="w3-third w3-margin-bottom">\n' +
        '          <img src=IMAGE_URL alt="Planet" style="width:100%;object-fit:cover;">\n' +
        '          <div class="w3-container w3-white">\n' +
        '            <br>\n' +
        '            <button id=BUTTON_ID class="w3-button w3-black w3-margin-bottom">PLANET_NAME</button>\n' +
        '            <br>\n' +
        '            <br>\n' +
        '          </div>\n' +
        '        </div>';

    let currentPage = window.location.pathname.split("/").pop();
    json[currentPage].forEach(addButton);

    function addButton(objectImagePair, id){
        let objectName = objectImagePair.objectName;
        let objectImageUrl = objectImagePair.objectImage;

        let buttonHtml = standardButtonHtml.replace("IMAGE_URL", objectImageUrl)
            .replace("PLANET_NAME", objectName)
            .replace("BUTTON_ID", "pointButton" + id);

        buttonHolder.innerHTML += buttonHtml;
    }
}

