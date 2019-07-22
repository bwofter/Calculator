package wofter.calculator.internals;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides a context for the interpreter to bind variables to.
 *
 * This should not be used by outside code.
 */
final public class Context {

    /**
     * The variables of this context.
     */
    final private Map<String, BigDecimal> vars = new HashMap<>();

    /**
     * Attempts to obtain a variable from the context. If no variable exists by the given name, 0 is returned.
     *
     * @param var The name of the variable to resolve.
     * @return The value of the variable, or 0.
     */
    public BigDecimal getVar(final String var) {
        if (vars.containsKey(var)) {
            return Optional.ofNullable(vars.get(var)).orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Attempts to add a variable to the context.
     *
     * @param var The name of the variable to set.
     * @param val The new value of the variable.
     * @return The value of val.
     */
    public BigDecimal putVar(final String var, final BigDecimal val) {
        if (null == var) {
            throw new IllegalArgumentException("var must be a valid string.");
        }
        if (null == val) {
            throw new IllegalArgumentException("val must be a valid big decimal.");
        }
        vars.put(var, val);
        return val;
    }
}