import java.util.ArrayList;
import java.util.List;

public class Opr { // базовый узел дерева

    NodeType typeNode; // тип узла (константа, переменная, другой узел)
    operType operType; // тип операции узла
    String funcCall;
    Opr(NodeType nt){
        this.typeNode = nt;
    }
    Opr() {}
    Opr (String funcName){
        this.funcCall = funcName;
    }
    Opr(NodeType nt, operType ot){
        this.typeNode = nt;
        this.operType = ot;
    }
    protected List<Opr> ops = new ArrayList<>(); // список детей узла
    public void addInListOpr(Opr oper){
        ops.add(oper);
    }
}
