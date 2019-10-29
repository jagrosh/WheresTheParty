/*
 * Copyright 2019 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.hackweek;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONObject;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class DiscordHandler implements EventListener
{
    private static int servercount = 0;
    
    private final String website;
    
    public DiscordHandler(String website)
    {
        this.website = website;
    }
    
    @Override
    public void onEvent(GenericEvent event)
    {
        if(event instanceof GuildMessageReceivedEvent)
            onGuildMessageReceived((GuildMessageReceivedEvent) event);
        else if(event instanceof GuildJoinEvent || event instanceof GuildLeaveEvent || event instanceof ReadyEvent)
            onGuildTotalChange(event);
        else if(event instanceof MessageReceivedEvent)
            onMessageReceived((MessageReceivedEvent) event);
    }
    
    private void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if(event.isWebhookMessage())
            return; // ignore unknown users and webhooks
        SocketHandler.sendToAllSessions(new JSONObject()
                .put("user", new JSONObject()
                        .put("id", event.getAuthor().getId())
                        .put("name", event.getAuthor().getName()+"#"+event.getAuthor().getDiscriminator())
                        .put("avatar", event.getAuthor().getEffectiveAvatarUrl()))
                .put("channel", new JSONObject()
                        .put("id", "#"+event.getChannel().getId())
                        .put("name", event.getChannel().getName()))
                .put("guild", new JSONObject()
                        .put("id", event.getGuild().getId())
                        .put("name", event.getGuild().getName())
                        .put("icon", event.getGuild().getIconUrl()))
                .toString());
    }
    
    private void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getAuthor().isBot())
            return;
        if(!event.isFromGuild() || 
                (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()) 
                && event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_WRITE)))
        {
            event.getChannel().sendMessage("\uD83C\uDF8A Hey there! You can find out where the party is on "+website).queue(); // 🎊
        }
    }
    
    private void onGuildTotalChange(GenericEvent event)
    {
        ShardManager sm = event.getJDA().getShardManager();
        servercount = sm == null ? 0 : (int) sm.getGuildCache().size();
        SocketHandler.sendToAllSessions(new JSONObject().put("stats", new JSONObject().put("servers", servercount)).toString());
    }
    
    public static int getLastKnownGuildCount()
    {
        return servercount;
    }
}
