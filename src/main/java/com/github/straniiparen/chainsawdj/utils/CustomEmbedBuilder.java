package com.github.straniiparen.chainsawdj.utils;

import com.github.straniiparen.chainsawdj.bot.ChainsawDJ;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class CustomEmbedBuilder {
    private static final Color DEFAULT_COLOR = Color.decode(ChainsawDJ.getEmbedColor());

    public static EmbedBuilder createBaseEmbed() {
        return new EmbedBuilder()
                .setColor(DEFAULT_COLOR)
                .setAuthor("ChainsawDJ", null, ChainsawDJ.getAvatarUrl());
    }

    public static EmbedBuilder createMusicEmbed(String title, String description, User user) {
        return createBaseEmbed()
                .setTitle(title)
                .setDescription(description)
                .setFooter("Запросил " + user.getName(), user.getAvatarUrl());
    }

    public static EmbedBuilder createQueueEmbed(String currentTrack, java.util.List<String> queue, User user) {
        EmbedBuilder embed = createBaseEmbed()
                .setTitle("Очередь воспроизведения")
                .setDescription("Сейчас играет: " + currentTrack)
                .setFooter("Запросил " + user.getName(), user.getAvatarUrl());

        if (queue.isEmpty()) {
            embed.addField("Очередь пуста", "Добавьте треки с помощью /play", false);
        } else {
            StringBuilder queueList = new StringBuilder();
            int limit = Math.min(queue.size(), 10);

            for (int i = 0; i < limit; i++) {
                queueList.append(String.format("%d. %s\n", i + 1, queue.get(i)));
            }

            if (queue.size() > 10) {
                queueList.append(String.format("\n...и ещё %d треков", queue.size() - 10));
            }

            embed.addField("Следующие треки:", queueList.toString(), false);
        }

        return embed;
    }

    public static EmbedBuilder createEmptyQueueEmbed(User user) {
        return createBaseEmbed()
                .setTitle("Очередь:")
                .addField("Пусто!", "(Ничего нет)", false)
                .setFooter("Запросил " + user.getName(), user.getAvatarUrl());
    }

    public static EmbedBuilder createNotConnectedEmbed(User user) {
        return createBaseEmbed()
                .setTitle("Бот не подключен к каналу!")
                .setDescription("Используйте /play")
                .setFooter("Запросил " + user.getName(), user.getAvatarUrl());
    }
}