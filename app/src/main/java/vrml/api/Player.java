package vrml.api;

public class Player {
    /* 
    [{"isCooldown":false,
    "cooldownNote":null,
    "cooldownDateExpiresUTC":null,
    "honoursMentionNote":null,
    "honoursMentionLogo":null,
    "discordID":395501359517925377,
    "discordTag":"spencer#7906",
    "discordTeamRole":1,"playerID":
    "VG1L2GdoJsclzZ6-mFJAQw2",
    "playerName":"SpencerPCS",
    "userID":"FO0cxf5ZFUjQ2FU6oO0M2A2",
    "userLogo":"/images/logos/users/73ec0f0f-b2e8-442c-b4d2-782b755ba18b.png",
    "country":"AU",
    "nationality":"IT",
    "streamURL":"https://twitch.tv/spencerpcs",
    "teamID":"e13XNkApf6WVpBI9jgsLBQ2",
    "teamName":"Devils",
    "roleID":"M40DmiDmyky3AVn8ypAC1g2",
    "role":"Team Owner",
    "isTeamOwner":true,
    "isTeamStarter":true}

    */

    public String playerName;
    public String discordID;
    public String country;
    public boolean captain;
    public boolean starter;
    public String userLogo;
    public String userID;
    public String discordName;
    public String lowName;

    public Player(){
        playerName = "";
        discordID = "";
        country = "";
        captain = false;
        starter = false;
        userLogo = "";
        userID = "";
    }

    public Player(String playerName, String discordID, String country, String captain, String starter, String userLogo, String userID, String discordName) {
        this.playerName = playerName;
        this.discordID = discordID;
        this.country = country;
        this.captain = captain.equals("true") ? true : false;
        this.starter = starter.equals("true") ? true : false;
        this.userLogo = userLogo;
        this.userID = userID;
        this.discordName = discordName;
        this.lowName = playerName.toLowerCase();
    }
}
