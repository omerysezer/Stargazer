$.getJSON('/js/objects.json', loadButtons);

function loadButtons(objectsJson){
    let buttonHolder = document.getElementById("buttonContainer");

    let standardButtonHtml = '<div class="test_box box column">' +
        '          <div class="inner" style="background-image: IMAGE_URL;">' +
        '            <a class="test_click">' +
        '              <div class="flex_this">' +
        // '                <h1 class="test_title">Title</h1>' +
        '                <button id=BUTTON_ID class="test_link">OBJECT_NAME</button>\n' +
        '              </div>\n' +
        '            </a>\n' +
        '          </div>\n' +
        '        </div>';

    let currentPage = window.location.pathname.split("/").pop();
    let imageFolder = objectsJson[currentPage]["imageFolder"];
    objectsJson[currentPage]["objects"].forEach(addButton);

    function addButton(objectName, id){
        let objectImageUrl = "url(" + imageFolder + "/" + objectName + ".jpg);";
        let objectButtonId = "pointButton" + id;
        let buttonHtml = standardButtonHtml.replace("IMAGE_URL", objectImageUrl)
            .replace("OBJECT_NAME", objectName)
            .replace("BUTTON_ID", objectButtonId);

        buttonHolder.innerHTML += buttonHtml;
    }
}