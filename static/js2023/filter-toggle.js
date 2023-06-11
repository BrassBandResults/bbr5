const filterLinks = document.querySelectorAll('.toggle-filter');
filterLinks.forEach(filterBlock => {
    filterBlock.addEventListener('click', function toggleFilter() {
        const filterBlockId = "toggle-filter-" + filterBlock.dataset.id;
        const filterBlockDiv = document.getElementById(filterBlockId);
        const currentStyle = filterBlockDiv.style.display;
        if (currentStyle === '' || currentStyle === 'none') {
            filterBlockDiv.style.display = 'block';
        } else {
            filterBlockDiv.style.display = 'none';
        }
    });
});