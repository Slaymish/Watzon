package vrml.api;

import javax.security.auth.login.LoginException;
import org.json.simple.parser.ParseException;
import java.awt.Color;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Bot extends ListenerAdapter {
    public static long startTime;
    static EmbedBuilder helpEmbed = new EmbedBuilder();
    static EmbedBuilder teamEmbed = new EmbedBuilder();
    static EmbedBuilder matchesEmbed =  new EmbedBuilder();
    static EmbedBuilder infoEmbed = new EmbedBuilder();
    static EmbedBuilder playerStats = new EmbedBuilder();
    public String shortUrl = "https://vrmasterleague.com";
    public String url = "https://api.vrmasterleague.com";
    private static String TOKEN = "";
    private static String botURl;
    //private static final String APP_ID = "12345654321";
    private static final Color WATZ_COL = new Color(30,203,225);
    

    // Add guilds
    public static Guild scrimOrg;
    public static Guild hydra;

    public static void initialize() throws LoginException, InterruptedException {
        // Get token from file
        try {
            Path path = FileSystems.getDefault().getPath("token.txt");
            File tokenFile = new File(path.toString());
            try (Scanner scanner = new Scanner(tokenFile)) {
                TOKEN = scanner.nextLine();
                System.out.println(TOKEN);
            }
            
        } catch (Exception e) {
            System.out.println("Error Files" + e.getMessage());
        }

        startTime = System.currentTimeMillis();

        JDA jda = JDABuilder.createDefault(TOKEN)
            .setActivity(Activity.playing("Use '/help'"))
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(new Bot())
            .build();

        jda.awaitReady();

        // Add guilds
        scrimOrg = jda.getGuildById("810205195941838908");
        hydra = jda.getGuildById("601584998616924189");

        // Set up commands
        addCommands(scrimOrg);
        addCommands(hydra);

        // Get bots avatar
        botURl = jda.getSelfUser().getAvatar().getUrl();

        // Set list to 2 commands

        helpEmbed.setTitle("Watz Help");
        helpEmbed.setDescription("Here are the commands for Watz");
        helpEmbed.addField("/help", "Shows this message", false);
        helpEmbed.addField("/team <team name>", "Shows the stats for the team", false);
        helpEmbed.addField("/top", "Shows the top ten OCE teams", false);
        helpEmbed.addField("/size", "Shows no. of active OCE teams", false);
        helpEmbed.addField("/matches WIP", "Shows all upcoming matches", false);
        helpEmbed.addField("/match <match number> WIP", "Shows details about a match (use /matches to see numbers)", false);
        helpEmbed.addField("/player <player name>", "Shows the players VRML team", false);
        addFooter(helpEmbed);

        helpEmbed.setColor(WATZ_COL);

        infoEmbed.setTitle("Watzon Information");
        infoEmbed.setColor(WATZ_COL);
        infoEmbed.addField("Use /help for more information","", false);
        infoEmbed.addField("Github", "https://github.com/Slaymish/Watzon", false);
        infoEmbed.addField("Trello","[Trello Page](https://trello.com/invite/b/AeB7rrWR/c5a1f34f3fbb07945d454b888c910b6a/watzon)", false);
        infoEmbed.setThumbnail(botURl);
        addFooter(infoEmbed);
    }

    public static void addCommands(Guild g){
        // Delete all commands
        g.updateCommands().queue();

            g.updateCommands()
                .addCommands(Commands.slash("test", "to test slash commands"))
                .addCommands(Commands.slash("help", "Information about the bot"))
                .addCommands(Commands.slash("top", "View the top 10 teams")
                    .addOption(OptionType.STRING, "amount", "How many teams do you want to display"))
                .addCommands(Commands.slash("size", "View the size of OCE"))
                .addCommands(Commands.slash("team", "Information about a team")
                    .addOption(OptionType.STRING, "name", "What team would you like to know about?", true))
                .addCommands(Commands.slash("matches", "View all upcoming matches"))
                .addCommands(Commands.slash("match", "View details about a match")
                    .addOption(OptionType.INTEGER, "number", "What match would you like to know about?", true))
                .addCommands(Commands.slash("player", "View the team that a player is on")
                    .addOption(OptionType.STRING, "player", "What player would you like to know about?", true))
                .addCommands(Commands.slash("info", "Shows bot infomation"))
                  .queue();
    }

    public static void addFooter(EmbedBuilder embed){
        embed.setFooter("Watz", botURl);
        embed.setTimestamp(Instant.now());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){

        switch (event.getName()){
            case "help":
                event.replyEmbeds(helpEmbed.build()).setEphemeral(false) // Ephemeral means only sender can see
                .queue();
                break;
            case "team":
                String teamName = event.getOption("name").getAsString().toLowerCase();
                try {
                    event.replyEmbeds(teamInfo(teamName, event).build()).setEphemeral(false) // Ephemeral means only sender can see
                    .queue();
                } catch (ParseException e) {
                    event.reply("An error occurred while parsing the team info").setEphemeral(true).queue(); // Ephemeral means only sender can see
                    e.printStackTrace();
                }
                teamEmbed.clear();
                break;
            case "top":
                Integer num = Integer.parseInt(event.getOption("amount").getAsString());
                event.replyEmbeds(topTeams(event, num).build()).setEphemeral(false) // Ephemeral means only sender can see
                .queue();
                break;

            case "size":
                event.reply("OCE currently has " + App.activeTeams(App.OCETeams)[0] + " active teams! (" + App.activeTeams(App.OCETeams)[1] + " total teams)").setEphemeral(false) // Ephemeral means only sender can see
                .queue();
                break;

            case "matches":
                event.replyEmbeds(showMatches(event).build()).setEphemeral(false) // Ephemeral means only sender can see
                .queue();
                break;
            case "player":
                event.replyEmbeds(infoEmbed.build()).setEphemeral(false) // Ephemeral means only sender can see
                .queue();
            case "info":
                event.replyEmbeds(infoEmbed.build()).setEphemeral(false).queue(); // Ephemeral means.queue(); // Ephemeral means only sender can see

            default:
                event.reply("Unknown command (use /help)").setEphemeral(false).queue(); // Ephemeral means only sender can see
                break;
        }
    }

    /* 
    public void onMessageReceived(MessageReceivedEvent event){
        String message = event.getMessage().getContentRaw();
        boolean admin = false;
        message = message.toLowerCase();
        System.out.println("Name: " + event.getAuthor().getName() + "  -  " + message);

        if (message.equals("watz")){
            event.getChannel().sendTyping();
            event.getChannel().sendMessage("Type 'watz help' for a list of commands.").queue();
            
            return;
        }

        if (event.getAuthor().getId() == "688474086573211677"){
            admin = true;
        }
        
        if (event.getAuthor().isBot()){
            return;
        }


        if (message.startsWith("watz vc")){
            this.vcChat(message, event);
            return;
        }

        if (message.equals("watz im gonna do it") || message.equals("watz i'm gonna do it")){
            event.getChannel().sendMessage("Please do").queue();
            return;
        }

        if (message.equals("watz top")){
            topTeams(event);
            return;
        }

        if (message.equals("watz hi") || message.equals("watz hello") || message.equals("watz hey")){
            int hiCount = rand.nextInt(3);

            if (hiCount == 1){
                event.getChannel().sendMessage("https://tenor.com/view/omori-basil-kiss-gay-gif-25386247").queue();
            }
            if (hiCount == 2){
                event.getChannel().sendMessage("Hello " + event.getAuthor().getName() + "!").queue();
            }
            if (hiCount == 3){
                event.getChannel().sendMessage("Hello...").queue();
            }
            if (hiCount >= 4){
                event.getChannel().sendMessage("Heyoo").queue();
            }



            return;
        }

        if (message.equals("watz uptime")){
            long uptime = (System.currentTimeMillis() - startTime)/1000;
            
            event.getChannel().sendMessage(uptime + " secs").queue();
            return;
        }
    
        if (message.equals("watz oce size")){
            event.getChannel().sendMessage("OCE currently has " + App.activeTeams(App.OCETeams)[0] + " active teams! (" + App.activeTeams(App.OCETeams)[1] + " total teams)").queue();
            return;
        }

        if (message.equals("watz up")){
            event.getChannel().sendMessage("Nothin much watz up with you?").queue();
            return;
        }

        if (message.startsWith("watz")){
            event.getChannel().sendMessage("I'm not sure how to respond to that.").queue();
            return;
        }
    
    }
    */

    private EmbedBuilder playerStats(SlashCommandInteractionEvent event) throws LoginException, ParseException, InterruptedException {
        playerStats.clear();
        String player = event.getOption("player").getAsString();
        String playerLow = player.toLowerCase();
        playerStats.setTitle(player);
        playerStats.setDescription("Player stats");
        playerStats.setColor(WATZ_COL);
        addFooter(playerStats);
        
        Player p = App.getTeamPlayer(playerLow);
        Team t = App.getTeam(playerLow);

        if (p == null){
            playerStats.setDescription("Player not found");
            playerStats.setColor(Color.RED);
            return playerStats;
        }

        playerStats.setTitle(p.playerName);
        playerStats.setDescription("<@" + p.discordID + ">");
        playerStats.addField("Team", t.teamName, false);
        playerStats.addField("Rank", ((Integer) t.teamRank).toString(), false);
        
        playerStats.setThumbnail(shortUrl + p.userLogo);


        return playerStats;
    }


    private EmbedBuilder showMatches(SlashCommandInteractionEvent event) {
        matchesEmbed.clear();
        matchesEmbed.setTitle("Upcoming matches");
        matchesEmbed.setColor(WATZ_COL);
        matchesEmbed.setDescription("Work in progress");
        addFooter(matchesEmbed);
        

        return matchesEmbed;
    }

    // VC stuff
    /*
    public void vcChat(String message, MessageReceivedEvent event) {
        StringTokenizer st = new StringTokenizer(message);

            st.nextToken();
            st.nextToken();
            boolean found = false;
            String msg = "";
            while (st.hasMoreTokens()){
                String token = st.nextToken();
                msg = msg + " " + token;
            }

            ArrayList<String> vcChats;


            if (msg != ""){
                event.getChannel().sendMessage("VC: " + msg).queue();
                for (String id: getUsersInVoiceChannel(event)){
                    event.getChannel().sendMessage("<@" + id + ">").queue();
                } 
            }
            else {
                event.getChannel().sendMessage("Please specify a messsage").queue();
                return;
            }
    }

    public String[] getUsersInVoiceChannel(MessageReceivedEvent event) {
        ArrayList<String> userList = new ArrayList<>();

        

        for (VoiceChannel vc : event.getGuild().getVoiceChannels()){
            for (Member m : vc.getMembers()){
                userList.add(m.getUser().getId());
            }
        }

        String[] userArray = new String[userList.size()];
        userArray = userList.toArray(userArray);
        return userArray;
      }

    */
    

    public static void captainRoles(){

        // Remove captain role from all players
        for (Member m : scrimOrg.getMembers()){
            for (Role r : m.getRoles()){
                if (r.getName().equals("Captain")){
                    scrimOrg.removeRoleFromMember(m, r);
                }
            }
        }

        Role capRole = null;
        // Add captain role to captains
        for (Role r : scrimOrg.getRoles()){
            if (r.getId().equals("993482208067203152")){
                capRole = r;
            }
        }
        
        ArrayList<Member> scrimMems = (ArrayList<Member>) scrimOrg.getMembers();
        
        for (Player p: App.captains){
            for (Member m : scrimMems){
                if (m.getId().equals(p.discordID)){
                    System.out.println(scrimOrg.getMemberById(p.discordID).getNickname());
                    scrimOrg.addRoleToMember(scrimOrg.getMemberById(p.discordID), capRole).queue();;
                }
            }
        }
    }
        
    
    public EmbedBuilder teamInfo(String team, SlashCommandInteractionEvent event) throws ParseException{
        teamEmbed.clear();
        for (Team t: App.OCETeams){
            if (t.teamName.toLowerCase().equals(team)){
                return this.embedTeam(t, event);
            }
        }

        teamEmbed.setTitle("Team not found");
        teamEmbed.setDescription("Please try again");
        teamEmbed.setColor(Color.red);
        addFooter(teamEmbed);
        return teamEmbed;
    }

    public EmbedBuilder embedTeam(Team t, SlashCommandInteractionEvent event) throws ParseException{                
        // Get team info
        String teamLogoURL = shortUrl + t.teamLogo;
        String tierURL = shortUrl + t.tierImg;
        String urlT = "";
        urlT = url + t.teamId;
        String urlTeam = "[Team Page](" + urlT + ")";

        // Set up embed
        teamEmbed.setTitle(t.teamName);
        teamEmbed.setDescription(urlTeam);
        teamEmbed.setThumbnail(tierURL);
        teamEmbed.setImage(teamLogoURL);
        teamEmbed.addField("Rank", t.teamRank + "", true);
        String active = t.active ? "Yes" : "No";
        teamEmbed.addField("Active", active, true);


        teamEmbed.addField("MMR", t.teamMMR + "", true);
        teamEmbed.setColor(WATZ_COL);
        addFooter(teamEmbed);


        // Get captain info
        Player captain = App.getCaptain(t)==null ? new Player() : App.getCaptain(t);
        System.out.println(captain.discordID);
        teamEmbed.addField("Captain", "<@" + captain.discordID + ">", true);


        return teamEmbed;

    }

    public EmbedBuilder topTeams(SlashCommandInteractionEvent event, int num){
        String[] teams = App.getTopTeams(App.OCETeams, num);
        String msg = "";
        EmbedBuilder em = new EmbedBuilder();
        em.setTitle("Top ten OCE teams");
        for (int i = 0; i < teams.length; i++){
            msg = (i+1) + ":  " + teams[i];
            em.addField(msg, "", false);
            
        }

        em.setColor(WATZ_COL);
        addFooter(em);

        return em;

    }

}

