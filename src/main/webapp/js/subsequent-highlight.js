var HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES = {
    '<span.*?>|<\/span>': '',
    '&lt;'              : '<',
    '&gt;'              : '>',
    '&amp;'             : '&',
    '^\\s\\s*'          : '',
    '\\s\\s*$'          : ''
};

function prepareSubsequentHighlight() {
    var highlightedSource = document.getElementById("highlighted-source");
    var rawSource = document.getElementById("raw-source");
    if (!highlightedSource || !rawSource) {
        return;
    }
    
    var rawText = highlightedSource.innerHTML;
    for (var from in HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES) {
        rawText = rawText.replace(new RegExp(from, "g"), HIGHLIGHTED_TO_RAW_TEXT_REPLACEMENT_RULES[from]);
    }
    
    rawSource.value = rawText; 
}