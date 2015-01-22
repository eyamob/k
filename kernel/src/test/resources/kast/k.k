// Copyright (c) 2014-2015 K Team. All Rights Reserved.

module K

  syntax KLabel ::= r"'(([^`(){}\\s\\[\\]]|`[(){}\\[\\]])+)"   [token]

  syntax KList ::= K
                 | ".::KList"       [klabel(#emptyKList)]
                 | KList ",," KList [klabel(#KList), left, assoc, unit(#emptyKList)]
  syntax K     ::= K "=>" K         [klabel(#KRewrite), hook(Rewrite)]
                 | "(" K ")"        [bracket]
				 | ".::K"           [klabel(#emptyK)]
			     | K "~>" K         [klabel(#KSequence), left, assoc, unit(#emptyK)]
				 | KItem

  syntax KItem ::= "#token" "(" KString "," KString ")"  [klabel(#KToken)]
                 | "#klabel" "(" KLabel ")"              [klabel(#KLabel)]
                 | KLabel "(" KList ")"                  [klabel(#KApply)]

  syntax KVariable ::= r"(?<![A-Za-z0-9\\_])(\\$|\\!|\\?)?([A-Z][A-Za-z0-9']*|_)" [hook(variable)]
  syntax KItem     ::= KVariable
  syntax KLabel    ::= KVariable

  syntax KString ::= r"[\\\"]([^\\\"\\n\\r\\\\]|[\\\\][nrtf\\\"\\\\])*[\\\"]"   [token, noAutoReject, onlyLabel]

// Sort membership not needed anymore, as we can use requires/ensures
//  syntax KItem     ::= KItem ":" KSort  [hook(cast)]
endmodule
