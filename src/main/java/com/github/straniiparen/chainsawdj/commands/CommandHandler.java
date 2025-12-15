package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {
    private final Map<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();

        // Регистрируем все команды
        registerCommand(new PingCommand());
        registerCommand(new CoinFlipCommand());
        /*
        registerCommand(new PlayCommand());
        registerCommand(new PauseCommand());
        registerCommand(new StopCommand());
        registerCommand(new ShuffleCommand());
        registerCommand(new QueueCommand());
        registerCommand(new SkipCommand());
        registerCommand(new LoopCommand());
         */
    }

    private void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void registerCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Пингует в ответ"),
                Commands.slash("coinflip", "Подбрасывает монетку")
                /*
                Commands.slash("play", "Добавляет музыку в очередь и приглашает бота в канал")
                        .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.STRING,
                                "url", "URL YouTube видео или плейлиста", true),
                Commands.slash("pause", "Ставит текущий трек на паузу или снимает с неё"),
                Commands.slash("stop", "Полностью очищает очередь и выгоняет бота из канала"),
                Commands.slash("shuffle", "Перемешивает очередь"),
                Commands.slash("queue", "Выводит очередь"),
                Commands.slash("skip", "Убирает трек из очереди")
                        .addOption(net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER,
                                "position", "Позиция трека (0 - текущий)", false),
                Commands.slash("loop", "Включает/выключает повтор текущего трека")
                 */
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = commands.get(event.getName());
        if (command != null) {
            command.execute(event);
        } else {
            event.reply("❌ Неизвестная команда").setEphemeral(true).queue();
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Команды зарегистрированы!");
    }
}