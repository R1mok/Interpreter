package ru.mephi;

public enum Metasymbols {
    AND("."),
    OR("|"),
    CIRCUIT("+"),
    LBRACKET("{"),
    RBRACKET("}"),
    CIRCUMFLEXUS("^"),
    EPSILON("ε");
    public String value;
    Metasymbols(String value) {
        this.value = value;
    }
}
