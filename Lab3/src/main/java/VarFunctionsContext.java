import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class VarFunctionsContext extends Construction{
    private HashMap<String, FunctionDefinition> functions = new HashMap<>();
    private LinkedList<HashMap<String, Variable>> variables = new LinkedList<>();
    public void getFunctions(){
        System.out.println(functions);
    }
    public void getVariables(){
        for (HashMap<String, Variable> elem : variables){
            System.out.print(elem);
        }
        System.out.println("");
    }
    public Opr rootFunc(String funcName){
        FunctionDefinition func = functions.get(funcName);
        return func.getFunctionStatements();
    }
    public Opr funcParametrs(String funcName){
        FunctionDefinition func = functions.get(funcName);
        return func.getParametrs();
    }
    public int ex(Opr p){
        if (p == null) return 0;
        switch (p.typeNode){
            case CONST -> {
                return ((Const) p).getValue();
            }
            case VAR -> {
                return ((Variable)p).getIntValue(); // mb return var value
            }
            case OPR -> {
                if (p.operType == null){
                    return ex(p.ops.get(0));
                } else
                switch (p.operType){
                    case ASSIGN -> {
                        int intVal = ex(p.ops.get(1));
                        ((Variable)p.ops.get(0)).setIntValue(intVal);
                        ((Variable)p.ops.get(0)).setValue(p.ops.get(1));
                        return intVal;
                    }
                    case PLUS -> {
                        return ex(p.ops.get(0)) + ex(p.ops.get(1));
                    }
                    case TIMES -> {
                        return ex(p.ops.get(0)) * ex(p.ops.get(1));
                    }
                    case NEXTSTMT -> {
                        ex(p.ops.get(0));
                        return ex(p.ops.get(1));
                    }
                    case RETURN -> {
                        return ((Variable)p.ops.get(0)).getIntValue();
                    }
                    case DIVIDE -> {
                        return ex(p.ops.get(0)) / ex(p.ops.get(1));
                    }
                    case MOD -> {
                        return ex(p.ops.get(0)) % ex(p.ops.get(1));
                    }
                    case MINUS -> {
                        return ex(p.ops.get(0)) - ex(p.ops.get(1));
                    }
                    case FUNC_CALL -> {
                        ((Variable) p.ops.get(1)).value = ((Variable) p.ops.get(2)).value;
                        ((Variable) p.ops.get(1)).intValue = ((Variable) p.ops.get(2)).intValue;
                        return ex(p.ops.get(0));
                    }
                }

            }
        }
        return 0;
    }
    public Variable getVar(String varName){
        for (HashMap<String, Variable> elem : variables){
            if (elem.get(varName)!= null)
                return elem.get(varName);
        }
        return null;
    }
    public void registerFunction(FunctionDefinition funcdef){
        newScope();
        functions.put(funcdef.getName(), funcdef);
    }
    public void newScope(){
        variables.push(new HashMap<>());
    }
    public void deleteScope(){
        variables.pop();
    }
    public void addVar(Variable var){
        variables.peek().put(var.name, var);
    }

}
