package ast;

public interface Node {
    // tokenLiteral() is only used for debugging.
    // it is not reachable from the entry point in the project;
    // IDEA will warn, to use annotation to eliminate it.
    @SuppressWarnings("unused")
    String tokenLiteral();
}
