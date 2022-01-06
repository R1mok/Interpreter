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
                if (p.ops.size() == 2) {
                    int size = ex(p.ops.get(1));
                    Variable[] value = new Variable[size];
                    for (int i = 0; i < size; ++i) {
                        value[i] = new Variable(NodeType.VAR);
                        value[i].setType(((Variable)p.ops.get(0)).type.type);
                    }
                    ((Variable)p.ops.get(0)).value = value;
                } else
                return ((Variable)p).getIntValue();
            }
            case OPR -> {
                if (p.operType == null){
                    return ex(p.ops.get(0));
                }
                else
                switch (p.operType){
                    case TAKE_FROM_ARRAY -> {
                        int index = ((Const)p.ops.get(1)).value;
                        return ((Variable[])((Variable)p.ops.get(0)).value)[index].intValue;
                    }
                    case ASSIGN -> {
                        if (p.ops.size() == 3){
                           int index = ((Const)p.ops.get(1)).value;
                            if (p.ops.get(2) instanceof Const && (((Variable) p.ops.get(0)).type.type.equals(Types.VALUE) || (((Variable) p.ops.get(0)).type.type.equals(Types.CONST_VALUE))))
                            {
                                Variable var = new Variable(NodeType.CONST);
                                var.intValue = ((Const)p.ops.get(2)).getValue();
                                var.value = p.ops.get(2);
                                var.type = Types.VALUE;
                                ((Variable[]) ((Variable) p.ops.get(0)).value)[index] = var;
                            }
                        } else {
                            int intVal = ex(p.ops.get(1));
                            ((Variable) p.ops.get(0)).setIntValue(intVal);
                            ((Variable) p.ops.get(0)).setValue(p.ops.get(1));
                            return intVal;
                        }
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
                        setFuncParams(p.ops.get(1), p.ops.get(2));
                        return ex(p.ops.get(0));
                    }
                }

            }
        }
        return 0;
    }
    public void setFuncParams(Opr newVal, Opr val){
            ((Variable)newVal.ops.get(0)).value = ((Variable) val.ops.get(0)).value;
            ((Variable) newVal.ops.get(0)).intValue = ((Variable) val.ops.get(0)).intValue;
            if (newVal.ops.size() != 1 || val.ops.size() != 1)
                setFuncParams(newVal.ops.get(1), val.ops.get(1));
            else return;
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
