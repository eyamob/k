// Copyright (c) 2014-2015 K Team. All Rights Reserved.

module TEST [location(startLine(#token(Int,"3")),startColumn(#token(Int,"1")),endLine(#token(Int,"6")),endColumn(#token(Int,"9")))]
  syntax Stxs ::= Stxs "," Stxs [userList(Stxs)]
  syntax Stxs ::= Stx [userList(Stxs)]
  syntax Stx ::= "1" [location(startLine(#token(Int,"4")),startColumn(#token(Int,"18")),endLine(#token(Int,"4")),endColumn(#token(Int,"20")))]
endmodule
