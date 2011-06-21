var HIGHLIGHTED_SOURCE_ELEMENT_ID = "highlighted-source";
var RAW_SOURCE_ELEMENT_ID = "raw-source";
var COPY_BUTTON_ID = "copy-html-button";

var HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES = {
    '<span.*?>|<\/span>': '',
    '&lt;'              : '<',
    '&gt;'              : '>',
    '&amp;'             : '&',
    '^\\s\\s*'          : '',
    '\\s\\s*$'          : ''
};

var clip;

function prepareSubsequentHighlight() {
    var highlightedSource = document.getElementById(HIGHLIGHTED_SOURCE_ELEMENT_ID);
    var rawSource = document.getElementById(RAW_SOURCE_ELEMENT_ID);
    if (!highlightedSource || !rawSource) {
        return;
    }

    clip.setText(highlightedSource.innerHTML.toString());
    var rawText = highlightedSource.innerHTML;
    for (var from in HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES) {
        rawText = rawText.replace(new RegExp(from, "g"), HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES[from]);
    }
    
    rawSource.value = rawText;
}

function prepareClipboardProcessing() {
    ZeroClipboard.setMoviePath("/flash/ZeroClipboard.swf");
    clip = new ZeroClipboard.Client();
    clip.glue(COPY_BUTTON_ID);
    clip.setHandCursor(true);
}