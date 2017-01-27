# BlankCanvas - the plugin

This plugin is a blank canvas for your plugin.

It offers:

1. A framework for an empty plugin
2. A sample of how to use configuration based on a default config file stored in the Plugin jar file.
3. A checkstyle build task (to check your Java code style)
4. 
## How to build BlankCanvas


1. `git clone https://github.com/sibomots/BlankCanvas.git`
2. `cd BlankCanvas`
3. `gradlew setupDecompWorkspace --refresh-dependencies`
4. `gradlew build`


## Supar Fast Way to Test (Without an IDE)

1. Setup a Forge Server
2. Drop a release build of Sponge into the mods folder
3. Drop your BlankCanvas plugin into the mods folder
4. Start the server.

ELSE, if you want to DEBUG with SpongeForge in place in  IDEA, then read
on:

## How debug/run within IDEA (SpongeForge + BlankCanvas)

Follow the directions on GitHub for SpongePowered.  Especially these steps:

1. `git clone --recursive https://github.com/SpongePowered/SpongeForge.git`
2. `cd SpongeForge`
3. `cp scripts/pre-commit .git/hooks`
4. `gradlew setupDecompWorkspace --refresh-dependencies`
5. `gradlew build`

Now, Start InteliJ IDEA.

Choose `Import Project`
Select the `build.gradle` for `SpongeForge` 

IDEA will take some time to configure.  Once it is settled:

Choose: `File > Project Structure > Modules`

Press `+`

Choose the `build.gradle` of `BlankCanvas`

Press `OK`

IDEA will take some time to configure; once it is settled:

We need to configure the Run/Debug sessions.  Do not use the advice on the SpongePowered site to have gradle do it.

Instead do this:

1. `Run > Edit Configurations`
2. Choose Application from left panel
3. Give it a name `SpongeServer`
4. Main class: `GradleStartServer`
5. VM options: `-Dfml.coreMods.load=org.spongepowered.mod.SpongeCoremod`
6. Working Directory: `C:\WHATEVER_IT_IS\SpongeForge\run`
7. Use classpath of module: `SpongeForge_java6`

Press `OK`

## Test Run environment

Choose `Run SpongeServer`

You'll notice in the Console window an error regarding the EULA.

So fix that with an editor (set false to true, if you agree to the EULA)

Re-run `SpongeServer` (or choose to Debug, your choice)

The server should start up and you will see spew in the 
Console window area of the IDEA GUI.  

When it has reached an idle state, then you can start to interact
with the server via the console commands:



`/list`

`/sponge`

`/sponge plugins`

## Add your BlankCanvas Plugin

Copy from `BlankCanvas\build\lib` the `.jar` file of your plugin.
Put it into `SOME_PATH\SpongeForge\run\mods`

Re-run your SpongeForge as before.

If you are successful, your BlankCanvas plugin will show up in the list via:

`/sponge plugins`


## Edit this to Suit

This page was made quickly. It may have errors. More importantly, it may
differ from other developer's advice.  If so, consult the `#spongedev`
channel on IRC for further information.

The morale to the story is this:

You can make a Plugin, deploy and test it yourself.   You can do it.  You
just need to read a little documentation and try a little bit of
experimentation.  Everything above was gleaned by reading, trying, and
asking questions after making an honest attempt.

Good luck.

