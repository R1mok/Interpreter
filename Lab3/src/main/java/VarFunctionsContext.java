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
    public Opr ex(Opr p){
        if (p == null) return new Const(0);
        switch (p.typeNode){
            case CONST -> {
                return p;
            }
            case VAR -> {
                if (p.ops.size() == 2) {
                    int size = ((Const)ex(p.ops.get(1))).value;
                    Variable[] value = new Variable[size];
                    for (int i = 0; i < size; ++i) {
                        value[i] = new Variable(NodeType.VAR);
                        value[i].setType(((Variable)p.ops.get(0)).type.type);
                    }
                    ((Variable)p.ops.get(0)).value = value;
                    return p.ops.get(0);
                } else
                return p;
            }
            case OPR -> {
                if (p.operType == null){
                    return ex(p.ops.get(0));
                }
                else
                switch (p.operType){
                    case TAKE_FROM_ARRAY -> {
                        int index = ((Const)p.ops.get(1)).value;
                        return ex(((Variable[])((Variable)p.ops.get(0)).value)[index]);
                    }
                    case WHILE -> {
                        Opr fst = ex(p.ops.get(0));
                        if (fst instanceof Variable) {
                            fst = p.ops.get(0);
                            Opr sec = p.ops.get(1);
                            while (((Variable)ex(fst)).intValue != 0){
                                sec = ex(p.ops.get(1));
                            }
                            return sec;
                        }
                        else if (fst instanceof Const){
                            fst = p.ops.get(0);
                            Opr sec = p.ops.get(1);
                            while (((Const)ex(fst)).value != 0){
                                sec = ex(p.ops.get(1));
                            }
                            return sec;
                        }
                    }
                    case ASSIGN -> {
                        if (((Variable) p.ops.get(0)).type.equals(Types.ARRAY_OF) && p.ops.size() == 2){
                            ((Variable) p.ops.get(0)).value = ((Variable)ex(p.ops.get(1))).value;
                            return p.ops.get(0);
                        }
                        if (((Variable) p.ops.get(0)).type.equals(Types.ARRAY_OF) && p.ops.size() == 3){
                            Opr fst = p.ops.get(1);
                            if (p.ops.get(1) instanceof Variable)
                                fst = ex(p.ops.get(1));
                           int index = 0;
                           if (fst instanceof Variable)
                               index = ((Variable)fst).intValue;
                           if (fst instanceof Const)
                               index = ((Const)fst).value;
                            if ((((Variable) p.ops.get(0)).type.type.equals(Types.VALUE) || (((Variable) p.ops.get(0)).type.type.equals(Types.CONST_VALUE))))
                            {
                                Opr val = p.ops.get(2);
                                if (!(p.ops.get(2) instanceof Const) && !(p.ops.get(2) instanceof Variable)){
                                    val = ex(p.ops.get(2));
                                }
                                Variable var = new Variable(NodeType.CONST);
                                if (val instanceof Const)
                                    var.intValue = ((Const)val).getValue();
                                if (val instanceof Variable)
                                    var.intValue = ((Variable)val).intValue;
                                var.value = val;
                                var.type = Types.VALUE;
                                ((Variable[]) ((Variable) p.ops.get(0)).value)[index] = var;
                                return p.ops.get(0);
                            }
                        } else {
                            Opr val = ex(p.ops.get(1));
                            int intVal = 0;
                            if (val instanceof Variable)
                                intVal = ((Variable)val).intValue;
                            else if (val instanceof Const)
                                intVal = ((Const)val).value;
                            ((Variable) p.ops.get(0)).setIntValue(intVal);
                            ((Variable) p.ops.get(0)).setValue(p.ops.get(1));
                            return val;
                        }
                    }
                    case GTE -> {
                        int a = 0, b = 0;
                        Opr fst = p.ops.get(0), sec = p.ops.get(1);
                        if (!(p.ops.get(0) instanceof Variable) && !(p.ops.get(0) instanceof Const)) {
                            fst = ex(p.ops.get(0));
                        }
                        if (!(p.ops.get(1) instanceof Variable) && !(p.ops.get(1) instanceof Const)) {
                            sec = ex(p.ops.get(1));
                        }
                        if (fst instanceof Variable)
                            a = ((Variable)ex(fst)).intValue;
                        else if (fst instanceof Const)
                            a = ((Const)ex(fst)).value;
                        if (sec instanceof Variable)
                            b = ((Variable)ex(sec)).intValue;
                        else if (sec instanceof Const)
                            b = ((Const)ex(sec)).value;
                        if (a >= b) return new Const(1);
                        else return new Const(0);
                    }
                    case LTE -> {
                        int a = 0, b = 0;
                        Opr fst = p.ops.get(0), sec = p.ops.get(1);
                        if (!(p.ops.get(0) instanceof Variable) && !(p.ops.get(0) instanceof Const)) {
                            fst = ex(p.ops.get(0));
                        }
                        if (!(p.ops.get(1) instanceof Variable) && !(p.ops.get(1) instanceof Const)) {
                            sec = ex(p.ops.get(1));
                        }
                        if (fst instanceof Variable)
                            a = ((Variable)ex(fst)).intValue;
                        else if (fst instanceof Const)
                            a = ((Const)ex(fst)).value;
                        if (sec instanceof Variable)
                            b = ((Variable)ex(sec)).intValue;
                        else if (sec instanceof Const)
                            b = ((Const)ex(sec)).value;
                        if (a <= b) return new Const(1);
                        else return new Const(0);
                    }
                    case NE -> {
                        int a = 0, b = 0;
                        Opr fst = p.ops.get(0), sec = p.ops.get(1);
                        if (!(p.ops.get(0) instanceof Variable) && !(p.ops.get(0) instanceof Const)) {
                            fst = ex(p.ops.get(0));
                        }
                        if (!(p.ops.get(1) instanceof Variable) && !(p.ops.get(1) instanceof Const)) {
                            sec = ex(p.ops.get(1));
                        }
                        if (fst instanceof Variable)
                            a = ((Variable)ex(fst)).intValue;
                        else if (fst instanceof Const)
                            a = ((Const)ex(fst)).value;
                        if (sec instanceof Variable)
                            b = ((Variable)ex(sec)).intValue;
                        else if (sec instanceof Const)
                            b = ((Const)ex(sec)).value;
                        if (a != b) return new Const(1);
                        else return new Const(0);
                    }
                    case PLUS -> {
                        int a = 0, b = 0;
                        Opr fst = p.ops.get(0), sec = p.ops.get(1);
                        if (!(p.ops.get(0) instanceof Variable) && !(p.ops.get(0) instanceof Const)) {
                            fst = ex(p.ops.get(0));
                        }
                        if (!(p.ops.get(1) instanceof Variable) && !(p.ops.get(1) instanceof Const)) {
                            sec = ex(p.ops.get(1));
                        }
                        if (fst instanceof Variable)
                            a = ((Variable)ex(fst)).intValue;
                        else if (fst instanceof Const)
                            a = ((Const)ex(fst)).value;
                        if (sec instanceof Variable)
                            b = ((Variable)ex(sec)).intValue;
                        else if (sec instanceof Const)
                            b = ((Const)ex(sec)).value;
                        return new Const(a + b);
                    }
                    case TIMES -> {
                        int a = 0, b = 0;
                        if (p.ops.get(0) instanceof Variable)
                            a = ((Variable)ex(p.ops.get(0))).intValue;
                        else if (p.ops.get(0) instanceof Const)
                            a = ((Const)ex(p.ops.get(0))).value;
                        if (p.ops.get(1) instanceof Variable)
                            b = ((Variable)ex(p.ops.get(1))).intValue;
                        else if (p.ops.get(1) instanceof Const)
                            b = ((Const)ex(p.ops.get(1))).value;
                        return new Const(a * b);
                    }
                    case NEXTSTMT -> {
                        ex(p.ops.get(0));
                        return ex(p.ops.get(1));
                    }
                    case RETURN -> {
                        return p.ops.get(0);
                    }
                    case DIVIDE -> {
                        int a = 0, b = 0;
                        if (p.ops.get(0) instanceof Variable)
                            a = ((Variable)ex(p.ops.get(0))).intValue;
                        else if (p.ops.get(0) instanceof Const)
                            a = ((Const)ex(p.ops.get(0))).value;
                        if (p.ops.get(1) instanceof Variable)
                            b = ((Variable)ex(p.ops.get(1))).intValue;
                        else if (p.ops.get(1) instanceof Const)
                            b = ((Const)ex(p.ops.get(1))).value;
                        return new Const(a / b);
                    }
                    case MOD -> {
                        int a = 0, b = 0;
                        if (p.ops.get(0) instanceof Variable)
                            a = ((Variable)ex(p.ops.get(0))).intValue;
                        else if (p.ops.get(0) instanceof Const)
                            a = ((Const)ex(p.ops.get(0))).value;
                        if (p.ops.get(1) instanceof Variable)
                            b = ((Variable)ex(p.ops.get(1))).intValue;
                        else if (p.ops.get(1) instanceof Const)
                            b = ((Const)ex(p.ops.get(1))).value;
                        return new Const(a % b);
                    }
                    case MINUS -> {
                        int a = 0, b = 0;
                        if (p.ops.get(0) instanceof Variable)
                            a = ((Variable)ex(p.ops.get(0))).intValue;
                        else if (p.ops.get(0) instanceof Const)
                            a = ((Const)ex(p.ops.get(0))).value;
                        if (p.ops.get(1) instanceof Variable)
                            b = ((Variable)ex(p.ops.get(1))).intValue;
                        else if (p.ops.get(1) instanceof Const)
                            b = ((Const)ex(p.ops.get(1))).value;
                        return new Const(a - b);
                    }
                    case FUNC_CALL -> {
                        Opr fst = p.ops.get(1), scnd = p.ops.get(2);
                        if (p.ops.get(1) != null && p.ops.get(2) != null) {
                            if (p.ops.get(1).operType.equals(operType.NEXTSTMT))
                                fst = ex(p.ops.get(1));
                            if (p.ops.get(0).operType.equals(operType.NEXTSTMT))
                                scnd = ex(p.ops.get(2));
                            setFuncParams(fst, scnd);
                        }
                        return ex(p.ops.get(0));
                    }
                }

            }
        }
        return new Const(0);
    }
    public void setFuncParams(Opr newVal, Opr val){
        if (newVal.ops.get(0) instanceof Variable && val.ops.get(0) instanceof Variable) {
            if (((Variable) newVal.ops.get(0)).type.equals(Types.VALUE) && ((Variable) val.ops.get(0)).type.equals(Types.VALUE)) {
                ((Variable) newVal.ops.get(0)).value = ((Variable) val.ops.get(0)).value;
                ((Variable) newVal.ops.get(0)).intValue = ((Variable) val.ops.get(0)).intValue;
                if (newVal.ops.size() != 1 || val.ops.size() != 1)
                    setFuncParams(newVal.ops.get(1), val.ops.get(1));
                else return;
            } else if (((Variable) newVal.ops.get(0)).type.equals(Types.ARRAY_OF) && ((Variable) val.ops.get(0)).type.equals(Types.ARRAY_OF)) {
                newVal = ex(newVal.ops.get(0));
                ((Variable) newVal.ops.get(0)).value = ((Variable) val.ops.get(0)).value;
                if (newVal.ops.size() != 1 || val.ops.size() != 1)
                    setFuncParams(newVal.ops.get(1), val.ops.get(1));
                else return;
            }
        } else {
            Opr fst = newVal, scnd = val;
            if (newVal.ops.get(0).operType.equals(operType.NEXTSTMT))
                fst = newVal.ops.get(0);
            if (val.ops.get(0).operType.equals(operType.NEXTSTMT))
                scnd = val.ops.get(0);
            setFuncParams(fst, scnd);
        }
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
