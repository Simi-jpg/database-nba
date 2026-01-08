package controls;

import db.DBConnection;
import utils.ClearConsole;
import utils.InputReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ResetDatabase {
    private static final String RED_TEXT = "\u001B[31m";
    private static final String GREEN_TEXT = "\u001B[32m";
    private static final String RESET_TEXT = "\u001B[0m";

    public static void confirm() {

        ClearConsole.clear();

        System.out.println("Type 'YES' or 'Y' to confirm that you want to DELETE ALL data?");

        String userChoice = InputReader.getString("");

        if (!userChoice.equalsIgnoreCase("YES") && !userChoice.equalsIgnoreCase("Y")) {
            System.out.println(RED_TEXT + "\nDelete cancelled. \n" + RESET_TEXT);

            userChoice = InputReader.getString("Press " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");

            return;
        }

        deleteData();

        System.out.println("[DB] All tables deleted successfully. \n");

        userChoice = InputReader.getString("Press " + GREEN_TEXT + "ENTER" + RESET_TEXT + " to return to Main Menu.");
    }

    public static void deleteData() {
        Connection conn = DBConnection.getConnection();

        try {

            Statement stmt = conn.createStatement();

            conn.setAutoCommit(false);

            stmt.executeUpdate("DELETE FROM manages");
            stmt.executeUpdate("DELETE FROM officiated");
            stmt.executeUpdate("DELETE FROM compete_in");
            stmt.executeUpdate("DELETE FROM play");
            stmt.executeUpdate("DELETE FROM part_of");
            stmt.executeUpdate("DELETE FROM player_positions");
            stmt.executeUpdate("DELETE FROM arena_facilities");
            stmt.executeUpdate("DELETE FROM coach");
            stmt.executeUpdate("DELETE FROM game");
            stmt.executeUpdate("DELETE FROM referee");
            stmt.executeUpdate("DELETE FROM team");
            stmt.executeUpdate("DELETE FROM arena");
            stmt.executeUpdate("DELETE FROM player");

            conn.commit();

            ClearConsole.clear();

        } catch (Exception e) {

            ClearConsole.clear();
            System.out.println("[DB] Error while deleting data");

            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }

            System.out.println(e.getMessage());

        }
    }

}
