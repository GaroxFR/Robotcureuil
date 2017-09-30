package io.squirrelsociety.robotcureuil;


import io.squirrelsociety.robotcureuil.command.CommandListener;
import io.squirrelsociety.robotcureuil.command.CommandMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.Scanner;

/**
 * Created by GartoxFR on 30/09/2017.
 */
public class RobotCureuil implements Runnable{

    private JDA jda;
    private boolean running = false;
    private static RobotCureuil instance;
    private Scanner scanner;
    private CommandMap commandMap;

    public RobotCureuil(String token) {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        } catch (LoginException e) {
            System.out.println("Echec de l'authentification !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
        scanner = new Scanner(System.in);
        commandMap = new CommandMap(this);
        jda.addEventListener(new CommandListener(commandMap));
        System.out.println("Connecté en temps que : " + jda.getSelfUser().getName());
    }

    @Override
    public void run() {
        instance = this;
        running = true;

        while(running) {
            if(scanner.hasNextLine()) commandMap.commandConsole(scanner.nextLine());
        }

        if(jda != null)jda.shutdown();
        scanner.close();
        System.out.println("Bot stopped");
        System.exit(0);
    }

    public boolean isRunning() {
        return running;
    }

    public JDA getJda() {
        return jda;
    }

    public static RobotCureuil getInstance() {
        return instance;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public static void main(String[] args) {
        new Thread(new RobotCureuil(args[0])).start();
    }

}
