// Copyright (c) 2013-2014 K Team. All Rights Reserved.
package org.kframework.backend.java.builtins;

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.kframework.backend.java.kil.State;
import org.kframework.utils.StringUtil;

/**
 * Table of {@code public static} methods on builtin strings.
 *
 * @author: DwightG
 */

public class BuiltinStringOperations {

    public static StringToken add(StringToken term1, StringToken term2, State context) {
        return StringToken.of(term1.stringValue() + term2.stringValue());
    }

    public static BoolToken eq(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) == 0);
    }

    public static BoolToken ne(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) != 0);
    }

    public static BoolToken gt(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) > 0);
    }

    public static BoolToken ge(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) >= 0);
    }

    public static BoolToken lt(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) < 0);
    }

    public static BoolToken le(StringToken term1, StringToken term2, State context) {
        return BoolToken.of(term1.stringValue().compareTo(term2.stringValue()) <= 0);
    }

    public static IntToken len(StringToken term, State context) {
        return IntToken.of(term.stringValue().codePointCount(0, term.stringValue().length()));
    }

    public static IntToken ord(StringToken term, State context) {
        if (term.stringValue().codePointCount(0, term.stringValue().length()) != 1) {
            throw new IllegalArgumentException();
        }
        return IntToken.of(term.stringValue().codePointAt(0));
    }

    public static StringToken chr(IntToken term, State context) {
        //safe because we know it's in the unicode code range or it will throw
        int codePoint = term.intValue();
        StringUtil.throwIfSurrogatePair(codePoint);
        char[] chars = Character.toChars(codePoint);
        return StringToken.of(new String(chars));
    }

    public static StringToken substr(StringToken term, IntToken start, IntToken end, State context) {
        int beginOffset = term.stringValue().offsetByCodePoints(0, start.intValue());
        int endOffset = term.stringValue().offsetByCodePoints(0, end.intValue());
        return StringToken.of(term.stringValue().substring(beginOffset, endOffset));
    }

    public static IntToken find(StringToken term1, StringToken term2, IntToken idx, State context) {
        int offset = term1.stringValue().offsetByCodePoints(0, idx.intValue());
        int foundOffset = term1.stringValue().indexOf(term2.stringValue(), offset);
        return IntToken.of((foundOffset == -1 ? -1 : term1.stringValue().codePointCount(0, foundOffset)));
    }

    public static IntToken rfind(StringToken term1, StringToken term2, IntToken idx, State context) {
        int offset = term1.stringValue().offsetByCodePoints(0, idx.intValue());
        int foundOffset = term1.stringValue().lastIndexOf(term2.stringValue(), offset);
        return IntToken.of((foundOffset == -1 ? -1 : term1.stringValue().codePointCount(0, foundOffset)));
    }

    public static IntToken findChar(StringToken term1, StringToken term2, IntToken idx, State context) {
        int offset = term1.stringValue().offsetByCodePoints(0, idx.intValue());
        int foundOffset = StringUtil.indexOfAny(term1.stringValue(), term2.stringValue(), offset);
        return IntToken.of((foundOffset == -1 ? -1 : term1.stringValue().codePointCount(0, foundOffset)));
    }

    public static IntToken rfindChar(StringToken term1, StringToken term2, IntToken idx, State context) {
        int offset = term1.stringValue().offsetByCodePoints(0, idx.intValue());
        int foundOffset = StringUtil.lastIndexOfAny(term1.stringValue(), term2.stringValue(), offset);
        return IntToken.of((foundOffset == -1 ? -1 : term1.stringValue().codePointCount(0, foundOffset)));
    }

    public static IntToken string2int(StringToken term, State context) {
        try {
            return IntToken.of(new BigInteger(term.stringValue()));
        } catch (NumberFormatException e) {
            if (term.stringValue().codePointCount(0, term.stringValue().length()) == 1) {
                int numericValue = Character.getNumericValue(term.stringValue().codePointAt(0));
                if (numericValue >= 0) {
                    return IntToken.of(numericValue);
                }
            }
            throw e;
        }
    }

    public static IntToken string2base(StringToken term, IntToken base, State context) {
        return IntToken.of(new BigInteger(term.stringValue(), base.intValue()));
    }

    public static UninterpretedToken string2float(StringToken term, State context) {
        return UninterpretedToken.of("Float", term.value());
    }

    public static StringToken float2string(UninterpretedToken term, State context) {
        return StringToken.of(term.value());
    }

    public static StringToken int2string(IntToken term, State context) {
        return StringToken.of(term.bigIntegerValue().toString());
    }
/*
    // when we support java 7
    public static StringToken name(StringToken term) {
        if (term.stringValue().codePointCount(0, term.stringValue().length()) != 1) {
            throw new IllegalArgumentException();
        }
        String name = Character.getName(term.stringValue().codePointAt(0));
        if (name == null) {
            throw new IllegalArgumentExceptino();
        }
        return StringToken.of(name);
    }
*/
    public static StringToken category(StringToken term, State context) {
        if (term.stringValue().codePointCount(0, term.stringValue().length()) != 1) {
            throw new IllegalArgumentException();
        }
        int cat = Character.getType(term.stringValue().codePointAt(0));
        assert cat >= 0 && cat < 128 : "not a byte???";
        return StringToken.of(StringUtil.getCategoryCode((byte)cat));
    }

    public static StringToken directionality(StringToken term, State context) {
        if (term.stringValue().codePointCount(0, term.stringValue().length()) != 1) {
            throw new IllegalArgumentException();
        }
        byte cat = Character.getDirectionality(term.stringValue().codePointAt(0));
        return StringToken.of(StringUtil.getDirectionalityCode(cat));
    }

    public static StringToken token2string(UninterpretedToken token, State context) {
        return StringToken.of(token.value());
    }
    
    /**
     * Replaces all occurrences of a string within another string.
     * 
     * @param text
     *            the string to search and replace in
     * @param search
     *            the string to search for
     * @param replacement
     *            the string to replace it with
     * @param context
     *            the state
     * @return the text with any replacements processed
     */
    public static StringToken replaceAll(StringToken text,
            StringToken searchString, StringToken replacement,
            State context) {
        return StringToken.of(StringUtils.replace(text.stringValue(),
                searchString.stringValue(), replacement.stringValue()));
    }
    
    /**
     * Replaces all occurrences of a string within another string, for the first
     * max values of the search string.
     * 
     * @param text
     *            the string to search and replace in
     * @param search
     *            the string to search for
     * @param replacement
     *            the string to replace it with
     * @param max
     *            the maximum number of occurrences to be replaced
     * @param context
     *            the state
     * @return the text with any replacements processed
     */
    public static StringToken replace(StringToken text,
            StringToken searchString, StringToken replacement, IntToken max,
            State context) {
        return StringToken.of(StringUtils.replace(text.stringValue(),
                searchString.stringValue(), replacement.stringValue(),
                max.intValue()));
    }
    
    /**
     * Replaces the first occurrence of a string within another string.
     * 
     * @param text
     *            the string to search and replace in
     * @param search
     *            the string to search for
     * @param replacement
     *            the string to replace it with
     * @param context
     *            the state
     * @return the text with any replacements processed
     */
    public static StringToken replaceFirst(StringToken text,
            StringToken searchString, StringToken replacement,
            State context) {
        return StringToken.of(StringUtils.replaceOnce(text.stringValue(),
                searchString.stringValue(), replacement.stringValue()));
    }
    
    /**
     * Counts how many times the substring appears in another string.
     * 
     * @param text
     *            the string to search in
     * @param substr
     *            the substring to search for
     * @param context
     *            the state
     * @return the number of occurrences
     */
    public static IntToken countOccurences(StringToken text,
            StringToken substr, State context) {
        return IntToken.of(StringUtils.countMatches(text.stringValue(),
                substr.stringValue()));
    }
}
