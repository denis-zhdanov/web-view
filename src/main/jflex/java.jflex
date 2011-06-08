package org.denis.webview.syntax.logic.java;
import static org.denis.webview.syntax.logic.java.JavaTokenType.*;
import org.denis.webview.syntax.logic.*;

%%

%class JavaLexer
%unicode
%implements Lexer
%public
%line
%column
%function advance
%type TokenType

%{
@Override
public int getStartOffset() {
    return zzStartRead;
}

@Override
public int getEndOffset() {
    return zzMarkedPos;
}

private boolean isWhiteSpace(int offset) {
    char c = zzBuffer[offset];
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
}
%}

LF        = \r|\n|\r\n
WS        = " "|\t
AnySymbol = .|{LF}

KEYWORD = "abstract"|"continue"|"for"|"new"|"switch"|"assert"|"default"|"goto"|"package"|"synchronized"|"boolean"|"do"|"if"|"private"|"this"|"break"|"double"|"implements"|"protected"|"throw"|"byte"|"else"|"import"|"public"|"throws"|"case"|"enum"|"instanceof"|"return"|"transient"|"catch"|"extends"|"int"|"short"|"try"|"char"|"final"|"interface"|"static"|"void"|"class"|"finally"|"long"|"strictfp"|"volatile"|"const"|"float"|"native"|"super"|"while"

/* comments */
%state END_LINE_COMMENT MULTI_LINE_COMMENT DOC_TAG_AWARE_COMMENT DOC_TAG_UNAWARE_COMMENT DOC_TAG DOC_HTML_TAG

%state STRING

%%

<YYINITIAL> {
    "//"                  { yybegin(END_LINE_COMMENT); return SINGLE_LINE_COMMENT_START; }
    "/**"                 { yybegin(DOC_TAG_UNAWARE_COMMENT); return JAVADOC_START; }
    "/*"                  { yybegin(MULTI_LINE_COMMENT); return MULTI_LINE_COMMENT_START; }
    \"                    { yybegin(STRING); return STRING_LITERAL_START; }
    '.'                   { return CHAR_LITERAL; }
    {KEYWORD}/{WS}|{LF}   { 
                            if (getStartOffset() == 0 || isWhiteSpace(getStartOffset() - 1)) {
                                return KEYWORD;
                            }
                          }
    {AnySymbol}           { }
}                                     
                                      
<END_LINE_COMMENT> {                  
    {LF}                  { yybegin(YYINITIAL); return TokenType.END_LOOK_AHEAD_TOKEN; }
    .                     { }
}                         
                          
<MULTI_LINE_COMMENT> {    
    "*/"                  { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}           { }
}                         
                          
<DOC_TAG_AWARE_COMMENT>   {
    "<"\/?/[:jletter:]    { yybegin(DOC_HTML_TAG); return JAVADOC_HTML_TAG_START; }
    {LF}                  { yybegin(DOC_TAG_AWARE_COMMENT); }
    [^ *@{]               { yybegin(DOC_TAG_UNAWARE_COMMENT); }
    "@"/[:jletterdigit:]  { yybegin(DOC_TAG); return JAVADOC_TAG_START; }
    "*/"                  { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}           { }
}

<DOC_TAG_UNAWARE_COMMENT> {
    "<"\/?/[:jletter:]    { yybegin(DOC_HTML_TAG); return JAVADOC_HTML_TAG_START; }
    "{"|{LF}              { yybegin(DOC_TAG_AWARE_COMMENT); }
    "*/"                  { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}           { }
}                                     
                                      
<DOC_TAG> {                           
    {LF}                  { yybegin(DOC_TAG_AWARE_COMMENT); return TokenType.END_LOOK_AHEAD_TOKEN; }
    [:jletterdigit:]      { }
    {AnySymbol}           { yybegin(DOC_TAG_UNAWARE_COMMENT); return TokenType.END_LOOK_AHEAD_TOKEN; }
}                                     
                                      
<DOC_HTML_TAG> {                      
    {LF}                  { yybegin(DOC_TAG_AWARE_COMMENT); return TokenType.END_LOOK_AHEAD_TOKEN; }
    [:jletterdigit:]      { }
    "/>"                  { yybegin(DOC_TAG_UNAWARE_COMMENT); return TokenType.END_TOKEN; }
    {AnySymbol}           { yybegin(DOC_TAG_UNAWARE_COMMENT); return TokenType.END_TOKEN; }
}                                     
                                      
<STRING> {                
    \"                    { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}           { }
}