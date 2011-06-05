package org.denis.webview.syntax.logic.java;
import static org.denis.webview.syntax.logic.java.JavaTokenType.*;
import org.denis.webview.syntax.logic.TokenType;

%%

%class JavaLexer
%unicode
%public
%line
%column
%function advance
%type TokenType

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*
%state END_LINE_COMMENT

%%

<YYINITIAL> {
    /* identifiers */
    /*{Identifier}                   { return IDENTIFIER; }*/
 
    /* literals */
    /*{DecIntegerLiteral}            { return INTEGER_LITERAL; }*/
    /*\"                             { yybegin(STRING); }*/

    /* comments */
    \/\/                             { yybegin(END_LINE_COMMENT); return SINGLE_LINE_COMMENT_START;  }
    /*{TraditionalComment}           { return MULTI_LINE_COMMENT;  }*/

    /* whitespace */
    {WhiteSpace}                     { /* ignore */ }
}

<END_LINE_COMMENT> {
    {LineTerminator}                 { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
}