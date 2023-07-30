function _lookup(inputId, entity, invalidClass) {
    let inputElement = document.getElementById(inputId);
    let inputValue = inputElement.value;
    inputElement.classList.remove("bg-success-subtle");
    inputElement.classList.add(invalidClass);

    let inputElementSlug = document.getElementById(inputId + '-slug');
    if (inputElementSlug) {
        inputElementSlug.value="";
    }

    if (inputValue.length > 2 ) {
     
        let searchList = document.getElementById('list-' + inputId);
        if (searchList === null) {
            searchList = document.createElement("ul");
            searchList.id = 'list-' + inputId;
            searchList.className="list-lookup";
            inputElement.parentElement.append(searchList);
        }
  
        const httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = () => {
            if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                    let resultsHtml = "";
                    let data = JSON.parse(httpRequest.responseText);
                    for (let i = 0; i < data.matches.length; i++) {
                        let displayText = "<b>" + data.matches[i].name + "</b> <small>" + data.matches[i].context + "</small>";
                        resultsHtml += "<li style='cursor:pointer' onclick=\"fill('" + inputId + "', '" + data.matches[i].slug + "', '" + data.matches[i].name + "');\">" + displayText + "</li>";
                    }
                    searchList.innerHTML = resultsHtml;
                }
            }
        };
        httpRequest.open("GET", "/lookup/" + entity + "/data.json?s=" + inputValue, true);
        httpRequest.send();
    }
}

function lookup(inputId, entity) {
    _lookup(inputId, entity, 'bg-warning-subtle');
}

function lookupMandatory(inputId, entity) {
    _lookup(inputId, entity, 'bg-danger-subtle');
}