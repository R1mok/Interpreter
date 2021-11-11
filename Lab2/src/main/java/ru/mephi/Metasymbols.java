package ru.mephi;

public enum Metasymbols {
    AND("."),
    OR("|"),
    KLINI("+"),
    BRACKET("()"),
    CIRCUMFLEXUS("^");
    //Repeat("{", int x, int y);
    //CaptureGroup();
    public String value;
    Metasymbols(String value) {
        this.value = value;
    }
}
