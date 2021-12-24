package ru.mephi;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.ref.SoftReference;

import static org.junit.Assert.*;

public class RegexLibTest {

    @Test
    public void kpath() {
        String str1 = "abc";
        String str2 = "a|b";
        String str3 = "ab|c";
        String str4 = "a+";
        String str5 = "ab+";
        String str6 = "ab|c+";
        String str7 = "ab{2,2}";
        String str8 = "a|(b{3})";
        String str9 = "(abc){1,2}";
        String str10 = "ab(3:(a|b))";
        RegexLib rl = new RegexLib();
        String out1 = rl.kpath(rl.compile(str1));
        Assert.assertEquals(out1, "((a((^)+|^)(b))((^)+|^)(c|(c((^)+|^)(^))))|(((^)+|^)(((a((^)+|^)(b))((^)+|^)(c|(c((^)+|^)(^))))))");
        String out2 = rl.kpath(rl.compile(str2));
        Assert.assertEquals(out2, "b|a|(b|a((^)+|^)(^))|(((^)+|^)(b|a|(b|a((^)+|^)(^))))");
        String out3 = rl.kpath(rl.compile(str3));
        Assert.assertEquals(out3, "(a((^)+|^)(c|b))|((a((^)+|^)(c|b))((^)+|^)(^))|(((^)+|^)((a((^)+|^)(c|b))|((a((^)+|^)(c|b))((^)+|^)(^))))");
        String out4 = rl.kpath(rl.compile(str4));
        Assert.assertEquals(out4, "a|(((^)+|^)(a))|(a|(((^)+|^)(a))((a|^)+|^)(a|^))");
        String out5 = rl.kpath(rl.compile(str5));
        Assert.assertEquals(out5, "(a|(((^)+|^)(a))((^)+|^)(b))|((a|(((^)+|^)(a))((^)+|^)(b))((b|^)+|^)(b|^))");
        String out6 = rl.kpath(rl.compile(str6));
        Assert.assertEquals(out6, "(a((^)+|^)(c|b))|((a((^)+|^)(c|b))((c|^)+|^)(c|^))|(((^)+|^)((a((^)+|^)(c|b))|((a((^)+|^)(c|b))((c|^)+|^)(c|^))))");
        String out7 = rl.kpath(rl.compile(str7));
        Assert.assertEquals(out7, "(a((^)+|^)((b((^)+|^)(b))))|((a((^)+|^)((b((^)+|^)(b))))((^)+|^)(^))|(((^)+|^)((a((^)+|^)((b((^)+|^)(b))))|((a((^)+|^)((b((^)+|^)(b))))((^)+|^)(^))))");
        String out8 = rl.kpath(rl.compile(str8));
        Assert.assertEquals(out8, "a|(a|((b|^)+|^)(b|^))|(((^)+|^)(a|(a|((b|^)+|^)(b|^))))|((b|((^)+|^)(b))|(((^)+|^)((b|((^)+|^)(b))))((^)+|^)(b|(b((b|^)+|^)(b|^))))");
        String out9 = rl.kpath(rl.compile(str9));
        Assert.assertEquals(out9, "((a((^)+|^)(b))|(((^)+|^)((a((^)+|^)(b))))((^)+|^)(c))|(((a((^)+|^)(b))|(((^)+|^)((a((^)+|^)(b))))((^)+|^)(c))((^)+|^)(^))|(((a((^)+|^)(b))|(((^)+|^)((a((^)+|^)(b))))((^)+|^)(c))((^)+|^)((a((^)+|^)((b((^)+|^)(c))))|((a((^)+|^)((b((^)+|^)(c))))((^)+|^)(^))))");
        String out10 = rl.kpath(rl.compile(str10));
        Assert.assertEquals(out10, "(a((^)+|^)((b((^)+|^)(b|a))))|(((^)+|^)((a((^)+|^)((b((^)+|^)(b|a))))))|((a((^)+|^)((b((^)+|^)(b|a))))|(((^)+|^)((a((^)+|^)((b((^)+|^)(b|a))))))((^)+|^)(^))");
    }

    @Test
    public void search() throws IOException {
        RegexLib rl = new RegexLib();
        // TRUE search
        assertTrue(rl.search("a|(bc)+", "bc"));
        assertTrue(rl.search("a+bc", "aaabc"));
        assertTrue(rl.search("r{1,3}", "rr"));
        assertTrue(rl.search("(abc){1}", "abcabc"));
        assertTrue(rl.search("a+|bc", "bc"));
        // FALSE search
        assertFalse(rl.search("a(b|c)ad", "abd"));
        assertFalse(rl.search("abc+d", "ac"));
        assertFalse(rl.search("a|b|c|(def)", "ab"));
        assertFalse(rl.search("(3:(abc))", "ac"));
        assertFalse(rl.search("ab|(cde|da)", "^"));
    }

    @Test
    public void complement() throws IOException {
        RegexLib rl = new RegexLib();
        mulDFA comp = rl.complement("ab");
        Viz.VizMulDFA(new SoftReference<>(comp));
        Viz.printMulDFA(comp);
        comp = rl.complement("a+");
        Viz.VizMulDFA(new SoftReference<>(comp));
        Viz.printMulDFA(comp);
        comp = rl.complement("b|c|d");
        Viz.VizMulDFA(new SoftReference<>(comp));
        Viz.printMulDFA(comp);
        comp = rl.complement("(ab){1,3}");
        Viz.VizMulDFA(new SoftReference<>(comp));
        Viz.printMulDFA(comp);
        comp = rl.complement("ab|c(de|f)");
        Viz.VizMulDFA(new SoftReference<>(comp));
        Viz.printMulDFA(comp);
    }

    @Test
    public void intersection() throws IOException {
        RegexLib rl = new RegexLib();
        mulDFA intersection = rl.intersection("ab", "cd");
        Viz.VizMulDFA(new SoftReference<>(intersection));
        for (MulDFANode elem : intersection.nodesArray){
            Assert.assertEquals(elem.listnodes.size(), 0);
        }
        intersection = rl.intersection("ab+", "ab");
        Viz.VizMulDFA(new SoftReference<>(intersection));
        Viz.printMulDFA(intersection);
        intersection = rl.intersection("a|(bc)", "bc");
        Viz.VizMulDFA(new SoftReference<>(intersection));
        Viz.printMulDFA(intersection);
        intersection = rl.intersection("a+b|c", "ac");
        Viz.VizMulDFA(new SoftReference<>(intersection));
        Viz.printMulDFA(intersection);
        intersection = rl.intersection("a{1,2}b|c", "aab");
        Viz.VizMulDFA(new SoftReference<>(intersection));
        Viz.printMulDFA(intersection);

    }
}