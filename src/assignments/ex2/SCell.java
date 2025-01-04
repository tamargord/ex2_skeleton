package assignments.ex2;

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;

    public SCell(String s) {
        setData(s);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        this.line = s;
        this.type = detectType(s);
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    private int detectType(String s) {
        // If the string is null or empty, treat it as text (empty cell)
        if (s == null || s.isEmpty()) {
            return Ex2Utils.TEXT;
        }
        if (isNumber(s)) {
            return Ex2Utils.NUMBER;
        } else if (isForm(s)) {
            return Ex2Utils.FORM;
        } else if (isText(s)) {
            return Ex2Utils.TEXT;
        }
        return Ex2Utils.ERR_FORM_FORMAT;
    }


    public static boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isText(String text) {
        if (text == null || text.isEmpty()) return false;
        return !isNumber(text) && !isForm(text);
    }

    // ---------------- SCell.java ----------------
    public static boolean isForm(String text) {
        if (text == null || text.isEmpty()) return false;
        if (!text.startsWith("=")) return false;
        String expr = text.substring(1).trim();
        if (expr.isEmpty()) return false;

        int pCount = 0;
        boolean lastOp = true;  // track consecutive ops
        String token = "";

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            // accumulate letters, digits, decimal point in 'token'
            if (Character.isLetterOrDigit(c) || c == '.') {
                token += c;
            } else {
                // once we hit an operator or bracket, validate the 'token'
                if (!token.isEmpty()) {
                    if (!isValidToken(token)) return false;
                    token = "";
                    lastOp = false;
                }
                // handle the operator or bracket
                switch (c) {
                    case '+', '-', '*', '/':
                        if (lastOp) return false;   // e.g. "++" is invalid
                        lastOp = true;
                        break;
                    case '(':
                        pCount++;
                        lastOp = true;
                        break;
                    case ')':
                        pCount--;
                        if (pCount < 0) return false;
                        lastOp = false;
                        break;
                    default:
                        return false;  // invalid character
                }
            }
        }

        // if there's a trailing token after the loop
        if (!token.isEmpty()) {
            if (!isValidToken(token)) return false;
        }
        // matched parentheses?
        if (pCount != 0) return false;

        return true;
    }

    // SCell.java
    private static boolean isValidToken(String token) {
        if (token.matches("\\d+(\\.\\d+)?")) {
            return true;
        }
        // 2) Accept cell references (e.g. A0, B2, C10, AA9)
        if (token.matches("[A-Z]+[0-9]{1,2}")) {
            return true;
        }
        // 3) Otherwise, invalid
        return false;
    }




    private static boolean countBrackets(String brackets) {
        return countBrackets(brackets, 0, 0, 0);
    }

    private static boolean countBrackets(String brackets, int index, int open, int close) {
        if (index == brackets.length()) return open == close;

        char ch = brackets.charAt(index);
        if (ch == '(') open++;
        if (ch == ')') {
            close++;
            if (close > open) return false;
        }
        return countBrackets(brackets, index + 1, open, close);
    }

    public static double computeForm(String form) {
        if (!isForm(form)) throw new IllegalArgumentException("Invalid formula: " + form);

        if (form.startsWith("=")) {
            form = form.substring(1);
        }
        return evaluateExpression(form);
    }

    private static double evaluateExpression(String expr) {
        if (!expr.contains("(")) {
            return evaluateSimpleExpression(expr);
        }

        int openIndex = expr.lastIndexOf('(');
        int closeIndex = expr.indexOf(')', openIndex);

        if (closeIndex == -1) throw new IllegalArgumentException("Mismatched parentheses: " + expr);

        String inside = expr.substring(openIndex + 1, closeIndex);
        double valueInside = evaluateExpression(inside);

        String newExpr = expr.substring(0, openIndex) + valueInside + expr.substring(closeIndex + 1);
        return evaluateExpression(newExpr);
    }

    private static double evaluateSimpleExpression(String expr) {
        while (expr.contains("+") || expr.contains("-") || expr.contains("*") || expr.contains("/")) {
            if (expr.contains("*") || expr.contains("/")) {
                expr = processMultiplicationAndDivision(expr);
            } else {
                expr = processAdditionAndSubtraction(expr);
            }
        }
        return Double.parseDouble(expr);
    }

    private static String processMultiplicationAndDivision(String expr) {
        return processExpression(expr, "*/");
    }

    private static String processAdditionAndSubtraction(String expr) {
        return processExpression(expr, "+-");
    }

    private static String processExpression(String expr, String operators) {
        int operatorIndex = findOperatorIndex(expr, operators);

        int leftStart = operatorIndex - 1;
        while (leftStart >= 0 && (Character.isDigit(expr.charAt(leftStart)) || expr.charAt(leftStart) == '.')) {
            leftStart--;
        }
        String leftStr = expr.substring(leftStart + 1, operatorIndex);
        double left = Double.parseDouble(leftStr);

        int rightEnd = operatorIndex + 1;
        while (rightEnd < expr.length() && (Character.isDigit(expr.charAt(rightEnd)) || expr.charAt(rightEnd) == '.')) {
            rightEnd++;
        }
        String rightStr = expr.substring(operatorIndex + 1, rightEnd);
        double right = Double.parseDouble(rightStr);

        char operator = expr.charAt(operatorIndex);
        double result = switch (operator) {
            case '*' -> left * right;
            case '/' -> left / right;
            case '+' -> left + right;
            case '-' -> left - right;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };

        return expr.substring(0, leftStart + 1) + result + expr.substring(rightEnd);
    }

    private static int findOperatorIndex(String expr, String operators) {
        int minIndex = -1;
        for (char operator : operators.toCharArray()) {
            int index = expr.indexOf(operator);
            if (index != -1 && (minIndex == -1 || index < minIndex)) {
                minIndex = index;
            }
        }
        return minIndex;
    }

    public static void main(String[] args) {
        // Test cases
        SCell cell1 = new SCell("5");
        System.out.println("Cell1: " + cell1.getData() + " Type: " + cell1.getType());

        SCell cell2 = new SCell("=2+3");
        System.out.println("Cell2: " + cell2.getData() + " Type: " + cell2.getType() + " Computed: " + computeForm(cell2.getData()));

        SCell cell3 = new SCell("hello");
        System.out.println("Cell3: " + cell3.getData() + " Type: " + cell3.getType());

        SCell cell4 = new SCell("=4*5-2/1");
        System.out.println("Cell4: " + cell4.getData() + " Type: " + cell4.getType() + " Computed: " + computeForm(cell4.getData()));

        SCell cell5 = new SCell("=(3+2)*(4-1)");
        System.out.println("Cell5: " + cell5.getData() + " Type: " + cell5.getType() + " Computed: " + computeForm(cell5.getData()));
    }
}
