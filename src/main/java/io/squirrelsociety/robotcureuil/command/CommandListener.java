package io.squirrelsociety.robotcureuil.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
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
}
