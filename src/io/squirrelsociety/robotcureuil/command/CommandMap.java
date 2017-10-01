package io.squirrelsociety.robotcureuil.command;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.squirrelsociety.robotcureuil.RobotCureuil;
import io.squirrelsociety.robotcureuil.commands.PoliticCommands;
import io.squirrelsociety.robotcureuil.commands.UserCommands;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 * Created by Ewan on 07/07/2017.
 */
public final class CommandMap {

    private final RobotCureuil bot;
    private final Map<String, SimpleCommand> commands = new HashMap<>();
    private final String tag = "-";

    public CommandMap(RobotCureuil bot) {
        this.bot = bot;
        registerCommands(new UserCommands(), new PoliticCommands());
    }

    public void registerCommands(Object... objects) {
        for (Object object : objects)registerCommand(object);
    }

    public void registerCommand(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(Command.class)){
                Command command = method.getAnnotation(Command.class);
                method.setAccessible(true);
                SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(), object, method);
                commands.put(command.name(), simpleCommand);
            }
        }
    }

    public void commandConsole(String command) {
        Object[] objects = getCommand(command);
        if(objects[0] == null || ((SimpleCommand)objects[0]).getExecutorType() == Command.ExecutorType.USER) {
            return;
        }
        try {
            execute(((SimpleCommand)objects[0]), command, (String[])objects[1], null);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public boolean commandUser(User user, String command, Message message){
        Object[] object = getCommand(command);
        if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == Command.ExecutorType.CONSOLE)return false;
        try {
            execute(((SimpleCommand) object[0]), command, ((String[]) object[1]), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception {
        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[]  objects = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            if(parameters[i].getType() == String[].class) objects[i] = args;
            else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
            else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
            else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
            else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
            else if(parameters[i].getType() == String.class) objects[i] = command;
            else if(parameters[i].getType() == Message.class) objects[i] = message == null ? null : message;
            else if(parameters[i].getType() == JDA.class) objects[i] = bot.getJda();
            else if(parameters[i].getType() == MessageChannel.class) objects[i] = message == null ? null : message.getChannel();
        }

        simpleCommand.getMethod().setAccessible(true);
        simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
    }

    private Object[] getCommand(String command) {
        String[] commandSplit = command.split(" ");
        String[] args = new String[commandSplit.length-1];
        for (int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
        SimpleCommand simpleCommand = commands.get(commandSplit[0]);
        return  new Object[]{simpleCommand, args};
    }

    public Collection<SimpleCommand> getCommands() {
        return commands.values();
    }

    public String getTag() {
        return tag;
    }
}
