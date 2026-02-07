grammar Dice;

expression
   : left = expression op = ('*' | '/') right = expression # binaryMulDivExpr
   | left = expression op = ('+' | '-') right = expression # binaryAddSubExpr
   | '(' expression ')' # parenExpr
   | constant = (POSINT | '0') # constExpr
   | op = '-' right = expression # unaryExpr
   | dicePool # dieExpr
   ;

dicePool
   : '{' dicePool (',' dicePool)* '}' poolModifier* # explicitDicePool
   | count = POSINT 'd' sides = POSINT poolModifier* # implicitDicePool
   | constant = POSINT # constantDie
   ;

poolModifier
   : < assoc = right > 'kh' qty = POSINT? # keepHighest
   | < assoc = right > 'kl' qty = POSINT? # keepLowest
   | < assoc = right > 'dh' qty = POSINT? # dropHighest
   | < assoc = right > 'dl' qty = POSINT? # dropLowest
   | < assoc = right > 'r' rel = RELOP? qty = POSINT? # reroll
   | < assoc = right > 'rr' rel = RELOP? qty = POSINT? # rerollRecursive
   | < assoc = right > 'x' qty = POSINT? # explode
   | < assoc = right > 'min' value = POSINT # minimum
   | < assoc = right > 'max' value = POSINT # maximum
   ;

POSINT
   : [1-9] [0-9]*
   ; // Positive integers (no leading zero)

RELOP
   : ('>' | '<' | '=')
   ;

WS
   : [ \t\r\n]+ -> skip
   ;

