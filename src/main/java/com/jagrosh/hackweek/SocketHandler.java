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

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
@WebSocket
public class SocketHandler
{
    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();
    private static final Logger LOG = LoggerFactory.getLogger("Sockets");
    
    // I'm not a huge fan of using statics like this, but Spark is static which means the path of least resistance
    // is to make other things conform in a simple way. Using statics for this is not ideal, but should be fine since
    // it's unlikely that someone would be trying to run two of this website in a single JVM somehow
    public static void sendToAllSessions(String message)
    {
        SESSIONS.forEach(s ->
        {
            try
            {
                s.getRemote().sendString(message);
            }
            catch(IOException ex)
            {
                LOG.error(ex.toString(), ex);
            }
        });
    }
    
    @OnWebSocketConnect
    public void connected(Session session) 
    {
        SESSIONS.add(session);
        sendToAllSessions(new JSONObject().put("stats", new JSONObject().put("sessions", SESSIONS.size()).put("servers", DiscordHandler.getLastKnownGuildCount())).toString());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) 
    {
        SESSIONS.remove(session);
        sendToAllSessions(new JSONObject().put("stats", new JSONObject().put("sessions", SESSIONS.size()).put("servers", DiscordHandler.getLastKnownGuildCount())).toString());
    }

    @OnWebSocketMessage
    public void message(Session session, String message)
    {
        // do nothing
    }
}
