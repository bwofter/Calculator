package wofter.calculator.internals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Represents a node in the AST.
 */
abstract public class Node {

    /**
     * The children of this node.
     */
    final private List<Node> children = new ArrayList<>();

    /**
     * Gets a child from this node.
     */
    final Node getChild(final int i) {
        return children.get(i);
    }

    /**
     * Adds a child to this node.
     */
    final void addChild(final Node child) {
        children.add(child);
    }

    /**
     * Executes this node.
     */
    abstract public BigDecimal execute(final Context context);

    /**
     * Represents the associativity of an operation node.
     */
    public enum Associativity {
        /**
         * Declares no associativity.
         */
        NONE,
        /**
         * Declares left associativity.
         */
        LEFT,
        /**
         * Declares right associativity.
         */
        RIGHT,
    }

    /**
     * Represents an operation node.
     */
    public interface Operation {

        /**
         * Returns the precedence of this operation.
         */
        int getPrecedence();

        /**
         * Returns the associativity of this operation.
         */
        Associativity getAssociativity();
    }

    /**
     * Represents a literal node.
     */
    final public static class Literal extends Node {

        /**
         * The value of this literal.
         */
        final private BigDecimal v;

        /**
         * initializes this node with the provided parameters.
         */
        Literal(final BigDecimal v) {
            if (null == v) {
                throw new IllegalArgumentException("v must be a valid big decimal.");
            }
            this.v = v;
        }

        /**
         * @inheritDoc
         */
        @Override
        public BigDecimal execute(final Context context) {
            return v;
        }
    }

    /**
     * Represents an identifier node.
     */
    final public static class Identifier extends Node {

        /**
         * The value of this identifier.
         */
        final private String v;

        /**
         * initializes this node with the provided parameters.
         */
        Identifier(final String v) {
            if (null == v) {
                throw new IllegalArgumentException("v must be a valid big decimal.");
            }
            this.v = v;
        }

        /**
         * @inheritDoc
         */
        @Override
        public BigDecimal execute(final Context context) {
            if (null == context) {
                throw new IllegalArgumentException("context must be a valid Context.");
            }
            return context.getVar(v);
        }
    }

    /**
     * Represents a unary node.
     */
    final public static class Unary extends Node implements Operation {

        /**
         * The operation of this unary.
         */
        final private UnaryOperator<BigDecimal> o;

        /**
         * The precedence of this unary.
         */
        final private int p;

        /**
         * The associativity of this unary.
         */
        final private Associativity a;

        /**
         * initializes this node with the provided parameters.
         */
        Unary(final UnaryOperator<BigDecimal> o, final int p, final Associativity a) {
            if (null == o) {
                throw new IllegalArgumentException("o must be a valid unary operator.");
            }
            if (null == a) {
                throw new IllegalArgumentException("a must be a valid associativity enum.");
            }
            this.o = o;
            this.p = p;
            this.a = a;
        }

        /**
         * @inheritDoc
         */
        @Override
        public BigDecimal execute(final Context context) {
            return this.o.apply(getChild(0).execute(context));
        }

        /**
         * @inheritDoc
         */
        @Override
        public int getPrecedence() {
            return p;
        }

        /**
         * @inheritDoc
         */
        @Override
        public Associativity getAssociativity() {
            return a;
        }
    }

    /**
     * Represents a binary node.
     */
    final public static class Binary extends Node implements Operation {

        /**
         * The operation of this binary.
         */
        final private BinaryOperator<BigDecimal> o;

        /**
         * The precedence of this binary.
         */
        final private int p;

        /**
         * The associativity of this binary.
         */
        final private Associativity a;

        /**
         * initializes this node with the provided parameters.
         */
        Binary(final BinaryOperator<BigDecimal> o, final int p, final Associativity a) {
            if (null == o) {
                throw new IllegalArgumentException("o must be a valid unary operator.");
            }
            if (null == a) {
                throw new IllegalArgumentException("a must be a valid associativity enum.");
            }
            this.o = o;
            this.p = p;
            this.a = a;
        }

        /**
         * @inheritDoc
         */
        @Override
        public BigDecimal execute(final Context context) {
            return this.o.apply(getChild(1).execute(context), getChild(0).execute(context));
        }

        /**
         * @inheritDoc
         */
        @Override
        public int getPrecedence() {
            return p;
        }

        /**
         * @inheritDoc
         */
        @Override
        public Associativity getAssociativity() {
            return a;
        }
    }
}