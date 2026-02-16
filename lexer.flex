/* ================= USER CODE SECTION ================= */

import java.util.*;

/* ================= OPTIONS SECTION ================= */

%%

%public
%class Lexer
%unicode
%line
%column
%type Token

%{

private Token token(TokenType type) {
    return new Token(type, yytext(), yyline + 1, yycolumn + 1);
}

%}

/* ================= MACROS ================= */

DIGIT = [0-9]
LETTER = [A-Z]
LOWER = [a-z]
WHITESPACE = [ \t\r\n]+

%%

/* ================= RULES ================= */

/* Ignore whitespace */
{WHITESPACE}                { /* skip */ }

/* Single-line comment */
"//".*                      { return token(TokenType.SINGLE_LINE_COMMENT); }

/* Keywords */
"start"|"finish"|"loop"|"condition"|"declare"|"output"|"input"|
"function"|"return"|"break"|"continue"|"else"
                                { return token(TokenType.KEYWORD); }

/* Boolean */
"true"|"false"
                                { return token(TokenType.BOOLEAN_LITERAL); }

/* Float */
{DIGIT}+"."{DIGIT}+         { return token(TokenType.FLOAT_LITERAL); }

/* Integer */
{DIGIT}+                    { return token(TokenType.INTEGER_LITERAL); }

/* String literal */
\"([^\"\\]|\\.)*\"          { return token(TokenType.STRING_LITERAL); }

/* Unterminated string */
\"([^\"\\]|\\.)*            {
                                return new Token(
                                    TokenType.ERROR,
                                    "Unterminated string",
                                    yyline + 1,
                                    yycolumn + 1);
                            }

/* Char literal */
\'([^\'\\]|\\.)\'           { return token(TokenType.CHAR_LITERAL); }

/* Invalid char */
\'.*                        {
                                return new Token(
                                    TokenType.ERROR,
                                    "Invalid char literal",
                                    yyline + 1,
                                    yycolumn + 1);
                            }

/* Relational operators */
"=="|"!="|"<="|">="|"&&"|"||"|"<"|">"
                                { return token(TokenType.RELATIONAL_OPERATOR); }

/* Operators */
"+"|"-"|"*"|"/"|"="
                                { return token(TokenType.OPERATOR); }

/* Delimiters */
";"|","|"("|")"|"{"|"}"
                                { return token(TokenType.DELIMITER); }

/* Identifier (Capital first rule like your DFA) */
{LETTER}({LETTER}|{LOWER})*
                                { return token(TokenType.IDENTIFIER); }

/* Error fallback */
.                               {
                                    return new Token(
                                        TokenType.ERROR,
                                        "Unexpected character: " + yytext(),
                                        yyline + 1,
                                        yycolumn + 1);
                                }
