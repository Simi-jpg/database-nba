package utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetPrinter {

    private static final String YELLOW_TEXT = "\u001B[33m";
    private static final String RESET_TEXT = "\u001B[0m";
    private static final String GREEN_TEXT = "\u001B[32m";
    private final static String RED_TEXT = "\u001B[31m";



    private static final int ROWS_PER_PAGE = 15;
    private static final int COLS_PER_PAGE = 4;
    private static final int COL_WIDTH = 30;
    

    public static void printResult(ResultSet rs, String title) throws SQLException{
        printResult(rs, title, ROWS_PER_PAGE, COLS_PER_PAGE);

    }

    public static void printResult(ResultSet rs, String title, int rowsPerPage, int colsPerPage) throws SQLException{
       
        ResultSetMetaData rsmd = rs.getMetaData();

        int colCount = rsmd.getColumnCount();

        List<String> headers = new ArrayList<>();

        for (int i = 1; i <= colCount; i++) {

            headers.add(rsmd.getColumnLabel(i));
        }

        List<List<String>> rows = new ArrayList<>();

        while(rs.next()) {

            List <String> r = new ArrayList<>(colCount);

            for (int i = 1; i <= colCount; i++) {

                String tok = rs.getString(i);
                if (tok == null) {

                    tok = "NULL";
                }
                r.add(tok);
            }
            rows.add(r);
        }

        int totalRows = rows.size();
        int totalCols = colCount;



        if (totalRows == 0) {

            System.out.println("No results");
            InputReader.getString("Press Enter to exit query view. ");
            return;
        }

        if(totalRows <= rowsPerPage && totalCols <= colsPerPage) {

            ClearConsole.clear();

            System.out.println("==============================================================================");

            System.out.printf("Viewing: %s | Rows: 1-%d of %d | Cols: 1-%d of %d\n", 
                            title,
                            totalRows, totalRows,
                            totalCols, totalCols
                            );

            
            System.out.println("==============================================================================");
            System.out.println();

            System.out.print(YELLOW_TEXT);

            // Print the header row for visible columns
            for (int c = 0; c < totalCols; c++) {

                String head = headers.get(c);

                if (head.length() > COL_WIDTH - 2 ) {

                    head = head.substring(0, COL_WIDTH - 4) + "...";
                }

                System.out.printf("%-" + COL_WIDTH + "s", head); //TEST
            }

            System.out.print(RESET_TEXT);

            System.out.println();
            System.out.println("-".repeat(totalCols * COL_WIDTH));

            //Print all the rows

            for(List<String> row : rows) {

                for (int c = 0; c < totalCols; c++) {

                    String tok = row.get(c);

                    if (tok.length() > COL_WIDTH - 2 ) {

                        tok = tok.substring(0, COL_WIDTH - 4) + "...";
                    }

                    System.out.printf("%-" + COL_WIDTH + "s", tok); 
                }

                System.out.println();
            }

            System.out.println("==============================================================================");
            return;
            
        }

        int showRow = 0;
        int showCol = 0;

        boolean viewing = true;
        String status = "";

        while(viewing) {

            int rowEnd = Math.min(showRow + rowsPerPage, totalRows);
            int colEnd = Math.min(showCol + colsPerPage, totalCols);

            ClearConsole.clear();

            

            System.out.println("==============================================================================");

            System.out.printf("Viewing: %s | Rows: %d-%d of %d | Cols: %d-%d of %d\n", 
                            title,
                            showRow + 1, rowEnd, totalRows,
                            showCol + 1, colEnd, totalCols
                            );

            
            System.out.println("==============================================================================");
            System.out.println();

            System.out.print(YELLOW_TEXT);

            // Print the header row for visible columns
            for (int c = showCol; c < colEnd; c++) {

                String head = headers.get(c);

                if (head.length() > COL_WIDTH - 2 ) {

                    head = head.substring(0, COL_WIDTH - 4) + "...";
                }

                System.out.printf("%-" + COL_WIDTH + "s", head); //TEST
            }

            System.out.print(RESET_TEXT);

            System.out.println();
            System.out.println("-".repeat((colEnd - showCol) * COL_WIDTH));

            // Print rows
            for (int r = showRow; r < rowEnd; r++) {

                List<String> row = rows.get(r);

                for (int c = showCol; c < colEnd; c++) {

                    String tok = row.get(c);

                    if (tok.length() > COL_WIDTH - 2 ) {

                        tok = tok.substring(0, COL_WIDTH - 4) + "...";
                    }

                    System.out.printf("%-" + COL_WIDTH + "s", tok); 
                }

                System.out.println();

            }

            System.out.println();
            System.out.println("==============================================================================");

            if (!status.isEmpty()) {

                System.out.println(RED_TEXT + "Status: " + status + RESET_TEXT);
                status = "";
            }

            System.out.println("Controls: W = Up    S = Down    A = Left    D = Right   " + RED_TEXT + "Q = Quit" + RESET_TEXT);

            System.out.println();

            String userChoice = InputReader.getString(GREEN_TEXT + "Enter choice " + RESET_TEXT + "(W/S/A/D/Q):");

            if (userChoice == null || userChoice.isEmpty()) {

                continue;
            }

            String ch = userChoice.toLowerCase();
            
            switch (ch) {


                case "w" -> {

                    if(showRow == 0) {

                        status = "Already at the top row.";
                    }

                    else if (showRow > 0) {

                        showRow = Math.max(0, showRow - rowsPerPage);
                    }
                }
                    
                case "a" -> {

                    if (showCol == 0) {

                        status = "Already at the leftmost column.";

                    }
                    else if (showCol > 0) {

                        showCol = Math.max(0, showCol - colsPerPage);
                    }
                }

                case "s" -> {

                    if (rowEnd == totalRows) {

                        status = "Already at the bottom row.";
                    }

                    else if (showRow + rowsPerPage < totalRows) {

                        showRow = showRow + rowsPerPage;

                        if (showRow >= totalRows) {
                            showRow = Math.max(0, totalRows - rowsPerPage);
                        }
                    }
                }

                case "d" -> {

                    if(colEnd == totalCols) {

                        status = "Already at rightmost column";
                    }

                    else if (showCol + colsPerPage < totalCols) {

                        showCol = showCol + colsPerPage;

                        if (showCol >= totalCols) {
                            showCol = Math.max(0, totalCols - colsPerPage);
                        }
                    }
                }

                case "q" -> viewing = false;
            
                default -> {

                    // do nothing
                    status = "Invalid input. Use WSAD or Q";
                }
                   
            }
            

        }




    }


    public static void twoColumns(List<String> items1, List<String> items2) {

        int columns = 2;
        int size = items1.size();
        int columnSpace = 50;

        for (int i = 0; i < size; i += columns) {

            String leftText = "";
            String leftJustify = "";

            if(items2 != null) {

                leftText = format(items1.get(i), items2.get(i), columnSpace);
                leftJustify = String.format(GREEN_TEXT + "%-3d%-" + columnSpace + "s", i+1, RESET_TEXT + leftText);
            } else {

                leftText = format(items1.get(i), null, COL_WIDTH);
                leftJustify = String.format(GREEN_TEXT + "%-3d%-" + COL_WIDTH + "s", i+1, RESET_TEXT + leftText);

            }

            // String leftJustify = String.format(GREEN_TEXT + "%-3d%-" + columnSpace + "s", i+1, RESET_TEXT + leftText);

            String rightJustify = "";

            if (i + 1 < size) {

            String rightText = "";

            if(items2 != null) {

                rightText = format(items1.get(i+1), items2.get(i+1), columnSpace);
                rightJustify = String.format(GREEN_TEXT + "%-3d%-" + columnSpace +"s", i+2, RESET_TEXT + rightText);

            } else {

                rightText = format(items1.get(i+1), null, COL_WIDTH);
                rightJustify = String.format(GREEN_TEXT + "%-3d%-" + COL_WIDTH +"s", i+2, RESET_TEXT + rightText);
            }
            
                // rightJustify = String.format(GREEN_TEXT + "%-3d%-" + columnSpace +"s", i+2, RESET_TEXT + rightText);
            }

            System.out.println(leftJustify + "\t" + rightJustify);

        }

    }


    private static String format(String str1, String str2, int col) {

        String text = str1;

        if(str2 != null) {

            text += " (" + str2 + ")";
        }

        if (text.length() > col - 4) {

            text = text.substring(0, col - 7) + "...";
            
        }

        return text;
    }

}
