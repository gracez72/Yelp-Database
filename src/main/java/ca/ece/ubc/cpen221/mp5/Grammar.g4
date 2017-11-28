
/*
 * Lexer Rules
 */
 
 grammar Grammar;

<orExpr> ::= <andExpr>(<or><andExpr>)*
<andExpr> ::= <atom>(<and><atom>)*

ineq ::= <gt>|<gte>|<lt>|<lte>|<eq>
<gt> ::= ">"
<gte> ::= ">="
<in> ::= "in" <LParen><string><RParen>
<category> ::= "category" <LParen><string><RParen>
<name> ::= "name" <LParen><string><RParen>
<rating> ::= "rating" <ineq><num>

ATOM : <in>|<category>|<rating>|<price>|<name>|<LParen><orExpr><RParen> ;

WORD : [A-Za-z]+ ;

OR : "||" ; 

EQ : "=" ;

AND : "&&" ;

NUM : ("1" .. "5");

LTE : "<=" ;

LT : "<" ;

LPAREN : "(" ; 

RPAREN : ")" ;

PRICE : "price" INEQ NUM ; 