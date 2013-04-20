function addTitleToOptions() {
    var options = document.getElementsByTagName('option');
    for (var i = 0; i < options.length; i++) {
        optionText = options[i].innerHTML;
        options[i].title = optionText;
    }
}