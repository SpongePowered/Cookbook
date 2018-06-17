# BlankCanvas - the plugin

This plugin is a blank canvas for your plugin.

It offers:

1. A framework for an empty plugin
2. A sample of how to use configuration based on a default config file stored in the Plugin jar file.
3. A checkstyle build task (to check your Java code style)

## How to build BlankCanvas

One method to build it is just to navigate to the Plugin folder and run `gradlew build`


1. `git clone https://github.com/SpongePowered/Cookboook.git`
2. `cd Plugin`
3. `gradlew clean build`


## Debugging Sponge Plugins

In general there are work-flows that have been documented
by the Sponge Powered team to show you the best practices
for debugging Sponge Plugins.  
[SpongePowered Plugin Debugging](https://docs.spongepowered.org/master/en/plugin/debugging.html)


## How debug/run within *IDEA* (SpongeForge + BlankCanvas)

For those who do work in IDEA, there are some additional notes to consider.

Refer to the SpongePowered documentation:

[Walk-throughs for IDEA and Sponge Plugin Debugging](https://docs.spongepowered.org/master/en/plugin/tutorials.html)

Follow the [directions on GitHub for SpongePowered.](https://github.com/SpongePowered/SpongeForge)


1. `git clone --recursive https://github.com/SpongePowered/SpongeForge.git`
2. `cd SpongeForge`
3. `cp scripts/pre-commit .git/hooks`
4. `gradlew setupDecompWorkspace --refresh-dependencies`
5. `gradlew clean build`

Now, Start InteliJ IDEA.

Choose `Import Project`

Select the `build.gradle` for `SpongeForge` 

IDEA will take some time to configure.  Once it is settled:

Choose: `File > Project Structure > Modules`

Press `+`

Choose the `build.gradle` of `BlankCanvas`

Press `OK`

IDEA will take some time to configure; once it is settled:

We need to configure the Run/Debug sessions.  There is a Gradle task 
to initalize the run-configurations. 

The task is genIntellijRuns.  
You will find it under View > Tool Windows > Gradle
Then:  Tasks > forgegradle > genIntellijRuns

The dialog box will let you choose the "Use classpath of module"
drop down.  Pick `SpongeForge_java6`

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

There are several ways to deal with testing in this manner. One 
method is straight-forward and involves making your Plugin build
via Artifact.  Change the destination path to the `run\mods` folder
of your local development server (the SpongeForge you just setup
in IDEA)

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

