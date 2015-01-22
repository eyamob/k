// Copyright (c) 2014-2015 K Team. All Rights Reserved.

module K
  syntax Stmts ::= List{Stmt,""}
  syntax Stmt  ::= Exp ";"
  syntax Exp   ::= Exp "+" Exp
                 | Int

  syntax Int   ::= r"[-+]?[0-9]+" [token]

endmodule
