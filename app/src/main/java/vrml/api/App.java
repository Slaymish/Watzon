/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package vrml.api;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.Guild;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;



public class App implements Runnable {
    private static final String url = "https://api.vrmasterleague.com";
    private static final String region = "OCE"; // query
    public static ArrayList<Team> OCETeams;
    public static ArrayList<Team> NATeams;
    public static ArrayList<Player> captains;

    public static void initialize() throws ParseException, LoginException, InterruptedException{
        // Configure Unirest
        Unirest.config().defaultBaseUrl(url);

        // Start bot
        Bot.initialize();

        System.out.println("Initialization Complete");
    }

    public static ArrayList<Player> getCaptains(){
        ArrayList<Player> tempCaps = new ArrayList<Player>();

        for(Team t : OCETeams){
            tempCaps.add(getCaptain(t));
        }

        return tempCaps;
    }

    public static Player getTeamPlayer(String playerName) throws ParseException, LoginException, InterruptedException{
        for (Team t: OCETeams) {
            for (Player p: t.players) {
                if (p.lowName.equals(playerName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public static Team getTeam(String playerName) throws ParseException, LoginException, InterruptedException{
        for (Team t: OCETeams) {
            for (Player p: t.players) {
                if (p.lowName.equals(playerName)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static int[] activeTeams(ArrayList<Team> teams){
        int[] OCESize = new int[2];

        for (int i = 0; i < OCETeams.size(); i++) {
            OCESize[1]++;
            if (OCETeams.get(i).active) {
                OCESize[0]++;
            }
        }

        return OCESize;
    }

    public static ArrayList<Team> getOCETeams() throws ParseException {
        ArrayList<Team> teams = new ArrayList<Team>();

        // Go through all teams and save oce teams id to arraylist
        // https://api.vrmasterleague.com/EchoArena/Standings?region=OCE
        String body = Unirest.get("/EchoArena/Standings?region=" + region).asString().getBody(); // All teams as string
        
        // Parse JSON
        Object obj = new JSONParser().parse(body);
        
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;

        // Get JSONArray of teams
        JSONArray arr = (JSONArray) jo.get("teams");

        if (arr != null){
            // Loop through all teams
            for (int i = 0; i < arr.size(); i++){
                // Get team object
                JSONObject team = (JSONObject) arr.get(i);

                Team t = getTeam((JSONObject) team);

                // Add team to arraylist
                teams.add(t);
                if (i == Math.round(arr.size()*(1/4))){
                    System.out.println("25%");
                }
                if (i == Math.round(arr.size()*(2/4))){
                    System.out.println("50%");
                } 
                if (i == Math.round(arr.size()*(3/4))){
                    System.out.println("75%");
                }
                if (i == arr.size()-1){
                    System.out.println("100%");
                }
            }
        }

        return teams;
    }

    // top teams
    public static Team[] getTopTeams(ArrayList<Team> OCEteams, int num) {
        Team[] teams = new Team[num];

        // Loop through all teams
        for (int i=0; i < num && i < OCEteams.size(); i++) {
            teams[i] = OCEteams.get(i);
        }

        return teams;
    }

    // get matches array
    /* 
    public static ArrayList<Match> getMatches(ArrayList<Team> teams) throws ParseException {
        ArrayList<Match> matches = new ArrayList<Match>();

        // cycle through all team ids
        // for each team id -> search for upcoming matches
        for (Team t: teams){
            String body = Unirest.get("Teams/" + t.teamId).asString().getBody(); // All teams as string

            Object obj = new JSONParser().parse(body);
        
            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;

            // Get JSONArray of teams
            JSONArray arr = (JSONArray) jo.get("upcomingMatches");

            if (arr != null){
                // Go through each up coming match
                for (int i = 0; i < arr.size(); i++){
                    // Get team object
                    JSONObject match = (JSONObject) arr.get(i);
                    String matchID = (String) match.get("matchID");
                    int homeBetCount = (int) match.get("homeBetCount");
                    int awayBetCount = (int) match.get("awayBetCount");
                    int castUpvotes = (int) match.get("castUpvotes");
                    String dateScheduled = (String) match.get("dateScheduled");
                    int week = (int) match.get("week");

                    // Gets teams
                    Team homeTeam = getTeam((JSONObject) match.get("homeTeam"));
                    Team awayTeam = getTeam((JSONObject) match.get("awayTeam"));

                    // Create match
                    Match m = new Match(matchID,homeTeam,awayTeam,homeBetCount,awayBetCount,castUpvotes,dateScheduled,week);
                    matches.add(m);
                }
            }
        }
        return matches;
    }
    */

    // Get singular team (return Team)
    private static Team getTeam(JSONObject team) throws ParseException {
        // Get team name
        String teamName = (String) team.get("teamName");

        // Get team id
        String teamId = (String) team.get("teamID"); 
        //System.out.println(teamId);
        String teamMMR = (String) team.get("mmr");
        String tierImg = (String) team.get("divisionLogo");
        String teamLogo = (String) team.get("teamLogo");
        String divName = (String) team.get("divisionName");

        // Get team rank
        int teamRank = Math.toIntExact((long) team.get("rank")); // long?

        // is active
        boolean isActive = (boolean) team.get("isActive");

        // Get players
        ArrayList<Player> players = new ArrayList<Player>();

        // Do an api call for specific team id
        String body = Unirest.get("/Teams/" + teamId).asString().getBody(); // All teams as string
        Object obj = new JSONParser().parse(body);
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        // Get JSONArray of teams
        JSONObject teamarr = (JSONObject) jo.get("team");
        JSONArray arr = (JSONArray) teamarr.get("players");


        if (arr != null){
            for (int i = 0; i < arr.size(); i++){
                // Get player object
                JSONObject player = (JSONObject) arr.get(i);

                String discordID = null;
                String discordName = null;
                // Get values
                if (player.get("discordID") != null){
                    discordID = (String) player.get("discordID").toString();
                    discordName = (String) player.get("discordTag").toString();
                }
                String playerName = (String) player.get("playerName"); // can't cast long to string
                String country = (String) player.get("country");
                String captain = (String) player.get("isTeamOwner").toString();
                String starter = (String) player.get("isTeamStarter").toString();
                String userLogo = (String) player.get("userLogo").toString();
                String userID = (String) player.get("userID").toString();

                // Create player
                Player p = new Player(playerName, discordID, country,captain,starter,userLogo, userID, discordName);
                players.add(p);
            }
        }
        else {
            System.out.println("No players");
        }

        // Create new team
        return new Team(teamName, teamId, teamRank, isActive,teamMMR,tierImg,teamLogo,divName, players);
    }

    // Get captain
    public static Player getCaptain(Team team) {
        System.out.println(team.players);
        for (Player p: team.players){
            if (p.captain){
                return p;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting.............");
            initialize();
        } catch (LoginException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Runnable updateAll() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Updating all");
                    OCETeams = getOCETeams();
                    captains = getCaptains();
                    Bot.captainRoles(Bot.scrimOrg);
                    System.out.println("OCE teams updated");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            
        } , 0, 60000 * 20);

        return null;
    }
    
}

