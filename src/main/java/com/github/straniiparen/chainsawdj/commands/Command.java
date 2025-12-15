package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {
    String getName();
    String getDescription();
    SlashCommandData getCommandData();
    void execute(SlashCommandInteractionEvent event);
}