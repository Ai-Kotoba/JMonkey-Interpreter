package evaluator;


import object.ObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static evaluator.Builtins.builtins;

public class Evaluator {
    public static final object.Null NULL = new object.Null();
    public static final object.Boolean TRUE = new object.Boolean(true);
    public static final object.Boolean FALSE = new object.Boolean(false);


    public static object.Object eval(ast.Node node, object.Environment env) {
        switch (node) {
            // Statements
            case ast.Program actual -> {
                return evalProgram(actual, env);
            }
            case ast.BlockStatement actual -> {
                return evalBlockStatement(actual, env);
            }
            case ast.ExpressionStatement actual -> {
                return eval(actual.expression(), env);
            }
            case ast.ReturnStatement actual -> {
                object.Object val = eval(actual.returnValue(), env);
                if (isError(val)) {
                    return val;
                }
                return new object.ReturnValue(val);
            }
            case ast.LetStatement actual -> {
                object.Object val = eval(actual.value(), env);
                if (isError(val)) {
                    return val;
                }
                env.set(actual.name().value(), val);
            }
            // Expressions
            case ast.IntegerLiteral actual -> {
                return new object.Integer(actual.value());
            }
            case ast.StringLiteral actual -> {
                return new object.String(actual.value());
            }
            case ast.Boolean actual -> {
                return nativeBoolToBooleanObject(actual.value());
            }
            case ast.PrefixExpression actual -> {
                object.Object right = eval(actual.right(), env);
                if (isError(right)) {
                    return right;
                }
                return evalPrefixExpression(actual.operator(), right);
            }
            case ast.InfixExpression actual -> {
                object.Object left = eval(actual.left(), env);
                if (isError(left)) {
                    return left;
                }
                object.Object right = eval(actual.right(), env);
                if (isError(right)) {
                    return right;
                }
                // Speechlessly, IDEA warns that left may be null, but does not warn right!
                // noinspection ConstantConditions
                return evalInfixExpression(actual.operator(), left, right);
            }
            case ast.IfExpression actual -> {
                return evalIfExpression(actual, env);
            }
            case ast.Identifier actual -> {
                return evalIdentifier(actual, env);
            }
            case ast.FunctionLiteral actual -> {
                List<ast.Identifier> params = actual.parameters();
                ast.BlockStatement body = actual.body();
                return new object.Function(params, body, env);
            }
            case ast.CallExpression actual -> {
                object.Object function = eval(actual.function(), env);
                if (isError(function)) {
                    return function;
                }
                List<object.Object> args = evalExpressions(actual.arguments(), env);
                // Error will be returned as an Object in List.
                if (args.size() == 1 && isError(args.get(0))) {
                    return args.get(0);
                }
                assert function != null;
                return applyFunction(function, args);
            }
            case ast.ArrayLiteral actual -> {
                List<object.Object> elements = evalExpressions(actual.elements(), env);
                if (elements.size() == 1 && isError(elements.get(0))) {
                    return elements.get(0);
                }
                return new object.Array(elements);
            }
            case ast.IndexExpression actual -> {
                object.Object left = eval(actual.left(), env);
                if (isError(left)) {
                    return left;
                }
                object.Object index = eval(actual.index(), env);
                if (isError(index)) {
                    return index;
                }
                assert left != null;
                return evalIndexExpression(left, index);
            }
            case ast.HashLiteral actual -> {
                return evalHashLiteral(actual, env);
            }
            // This line of code is semantically redundant and cannot be deleted syntactically,
            // unless the switch statement is changed to an if-else statement.
            default -> throw new IllegalStateException("Unexpected value: " + node);
        }
        return null;
    }

    private static object.Object evalHashLiteral(ast.HashLiteral node, object.Environment env) {

        Map<object.HashKey, object.HashPair> pairs = new HashMap<>();
        for (var n : node.pairs().entrySet()) {
            object.Object key = eval(n.getKey(), env);
            if (isError(key)) {
                return key;
            }
            if (!(key instanceof object.HashTable hashKey)) {
                assert key != null;
                return newError("unusable as hash key: %s", key.type().literal());
            }
            object.Object value = eval(n.getValue(), env);
            if (isError(value)) {
                return value;
            }
            object.HashKey hashed = hashKey.hashKey();
            pairs.put(hashed, new object.HashPair(key, value));
        }
        return new object.Hash(pairs);
    }

    private static object.Object evalIndexExpression(object.Object left, object.Object index) {
        if (left.type() == ObjectType.ARRAY_OBJ && index.type() == ObjectType.INTEGER_OBJ) {
            return evalArrayIndexExpression(left, index);
        } else if (left.type() == ObjectType.HASH_OBJ) {
            return evalHashIndexExpression(left, index);
        } else {
            return newError("index operator not supported: %s", left.type());
        }
    }

    private static object.Object evalArrayIndexExpression(object.Object array, object.Object index) {
        object.Array arrayObject = (object.Array) array;
        int idx = ((object.Integer) index).value();
        int max = arrayObject.elements().size() - 1;
        if (idx < 0 || idx > max) {
            return NULL;
        }
        return arrayObject.elements().get(idx);
    }

    private static object.Object evalHashIndexExpression(object.Object hash, object.Object index) {
        object.Hash hashObject = (object.Hash) hash;
        if (!(index instanceof object.HashTable key)) {
            return newError("unusable as hash key: %s", index.type());
        }
        object.HashPair pair = hashObject.pairs().get(key.hashKey());
        if (pair == null) {
            return NULL;
        }
        return pair.value();
    }

    private static object.Object evalProgram(ast.Program program, object.Environment env) {
        object.Object result = null;
        for (var statement : program.statements()) {
            result = eval(statement, env);
            if (result instanceof object.ReturnValue returnValue) {
                return returnValue.value();
            }
            if (result instanceof object.Error error) {
                return error;
            }
        }
        return result;
    }

    private static object.Object evalBlockStatement(ast.BlockStatement block, object.Environment env) {
        object.Object result = null;
        for (var statement : block.statements()) {
            result = eval(statement, env);
            if (result != null) {
                ObjectType type = result.type();
                if (type == ObjectType.RETURN_VALUE_OBJ || type == ObjectType.ERROR_OBJ) {
                    return result;
                }
            }
        }
        return result;
    }

    private static object.Boolean nativeBoolToBooleanObject(boolean input) {
        if (input) {
            return TRUE;
        }
        return FALSE;
    }

    private static object.Object evalPrefixExpression(String operator, object.Object right) {
        return switch (operator) {
            case "!" -> evalBangOperatorExpression(right);
            case "-" -> evalMinusPrefixOperatorExpression(right);
            default -> newError("unknown operator: %s%s", operator, right.type().literal());
        };
    }

    private static object.Object evalInfixExpression(String operator, object.Object left, object.Object right) {
        if (left.type() == ObjectType.INTEGER_OBJ && right.type() == ObjectType.INTEGER_OBJ) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if (left.type() == ObjectType.STRING_OBJ && right.type() == ObjectType.STRING_OBJ) {
            return evalStringInfixExpression(operator, left, right);
        } else if (operator.equals("==")) {
            // left and right are TRUE or FALSE, there is no problem using "=="
            // to compare whether it is the same object.
            return nativeBoolToBooleanObject(left == right);
        } else if (operator.equals("!=")) {
            return nativeBoolToBooleanObject(left != right);
        } else if (left.type() != right.type()) {
            return newError("type mismatch: %s %s %s", left.type().literal(), operator, right.type().literal());
        } else {
            return newError("unknown operator: %s %s %s", left.type().literal(), operator, right.type().literal());
        }
    }

    private static object.Object evalBangOperatorExpression(object.Object right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private static object.Object evalMinusPrefixOperatorExpression(object.Object right) {
        if (right.type() != ObjectType.INTEGER_OBJ) {
            return newError("unknown operator: -%s", right.type().literal());
        }
        int value = ((object.Integer) right).value();
        return new object.Integer(-value);
    }

    private static object.Object evalIntegerInfixExpression(String operator, object.Object left, object.Object right) {
        int leftVal = ((object.Integer) left).value();
        int rightVal = ((object.Integer) right).value();
        return switch (operator) {
            case "+" -> new object.Integer(leftVal + rightVal);
            case "-" -> new object.Integer(leftVal - rightVal);
            case "*" -> new object.Integer(leftVal * rightVal);
            case "/" -> new object.Integer(leftVal / rightVal);
            case "<" -> nativeBoolToBooleanObject(leftVal < rightVal);
            case ">" -> nativeBoolToBooleanObject(leftVal > rightVal);
            case "==" -> nativeBoolToBooleanObject(leftVal == rightVal);
            case "!=" -> nativeBoolToBooleanObject(leftVal != rightVal);
            default -> newError("unknown operator: %s %s %s", left.type().literal(), operator, right.type().literal());
        };
    }

    private static object.Object evalStringInfixExpression(String operator, object.Object left, object.Object right) {
        if (!operator.equals("+")) {
            return newError("unknown operator: %s %s %s", left.type().literal(), operator, right.type().literal());
        }
        String leftVal = ((object.String) left).value();
        String rightVal = ((object.String) right).value();
        return new object.String(leftVal + rightVal);
    }

    private static object.Object evalIfExpression(ast.IfExpression ie, object.Environment env) {
        object.Object condition = eval(ie.condition(), env);
        if (isError(condition)) {
            return condition;
        }
        if (isTruthy(condition)) {
            return eval(ie.consequence(), env);
        } else if (ie.alternative() != null) {
            return eval(ie.alternative(), env);
        } else {
            return NULL;
        }
    }

    private static object.Object evalIdentifier(ast.Identifier node, object.Environment env) {
        object.Object val = env.get(node.value());
        if (val != null) {
            return val;
        }
        object.Builtin builtin = builtins.get(node.value());
        if (builtin != null) {
            return builtin;
        }
        return newError("identifier not found: " + node.value());
    }

    private static boolean isTruthy(object.Object obj) {
        if (obj == TRUE) {
            return true;
        } else if (obj == FALSE) {
            return false;
        } else {
            return obj != NULL;
        }
    }

    public static object.Error newError(String format, Object... args) {
        return new object.Error(String.format(format, args));
    }

    private static boolean isError(object.Object obj) {
        if (obj != null) {
            return obj.type() == ObjectType.ERROR_OBJ;
        }
        return false;
    }

    private static List<object.Object> evalExpressions(List<ast.Expression> exps, object.Environment env) {
        List<object.Object> result = new ArrayList<>();
        for (ast.Expression expr : exps) {
            object.Object evaluated = eval(expr, env);
            if (isError(evaluated)) {
                return List.of(evaluated);
            }
            result.add(evaluated);
        }
        return result;
    }

    private static object.Object applyFunction(object.Object fn, List<object.Object> args) {
        switch (fn) {
            case object.Function actual -> {
                object.Environment extendedEnv = extendFunctionEnv(actual, args);
                object.Object evaluated = eval(actual.body(), extendedEnv);
                return unwrapReturnValue(evaluated);
            }
            case object.Builtin actual -> {
                // This is ugly!!!
                return actual.fn().exec(args.toArray(new object.Object[]{}));
            }
            default -> {
                return newError("not a function: %s", fn.type());
            }
        }
    }

    private static object.Environment extendFunctionEnv(object.Function fn, List<object.Object> args) {
        object.Environment env = object.Environment.newEnclosedEnvironment(fn.env());
        for (int i = 0; i < fn.parameters().size(); i++) {
            env.set(fn.parameters().get(i).value(), args.get(i));
        }
        return env;
    }

    private static object.Object unwrapReturnValue(object.Object obj) {
        if (obj instanceof object.ReturnValue object) {
            return object.value();
        }
        return obj;
    }
}
