package assignments.ex2;
import java.io.*;

public class Ex2Sheet implements Sheet {
    private Cell[][] cells;

    public Ex2Sheet(int w, int h) {
        cells = new SCell[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                cells[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && y >= 0 && x < width() && y < height();
    }

    @Override
    public int width() {
        return cells.length;
    }

    @Override
    public int height() {
        return cells[0].length;
    }

    @Override
    public void set(int x, int y, String value) {
        if (!isIn(x, y)) return;
        if (value == null) value = "";

        // 1) Numeric?
        if (SCell.isNumber(value)) {
            cells[x][y] = new SCell(value);
            return;
        }

        // 2) Formula? (must start with '=')
        if (value.startsWith("=")) {
            // Check if it's a valid formula. If not, store "ERR_FORM!"
            if (isProbablyValidFormula(value)) {
                cells[x][y] = new SCell(value);
            } else {
                cells[x][y] = new SCell(Ex2Utils.ERR_FORM);
            }
            return;
        }

        // 3) Otherwise, treat as text
        cells[x][y] = new SCell(value);
    }

    /**
     * A "quick" check for formula validity.
     * You can customize or replace this with something more robust.
     */
    private boolean isProbablyValidFormula(String val) {
        // e.g. at minimum, we might just require "=..." to have something after '='
        // and skip advanced checks if you like.
        if (val.length() < 2) return false;
        // Optionally, do a lightweight parse or check parentheses, etc.
        // For now, let's just say it's "valid" if it has at least 1 character after '='.
        return true;
    }

    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return cells[x][y];
        }
        return null;
    }

    @Override
    public Cell get(String entry) {
        Index2D idx = parseIndex(entry);
        if (idx != null && isIn(idx.getX(), idx.getY())) {
            return cells[idx.getX()][idx.getY()];
        }
        return null;
    }

    /**
     * We do cycle detection at runtime. If we see a cycle, we return ERR_CYCLE!.
     */
    @Override
    public String value(int x, int y) {
        boolean[][] visited = new boolean[width()][height()];
        return evaluateCell(x, y, visited);
    }

    private String evaluateCell(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        if (visited[x][y]) return Ex2Utils.ERR_CYCLE;
        visited[x][y] = true;

        Cell c = cells[x][y];
        if (c == null || c.getData() == null) return Ex2Utils.EMPTY_CELL;

        String data = c.getData();

        if (SCell.isNumber(data)) {
            return data;
        }
        if (data.equals(Ex2Utils.ERR_FORM)) {
            return Ex2Utils.ERR_FORM;
        }
        if (data.startsWith("=")) {
            return evaluateFormula(data, visited);
        }
        return data; // text
    }

    private String evaluateFormula(String data, boolean[][] visited) {
        try {
            String expr = data.substring(1);
            String result = "";
            String token = "";
            for (int i = 0; i < expr.length(); i++) {
                char ch = expr.charAt(i);
                if (Character.isLetterOrDigit(ch)) {
                    token += ch;
                } else {
                    if (!token.isEmpty()) {
                        String sub = resolveToken(token, visited);
                        if (Ex2Utils.ERR_CYCLE.equals(sub)) return Ex2Utils.ERR_CYCLE;
                        result += sub;
                        token = "";
                    }
                    result += ch;
                }
            }
            if (!token.isEmpty()) {
                String sub = resolveToken(token, visited);
                if (Ex2Utils.ERR_CYCLE.equals(sub)) return Ex2Utils.ERR_CYCLE;
                result += sub;
            }
            double val = SCell.computeForm("=" + result);
            return String.valueOf(val);
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM;
        }
    }

    private String resolveToken(String token, boolean[][] visited) {
        if (isCellReference(token)) {
            Index2D idx = parseIndex(token);
            if (idx != null) {
                String val = evaluateCell(idx.getX(), idx.getY(), visited);
                if (Ex2Utils.ERR_CYCLE.equals(val)) return Ex2Utils.ERR_CYCLE;
                if (SCell.isNumber(val)) return val;
                if (val.equals(Ex2Utils.ERR_FORM)) return "0";
                return "0";
            }
            return "0";
        }
        return token;
    }

    private boolean isCellReference(String token) {
        if (token == null || token.length() == 0) return false;
        int i = 0;
        while (i < token.length() && Character.isLetter(token.charAt(i))) {
            if (token.charAt(i) < 'A' || token.charAt(i) > 'Z') return false;
            i++;
        }
        if (i == 0 || i == token.length()) return false;
        while (i < token.length()) {
            if (!Character.isDigit(token.charAt(i))) return false;
            i++;
        }
        return true;
    }

    private Index2D parseIndex(String entry) {
        if (entry == null || entry.equals("")) return null;
        String colPart = "";
        String rowPart = "";
        for (int i = 0; i < entry.length(); i++) {
            char ch = entry.charAt(i);
            if (Character.isLetter(ch)) colPart += ch;
            else if (Character.isDigit(ch)) rowPart += ch;
        }
        if (colPart.equals("") || rowPart.equals("")) return null;
        int x = 0;
        for (int i = 0; i < colPart.length(); i++) {
            x = x * 26 + (colPart.charAt(i) - 'A');
        }
        try {
            int y = Integer.parseInt(rowPart);
            return new CellEntry(x, y);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String eval(int x, int y) {
        return value(x, y);
    }

    @Override
    public void eval() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                value(i, j);
            }
        }
    }

    @Override
    public int[][] depth() {
        int[][] d = new int[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                d[i][j] = computeDepth(i, j);
            }
        }
        return d;
    }

    private int computeDepth(int x, int y) {
        Cell c = get(x, y);
        if (c == null) return 0;
        String data = c.getData();
        if (data == null) return 0;
        if (SCell.isNumber(data) || SCell.isText(data)) return 0;
        if (data.startsWith("=")) return 1;
        return Ex2Utils.ERR;
    }

    @Override
    public void save(String fileName) throws IOException {
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(fileName));
            w.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    Cell cell = cells[i][j];
                    if (cell != null && !Ex2Utils.EMPTY_CELL.equals(cell.getData())) {
                        w.write(i + "," + j + "," + cell.getData() + "\n");
                    }
                }
            }
        } finally {
            if (w != null) w.close();
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(fileName));
            r.readLine();
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    cells[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
                }
            }
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    int xx = Integer.parseInt(parts[0]);
                    int yy = Integer.parseInt(parts[1]);
                    set(xx, yy, parts[2]);
                }
            }
        } finally {
            if (r != null) r.close();
        }
    }
}