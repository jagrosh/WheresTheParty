<img align="right" src="https://cdn.discordapp.com/avatars/593976871545470976/4a6dc1c53b9c5b9d47917cd9624e4eb0.png?size=1024" height="200" width="200">

# 🎊 Where's the Party?

So... where's the party? This bot will help you find out! How? By visualizing where people are posting messages _right now!_ 

Take a look at a demo of this bot over at **<https://party.discord.website>**, or host your own [see below]!

![Example Loading...](https://i.imgur.com/gz8bAKP.png)

### Hosting
1. Download and build this repository
2. Install Java and Maven (if you don't have them installed)
3. Build by running `mvn clean install`
4. Create an `application.conf` file, filling in the blanks below:
```
bot {
    token = "BOT TOKEN HERE"
    clientid = "CLIENT ID HERE"
    game = "Hackweek!?"
}

web {
    static = "STATIC FILES FOLDER HERE"
    port = 8080
}
```
5. Run with `java -Dconfig.file=application.conf -jar Hackweek-0.1-All.jar`
6. Visit `localhost:8080` in your web browser!

This project was created for [Discord Hack Week 2019](https://blog.discordapp.com/discord-community-hack-week-build-and-create-alongside-us-6b2a7b7bba33).