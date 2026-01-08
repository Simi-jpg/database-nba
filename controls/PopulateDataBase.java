package controls;

import db.DBConnection;
import utils.CSVReader;
import utils.ClearConsole;

import java.sql.*;
import java.util.List;

public class PopulateDataBase {
    private final String GREEN_TEXT = "\u001B[32m";
    private final String RESET_TEXT = "\u001B[0m";

    public void repopulate() {

        ClearConsole.clear();

        ResetDatabase.deleteData();
        ClearConsole.clear();


        System.out.println("Repopulating the database...");

        Connection conn = DBConnection.getConnection();

        try {

            conn.setAutoCommit(false);

            insertArena(conn);
            insertTeam(conn);
            insertCoach(conn);
            insertReferee(conn);
            insertPlayers(conn);
            insertGames(conn);
            insertFacilities(conn);
            insertPlayerPosition(conn);
            insertCompeteIn(conn);
            insertManages(conn);
            insertOfficiated(conn);
            insertPartOf(conn);
            insertPlay(conn);

            conn.commit();
            
            ClearConsole.clear();
            System.out.println(GREEN_TEXT + "[DB] All tables have been restored successfully. \n" + RESET_TEXT);

        } catch (Exception e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table.");

            try {

                conn.rollback();
                System.out.println("Successfully rolled back");

            } catch (SQLException err) {

                System.out.println(err.getMessage());
            }
        }
    }

    private void insertPlayerPosition(Connection conn) {
        System.out.println("Repopulating 'Player Positions' table...");

        String sql = "INSERT INTO player_positions (player_id, position) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/player_positions.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Player Positions' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table PlayerPosition.");

        }

    }

    private void insertManages(Connection conn) {
        System.out.println("Repopulating 'Manages' table...");

        String sql = "INSERT INTO manages (team_id, coach_id, start_year, end_year) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/manages.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setInt(2, Integer.parseInt(r[1]));
                ps.setInt(3, Integer.parseInt(r[2]));

                if (!(r[3].equalsIgnoreCase("NULL"))) {
                    ps.setInt(4, Integer.parseInt(r[3]));
                } else {
                    ps.setNull(4, Types.INTEGER);
                }

                ps.addBatch();
            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Manages' table!" + RESET_TEXT);

        } catch (Exception e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table Manages.");

        }
    }

    private void insertOfficiated(Connection conn) {
        System.out.println("Repopulating 'Officiated' table...");

        String sql = "INSERT INTO officiated (game_id, referee_id) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/officiated.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setInt(2, Integer.parseInt(r[1]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Officiated' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table Officiated.");

        }

    }

    private void insertCompeteIn(Connection conn) {
        System.out.println("Repopulating 'Compete In' table...");

        String sql = """
                INSERT INTO compete_in (game_id, season_type, home_team_id, home_pts, home_fgm,
                            home_ftm, away_team_id, away_pts, away_fgm, away_ftm)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

                        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/compete_in.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.setInt(3, Integer.parseInt(r[2]));
                ps.setInt(4, Integer.parseInt(r[3]));
                ps.setInt(5, Integer.parseInt(r[4]));
                ps.setInt(6, Integer.parseInt(r[5]));
                ps.setInt(7, Integer.parseInt(r[6]));
                ps.setInt(8, Integer.parseInt(r[7]));
                ps.setInt(9, Integer.parseInt(r[8]));
                ps.setInt(10, Integer.parseInt(r[9]));

                ps.addBatch();
            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Compete In' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table CompeteIn.");

        }

    }

    private void insertPlay(Connection conn) {
        System.out.println("Repopulating 'Play' table...");

        String sql = "INSERT INTO play (game_id, player_id) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/play.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setInt(2, Integer.parseInt(r[1]));

                ps.addBatch();
            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Play' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table Play.");

        }
    }

    private void insertPartOf(Connection conn) {
        System.out.println("Repopulating 'Part Of' table...");

        String sql = "INSERT INTO part_of (player_id, team_id, start_year, end_year) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/part_of.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setInt(2, Integer.parseInt(r[1]));
                ps.setInt(3, Integer.parseInt(r[2]));
                ps.setInt(4, Integer.parseInt(r[3]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Part Of' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table PartOf.");

        }

    }

    private void insertReferee(Connection conn) {
        System.out.println("Repopulating 'Referee' table...");

        String sql = "INSERT INTO referee (referee_id, first_name, last_name, jersey_number) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/referee.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.setString(3, r[2]);
                ps.setInt(4, Integer.parseInt(r[3]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Referee' table!" + RESET_TEXT);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("[DB] error while populating table Referee.");

        }

    }

    private void insertCoach(Connection conn) {
        System.out.println("Repopulating 'Coach' table...");

        String sql = "INSERT INTO coach (coach_id, first_name, last_name, years_of_experience) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/coach.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.setString(3, r[2]);
                ps.setInt(4, Integer.parseInt(r[3]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Coach' table!" + RESET_TEXT);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("[DB] error while populating table Coach.");

        }

    }

    private void insertGames(Connection conn) {
        System.out.println("Repopulating 'Game' table...");

        String sql = "INSERT INTO game (game_id, game_date, season_type, arena_id) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/game.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setDate(2, Date.valueOf(r[1]));
                ps.setString(3, r[2]);
                ps.setInt(4, Integer.parseInt(r[3]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Game' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table Games.");

        }

    }

    private void insertPlayers(Connection conn) {
        System.out.println("Repopulating 'Player' table...");

        String sql = """
                INSERT INTO player (player_id, first_name, last_name, birthdate, school,
                    country, height, weight, season_experience, jersey_number, roster_status,
                    start_year, end_year, draft_year)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

                  """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/player.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.setString(3, r[2]);
                ps.setDate(4, Date.valueOf(r[3]));
                ps.setString(5, r[4]);
                ps.setString(6, r[5]);
                ps.setString(7, (r[6]));
                ps.setInt(8, Integer.parseInt(r[7]));
                ps.setInt(9, Integer.parseInt(r[8]));
                ps.setInt(10, Integer.parseInt(r[9]));
                ps.setString(11, r[10]);
                ps.setInt(12, Integer.parseInt(r[11]));
                ps.setInt(13, Integer.parseInt(r[12]));
                ps.setInt(14, Integer.parseInt(r[13]));

                ps.addBatch();
            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Player' table!" + RESET_TEXT);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            System.out.println("[DB] error while populating table Player.");

        }

    }

    private void insertTeam(Connection conn) {
        System.out.println("Repopulating 'Team' table...");

        String sql = "INSERT INTO team (team_id, arena_id, team_name, team_city, team_state, year_founded) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/team.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setInt(2, Integer.parseInt(r[1]));
                ps.setString(3, r[2]);
                ps.setString(4, r[3]);
                ps.setString(5, r[4]);
                ps.setInt(6, Integer.parseInt(r[5]));

                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Team' table!" + RESET_TEXT);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("[DB] error while populating table Team.");

        }

    }

    private void insertFacilities(Connection conn) {
        System.out.println("Repopulating 'Arena Facilities' table...");

        String sql = "INSERT INTO arena_facilities (arena_id, facilities) " +
                "VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/arena_facilities.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Arena Facilities' table!" + RESET_TEXT);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("[DB] error while populating table Facilities.");

        }

    }

    private void insertArena(Connection conn) {
        System.out.println("Repopulating 'Arena' table...");

        String sql = "INSERT INTO arena (arena_id, arena_name, capacity, location, owner) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            List<String[]> row = CSVReader.read("data/csv/arena.csv");

            for (String[] r : row) {

                ps.setInt(1, Integer.parseInt(r[0]));
                ps.setString(2, r[1]);
                ps.setInt(3, Integer.parseInt(r[2]));
                ps.setString(4, r[3]);
                ps.setString(5, r[4]);
                ps.addBatch();

            }

            ps.executeBatch();

            System.out.println(GREEN_TEXT + "Successfully repopulated 'Arena' table!" + RESET_TEXT);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("[DB] error while populating table Arena.");

        }

    }
}
