// https://plainenglish.io/blog/easy-table-sorting-with-javascript-370d8d97cad8
/**
 * Inject hyperlinks, into the column headers of sortable tables, which sort
 * the corresponding column when clicked.
 */
let tables = document.querySelectorAll("table.sortable");

for (let i = 0; i < tables.length; i++) {
    let table = tables[i];
    let thead = table.querySelector("thead")
    if (thead) {
        let headers = thead.querySelectorAll("th");

        for (let j = 0; j < headers.length; j++) {
            headers[j].innerHTML = "<a href='#'>" + headers[j].innerText + "</a>";
        }

        thead.addEventListener("click", sortTableFunction(table));
    }
}

/**
 * Create a function to sort the given table.
 */
function sortTableFunction(table) {
    return function(ev) {
        if (ev.target.tagName.toLowerCase() === 'a') {
            let alreadySortedAscending = ev.target.classList.contains("sorted-asc");
            sortRows(table, siblingIndex(ev.target.parentNode), alreadySortedAscending);
            // remove marker from all headers
            let thead = table.querySelector("thead");
            let headers = thead.querySelectorAll("th");
            for (let j = 0; j < headers.length; j++) {
                let link = headers[j].querySelector('a');
                link.classList.remove("text-success", "sorted-asc", "sorted-desc");
            }
            // add marker to selected header
            ev.target.classList.add("text-success");
            if (alreadySortedAscending) {
                ev.target.classList.add("sorted-desc");
            } else {
                ev.target.classList.add("sorted-asc");
            }
            ev.preventDefault();
        }
    };
}

/**
 * Get the index of a node relative to its siblings — the first (eldest) sibling
 * has index 0, the next index 1, etc.
 */
function siblingIndex(node) {
    let count = 0;

    while (node = node.previousElementSibling) {
        count++;
    }

    return count;
}

/**
 * Sort the given table by the numbered column (0 is the first column, etc.)
 */
function sortRows(table, columnIndex, reverse) {
    var rows = table.querySelectorAll("tbody tr"),
        sel = "thead th:nth-child(" + (columnIndex + 1) + ")",
        sel2 = "td:nth-child(" + (columnIndex + 1) + ")",
        classList = table.querySelector(sel).classList,
        values = [],
        cls = "",
        allNum = true,
        val,
        node;

    if (classList) {
        if (classList.contains("date")) {
            cls = "__date";
        } else if (classList.contains("number")) {
            cls = "__number";
        } else if (classList.contains("result")) {
            cls = "__result";
        }
        else if (classList.contains("percent")) {
            cls = "__percent";
        }
    }

    for (let index = 0; index < rows.length; index++) {
        node = rows[index].querySelector(sel2);
        val = node.innerText;

        if (isNaN(val)) {
            allNum = false;
        } else {
            val = parseFloat(val);
        }

        values.push({ value: val, row: rows[index] });
    }

    if (cls === "" && allNum) {
        cls = "__number";
    }

    if (cls === "__number") {
        values.sort(sortNumberVal);
        values = values.reverse();
    } else if (cls === "__result") {
        values.sort(sortResultVal);
        values = values.reverse();
    } else if (cls === "__date") {
        values.sort(sortDateVal);
    } else if (cls === "__percent") {
        values.sort(sortPercentVal);
    } else {
        values.sort(sortTextVal);
    }

    if (reverse) {
        values = values.reverse();
    }

    for (let idx = 0; idx < values.length; idx++) {
        table.querySelector("tbody").appendChild(values[idx].row);
    }
}

/**
 * Assign numerical value from non number
 * @param {*} val value to look at
 * @returns numerical value
 */
function cleanForNumberCompare(val) {
    if (isNaN(val) || val == undefined) {
        return -20;
    }
    if (typeof val == "string" && val.trim().length == 0) {
        return -10;
    }
    if (val === "W") {
        return -1;
    }
    if (val === "D") {
        return -2;
    }
    return parseFloat(val);
}

/**
 * Compare two 'value objects' numerically
 */
function sortNumberVal(a, b) {
    let a1 = cleanForNumberCompare(a.value);
    let b1 = cleanForNumberCompare(b.value);
    
    return sortNumber(a1, b1);
}

/**
 * Compare two 'value objects' numerically, after taking off a trailing % symbol
 */
function sortPercentVal(a, b) {
    let a1 = cleanForNumberCompare(a.value.slice(0, -1));
    let b1 = cleanForNumberCompare(b.value.slice(0, -1));
    
    return sortNumber(a1, b1);
}

/**
 * Compare two 'result objects' numerically, where W = -1 and D = -2
 */
function sortResultVal(a, b) {
    let a1 = cleanForNumberCompare(a.value);
    let b1 = cleanForNumberCompare(b.value);
    return sortNumber(a1, b1);
}

/**
 * Numeric sort comparison
 */
function sortNumber(a, b) {
    return a - b;
}

/**
 * Compare two 'value objects' as dates
 */
function sortDateVal(a, b) {
    let dateA = Date.parse(a.value);
    let dateB = Date.parse(b.value);

    return sortNumber(dateA, dateB);
}

/**
 * Compare two 'value objects' as simple text; case-insensitive
 */
function sortTextVal(a, b) {
    let textA = (a.value + "").toUpperCase();
    let textB = (b.value + "").toUpperCase();

    if (textA < textB) {
        return -1;
    }

    if (textA > textB) {
        return 1;
    }

    return 0;
}