grammar Grammar;

/*
 * Parser Rules
 */
query : atom (atom | AND | OR)* ;
atom : in | category | rating | price | name | (LPAREN orexpr RPAREN) ;
andexpr : atom (AND atom)* ;
orexpr : andexpr (OR andexpr)* ;

ineq : GT | GTE | LT | LTE | EQ ;
in : 'in' LPAREN (WORD | NUM)* RPAREN ;
category : 'category' LPAREN (WORD)* RPAREN ;
name : 'name' LPAREN (WORD | NUM)* RPAREN ;
price : 'price' ineq NUM;
rating : 'rating' ineq NUM;

/*
 * Lexer Rules
 */
WORD : [A-Za-z]+ ;
SPACE : [ \t\n]+ -> skip;
NUM: [0-9] ;

OR : '||' ; 
AND : '&&' ;
LPAREN : '(' ; 
RPAREN : ')' ;

EQ : '=' ;
LTE : '<=' ;
LT : '<' ;
GT : '>' ;
GTE : '>=' ;