package ru.mephi;

import java.lang.ref.SoftReference;

public class Main {

    public static void main(String[] args) {
        RegexLib rl = new RegexLib();
        String str = "ab|c";
        minDFA mindfa = rl.compile(str);
        rl.multiplyOfAutomatoes("ac");
    }
}
