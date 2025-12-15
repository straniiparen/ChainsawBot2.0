package com.github.straniiparen.chainsawdj.bot;

import com.github.straniiparen.chainsawdj.commands.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class ChainsawDJ {
    private final JDA jda;
    private static final String AVATAR_URL = "https://sun2-10.userapi.com/impg/Kv8lbBfjfSRuz6wYM4TskJIguLdhHz9z_NoqsQ/u0FY6VckcY0.jpg?size=512x512&quality=96&sign=560b5ee281780786ecb298fae0127f9b&type=album";
    private static final String EMBED_COLOR = "#18dce0";

    public ChainsawDJ(String token) throws Exception {
        JDABuilder builder = JDABuilder.createDefault(token);

        // Настройка активности бота
        builder.setActivity(Activity.listening("🕶️🏋️🥚✌"));

        // Включаем необходимые интенты
        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MEMBERS
        );

        // Отключаем ненужные кэши
        builder.disableCache(CacheFlag.EMOJI, CacheFlag.STICKER);

        // Добавляем слушателей
        CommandHandler commandHandler = new CommandHandler();
        builder.addEventListeners(commandHandler);

        // Собираем JDA
        this.jda = builder.build();
        this.jda.awaitReady();

        // Регистрируем команды
        commandHandler.registerCommands(jda);

        System.out.println("ChainsawDJ успешно запущен!");
        System.out.println("Бот: " + jda.getSelfUser().getAsTag());
        System.out.println("Серверов: " + jda.getGuilds().size());
    }

    public static String getAvatarUrl() {
        return AVATAR_URL;
    }

    public static String getEmbedColor() {
        return EMBED_COLOR;
    }

    public JDA getJDA() {
        return jda;
    }
}