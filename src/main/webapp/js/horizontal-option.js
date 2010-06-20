var LEFT = "left";
var RIGHT = "right";

var OPTIONS_SETTINGS = {
    display: {},
    image: {}
};

OPTIONS_SETTINGS.display[LEFT] = "block";
OPTIONS_SETTINGS.display[RIGHT] = "none";
OPTIONS_SETTINGS.image[LEFT] = "/pic/arrow-left.png";
OPTIONS_SETTINGS.image[RIGHT] = "/pic/arrow-right.png";

function onOptionArrowClick(button) {
    var newDirection = button.direction == LEFT ? RIGHT : LEFT;
    var allOptionsBlock = button.parentNode.parentNode.childNodes[5];
    allOptionsBlock.style.display = OPTIONS_SETTINGS.display[newDirection];
    button.src = OPTIONS_SETTINGS.image[newDirection];
    button.direction = newDirection;
}

function onOptionClick(button) {
    var topOptionWrapper = button.parentNode.parentNode.parentNode;
    var currentOptionButton = topOptionWrapper.childNodes[3].childNodes[1];
    currentOptionButton.src = button.src;
    currentOptionButton.alt = button.alt;

    var hiddenParameter = topOptionWrapper.childNodes[1];
    hiddenParameter.value = button.alt
}