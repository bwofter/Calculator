package wofter.calculator.internals;

import wofter.calculator.internals.collections.SimpleQueue;
import wofter.calculator.internals.collections.SimpleStack;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parser implementation.
 *
 * Operators:
 * +, -, *, /, ^ (power)
 * n (round), c (ceiling), f (floor), s (square root)
 */
final public class Parser {

    /**
     * The pattern used for parsing numeric nodes.
     */
    final private static Pattern NUMBER = Pattern.compile("^(?<decimal>[+-]?[0-9]+(?:\\.[0-9]+)?).*", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * The pattern used for parsing unary nodes.
     */
    final private static Pattern UNARY = Pattern.compile("^(?<operator>[nNcCfFsS])[^A-Za-z].*", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * The pattern used for parsing name nodes.
     */
    final private static Pattern NAME = Pattern.compile("^(?<identifier>[_A-Za-z][_A-Za-z0-9]*).*", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * The pattern used for parsing operator nodes.
     */
    final private static Pattern OPERATOR = Pattern.compile("^(?<operator>[-+*/^(=]).*", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * The pattern used for parsing comments.
     */
    final private static Pattern COMMENT = Pattern.compile("^(?<comment>/\\*.*\\*/).*", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Parses the provided formula.
     */
    final public Node parse(String formula) {
        if (null == formula) {
            throw new IllegalArgumentException("formula must be a valid string.");
        }
        final SimpleQueue<Node> values = new SimpleQueue<>();
        final SimpleStack<Node.Operation> operations = new SimpleStack<>();
        while (0 < formula.length()) {
            Matcher matcher = COMMENT.matcher(formula);
            if (matcher.matches()) {
                formula = formula.substring(matcher.group("comment").length());
                continue;
            }
            matcher = NUMBER.matcher(formula);
            if (matcher.matches()) {
                formula = formula.substring(matcher.group("decimal").length());
                values.push(new Node.Literal(new BigDecimal(matcher.group("decimal"))));
                continue;
            }
            matcher = UNARY.matcher(formula);
            if (matcher.matches()) {
                formula = formula.substring(matcher.group("operator").length());
                final UnaryOperator<BigDecimal> unary;
                //TODO: Move this into a visitor pattern instead.
                switch (matcher.group("operator")) {
                    case "n":
                    case "N":
                        unary = d -> d.setScale(0, RoundingMode.HALF_UP);
                        break;
                    case "c":
                    case "C":
                        unary = d -> d.setScale(0, RoundingMode.CEILING);
                        break;
                    case "s":
                    case "S":
                        unary = d -> d.sqrt(MathContext.DECIMAL128);
                        break;
                    default:
                        unary = d -> d.setScale(0, RoundingMode.FLOOR);
                        break;

                }
                final Node.Unary operation = new Node.Unary(unary, -1, Node.Associativity.RIGHT);
                operationsToQueue(values, operations, operation);
                operations.push(operation);
                continue;
            }
            matcher = NAME.matcher(formula);
            if (matcher.matches()) {
                formula = formula.substring(matcher.group("identifier").length());
                values.push(new Node.Identifier(matcher.group("identifier")));
                continue;
            }
            matcher = OPERATOR.matcher(formula);
            if (matcher.matches()) {
                final BinaryOperator<BigDecimal> binary;
                final int precedence;
                final Node.Associativity associativity;
                final Node.Binary operation;
                formula = formula.substring(matcher.group("operator").length());
                //TODO: Move this into a visitor pattern instead.
                switch (matcher.group("operator")) {
                    case "-":
                        binary = (l, r) -> ((BigDecimal) l).subtract((BigDecimal) r);
                        precedence = 0;
                        associativity = Node.Associativity.LEFT;
                        break;
                    case "+":
                        binary = (l, r) -> ((BigDecimal) l).add((BigDecimal) r);
                        precedence = 0;
                        associativity = Node.Associativity.LEFT;
                        break;
                    case "*":
                        binary = (l, r) -> ((BigDecimal) l).multiply((BigDecimal) r);
                        precedence = 1;
                        associativity = Node.Associativity.LEFT;
                        break;
                    case "/":
                        binary = (l, r) -> ((BigDecimal) l).divide((BigDecimal) r, RoundingMode.UNNECESSARY);
                        precedence = 1;
                        associativity = Node.Associativity.LEFT;
                        break;
                    case "^":
                        binary = (l, r) -> ((BigDecimal) l).pow(((BigDecimal) r).intValue());
                        precedence = 2;
                        associativity = Node.Associativity.LEFT;
                        break;
                    default:
                        binary = null;
                        precedence = -1;
                        associativity = Node.Associativity.NONE;
                        break;
                }
                operation = null == binary ? null : new Node.Binary(binary, precedence, associativity);
                operationsToQueue(values, operations, operation);
                operations.push(operation);
                continue;
            }
            final char imm = formula.charAt(0);
            if (')' == imm) {
                formula = formula.substring(1);
                operationsToQueue(values, operations);
                if (operations.isEmpty()) {
                    //TODO: Add error here.
                }
                operations.pop();
            } else if (Character.isWhitespace(imm)) {
                formula = formula.trim();
            }
        }
        operationsToQueue(values, operations);
        return buildTree(values);
    }

    private Node buildTree(final SimpleQueue<Node> values) {
        Node n = null;
        final SimpleStack<Node> nodes = new SimpleStack<>();
        while (!values.isEmpty()) {
            Node c = values.pop();
            if (c instanceof Node.Operation) {
                n = c;
                c.addChild(nodes.pop());
                if (c instanceof Node.Binary) {
                    c.addChild(nodes.pop());
                }
            } else if (null == n) {
                n = c;
            }
            nodes.push(c);
        }
        return n;
    }

    private void operationsToQueue(final SimpleQueue<Node> values, final SimpleStack<Node.Operation> operations) {
        while (!operations.isEmpty() && null != operations.peek()) {
            values.push((Node) operations.pop());
        }
    }

    private void operationsToQueue(final SimpleQueue<Node> values, final SimpleStack<Node.Operation> operations, final Node.Operation operation) {
        if (null == operation) {
            return;
        }
        final int currentPrecedence = operation.getPrecedence();
        while (!operations.isEmpty() && null != operations.peek()) {
            final Node.Operation priorOperation = operations.peek();
            final int priorPrecedence = priorOperation.getPrecedence();
            final Node.Associativity priorAssociativity = priorOperation.getAssociativity();
            if (priorPrecedence > currentPrecedence || priorPrecedence == currentPrecedence && priorAssociativity == Node.Associativity.LEFT) {
                values.push((Node) operations.pop());
            } else {
                break;
            }
        }
    }
}
