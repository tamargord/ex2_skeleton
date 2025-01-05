## Ex2Sheet:

The class does a spreedsheet where you can put a grid of cells with rows and columns.
You can put in the spreedsheet numbers,formulas and letters.


### Constructor Methods:
it initializes a spreedsheet with width and height and creates it with default dimensions.
### Methods:

- **`boolean isIn(int x, int y)`**
- it checks if the given coordinates are within the bounds of the spreadsheet, if not it"ll return false.

- **`int width()`**:
- returns the number of columns.

- **`int height()`**:
-returns the number of rows.

- **`void set(int x, int y, String value)`**:
- it sets the value of the cell and stores numeric values directly, also validates formulas.
   
- **`Cell get(int x, int y)`**:
-it retrieves the cell object at the coordinates and will return null if the cell is out of bounds.

- **`Cell get(String entry)`**:
- it retrieves a cell object on a cell reference sring and uses the parseIndex method to convert it.
  
 - **`String value(int x, int y)`**:
-it returns the value of the cell at the coordinates and returns plain test of numeric values.

- **`void eval()`**:
 - it iterates over the cells and evaluates their values.
 
- **`String eval(int x, int y)`**:
- it returns the values of a specific cell.

- **`String evaluateCell(int x, int y, boolean[][] visited)`**:
- it evaluates a cell while tracking visited cells and if there  is invalid formulas it"l return ERR_FORM.

- **`String evaluateFormula(String data, boolean[][] visited)`**:
-it evaluates a formula string that starts with equal and if there is invalid formulas it"ll return ERR_FORM.

- **`String resolveToken(String token, boolean[][] visited)`**:
- it resolves a token within a formula and if it is a cell reference it"ll retrieve it's value.
  if it's a number or a text it"ll return it directly.
  
- **`boolean isCellReference(String token)`**:
- it checks if a string is a valid cell reference and it"ll validate column letters.
  
- **`Index2D parseIndex(String entry)`**:
- it converts a cell reference into a 2D index and it"ll return null if it's invalid.
  
- **`int[][] depth()`**:
it computes the depth for the cells,
if the depth is zero-cell is with plain text or number.
if the depth s one-cell contains formulas.
  
- **`int computeDepth(int x, int y)`**:
- it computes the depth of the cell in it's coordinates based in it's value.
 
- **`void save(String fileName)`**
  - it saves the spreadsheet to a file.

- **`void load(String fileName)`**:
- it loads a spreedsheet data from a file and resets the cells before populating the data and it skips invalis entries.
 
- **`boolean isProbablyValidFormula(String val)`**:
- it checks if a string is a vaild formula.

  ## SCell Class:
`it represents a spreadsheet cell capable of handling text, numbers, and formulas.`
### Constructor:
`SCell(String s)`:
it initializes a new object with a given string and it sets the data.
### Methods:
`void setData(String s)`-
it sets the cell data and detects it's type.

`String getData()`:
it returns the current data in the cell.

`void normalizeToDoubleString(String s)`-
it ensures numeric values are converted to decimal string format.

`int getType()`-
it returns the type of the cell.

`void setType(int t)`- 
it sets the type of the cell.

`int detectType(String s)`- 
it detects the type of the string.

`int getOrder()`- 
it retrieves the cell's order.

`void setOrder(int t)`-
it sets the cell's order.

`boolean isNumber(String text)`-
it checks if the string is a valid number.

`boolean isText(String text)`-
it checks if the string is a valid text.

`boolean isForm(String text)`-
it validates if the string is well formed formula.

`boolean isValidToken(String token)`-
it validates tokens in formulas to ensure they are valid.

`double computeForm(String form)`-
it computes the result of a valid formula and if invalid it"ll throw an exception.

`double evaluateExpression(String expr)`:
it evaluates mathematical expressions.

`double evaluateSimpleExpression(String expr)`-
it processes simple mathematical expressions.

`String processMultiplicationAndDivision(String expr)`-
it handles multiplication and division operations in an expression.

`String processAdditionAndSubtraction(String expr)`-
it handles addition and subtraction operations in an expression.

`String processExpression(String expr, String operators)`-
it processes an expression for the operators.

`int findOperatorIndex(String expr, String operators)`-
It finds the index of the first occurrence of the specified operators in the expression.

`boolean countBrackets(String brackets)`-
 it checks for balanced parentheses in the string.

`boolean countBrackets(String brackets, int index, int open, int close)`-
it counts balanced parentheses.



