package org.denis.webview.syntax.logic.java;
import static org.denis.webview.syntax.logic.java.JavaTokenType.*;
import java_cup.runtime.*;

%%

%class JavaLexer
%unicode
%line
%column
%function advance
%type JavaTokenType

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
%state STRING

%%

<YYINITIAL> {
  /* identifiers */ 
  /*{Identifier}                   { return IDENTIFIER; }*/
 
  /* literals */
  /*{DecIntegerLiteral}            { return INTEGER_LITERAL; }*/
  /*\"                             { yybegin(STRING); }*/

  /* comments */
  {EndOfLineComment}             { return SINGLE_LINE_COMMENT;  }
  {TraditionalComment}           { return MULTI_LINE_COMMENT;  }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}