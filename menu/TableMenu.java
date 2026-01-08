package menu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import utils.ClearConsole;
import utils.InputReader;
import utils.ResultSetPrinter;

public class TableMenu {
    private final String RED_TEXT = "\u001B[31m";
    private final String GREEN_TEXT = "\u001B[32m";
    private final String RESET_TEXT = "\u001B[0m";

    private boolean invalidInput = false;

    public void display() {
        boolean sessionRunning = true;

        while (sessionRunning) {
            ClearConsole.clear();

            invalidInput = false;

            printMenu();

            System.out.println();

            String userChoice;
           
            userChoice = InputReader.getString("Enter a table number: ");

            if (userChoice.matches(("\\d+"))) {
                int tableNum;

                try {
                    tableNum = Integer.parseInt(userChoice);
                } catch (NumberFormatException nfe) {

                    invalidInput = true;
                    continue;
                }

                switch (tableNum) {
                    case 0 -> {
                        ClearConsole.clear();
                        sessionRunning = false;
                    }
                    case 1 -> showTable("player");
                    case 2 -> showTable("team");
                    case 3 -> showTable("game");
                    case 4 -> showTable("referee");
                    case 5 -> showTable("arena");
                    case 6 -> showTable("coach");
                    case 7 -> showTable("arena_facilities");
                    case 8 -> showTable("manages");
                    case 9 -> showTable("play");
                    case 10 -> showTable("part_of");
                    case 11 -> showTable("compete_in");
                    case 12 -> showTable("officiated");
                    case 13 -> showTable("player_positions");
                    default -> {

                        invalidInput = true;
                    }
                }
            } else {

                invalidInput = true;
            }             
            if (invalidInput) {

                InputReader.getString(RED_TEXT + "Invalid option. " + GREEN_TEXT + "Enter" + RESET_TEXT + " to try again. ");
            }
        }
    }

    private void showTable(String tableName) {
        try {
            Connection conn = db.DBConnection.getConnection();
            String sql = "SELECT * FROM " + tableName;

            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

                
                ResultSetPrinter.printResult(rs, tableName);
        
            }
        } catch (SQLException e) {
            System.out.println(RED_TEXT + "Error getting table " + tableName + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("=======================================");
        System.out.println("               DB TABLES               ");
        System.out.println("=======================================");
        System.out.println(RED_TEXT + "0." + RESET_TEXT + " Return to Main Menu");
        System.out.println(GREEN_TEXT + "1." + RESET_TEXT + " Players");
        System.out.println(GREEN_TEXT + "2." + RESET_TEXT + " Team");
        System.out.println(GREEN_TEXT + "3." + RESET_TEXT + " Games");
        System.out.println(GREEN_TEXT + "4." + RESET_TEXT + " Referee");
        System.out.println(GREEN_TEXT + "5." + RESET_TEXT + " Arena");
        System.out.println(GREEN_TEXT + "6." + RESET_TEXT + " Coach");
        System.out.println(GREEN_TEXT + "7." + RESET_TEXT + " Arena Facilities");
        System.out.println(GREEN_TEXT + "8." + RESET_TEXT + " Manages");
        System.out.println(GREEN_TEXT + "9." + RESET_TEXT + " Play");
        System.out.println(GREEN_TEXT + "10." + RESET_TEXT + " PartOf");
        System.out.println(GREEN_TEXT + "11." + RESET_TEXT + " CompeteIn");
        System.out.println(GREEN_TEXT + "12." + RESET_TEXT + " Officiated");
        System.out.println(GREEN_TEXT + "13." + RESET_TEXT + " Player Positions");
        System.out.println("=======================================");
    }
}