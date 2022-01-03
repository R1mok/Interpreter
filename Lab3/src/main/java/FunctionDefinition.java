import lombok.Data;

import java.util.HashMap;

@Data
public class FunctionDefinition extends Construction {
    private String name;
    private Opr parametrs;
    private Types returnType;
    private Opr functionStatements;
    private HashMap<String, Object> variables;

    FunctionDefinition(Types returnType, String name, Opr parametrs, Opr functionStatements) {
        this.functionStatements = functionStatements;
        this.name = name;
        this.parametrs = parametrs;
        this.returnType = returnType;
    }
}
