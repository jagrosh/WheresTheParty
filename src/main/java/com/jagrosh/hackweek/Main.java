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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.EnumSet;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import spark.Spark;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Main
{
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        Config config = ConfigFactory.load();
        new DefaultShardManagerBuilder(config.getString("bot.token"))
                .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE))
                .addEventListeners(new DiscordHandler(config.getString("website")))
                .setActivity(Activity.playing(config.getString("bot.game")))
                .build();
        Spark.staticFiles.externalLocation(config.getString("web.static"));
        Spark.port(config.getInt("web.port"));
        Spark.webSocket("/ws", SocketHandler.class);
        Spark.get("/invite", (req, res) -> 
        {
            res.redirect("https://discordapp.com/oauth2/authorize?client_id=" + config.getString("bot.clientid") + "&permissions=1024&scope=bot");
            return null;
        });
    }
}
