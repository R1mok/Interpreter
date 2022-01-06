import lombok.Data;

@Data
public class Variable extends Opr {
    Types type; // type of node
    String name; // var name
    Object value; // value
    int intValue;
    Variable(Types type, String name){
        super(NodeType.VAR);
        this.type = type;
        this.name = name;
    }
    Variable(NodeType nt) {
        super(nt);
    }

    public void setValue(Object value) { this.value = value; }
    public void setValue(Opr value) {
        this.value = value;
    }
    public void setIntValue(int value){
        this.intValue = value;
    }
    public Opr getValue(){
        return (Opr) this.value;
    }
    public int getIntValue(){
        return this.intValue;
    }
    @Override
    public String toString() {
        if (!(this.type.equals(Types.ARRAY_OF) || this.type.equals(Types.CONST_ARRAY_OF)))
            return type + " " + name + "=" + intValue;
        else return type + " " + name;
    }
}
