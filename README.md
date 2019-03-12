Slurpuff by SudoTrainer based off of gb-rta-bruteforce by stringflow
==========
#### Full Credits and Acknowledgements 

This version of gb-rta-bruteforce has been forked directly from stringflow and the majority of this code base is his creatation and hardwork along with previous botters in Pokemon Speedrunning. I would not have any hope of doing what I am doing without his hard work and passion. Thank you stringflow.

#### Overview

This is a brutefore bot program meant to find any encounter in gen 1 Pokemon without any programming experience. 

It runs a modified [GSR's libgambatte](https://github.com/gifvex/gambatte-speedrun) as a core, and spits out giant log files to be combed over manually.

#### Goal

I want to make a program anyone can download and search for an encounter manip (or no encounter manip) with NO programming experience. So the user experience looks something like:
- You select the game you are botting (Red/Blue/Yellow gen 2 coming soon)
- You select the pokemon you are trying to find
- You upload some saves you want to search (starting tiles for the manip)
- You enter some coordinates in the format of 61#13,13 where you want the bot to get the encounter on. You get these using: https://www.extratricky.com/pokeworld/rb/61#13,13
- You enter the min dvs you care about 
- You enter if you care about yoloball
- You enter if you have a min IGT sucessrate

Then you just run it and it will print all the found manips into a text file

#### TODO

Current state of this project: https://github.com/SudoProgramming/gb-rta-bruteforce/projects/1

#### Installation for Windows

Build a `libgambatte.dll` (Instructions can be found [here](https://github.com/gifvex/gambatte-speedrun))

Clone the repository, create a new folder called `libgambatte` and put the previously built dll in there.

After that fire up your favorite Java IDE, add the JNA library to the Classpath and start using it.
