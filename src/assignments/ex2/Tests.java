package assignments.ex2;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestScell {

    @Test
    public void testConstructorAndGetData() {
        SCell cell = new SCell("hello");
        assertEquals("hello", cell.getData());
    }

    @Test
    public void testDetectType_Text() {
        SCell cell = new SCell("hello");
        assertEquals(Ex2Utils.TEXT, cell.getType());
    }

    @Test
    public void testDetectType_Number() {
        SCell cell = new SCell("123.45");
        assertEquals(Ex2Utils.NUMBER, cell.getType());
    }

    @Test
    public void testDetectType_Form() {
        SCell cell = new SCell("=2+3");
        assertEquals(Ex2Utils.FORM, cell.getType());
    }

    @Test
    public void testSetData() {
        SCell cell = new SCell("initial");
        cell.setData("new data");
        assertEquals("new data", cell.getData());
    }

    @Test
    public void testComputeForm_ValidExpression() {
        SCell cell = new SCell("=2+3");
        assertEquals(5.0, SCell.computeForm(cell.getData()));
    }

    @Test
    public void testComputeForm_InvalidExpression() {
        SCell cell = new SCell("=2++3");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> SCell.computeForm(cell.getData()));
        assertTrue(exception.getMessage().contains("Invalid formula"));
    }

    @Test
    public void testIsNumber() {
        assertTrue(SCell.isNumber("123"));
        assertTrue(SCell.isNumber("123.45"));
        assertFalse(SCell.isNumber("abc"));
    }

    @Test
    public void testIsText() {
        assertTrue(SCell.isText("hello"));
        assertFalse(SCell.isText("123"));
        assertFalse(SCell.isText("=2+3"));
    }

    @Test
    public void testIsForm() {
        assertTrue(SCell.isForm("=2+3"));
        assertFalse(SCell.isForm("hello"));
        assertFalse(SCell.isForm("123"));
        assertFalse(SCell.isForm("=2++3"));
    }

    @Test
    public void testGetAndSetOrder() {
        SCell cell = new SCell("test");
        cell.setOrder(42);
        assertEquals(42, cell.getOrder());
    }

    @Test
    public void testToString() {
        SCell cell = new SCell("hello");
        assertEquals("hello", cell.toString());
    }
}
    class Ex2SheetTest {

        @Test
        public void testInitialization() {
            Ex2Sheet sheet = new Ex2Sheet(5, 5);
            assertEquals(5, sheet.width());
            assertEquals(5, sheet.height());

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(i, j).getData());
                }
            }
        }

        @Test
        public void testSetAndGet() {
            Ex2Sheet sheet = new Ex2Sheet(3, 3);
            sheet.set(0, 0, "42");
            assertEquals("42", sheet.get(0, 0).getData());

            sheet.set(1, 1, "Hello");
            assertEquals("Hello", sheet.get(1, 1).getData());

            sheet.set(2, 2, "=A1+B1");
            assertEquals("=A1+B1", sheet.get(2, 2).getData());
        }

        @Test
        public void testInvalidCoordinates() {
            Ex2Sheet sheet = new Ex2Sheet(2, 2);
            assertNull(sheet.get(-1, 0));
            assertNull(sheet.get(0, -1));
            assertNull(sheet.get(3, 0));
            assertNull(sheet.get(0, 3));
        }

        @Test
        public void testEmptyCell() {
            Ex2Sheet sheet = new Ex2Sheet(2, 2);
            assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(0, 0));
        }

        @Test
        public void testFormulaEvaluation() {
            Ex2Sheet sheet = new Ex2Sheet(3, 3);
            sheet.set(0, 1, "5");
            sheet.set(1, 2, "10");
            sheet.set(2, 2, "=A1+B2");
            assertEquals("15.0", sheet.value(2, 2));
        }

        @Test
        public void testFormulaError() {
            Ex2Sheet sheet = new Ex2Sheet(3, 3);
            sheet.set(0, 0, "=INVALID");
            assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        }

        @Test
        public void testCycleDetection() {
            Ex2Sheet sheet = new Ex2Sheet(2, 2);
            sheet.set(0, 0, "=B1");
            sheet.set(1, 1, "=A0");
            assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
            assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 1));
        }

        @Test
        public void testSaveAndLoad() throws IOException {
            Ex2Sheet sheet = new Ex2Sheet(3, 3);
            sheet.set(0, 0, "5");
            sheet.set(1, 1, "Hello");
            sheet.set(2, 2, "=A1+B1");

            String fileName = "test_sheet.csv";
            sheet.save(fileName);

            Ex2Sheet loadedSheet = new Ex2Sheet(3, 3);
            loadedSheet.load(fileName);

            assertEquals("5", loadedSheet.get(0, 0).getData());
            assertEquals("Hello", loadedSheet.get(1, 1).getData());
            assertEquals("=A1+B1", loadedSheet.get(2, 2).getData());

            new File(fileName).delete();
        }

        @Test
        public void testDepthComputation() {
            Ex2Sheet sheet = new Ex2Sheet(3, 3);
            sheet.set(0, 0, "=B1");
            sheet.set(1, 0, "42");

            int[][] depths = sheet.depth();
            assertEquals(1, depths[0][0]);
            assertEquals(0, depths[1][0]);
        }

        @Test
        public void testEdgeCases() {
            Ex2Sheet sheet = new Ex2Sheet(1, 1);

            // Null and empty values
            sheet.set(0, 0, null);
            assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(0, 0).getData());

            sheet.set(0, 0, "");
            assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(0, 0).getData());

            // Single-cell reference
            sheet.set(0, 0, "=A0");
            assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        }
}
