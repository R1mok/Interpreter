import lombok.Data;

import java.util.HashMap;

@Data
public class FunctionDefinition {
    private String name;
    private Opr parametrs;
    private Types returnType;
    private Opr functionStatements;

    FunctionDefinition(Types returnType, String name, Opr parametrs, Opr functionStatements) {
        this.functionStatements = functionStatements;
        this.name = name;
        this.parametrs = parametrs;
        this.returnType = returnType;
    }
    FunctionDefinition(String name, Opr params){
        this.name = name;
        this.functionStatements = new Opr(NodeType.OPR, operType.FUNC_CALL);
        this.parametrs = params;
    }
    public void setParametrs(Opr parametrs) {
        this.parametrs = parametrs;
    }
}
