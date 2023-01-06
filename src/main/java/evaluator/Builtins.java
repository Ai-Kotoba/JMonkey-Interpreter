package evaluator;

import object.Array;
import object.Object;
import object.Builtin;
import object.ObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static evaluator.Evaluator.NULL;
import static evaluator.Evaluator.newError;

public class Builtins {
    static final Map<String, Builtin> builtins = Map.ofEntries(
            Map.entry("len", new Builtin((Object... args) -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                switch (args[0]) {
                    case Array actual -> {
                        return new object.Integer(actual.elements().size());
                    }
                    case object.String actual -> {
                        return new object.Integer(actual.value().length());
                    }
                    default -> {
                        return newError("argument to `len` not supported, got %s",
                                args[0].type().literal());
                    }
                }
            })),
            Map.entry("puts", new Builtin((Object... args) -> {
                for (var arg : args) {
                    System.out.println(arg.inspect());
                }
                return NULL;
            })),
            Map.entry("first", new Builtin((Object... args) -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (args[0].type() != ObjectType.ARRAY_OBJ) {
                    return newError("argument to `first` must be ARRAY, got %s",
                            args[0].type().literal());
                }
                Array arr = (Array) args[0];
                int length = arr.elements().size();
                if (length > 0) {
                    return arr.elements().get(0);
                }
                return NULL;
            })),
            Map.entry("last", new Builtin((Object... args) -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (args[0].type() != ObjectType.ARRAY_OBJ) {
                    return newError("argument to `last` must be ARRAY, got %s",
                            args[0].type().literal());
                }
                Array arr = (Array) args[0];
                int length = arr.elements().size();
                if (length > 0) {
                    return arr.elements().get(length - 1);
                }
                return NULL;
            })),
            Map.entry("rest", new Builtin((Object... args) -> {
                if (args.length != 1) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (args[0].type() != ObjectType.ARRAY_OBJ) {
                    return newError("argument to `rest` must be ARRAY, got %s",
                            args[0].type().literal());
                }
                Array arr = (Array) args[0];
                int length = arr.elements().size();
                if (length > 0) {
                    return new Array(arr.elements().subList(1, length));
                }
                return NULL;
            })),
            Map.entry("push", new Builtin((Object... args) -> {
                if (args.length != 2) {
                    return newError("wrong number of arguments. got=%d, want=1", args.length);
                }
                if (args[0].type() != ObjectType.ARRAY_OBJ) {
                    return newError("argument to `push` must be ARRAY, got %s",
                            args[0].type().literal());
                }
                Array arr = (Array) args[0];
                List<Object> newElements = new ArrayList<>(arr.elements());
                newElements.add(args[1]);
                return new Array(newElements);
            }))
    );
}