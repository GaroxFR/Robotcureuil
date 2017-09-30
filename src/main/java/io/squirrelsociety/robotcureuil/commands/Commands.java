package io.squirrelsociety.robotcureuil.commands;

import io.squirrelsociety.robotcureuil.RobotCureuil;
import io.squirrelsociety.robotcureuil.command.Command;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Created by GartoxFR on 30/09/2017.
 */
public class Commands {

    @Command(name = "info", type = Command.ExecutorType.USER)
    public void info(TextChannel channel) {
        channel.sendMessage("Bonjour la populasse").queue();
    }

    @Command(name = "stop", type = Command.ExecutorType.CONSOLE)
    public void stop(){
        RobotCureuil.getInstance().setRunning(false);
    }

}
