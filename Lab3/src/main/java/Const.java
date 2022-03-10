import lombok.Data;

@Data
public class Const extends Opr {
    int value; // значение константы

    Const(int value){
        super(NodeType.CONST);
        this.value = value;
    }
    Const(NodeType nt) {
        super(nt);
    }
}
