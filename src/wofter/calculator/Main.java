package wofter.calculator;

import wofter.calculator.internals.Context;
import wofter.calculator.internals.Node;
import wofter.calculator.internals.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        Node node = parser.parse("s((xt-xo)^2+(yt-yo)^2)");
        Context context = new Context();
        context.putVar("xt", new BigDecimal(10));
        context.putVar("xo", new BigDecimal(11));
        context.putVar("yt", new BigDecimal(-1));
        context.putVar("yo", new BigDecimal(128));
        BigDecimal result = node.execute(context);
    }
}
