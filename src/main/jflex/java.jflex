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

.|\r|\n                 { /* ignore */ }

<YYINITIAL> {
    "//"                { yybegin(END_LINE_COMMENT); return SINGLE_LINE_COMMENT_START;  }
}

<END_LINE_COMMENT> {
    {LineTerminator}    { yybegin(YYINITIAL); return TokenType.END_TOKEN; }
}