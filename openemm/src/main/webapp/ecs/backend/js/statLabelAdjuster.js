function showPopups() {
    // get ecs-frame document
    var frameDocument = document;

    var nullColorElement = frameDocument.getElementById('info-null-color');
    if (nullColorElement == null) {
        return;
    }
    var nullColor = nullColorElement.value;
    var initialWidth = document.body.scrollWidth;

    // iterate through all links of document
    var links = frameDocument.getElementsByTagName('a');
    if (links != null && links.length > 0) {
        for (var i = 0; i < links.length; i++) {
            var linkUrl = links[i].getAttribute('href');
            if (!linkUrl)
                continue;

            if (linkUrl.lastIndexOf("http") == -1)
                continue;

            var lastPointIndex = linkUrl.lastIndexOf(".");
            if (lastPointIndex != -1) {
                var urlWithoutSignature = linkUrl.substr(0, lastPointIndex);
                var preLastPointIndex = urlWithoutSignature.lastIndexOf(".");
                if (preLastPointIndex != -1) {
                    var codedUrlId = urlWithoutSignature.substring(preLastPointIndex + 1);
                    // get stats info for the URL from hidden field
                    var infoElId = "info-" + codedUrlId;
                    var linkInfo = frameDocument.getElementById(infoElId);
                }
            }
            else {
                continue;
            }
            // if there is stats for the URL - create stats-label and put it near link
            // in other case create default stat-label with zero-value
            var clickValue = "0 (0%)";
            var bgColor = nullColor;
            if (linkInfo != null) {
                clickValue = linkInfo.value;
                bgColor = linkInfo.name;
            }
            var statLabel = createStatLabel(clickValue, bgColor, frameDocument);
            adjustLabelPosition(links[i], statLabel, frameDocument, initialWidth);
            var backgroundLabel = createOverlayLabel(bgColor, frameDocument);
            adjustOverlayPosition(links[i], backgroundLabel, frameDocument, initialWidth);

        }
    }
}

function createStatLabel(labelValue, bgColor, frameDocument) {
    var myDiv = frameDocument.createElement('div');
    myDiv.innerHTML = labelValue;
    myDiv.style.backgroundColor = bgColor;
    myDiv.style.padding = '1px';
    myDiv.style.border = '1px solid #777777';
    myDiv.style.fontFamily = 'Tahoma, Arial, Helvetica, sans-serif';
    myDiv.style.fontSize = '11px';
    myDiv.style.zIndex = "10";
    frameDocument.body.appendChild(myDiv);
    return myDiv;
}

function createOverlayLabel(bgColor,frameDocument) {
    var myDiv = frameDocument.createElement('div');
    myDiv.style.backgroundColor = bgColor;
    frameDocument.body.appendChild(myDiv);
    return myDiv;
}

function adjustOverlayPosition(linkElement, popup, frameDocument, documentWidth) {
    $(popup).css("position", "absolute");
    $(popup).css("opacity", "0.5");
    // fix for opacity in IE
    $(popup).css("filter", "progid:DXImageTransform.Microsoft.Alpha(opacity=50)");

    var maxWidth = parseInt($(linkElement).width());
    var maxHeight = parseInt($(linkElement).height());
    var posX = parseInt($(linkElement).offset().left);
    var posY = parseInt($(linkElement).offset().top);

    $(linkElement).children().each(function(i) {
        if (parseInt($(this).width()) > maxWidth) {
            maxWidth = parseInt($(this).width());
        }
        if (parseInt($(this).height()) > maxHeight) {
            maxHeight = parseInt($(this).height());
        }
        posX = $(this).offset().left;
        if ($(this).offset().top < posY) {
            posY = $(this).offset().top;
        }
    });

    $(popup).width(maxWidth);
    $(popup).height(maxHeight);
    $(popup).offset({
        left: posX,
        top: posY
    });
}

function adjustLabelPosition(linkElement, popup, frameDocument, documentWidth) {
    $(popup).css("position", "absolute");
    $(popup).css("textAlign", "center");

    var maxWidth = parseInt($(linkElement).width());
    var maxHeight = parseInt($(linkElement).height());
    var posX = parseInt($(linkElement).offset().left);
    var posY = parseInt($(linkElement).offset().top);

    $(linkElement).children().each(function(i) {
        if (parseInt($(this).width()) > maxWidth) {
            maxWidth = parseInt($(this).width());
        }
        if (parseInt($(this).height()) > maxHeight) {
            maxHeight = parseInt($(this).height());
        }
        posX = $(this).offset().left;
        if ($(this).offset().top < posY) {
            posY = $(this).offset().top;
        }
    });
    $(popup).offset({
        left: posX + maxWidth - $(popup).outerWidth(),
        top: posY + maxHeight
    });
}

if (window.addEventListener) {
    window.addEventListener('load', showPopups, false);
} else if (window.attachEvent) {
    window.attachEvent('onload', showPopups);
} else {
    window.onload = showPopups;
}