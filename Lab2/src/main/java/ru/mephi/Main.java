package ru.mephi;

import java.lang.ref.SoftReference;

public class Main {

    public static void main(String[] args) {
        RegexLib rl = new RegexLib();
        String str = "(3:(abc))xy";
        String str1 = "abc|abcxy+";
        minDFA mindfa = rl.compile(str);
        rl.multiplyOfAutomatoes("ac");
    }
}
