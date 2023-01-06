package object;

@FunctionalInterface
public interface BuiltinFunction {
    Object exec(Object... args);
}
