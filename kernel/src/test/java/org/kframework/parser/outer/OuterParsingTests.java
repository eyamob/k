// Copyright (c) 2014-2015 K Team. All Rights Reserved.
package org.kframework.parser.outer;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.kframework.kil.DefinitionItem;
import org.kframework.kil.Lexical;
import org.kframework.kil.Module;
import org.kframework.kil.Sources;
import org.kframework.kil.StringSentence;
import org.kframework.kil.Syntax;

public class OuterParsingTests {

    @Test
    public void testLexicalRules() throws Exception {
        // TODO: remove once the new parser is fully functional
        String def = "module TEST syntax Str ::= Token{((~[\\'\\n\\r\\\\])|([\\\\]~[\\n\\r]))*} endmodule";

        List<DefinitionItem> defItemList = Outer.parse(Sources.generatedBy(OuterParsingTests.class), def, null);

        Module mod = (Module) defItemList.get(0);
        Syntax syn = (Syntax) mod.getItems().get(0);
        Lexical lex = (Lexical) syn.getPriorityBlocks().get(0).getProductions().get(0).getItems().get(0);

        Assert.assertEquals("((~[\\'\\n\\r\\\\])|([\\\\]~[\\n\\r]))*", lex.getLexicalRule());
    }

    @Test
    public void testEmptyRules() throws Exception {
        // TODO: remove once the new parser is fully functional
        String def = "module TEST rule endmodule";

        List<DefinitionItem> defItemList = Outer.parse(Sources.generatedBy(OuterParsingTests.class), def, null);

        Module mod = (Module) defItemList.get(0);
        StringSentence sen = (StringSentence) mod.getItems().get(0);
        Assert.assertEquals(sen.getContent(), "");
    }

    @Test
    public void testLexicalRules2() throws Exception {
        // TODO: remove once the new parser is fully functional
        String def = "module TEST syntax Str ::= Token{{[a]|[b] \".\"}+NT~[x]*} endmodule";

        List<DefinitionItem> defItemList = Outer.parse(Sources.generatedBy(OuterParsingTests.class), def, null);

        Module mod = (Module) defItemList.get(0);
        Syntax syn = (Syntax) mod.getItems().get(0);
        Lexical lex = (Lexical) syn.getPriorityBlocks().get(0).getProductions().get(0).getItems().get(0);

        Assert.assertEquals("{[a]|[b] \".\"}+NTDz~[x]*", lex.getLexicalRule());
    }
}
