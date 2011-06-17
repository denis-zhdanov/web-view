package org.denis.webview.syntax.logic.java;
import static org.denis.webview.syntax.logic.TokenType.*;
import static org.denis.webview.syntax.logic.java.JavaTokenType.*;
import org.denis.webview.syntax.logic.*;
import java.util.*;

%%

%class JavaLexer
%unicode
%implements Lexer
%public
%char
%function advance
%type List<TokenInfo>

%{

public int getStartOffset() {
    return yychar;
}

public int getEndOffset() {
    return yychar + zzMarkedPos - zzStartRead;
}

private boolean isValidSymbolBeforeKeyword() {
    if (zzStartRead <= 0) {
        return true;
    }
    char c = zzBuffer[zzStartRead - 1];
    return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '(';
}

private final List<TokenInfo> tokenInfos = new ArrayList<TokenInfo>();

private List<TokenInfo> info(TokenType tokenType) {
    tokenInfos.clear();
    tokenInfos.add(new TokenInfo(tokenType, getStartOffset(), getEndOffset()));
    return tokenInfos;
}

private List<TokenInfo> infos(TokenType endTokenType, TokenType tokenType) {
    tokenInfos.clear();
    tokenInfos.add(new TokenInfo(endTokenType, getStartOffset(), getStartOffset()));
    tokenInfos.add(new TokenInfo(tokenType, getStartOffset(), getEndOffset()));
    return tokenInfos;
}

%}

LF        = \r|\n|\r\n
WS        = " "|\t
TODO      = [Tt][Oo][Dd][Oo]
AnySymbol = .|{LF}

KEYWORD = "abstract"|"continue"|"for"|"new"|"switch"|"assert"|"default"|"goto"|"package"|"synchronized"|"boolean"|"do"|"if"|"private"|"this"|"break"|"double"|"implements"|"protected"|"throw"|"byte"|"else"|"import"|"public"|"throws"|"case"|"enum"|"instanceof"|"return"|"transient"|"catch"|"extends"|"int"|"short"|"try"|"char"|"final"|"interface"|"static"|"void"|"class"|"finally"|"long"|"strictfp"|"volatile"|"const"|"float"|"native"|"super"|"while"|"null"

/* comments */
%state END_LINE_COMMENT MULTI_LINE_COMMENT DOC_TAG_AWARE_COMMENT DOC_TAG_UNAWARE_COMMENT DOC_TAG DOC_HTML_TAG

/* TODO */
%state TODO_END_OF_LINE_COMMENT TODO_MULTI_LINE_COMMENT TODO_JAVADOC

%state STRING ANNOTATION

%%

<YYINITIAL> {
    "//"                          { yybegin(END_LINE_COMMENT); return info(SINGLE_LINE_COMMENT_START); }
    "/**"                         { yybegin(DOC_TAG_UNAWARE_COMMENT); return info(JAVADOC_START); }
    "/*"                          { yybegin(MULTI_LINE_COMMENT); return info(MULTI_LINE_COMMENT_START); }
    \"                            { yybegin(STRING); return info(STRING_LITERAL_START); }
    '.'                           { return info(CHAR_LITERAL); }
    {KEYWORD}/{WS}|{LF}|[(;).\[]  {
                                      if (isValidSymbolBeforeKeyword()) {
                                          return info(KEYWORD);
                                      }
                                  }
    @/[:jletter:]                 { yybegin(ANNOTATION); return info(ANNOTATION_START); }
    {AnySymbol}                   { }
}                                     
                                      
<END_LINE_COMMENT> {                  
    {LF}                          { yybegin(YYINITIAL); return info(END_LOOK_AHEAD_TOKEN); }
    {TODO}/[^[:jletterdigit:]]    { yybegin(TODO_END_OF_LINE_COMMENT); return infos(END_TOKEN, TODO_COMMENT_START);}
    .                             { }
}                         

<TODO_END_OF_LINE_COMMENT> {
    {LF}                          { yybegin(YYINITIAL); return info(END_LOOK_AHEAD_TOKEN); }
    .                             { }
}

<MULTI_LINE_COMMENT> {    
    "*/"                          { yybegin(YYINITIAL); return info(END_TOKEN); }
    {TODO}/[^[:jletterdigit:]]    {yybegin(TODO_MULTI_LINE_COMMENT); return info(TODO_COMMENT_START);}
    {AnySymbol}                   { }
}                         

<TODO_MULTI_LINE_COMMENT> {
    {LF}                          { yybegin(MULTI_LINE_COMMENT); return info(END_LOOK_AHEAD_TOKEN); }
    .                             { }
}

<DOC_TAG_AWARE_COMMENT>   {
    "<"\/?/[:jletter:]            { yybegin(DOC_HTML_TAG); return info(JAVADOC_HTML_TAG_START); }
    {LF}                          { yybegin(DOC_TAG_AWARE_COMMENT); }
    [^ *@{]                       { yybegin(DOC_TAG_UNAWARE_COMMENT); }
    "@"/[:jletterdigit:]          { yybegin(DOC_TAG); return info(JAVADOC_TAG_START); }
    "*/"                          { yybegin(YYINITIAL); return info(END_TOKEN); }
    {TODO}/[^[:jletterdigit:]]    {yybegin(TODO_JAVADOC); return info(TODO_COMMENT_START);}
    {AnySymbol}                   { }
}

<DOC_TAG_UNAWARE_COMMENT> {
    "<"\/?/[:jletter:]            { yybegin(DOC_HTML_TAG); return info(JAVADOC_HTML_TAG_START); }
    "{"|{LF}                      { yybegin(DOC_TAG_AWARE_COMMENT); }
    "*/"                          { yybegin(YYINITIAL); return info(END_TOKEN); }
    {TODO}/[^[:jletterdigit:]]    {yybegin(TODO_JAVADOC); return info(TODO_COMMENT_START);}
    {AnySymbol}                   { }
}                                     
                                      
<DOC_TAG> {                           
    {LF}                          { yybegin(DOC_TAG_AWARE_COMMENT); return info(END_LOOK_AHEAD_TOKEN); }
    [:jletterdigit:]              { }
    {AnySymbol}                   { yybegin(DOC_TAG_UNAWARE_COMMENT); return info(END_LOOK_AHEAD_TOKEN); }
}                                     
                                      
<DOC_HTML_TAG> {                      
    {LF}                          { yybegin(DOC_TAG_AWARE_COMMENT); return info(END_LOOK_AHEAD_TOKEN); }
    [:jletterdigit:]              { }
    "/>"                          { yybegin(DOC_TAG_UNAWARE_COMMENT); return info(END_TOKEN); }
    {AnySymbol}                   { yybegin(DOC_TAG_UNAWARE_COMMENT); return info(END_TOKEN); }
}                                     

<TODO_JAVADOC> {
    {LF}                          { yybegin(DOC_TAG_AWARE_COMMENT); return info(END_LOOK_AHEAD_TOKEN); }
    .                             { }
}

<STRING> {                
    \"                            { yybegin(YYINITIAL); return info(END_TOKEN); }
    {AnySymbol}                   { }
}

<ANNOTATION> {
    [:jletterdigit:]              {}
    {AnySymbol}                   { yybegin(YYINITIAL); return info(END_LOOK_AHEAD_TOKEN); }
}