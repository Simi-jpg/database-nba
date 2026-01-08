package utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;

public class Queries {

    private final static String RED_TEXT = "\u001B[31m";
    private final static String GREEN_TEXT = "\u001B[32m";
    private final static String RESET_TEXT = "\u001B[0m";

    public static void queryOne() {

        ClearConsole.clear();


        int year;

        while(true) {

            String yearInput = InputReader.getString("Enter season year (e.g., 2010):");

            try {
                year = Integer.parseInt(yearInput);
                break;
                
            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. " + GREEN_TEXT + "Enter " + RESET_TEXT + "to try again");
                continue;
            }
        }

        String sql = """
                SELECT TOP 10 team.team_name,
                    SUM (
                        CASE
                            WHEN compete_in.home_team_id = team.team_id THEN compete_in.home_pts
                            WHEN compete_in.away_team_id = team.team_id THEN compete_in.away_pts
                        END
                    ) AS totalPoints
                    FROM team
                    INNER JOIN compete_in ON team.team_id IN (compete_in.home_team_id, compete_in.away_team_id)
                    INNER JOIN game ON compete_in.game_id = game.game_id
                    WHERE YEAR(game.game_date) = ?
                    GROUP BY team.team_id, team.team_name
                    ORDER BY totalPoints DESC;
                """;

        try {

            Connection conn = DBConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, year);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Top 10 teams by Points: (" + year + ")";

                    ResultSetPrinter.printResult(rs, title);

                }
            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 1" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }



    public static void queryTwo() {

        ClearConsole.clear();

        int startYear, endYear;

        while(true) {

            String start = InputReader.getString("Enter start year (e.g., 2002): ");
            String end = InputReader.getString("Enter end year (e.g., 2009): ");


            try {

                startYear = Integer.parseInt(start);
                endYear = Integer.parseInt(end);
                break;

            } catch (NumberFormatException nfe) {

                System.out.println(RED_TEXT + "Invalid input(s)\n" + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                SELECT TOP 10 team.team_name, AVG(totalPoints) AS averagePoints
                FROM (
                    SELECT team.team_id, C.game_id,
                    CASE
                        WHEN C.home_team_id = team.team_id THEN C.home_pts
                        WHEN C.away_team_id = team.team_id THEN C.away_pts
                    END AS totalPoints,
                    YEAR(game.game_date) AS year
                    FROM team
                    INNER JOIN compete_in C ON team.team_id IN (C.home_team_id, C.away_team_id)
                    INNER JOIN game ON C.game_id = game.game_id
                    WHERE YEAR(game.game_date) BETWEEN ? AND ?
                ) AS season
                JOIN team ON team.team_id = season.team_id
                GROUP BY team.team_name
                ORDER BY averagePoints DESC;
                """;

        try {

            Connection conn = DBConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, startYear);
                ps.setInt(2, endYear);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Top 10 teams by scoring average from " + startYear + " to " + endYear;

                    ResultSetPrinter.printResult(rs, title);
                }
            }
        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query2" + RESET_TEXT);
            System.out.println(e.getMessage());
        }

    }

    public static void queryThree() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        TeamSelection teamSel = selectTeam(conn);

        if (teamSel == null) {
            return;
        }

        int selectedTeamId = teamSel.teamId;
        String selectedTeamName = teamSel.teamName;

        ClearConsole.clear();

        try {
            // Load all positions
            List<String> positions = new ArrayList<>();

            String positionSql = "SELECT DISTINCT position FROM player_positions ORDER BY position";

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(positionSql)) {
                while ((rs.next())) {
                    positions.add(rs.getString("position"));
                }
            }

            if (positions.isEmpty()) {

                System.out.println(RED_TEXT + "No positions available in the database" + RESET_TEXT);
                return;
            }

            // Display positions

            String selectedPosition = null;

            while (selectedPosition == null) {

                for (int i = 0; i < positions.size(); i++) {

                    System.out.println(i + 1 + ". " + positions.get(i));
                }

                String userChoice = InputReader.getString("Choose a position (Q to quit):");

                if (userChoice.equalsIgnoreCase("q")) {

                    System.out.println("Query cancelled.");
                    return;
                }

                if (!userChoice.matches("\\d+")) {

                    InputReader.getString(RED_TEXT + "Invalid input. " + GREEN_TEXT + "Enter" + RESET_TEXT + " to try again");
                    continue;
                }

                int option = Integer.parseInt(userChoice);

                if (option < 1 || option > positions.size()) {

                    System.out.println(RED_TEXT + "Invalid option. Try again.\n" + RESET_TEXT);
                    continue;
                }

                selectedPosition = positions.get(option - 1);
            }

            // Run query using user's options

            String sql = """
                    SELECT team.team_name, player_positions.position, COUNT(player.player_id) AS num_players
                    FROM player
                    INNER JOIN player_positions ON player.player_id = player_positions.player_id
                    INNER JOIN part_of ON player.player_id = part_of.player_id
                    INNER JOIN team ON part_of.team_id = team.team_id
                    WHERE team.team_id = ? AND player_positions.position = ?
                    GROUP BY player_positions.position, team.team_name;
                    """;

            int num_players = 0;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, selectedTeamId);
                ps.setString(2, selectedPosition);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        num_players = rs.getInt("num_players");
                    }
                }
            }

            ClearConsole.clear();

            // print result

            System.out.println("Team: " + selectedTeamName);
            System.out.println("Position: " + selectedPosition);
            System.out.println("Number of players in this position: " + num_players);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 3" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryFour() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        TeamSelection teamSel = selectTeam(conn);

        if (teamSel == null) {
            return;
        }

        ClearConsole.clear();

        ArenaSelection arenaSel = selectArena(conn);

        if (arenaSel == null) {
            return;
        }

        int selectedTeamId = teamSel.teamId;
        String selectedTeamName = teamSel.teamName;

        int selectedArenaId = arenaSel.arenaId;
        String selectedArenaName = arenaSel.arenaName;

        // Run query using user's options

        String sql = """
                SELECT team.team_name, COUNT(game.game_id) AS num_wins
                FROM game
                JOIN compete_in ON game.game_id = compete_in.game_id
                JOIN team ON team.team_id = ?
                WHERE game.arena_id = ?
                    AND (
                        (compete_in.home_team_id = team.team_id AND compete_in.home_pts > compete_in.away_pts)
                        OR
                        (compete_in.away_team_id = team.team_id AND compete_in.away_pts > compete_in.home_pts)
                        )
                GROUP BY team.team_id, team.team_name;
                """;

        int numWins = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedTeamId);
            ps.setInt(2, selectedArenaId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    numWins = rs.getInt("num_wins");
                }
            }

            ClearConsole.clear();

            System.out.println("Team: " + selectedTeamName);
            System.out.println("Arena:" + selectedArenaName);
            System.out.println("Games won in this arena: " + numWins);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 4" + RESET_TEXT);
            System.out.println(e.getMessage());
        }

    }

    public static void queryFive() {

        String sql = """
                SELECT TOP 5 referee.referee_id, referee.first_name, referee.last_name, COUNT (officiated.referee_id) AS matches_officiated
                FROM referee
                INNER JOIN officiated ON referee.referee_id = officiated.referee_id
                GROUP BY referee.referee_id, referee.first_name, referee.last_name
                ORDER BY matches_officiated DESC;
                """;

        try {
            Connection conn = DBConnection.getConnection();
            ClearConsole.clear();

            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
                String title = "Top 5 referees by number of matches officiated";
                ResultSetPrinter.printResult(rs, title);

            }
        } catch (SQLException e) {
            System.out.println(RED_TEXT + "[DB] Error running Query 5" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void querySix() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        TeamSelection teamSel = selectTeam(conn);

        if (teamSel == null) {
            return;
        }

        int selectedTeamId = teamSel.teamId;
        String selectedTeamName = teamSel.teamName;

        String sql = """
                SELECT coach.coach_id, coach.first_name, coach.last_name
                FROM coach
                JOIN manages ON coach.coach_id = manages.coach_id
                WHERE manages.team_id = ? AND manages.end_year IS NULL;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedTeamId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("Team:" + selectedTeamName);
                    System.out.println("No current coach for this team");

                    return;
                }

                ClearConsole.clear();

                String title = "Current coach(es) for " + selectedTeamName + ": ";
                ResultSetPrinter.printResult(rs, title);
            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 6" + RESET_TEXT);
            System.out.println(e.getMessage());
        }

    }

    public static void querySeven() {
        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        TeamSelection teamSel = selectTeam(conn);

        if (teamSel == null) {
            return;
        }

        int selectedTeamId = teamSel.teamId;
        String selectedTeamName = teamSel.teamName;

        String sql = """
                SELECT
                    ROUND(
                        AVG(
                            (
                                TRY_CAST(LEFT(player.height, CHARINDEX('-', player.height) - 1) AS INT) * 12 +
                                TRY_CAST(SUBSTRING(player.height, CHARINDEX('-', player.height) + 1, 2) AS INT)
                            ) * 1.0
                        ), 2) AS avgHeight,

                    ROUND(AVG(player.weight), 2) AS avgWeight
                FROM part_of
                INNER JOIN player ON part_of.player_id = player.player_id
                WHERE part_of.team_id = ?
                GROUP BY part_of.team_id;
                        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedTeamId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    double avgHeight = rs.getDouble("avgHeight");
                    double avgWeight = rs.getDouble("avgWeight");

                    ClearConsole.clear();

                    System.out.println("Team: " + selectedTeamName);
                    System.out.println("Average Height: " + avgHeight);
                    System.out.println("Average Weight: " + avgWeight);
                }

            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 7" + RESET_TEXT);
            System.out.println(e.getMessage());

        }

    }

    public static void queryEight() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();


        int year;

        while(true) {

            String yearStr = InputReader.getString("Enter season year (e.g., 2020): ");

            try {
                year = Integer.parseInt(yearStr);
                break;
                
            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. " + GREEN_TEXT + "Enter " + RESET_TEXT + "to try again");
                continue;
            }
        }

        String sql = """
                WITH topTeams AS (
                    SELECT TOP 10 team.team_id, team.team_name,
                        SUM (
                            CASE
                                WHEN c.home_team_id = team.team_id THEN c.home_pts
                                WHEN c.away_team_id = team.team_id THEN c.away_pts
                                ELSE 0
                            END
                        ) AS total_points
                    FROM team
                    JOIN compete_in c
                        ON team.team_id = c.home_team_id
                        OR team.team_id = c.away_team_id
                    JOIN game ON c.game_id = game.game_id
                    WHERE YEAR(game.game_date) = ?
                    GROUP BY team.team_id, team.team_name
                    ORDER BY total_points DESC

                )


                SELECT
                    player.country,
                    COUNT(DISTINCT player.player_id) AS numPlayers
                FROM player
                JOIN part_of ON player.player_id = part_of.player_id
                WHERE part_of.team_id IN (SELECT team_id FROM topTeams)
                GROUP BY player.country
                ORDER BY numPlayers DESC;
                """;

        try {

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, year);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Countries with Most Players in Top 10 Teams (" + year + ")";
                    ResultSetPrinter.printResult(rs, title);
                }
            }
        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 8" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryNine() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();


        int n;

        while (true) {

            String num = InputReader.getString("Enter minimum number of teams: ");
            
            try {

                n = Integer.parseInt(num);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid entry. " + GREEN_TEXT + "Enter " + RESET_TEXT + "to try again\n");
                continue;
            }
        }
        
        if (n < 1) {

            System.out.println("Number should be at least 1.\n");
            return;
        }


        String sql = """
                SELECT player.player_id, player.first_name, player.last_name,
                    COUNT(DISTINCT part_of.team_id) AS numTeams
                FROM player
                JOIN part_of ON player.player_id = part_of.player_id
                GROUP BY player.player_id, player.first_name, player.last_name
                HAVING COUNT(DISTINCT part_of.team_id) >= ?
                ORDER BY numTeams DESC, player.last_name, player.first_name;

                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, n);

            try (ResultSet rs = ps.executeQuery()) {

                String title = "Players with more than " + n + " teams:";
                ResultSetPrinter.printResult(rs, title);
            }
        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 9" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryTen() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """

                WITH youngest AS (
                    SELECT part_of.team_id, MAX(player.birthdate) AS birthdate
                    FROM player
                    JOIN part_of ON player.player_id = part_of.player_id
                    GROUP BY part_of.team_id
                )

                SELECT team.team_name, player.player_id, player.first_name, player.last_name, player.birthdate
                FROM player
                JOIN part_of ON player.player_id = part_of.player_id
                JOIN team ON part_of.team_id = team.team_id
                JOIN youngest ON youngest.team_id = part_of.team_id
                    AND youngest.birthdate = player.birthdate

                ORDER BY team.team_name, player.birthdate DESC, player.last_name, player.first_name;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()) {

            String title = "Youngest Players on Each Team";
            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 10" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryEleven() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                WITH stats AS (
                    SELECT team.team_id, team.team_name, YEAR(game.game_date) AS season,
                    SUM (
                        CASE
                            WHEN (C.home_team_id = team.team_id AND C.home_pts < C.away_pts)
                            OR (C.away_team_id = team.team_id AND C.away_pts < C.home_pts)
                            THEN 1 ELSE 0
                        END
                    ) AS losses,

                    SUM (
                        CASE
                            WHEN (C.home_team_id = team.team_id AND C.home_pts > C.away_pts)
                            OR (C.away_team_id = team.team_id AND C.away_pts > C.home_pts)
                            THEN 1 ELSE 0
                        END
                    ) AS wins

                    FROM team
                    JOIN compete_in C ON team.team_id = C.home_team_id OR team.team_id = C.away_team_id
                    JOIN game ON game.game_id = C.game_id
                    GROUP BY team.team_id, team.team_name, YEAR(game.game_date)
                )

                SELECT season, team_name, wins, losses, (losses - wins) AS margin
                FROM stats
                WHERE losses > wins
                ORDER BY season, margin DESC, team_name;

                """;

        try (Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery(sql)) {

            String title = "Teams with more losses than wins per season";
            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {
            System.out.println(RED_TEXT + "[DB] Error running Query 11" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryTwelve() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                SELECT player.player_id, player.first_name, player.last_name
                FROM player
                JOIN coach
                ON LOWER(player.first_name) = LOWER (coach.first_name)
                AND LOWER(player.Last_name) = LOWER(coach.last_name)
                ORDER BY player.first_name, player.last_name
                """;

        try (Statement s = conn.createStatement();

                ResultSet rs = s.executeQuery(sql)) {

            String title = "Players who Became Coaches";

            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error Running Query 12" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryThirteen() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                SELECT TOP 1 arena.arena_id, arena.arena_name, COUNT (game.game_id) AS numberOfGames
                FROM arena
                JOIN game ON arena.arena_id = game.arena_id
                WHERE game.season_type = 'Playoffs'
                GROUP BY arena.arena_id, arena.arena_name
                ORDER BY numberOfGames DESC;
                    """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                String arenaName = rs.getString("arena_name");
                int arenaId = rs.getInt("arena_id");
                int numGames = rs.getInt("numberOfGames");

                ClearConsole.clear();

                System.out.println("Arena with most Playoff Games:");
                System.out.println("Arena Name: " + arenaName + "(ID: " + arenaId + ")");
                System.out.println("Games won: " + numGames);

            } else {

                System.out.println("No playoff games found in the database.");
            }

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 13" + RESET_TEXT);
            System.out.println(e.getMessage());
        }

    }

    public static void queryFourteen() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        PlayerSelection playSel = selectPlayer(conn);

        if (playSel == null) {
            return;
        }

        int selectedPlayerId = playSel.playerId;
        String selectedPlayerName = playSel.playerName;

        ClearConsole.clear();

        ArenaSelection arenaSel = selectArena(conn);

        if (arenaSel == null) {
            return;
        }

        int selectedArenaId = arenaSel.arenaId;
        String selectedArenaName = arenaSel.arenaName;

        ClearConsole.clear();


        int year;

        while(true) {

            String yearStr = InputReader.getString("Enter season year (e.g., 2019): ").trim();

            try {


                year = Integer.parseInt(yearStr);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. Enter to retry" + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                SELECT COUNT(DISTINCT game.game_id) AS winsNum
                FROM play
                JOIN game ON play.game_id = game.game_id
                JOIN compete_in ON compete_in.game_id = game.game_id
                JOIN part_of ON play.player_id = part_of.player_id
                AND YEAR(game.game_date)
                    BETWEEN part_of.start_year AND COALESCE (part_of.end_year, YEAR(game.game_date))

                WHERE play.player_id = ? AND game.arena_id = ? AND YEAR(game.game_date) = ?
                    AND (
                        (compete_in.home_team_id = part_of.team_id AND compete_in.home_pts >
                            compete_in.away_pts)
                        OR (compete_in.away_team_id = part_of.team_id AND compete_in.away_pts >
                            compete_in.home_pts)
                    );
                """;

        int wins = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedPlayerId);
            ps.setInt(2, selectedArenaId);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    wins = rs.getInt("winsNum");
                }
            }

            ClearConsole.clear();

            System.out.println("Player: " + selectedPlayerName + " (ID " + selectedPlayerId + ")");
            System.out.println("Arena: " + selectedArenaName + " (ID " + selectedArenaId + ")");
            System.out.println("Year: " + year);
            System.out.println("Games won in this arena and year: " + wins);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 14" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryFifteen() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        TeamSelection teamSel = selectTeam(conn);

        if (teamSel == null) {

            return;
        }

        int teamId = teamSel.teamId;
        String teamName = teamSel.teamName;

        String sql = """
                SELECT TOP 1 coach.coach_id, coach.first_name, coach.last_name,
                    SUM (
                        CASE
                            WHEN (compete_in.home_team_id = team.team_id AND compete_in.home_pts < compete_in.away_pts)
                                OR (compete_in.away_team_id = team.team_id AND compete_in.away_pts < compete_in.home_pts)
                            THEN 1
                            ELSE 0
                        END
                        ) AS losses
                FROM coach
                INNER JOIN manages ON coach.coach_id = manages.coach_id
                INNER JOIN team ON manages.team_id = team.team_id
                INNER JOIN compete_in ON team.team_id IN (compete_in.home_team_id, compete_in.away_team_id)
                INNER JOIN game ON compete_in.game_id = game.game_id
                WHERE team.team_id = ?
                    AND YEAR(game.game_date)
                        BETWEEN manages.start_year AND COALESCE (manages.end_year, YEAR(game.game_date))
                GROUP BY coach.coach_id, coach.first_name, coach.last_name
                ORDER BY losses DESC;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {

                    System.out.println("No coaching loss data found for team: " + teamName);
                    return;
                }

                int coachId = rs.getInt("coach_id");
                String first = rs.getString("first_name");
                String last = rs.getString("last_name");
                int losses = rs.getInt("losses");

                ClearConsole.clear();

                System.out.println("Team: " + teamName + " (ID " + teamId + ")");
                System.out.println("Coach: " + first + " " + last + " (ID " + coachId + ")");
                System.out.println("Losses: " + losses + " (while coaching this team)");

            }

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 15" + RESET_TEXT);
            System.err.println(e.getMessage());
        }
    }

    public static void querySixteen() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        int currYear = 0;

        while(true) {

            String yearStr = InputReader.getString("Enter the season year to compare (e.g., 2020): ");

            try {
                currYear = Integer.parseInt(yearStr);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. Enter to retry." + RESET_TEXT);
                continue;
            }
        }

        int prevYear = currYear - 1;

        String sql = """
                WITH teamPoints AS (
                    SELECT team.team_id, team.team_name, YEAR(game.game_date) AS year,
                        SUM (
                            CASE
                                WHEN team.team_id = compete_in.home_team_id
                                    THEN compete_in.home_pts
                                ELSE compete_in.away_pts
                            END
                        ) AS totalPoints

                    FROM team
                    JOIN compete_in ON team.team_id IN (compete_in.home_team_id, compete_in.away_team_id)
                    JOIN game ON compete_in.game_id = game.game_id
                    GROUP BY team.team_id, team.team_name, YEAR(game.game_date)
                )

                SELECT TOP 1 curr.team_name, (curr.totalPoints - prev.totalPoints) AS pointDiff, curr.year
                FROM teamPoints prev
                JOIN teamPoints curr ON prev.team_id = curr.team_id
                WHERE prev.year = ? AND curr.year = ?
                    AND (curr.totalPoints - prev.totalPoints) > 0
                ORDER BY pointDiff DESC;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prevYear);
            ps.setInt(2, currYear);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    ClearConsole.clear();

                    System.out.println("No data found for season " + currYear + " or its previous year.");
                    return;
                }

                String teamName = rs.getString("team_name");
                int currentSeason = rs.getInt("year");
                int diff = rs.getInt("pointDiff");

                ClearConsole.clear();

                System.out.println("Season compared: " + currentSeason + " vs " + (currentSeason - 1));
                System.out.println("Team with biggest improvement: " + teamName);
                System.out.println("Point Difference: " + diff);
                System.out.println("Season Year: " + currentSeason);

            }

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 16" + RESET_TEXT);
            System.err.println(e.getMessage());
        }
    }

    public static void querySeventeen() {

        Connection conn = DBConnection.getConnection();

        ClearConsole.clear();

        String yearStr = InputReader.getString("Enter season year (e.g., 2015): ").trim();
        int year;

        while(true) {

            try {

                year = Integer.parseInt(yearStr);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. Enter to try again." + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                WITH opponents AS (
                    SELECT YEAR(g.game_date) AS year,
                    c.home_team_id AS teamID, c.away_team_id AS opponentID
                    FROM compete_in c
                    JOIN game g ON c.game_id = g.game_id

                UNION

                    SELECT YEAR(g.game_date)  AS year,
                        c.home_team_id AS opponentID, c.away_team_id AS teamID
                    FROM compete_in c
                    JOIN game g ON c.game_id = g.game_id
                ),

                numTeams AS (
                    SELECT COUNT(DISTINCT team_id) AS freq
                    FROM team
                )

                SELECT team.team_name, opponents.year
                FROM team
                JOIN opponents ON team.team_id = opponents.teamID
                WHERE opponents.year = ?
                GROUP BY team.team_id, team.team_name, opponents.year
                HAVING COUNT(DISTINCT opponents.opponentID) = (SELECT freq - 1 FROM numTeams)
                ORDER BY team.team_name;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {

                String title = "Teams that played every other team in " + year;

                ResultSetPrinter.printResult(rs, title);
            }
        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 17" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryEighteen() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                WITH playerStats AS (
                    SELECT p.player_id,
                        CASE
                            WHEN (c.home_team_id = po.team_id AND c.home_pts > c.away_pts) OR
                                (c.away_team_id = po.team_id AND c.away_pts > c.home_pts)
                            THEN 1
                            ELSE 0
                        END AS gamesWon
                FROM player p
                JOIN play ON p.player_id = play.player_id
                JOIN game ON play.game_id = game.game_id
                JOIN compete_in c ON game.game_id = c.game_id
                JOIN part_of po ON p.player_id = po.player_id

                AND YEAR(game.game_date)
                    BETWEEN po.start_year AND COALESCE (po.end_year, YEAR(game.game_date) )
                ),

                undefeated AS (
                    SELECT player_id
                    FROM playerStats
                    GROUP BY player_id
                    HAVING MIN(gamesWon) = 1
                        AND COUNT(*) > 0
                )

                SELECT p2.player_id, p2.first_name, p2.last_name
                FROM player p2
                JOIN undefeated u ON u.player_id = p2.player_id
                ORDER BY p2.last_name, p2.first_name;
                """;

        try (Statement ps = conn.createStatement(); ResultSet rs = ps.executeQuery(sql)) {

            String title = "Players Who Never Lost a Game";
            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 18" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryNineteen() {

        ClearConsole.clear();


        int topN;

        while(true) {

            String topInput = InputReader.getString("Enter the N games:");

            try {
                topN = Integer.parseInt(topInput);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid number. Enter to try again\n" + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                WITH playerStats AS (
                    SELECT p.player_id, p.first_name, p.last_name,
                        CASE
                        WHEN (c.home_team_id = po.team_id AND c.home_pts > c.away_pts) OR
                        (c.away_team_id = po.team_id AND c.away_pts > c.home_pts)
                        THEN 1
                        ELSE 0
                        END AS gamesWon
                    FROM player p
                    JOIN play ON p.player_id = play.player_id
                    JOIN game ON play.game_id = game.game_id
                    JOIN compete_in c ON game.game_id = c.game_id
                    JOIN part_of po ON p.player_id = po.player_id
                )
                SELECT ps.first_name, ps.last_name, COUNT (*) AS gamesPlayed, SUM(ps.gamesWon) AS gamesWon FROM
                playerStats ps
                GROUP BY ps.player_id, ps.first_name, ps.last_name
                HAVING SUM(ps.gamesWon) >= ?
                ORDER BY gamesWon DESC
                """;
        try {

            Connection conn = DBConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, topN);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Players Who Won More Than " + topN + " Games";

                    ResultSetPrinter.printResult(rs, title);

                }
            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 20" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryTwenty() {

        ClearConsole.clear();


        int topN;
        
        while(true) {

            String topInput = InputReader.getString("Enter the top N players:");

            try {
                topN = Integer.parseInt(topInput);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid number. Enter to try again\n" + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                WITH playerStats AS (
                    SELECT player.player_id, player.first_name, player.last_name, player.country, COUNT(play.game_id) AS
                    gamesPlayed FROM player
                    JOIN play ON player.player_id = play.player_id
                    GROUP BY player.player_id, player.first_name, player.last_name, player.country
                )
                SELECT ps.country, ps.first_name, ps.last_name, ps.gamesPlayed FROM playerStats ps
                WHERE (
                    SELECT COUNT(*) FROM playerStats ps2
                    WHERE ps2.country = ps.country AND ps2.gamesPlayed > ps.gamesPlayed
                ) < ?
                ORDER BY ps.country, ps.gamesPlayed DESC;
                """;

        try {

            Connection conn = DBConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, topN);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Top " + topN + " Players in Each Country";

                    ResultSetPrinter.printResult(rs, title);

                }
            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 20" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryTwentyOne() {

        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                WITH refereedTeam AS (
                    SELECT Year(game.game_date) AS year, officiated.referee_id, compete_in.home_team_id
                    AS teamID FROM officiated
                    JOIN game ON officiated.game_id = game.game_id
                    JOIN compete_in ON game.game_id = compete_in.game_id
                    UNION
                    SELECT Year(game.game_date) AS year, officiated.referee_id, compete_in.away_team_id
                    AS teamID FROM officiated
                    JOIN game ON officiated.game_id = game.game_id
                    JOIN compete_in ON game.game_id = compete_in.game_id
                ),
                numTeams AS (
                    SELECT COUNT(DISTINCT team_id) AS freq FROM team
                )
                SELECT referee.first_name, referee.last_name, refereedTeam.year FROM referee
                JOIN refereedTeam ON referee.referee_id = refereedTeam.referee_id
                GROUP BY referee.referee_id, referee.first_name, referee.last_name, refereedTeam.year
                HAVING COUNT(DISTINCT refereedTeam.teamID) = (SELECT freq FROM numTeams)
                ORDER BY refereedTeam.year, referee.last_name;
                """;

        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {

            String title = "Referees that Officiated for Every Team in a Season\n\n";

            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 21" + RESET_TEXT);
            System.out.println(e.getMessage());

        }
    }

    public static void queryTwentyTwo() {

        ClearConsole.clear();


        int year;

        while(true) {

            String yearInput = InputReader.getString("Enter season year (e.g., 2010):");

            try {
                year = Integer.parseInt(yearInput);
                break;

            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid year. Enter to try again\n" + RESET_TEXT);
                continue;
            }
        }

        String sql = """
                SELECT team.team_name, COUNT(*) AS homeWins FROM team
                JOIN compete_in ON team.team_id = compete_in.home_team_id
                JOIN game ON compete_in.game_id = game.game_id
                WHERE YEAR(game.game_date) = ? AND compete_in.home_pts > compete_in.away_pts
                GROUP BY team.team_id, team.team_name
                ORDER BY homeWins DESC;
                """;

        try {

            Connection conn = DBConnection.getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, year);

                try (ResultSet rs = ps.executeQuery()) {

                    String title = "Best Performing Teams at Home: (" + year + ")\n\n";

                    ResultSetPrinter.printResult(rs, title);

                }
            }
        } catch (Exception e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 22" + RESET_TEXT);
            System.out.println(e.getMessage());
        }
    }

    public static void queryTwentyThree() {
        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                SELECT TOP 10 coach.first_name, coach.last_name, team.team_name, ROUND(AVG(manages.end_year - manages.start_year), 2) AS avgYears FROM coach
                JOIN manages ON coach.coach_id = manages.coach_id
                JOIN team ON manages.team_id = team.team_id
                WHERE manages.end_year IS NOT NULL
                GROUP BY coach.coach_id, coach.first_name, coach.last_name, team.team_name
                ORDER BY avgYears DESC
                """;

        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {

            String title = "Arenas with the most competitive (close-call) games";
            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 23" + RESET_TEXT);
            System.out.println(e.getMessage());

        }
    }

    public static void queryTwentyFour() {
        Connection conn = DBConnection.getConnection();
        ClearConsole.clear();

        String sql = """
                WITH closeGame AS (
                    SELECT compete_in.game_id, compete_in.home_team_id, compete_in.away_team_id FROM compete_in
                    WHERE ABS(compete_in.home_pts - compete_in.away_pts) <= 3
                )
                SELECT arena.arena_name, arena.location FROM game
                JOIN closeGame ON game.game_id = closeGame.game_id
                JOIN arena ON game.arena_id = arena.arena_id
                GROUP BY arena.arena_name, arena.location
                ORDER BY COUNT(game.game_id) DESC;
                """;

        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {

            String title = "Arenas with the most competitive (close-call) games";
            ResultSetPrinter.printResult(rs, title);

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error running Query 24" + RESET_TEXT);
            System.out.println(e.getMessage());

        }
    }

    private static class PlayerSelection {

        private int playerId;
        private String playerName;

        private PlayerSelection(int id, String name) {

            this.playerId = id;
            this.playerName = name;
        }

    }

    private static class TeamSelection {

        private int teamId;
        private String teamName;

        private TeamSelection(int id, String name) {

            this.teamId = id;
            this.teamName = name;
        }
    }

    private static class ArenaSelection {

        private int arenaId;
        private String arenaName;

        private ArenaSelection(int id, String name) {

            this.arenaId = id;
            this.arenaName = name;
        }
    }

    private static PlayerSelection selectPlayer(Connection conn) {

        while (true) {

            String userInput = InputReader.getString("Enter part of the player's name (or Q to cancel): ").trim();

            if (userInput.equalsIgnoreCase("q")) {

                System.out.println("Query cancelled.\n");
                return null;
            }

            if (userInput.isBlank()) {

                System.out.println("Please enter at an option.\n");
                continue;
            }

            String sql = """

                    SELECT player_id, first_name, last_name
                    FROM player
                    WHERE first_name LIKE ?
                        OR last_name LIKE ?
                        OR (first_name + ' ' + last_name) LIKE ?
                    ORDER BY last_name, first_name
                        """;

            List<Integer> ids = new ArrayList<>();
            List<String> names = new ArrayList<>();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                String fillName = "%" + userInput + "%";

                ps.setString(1, fillName);
                ps.setString(2, fillName);
                ps.setString(3, fillName);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        int id = rs.getInt("player_id");
                        String name = rs.getString("first_name") + " " + rs.getString("last_name");

                        ids.add(id);
                        names.add(name);
                    }
                }

            } catch (SQLException e) {

                System.out.println("[DB] Error searching for players");
                System.out.println(e.getMessage());
                return null;
            }

            if (ids.isEmpty()) {

                System.out.println("No players matched. Try again.");
                continue;
            }

            if (ids.size() >= 50) {

                System.out.println("Too many matches (50 or more). Enter a more specific name.\n");
                continue;
            }
            
            while(true) {

                ClearConsole.clear();
                System.out.println("Matching players:");

                ResultSetPrinter.twoColumns(names, null);

                String option = InputReader.getString("Select a player by number (or Q to cancel)");

                if (option.equalsIgnoreCase("q")) {

                    System.out.println("Query cancelled");
                    return null;
                }

                int index;

                try {
                    index = Integer.parseInt(option);

                } catch (NumberFormatException nfe) {

                    InputReader.getString(RED_TEXT + "Invalid input. Enter to try again" + RESET_TEXT);

                    continue;
                }                

                if (index < 1 || index > ids.size()) {

                    InputReader.getString(RED_TEXT + "Invalid input. Enter to try again" + RESET_TEXT);
                    continue;
                }

                return new PlayerSelection(ids.get(index - 1), names.get(index - 1));
            }
        }

    }

    private static TeamSelection selectTeam(Connection conn) {

        // Load all teams
        List<Integer> teamIds = new ArrayList<>();
        List<String> teamNames = new ArrayList<>();

        String teamSql = "SELECT team_id, team_name FROM team ORDER BY team_name";

        try (PreparedStatement ps = conn.prepareStatement(teamSql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                teamIds.add(rs.getInt("team_id"));
                teamNames.add(rs.getString("team_name"));
            }

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error loading teams." + RESET_TEXT);
            System.out.println(e.getMessage());
            return null;
        }

        if (teamIds.isEmpty()) {

            System.out.println("No teams in the database");
            return null;
        }


        while (true) {

            ClearConsole.clear();

            ResultSetPrinter.twoColumns(teamNames, null);

            String userChoice = InputReader.getString("Choose a team (Q to quit):");

            if (userChoice.equalsIgnoreCase("q")) {

                System.out.println("Query cancelled.");
                return null;
            }

            int option;

            try {

                option = Integer.parseInt(userChoice);
            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid input. Enter to try again" + RESET_TEXT);

                continue;                
            }

              if (option < 1 || option > teamNames.size()) {

                InputReader.getString(RED_TEXT + "Invalid option. Enter to try again.\n" + RESET_TEXT);
                continue;

            } else {

                return new TeamSelection(teamIds.get(option - 1), teamNames.get(option - 1));
            }

        }

    }

    private static ArenaSelection selectArena(Connection conn) {

        List<Integer> arenaId = new ArrayList<>();
        List<String> arenaNames = new ArrayList<>();
        List<String> associatedTeams = new ArrayList<>();

        String arenaSql = """
                            SELECT DISTINCT arena.arena_id, arena.arena_name, team.team_name FROM arena 
                            INNER JOIN team ON arena.arena_id = team.arena_id ORDER BY arena.arena_name
                        """;

        try (PreparedStatement ps = conn.prepareStatement(arenaSql);

                ResultSet rs = ps.executeQuery()) {

            while ((rs.next())) {
                arenaNames.add(rs.getString("arena_name"));
                arenaId.add(rs.getInt("arena_id"));
                associatedTeams.add(rs.getString("team_name"));
            }

        } catch (SQLException e) {

            System.out.println(RED_TEXT + "[DB] Error loading arenas" + RESET_TEXT);
            System.out.println(e.getMessage());
        }

        if (arenaId.isEmpty()) {

            System.out.println("No arenas available in the database");
            return null;
        }


        // Display arena for user's selection

        while (true) {

            ClearConsole.clear();

            ResultSetPrinter.twoColumns(arenaNames, associatedTeams);

            String userChoice = InputReader.getString("Choose an arena (Q to quit): ");

            if (userChoice.equalsIgnoreCase("q")) {

                System.out.println("Query cancelled.");
                return null;
            }

            int option;

            try {
                option = Integer.parseInt(userChoice);
            } catch (NumberFormatException nfe) {

                InputReader.getString(RED_TEXT + "Invalid input. Enter to try again" + RESET_TEXT);
                continue;

            }

            if (option < 1 || option > arenaNames.size()) {

                InputReader.getString(RED_TEXT + "Invalid option. Enter to try again.\n" + RESET_TEXT);
                continue;

            } else {

                return new ArenaSelection(arenaId.get(option - 1), arenaNames.get(option - 1));

            }
        }

    }

}