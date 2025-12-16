package com.github.straniiparen.chainsawdj.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {
    private final Map<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();

        // Приколы
        registerCommand(new PingCommand());
        registerCommand(new CoinFlipCommand());

        // Админки
        registerCommand(new ClearCommand());
        registerCommand(new KickCommand());
        registerCommand(new BanCommand());
        registerCommand(new UnbanCommand());
        registerCommand(new SlowmodeCommand());

        /*
        // Музыка
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
                Commands.slash("coinflip", "Подбрасывает монетку"),

                Commands.slash("clear", "Удаляет указанное количество сообщений")
                        .addOption(OptionType.INTEGER, "количество", "Количество сообщений для удаления (2-100)", true),
                Commands.slash("kick", "Исключает участника с сервера")
                        .addOptions(
                                new OptionData(OptionType.USER, "пользователь", "Пользователь для исключения", true),
                                new OptionData(OptionType.STRING, "причина", "Причина исключения", false)
                        ),
                Commands.slash("ban", "Блокирует участника на сервере")
                        .addOptions(
                                new OptionData(OptionType.USER, "пользователь", "Пользователь для блокировки", true),
                                new OptionData(OptionType.STRING, "причина", "Причина блокировки", false),
                                new OptionData(OptionType.INTEGER, "дни", "Удалить сообщения за последние дни (0-7)", false)
                        ),
                Commands.slash("unban", "Снимает блокировку с пользователя")
                        .addOption(OptionType.STRING, "пользователь", "ID или тег пользователя", true)
                        .addOption(OptionType.STRING, "причина", "Причина разблокировки", false
                        ),
                Commands.slash("slowmode", "Устанавливает медленный режим в канале")
                        .addOption(OptionType.INTEGER, "секунды", "Задержка между сообщениями (0-21600)", true
                        )


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
        System.out.println("✅ Команды зарегистрированы!");
    }
}