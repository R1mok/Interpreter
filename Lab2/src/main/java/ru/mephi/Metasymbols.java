package ru.mephi;

public enum Metasymbols {
    AND("."),
    OR("|"),
    CIRCUIT("+"),
    LBRACKET("{"),
    RBRACKET("}"),
    CIRCUMFLEXUS("^"),
    EPSILON("ε");
    //Repeat("{", int x, int y); // r{x,y}
    //CaptureGroup();
    public String value;
    Metasymbols(String value) {
        this.value = value;
    }
}
