package vrml.api;

import java.util.ArrayList;

public class Team {
    public String teamName;
    public String teamId;
    public int teamRank; // specific to oce
    public boolean active;
    public String teamMMR;
    public String tierImg;
    public String teamLogo;
    public String divisionName;
    public ArrayList<Player> players;

    public Team() {
        teamName = "";
        teamId = "";
        teamRank = -1;
        active = false;
    }

    public Team(String teamName, String teamId, int teamRank, boolean active, String teamMMR, String tierImg, String teamLogo, String divisionName, ArrayList<Player> players) {
        this.teamName = teamName;
        this.teamId = teamId;
        this.teamRank = teamRank;
        this.active = active;
        this.teamMMR = teamMMR;
        this.tierImg = tierImg;
        this.teamLogo = teamLogo;
        this.divisionName = divisionName;
        this.players = players;
    }


}
