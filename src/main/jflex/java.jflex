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
%}

LineTerminator = \r|\n|\r\n
AnySymbol      = .|{LineTerminator}

%state END_LINE_COMMENT MULTI_LINE_COMMENT DOC_TAG_AWARE_COMMENT DOC_TAG_UNAWARE_COMMENT DOC_TAG

%%

<YYINITIAL> {
    "//"                    { yybegin(END_LINE_COMMENT); return SINGLE_LINE_COMMENT_START; }
    "/**"                   { yybegin(DOC_TAG_UNAWARE_COMMENT); return JAVADOC_START; }
    "/*"                    { yybegin(MULTI_LINE_COMMENT); return MULTI_LINE_COMMENT_START; }
    {AnySymbol}             { }
}                           
                            
<END_LINE_COMMENT> {        
    {LineTerminator}        { yybegin(YYINITIAL); return TokenType.END_LOOK_AHEAD_TOKEN; }
    .                       { }
}                           
                            
<MULTI_LINE_COMMENT> {      
    "*/"                    { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}             { }
}                           
                            
<DOC_TAG_AWARE_COMMENT>     {
    {LineTerminator}        { yybegin(DOC_TAG_AWARE_COMMENT); }
    [^ *@{]                 { yybegin(DOC_TAG_UNAWARE_COMMENT); }
    "@"/[:jletterdigit:]    { yybegin(DOC_TAG); return JAVADOC_TAG_START; }
    "*/"                    { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}             { }
}

<DOC_TAG_UNAWARE_COMMENT> {
    "{"|{LineTerminator}    { yybegin(DOC_TAG_AWARE_COMMENT); }
    "*/"                    { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
    {AnySymbol}             { }
}                           
                            
<DOC_TAG> {                 
    {LineTerminator}        { yybegin(DOC_TAG_AWARE_COMMENT); return TokenType.END_LOOK_AHEAD_TOKEN; }
    [:jletterdigit:]        { }
    {AnySymbol}             { yybegin(DOC_TAG_UNAWARE_COMMENT); return TokenType.END_LOOK_AHEAD_TOKEN; }
}