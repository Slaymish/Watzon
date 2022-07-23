package vrml.api;

public class Multitread {
    public static void main(String[] args) throws InterruptedException {
        // Create a new thread
        Thread startApp = new Thread(new App());
        
        // Start the thread
        startApp.start();

        new App();
        Thread updateTeams  = new Thread(App.updateAll());

        updateTeams.start();

    }
}
