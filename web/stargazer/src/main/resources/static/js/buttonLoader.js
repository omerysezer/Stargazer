$.getJSON('/js/objects.json', loadButtons);

function loadButtons(objectsJson){
    let buttonHolder = document.getElementById("buttonContainer");
    let creditHolder = document.getElementById("credits");

    let standardCreditHtml = '<li class="credit">CREDIT</li>';

    let standardButtonHtml = '<div class="test_box box column">' +
        '          <div class="inner" style="background-image: IMAGE_URL; background-size: BACKGROUND_TYPE">' +
        '            <a class="test_click">' +
        '              <div class="flex_this">' +
        '                   <span style="display: none" id="OBJECT_ID_HOLDER">OBJECT_ID</span>' +
        '                <button id="BUTTON_ID" class="test_link">OBJECT_NAME</button>\n' +
        '              </div>\n' +
        '            </a>\n' +
        '          </div>\n' +
        '        </div>';

    let currentPage = window.location.pathname.split("/").pop();
    let imageFolder = objectsJson[currentPage]["imageFolder"];
    let fallbackBackgroundFillType = objectsJson[currentPage]["fallbackBackgroundFillType"];
    objectsJson[currentPage]["objects"].forEach(addButton);

    function addButton(objectWrapper, index){
        let name = objectWrapper["name"].toUpperCase().replaceAll("_", " ");
        let objectId = objectWrapper["id"];
        let url = objectWrapper["imageUrl"];
        let backgroundFillType = objectWrapper["backgroundFillType"] ? objectWrapper["backgroundFillType"] : fallbackBackgroundFillType;
        let credits = objectWrapper["credit"];

        let objectImageUrl = "url('" + url + "');";
        if(!url){
            let ext = objectWrapper["imageExtension"];
            objectImageUrl = "url('" + imageFolder + "/" + name + ext + "');";
        }

        let objectButtonId = "pointButton" + index;
        let objectIdHolder = "objectId" + index;
        let buttonHtml = standardButtonHtml.replace("IMAGE_URL", objectImageUrl)
            .replace("OBJECT_NAME", name)
            .replace("BUTTON_ID", objectButtonId)
            .replace("BACKGROUND_TYPE", backgroundFillType)
            .replace("OBJECT_ID_HOLDER", objectIdHolder)
            .replace("OBJECT_ID", objectId);

        let creditHtml = standardCreditHtml.replace("CREDIT", name + ': ' + credits);
        buttonHolder.innerHTML += buttonHtml;
        creditHolder.innerHTML += creditHtml;
        console.log(creditHolder);
    }
}