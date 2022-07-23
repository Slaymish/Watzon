package vrml.api;

public class Match {
    public String matchId;
    public Team homeTeam;
    public Team awayTeam;
    public int homeBetCount;
    public int awayBetCount;
    public int castUpvotes;
    public String dateScheduled; // ISO date format (in UTC)
    public int week;

    public Match(String matchId, Team homeTeam, Team awayTeam, int homeBetCount, int awayBetCount, int castUpvotes, String dateScheduled, int week) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeBetCount = homeBetCount;
        this.awayBetCount = awayBetCount;
        this.castUpvotes = castUpvotes;
        this.dateScheduled = dateScheduled;
        this.week = week;
    }
}
