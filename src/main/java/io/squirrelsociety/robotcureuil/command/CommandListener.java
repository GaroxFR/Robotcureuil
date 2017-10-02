package io.squirrelsociety.robotcureuil.command;

import java.awt.Color;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * Created by GartoxFR on 30/08/2017.
 */
public class CommandListener implements EventListener {

    private final  CommandMap commandMap;

    public CommandListener(CommandMap commandMap) {
        this.commandMap = commandMap;
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageReceivedEvent)onMessage(((MessageReceivedEvent) event));
    }

    private void onMessage(MessageReceivedEvent event) {
        if(event.getAuthor().equals(event.getJDA().getSelfUser()))return;
        String message = event.getMessage().getContent();
        if(message.startsWith(commandMap.getTag())) {
            message = message.replaceFirst(commandMap.getTag(), "");
            if(commandMap.commandUser(event.getAuthor(), message, event.getMessage())) {
                if(event.getTextChannel() != null && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    event.getMessage().deleteMessage().queue();
                }
            }
        }
    }
    
    private void onReactionAdd(MessageReactionAddEvent event) {
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        System.out.println("auteur " + message.getAuthor().getName());
        if(message.getAuthor().getName().equals("RobotCureuil")) {
            if(message.getContent().equals("Clique sur le 🐿  pour avoir le rôle !")) {
                System.out.println("Reaction added");
                if(event.getUser().equals(event.getJDA().getSelfUser()))return;
                if(event.getReactionEmote().getName().equals("🐿")) {
                    if(event.getMember().getEffectiveName().endsWith("cureuil")) {
                        System.out.println("Role to add : " + event.getGuild().getRoleById("364102814076633092").getName());
                        event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById("364102814076633092")).queue();
                        System.out.println("Role added to " + event.getMember().getEffectiveName());
                    }
                    else errorBuilder(event.getTextChannel(), "Votre pseudo ne finit pas par `cureuil`");
                }
            }
           
        }
    }
}
