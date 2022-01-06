public enum Types{
    VALUE, CONST_VALUE(), CONST_POINTER(Types.VALUE), POINTER(Types.VALUE), CONST_ARRAY_OF(Types.VALUE), ARRAY_OF(Types.VALUE);
    protected Types type;
    Types() {}
    Types(Types t){
        this.type = t;
    }

    public void setType(Types type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (this.equals(Types.CONST_VALUE)){
            return "const value";
        } else if (this.equals(Types.VALUE)){
            return "value";
        } else if (this.equals(Types.POINTER)){
            return "pointer " + type + " ";
        } else if (this.equals(Types.CONST_POINTER)){
            return "const pointer " + type + " ";
        } else if (this.equals(Types.CONST_ARRAY_OF)){
            return "const array of " + type + " ";
        } else if (this.equals(Types.ARRAY_OF)){
            return "array of " + type + " ";
        } else return "something wrong";
    }
}
