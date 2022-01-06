import java.util.ArrayList;
import java.util.List;

public class Opr extends Construction {

    NodeType typeNode;
    operType operType;
    Opr(NodeType nt){
        this.typeNode = nt;
    }
    Opr() {}
    Opr(NodeType nt, operType ot){
        this.typeNode = nt;
        this.operType = ot;
    }
    protected List<Opr> ops = new ArrayList<>();
    public void addInListOpr(Opr oper){
        ops.add(oper);
    }
}
