function fetchData(entity, inputValue) {
    const httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = () => {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                let key = entity + '-' + inputValue.toLowerCase();
                sessionStorage.setItem(key, httpRequest.responseText);
            }
        }
    };
    httpRequest.open("GET", "/lookup/" + entity + "/data.json?s=" + inputValue, true);
    httpRequest.send();
}

function showData(entity, inputValue){
    let key = entity + '-' + inputValue.substring(0,3).toLowerCase();
    let matchData = sessionStorage.getItem(key);
    if (matchData) {
        let resultsHtml = "";
        let data = JSON.parse(matchData);
        for (let i = 0; i < data.matches.length; i++) {
            if (data.matches[i].name.toLowerCase().includes(inputValue.toLowerCase())) {
                let displayText = "<b>" + data.matches[i].name + "</b> <small>" + data.matches[i].context + "</small>";
                resultsHtml += "<li style='cursor:pointer' onclick=\"fill('" + inputId + "', '" + data.matches[i].slug + "', '" + data.matches[i].name + "');\">" + displayText + "</li>";
            }
        }
        searchList.innerHTML = resultsHtml;
    }
}

function _lookup(inputId, entity, invalidClass) {
    let inputElement = document.getElementById(inputId);
    let inputValue = inputElement.value;
    inputElement.classList.remove("bg-success-subtle");
    inputElement.classList.add(invalidClass);

    let inputElementSlug = document.getElementById(inputId + '-slug');
    if (inputElementSlug) {
        inputElementSlug.value="";
    }

    if (inputValue.length == 0) {
        inputElement.classList.remove("bg-warning-subtle");
        inputElement.classList.remove("bg-danger-subtle");
    }

    if (inputValue.length > 2 ) {
     
        let searchList = document.getElementById('list-' + inputId);
        if (searchList === null) {
            searchList = document.createElement("ul");
            searchList.id = 'list-' + inputId;
            searchList.className="list-lookup";
            searchList.innerHTML = "<li>loading...</li>";
            inputElement.parentElement.append(searchList);
        }

        if (inputValue.length == 3) {
            fetchData(entity, inputValue);
        } else {
            showData(entity, inputValue);
        }
    }
}

function lookup(inputId, entity) {
    _lookup(inputId, entity, 'bg-warning-subtle');
}

function lookupMandatory(inputId, entity) {
    _lookup(inputId, entity, 'bg-danger-subtle');
}