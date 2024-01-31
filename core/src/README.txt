Hi Dear fellow game Lover,

I hope you will enjoy to explore our game, although it is simple we have put our blood, sweat and tears into it.
And are quite pleased with the results, but most importantly the lessons learned on the way.

There are two modes to play the game. Play the 5 Level Campaign via the "Go to Game" button, and find victory at the end.
Or load your very own map. We have created a very hard one that you can try, in the assets folder. Try to load it via the
"Load new Game" button.

In the game it is your goal to reach the treasure chest to obtain a key. Once you touch the chest the Heads Up Display (HUD)
will show you that you have obtained the key. Since you are in a haunted underground maze there are ghosts and traps everywhere.
Lucky that you brought a sword. So you can beat the ghosts and demolish the traps. The traps have many health points, so you need
to beat them a couple of times. To use your sword press the "A" key, the controls are also shown on the Menu screen.

Once you have collected the key, you can open any of the available doors and move on to the next Level in campaign mode, only the
last door will bring you to a celebratory victory screen.

We hope you like our sound effects, haunted music, and animations. We tried to make the chest and door animations nice. And we tried to make the
camera movements smooth and pleasing.
The ghosts are not very smart as enemies but their random movement is quite hard to predict. Especially in the final map.
You can collect score points for every map. Although lives always reset back to 3 after each level. To make it easier to win.

Btw, we intentionally left the visible red hitboxes inside the game, these are for your convenience to see the collision detection at work.

When we stitched the levels together for the first time, it was really fun to play the game.

ONE IMPORTANT TIP FOR PLAYING, to reach the necht level the player has to touch the bottom of the door Sprite. There is a small collision rectangle.


######################################
#### Class Hierarchy #################
######################################

Let's talk a bit about the class hierarchy. There is the Game, the Screens, the Map Class and the Entities.
The map has two functions. It sets the ambience, and it is also a collision layer for all the movable entities.
We chose a tiled map, and made the wall tiles "blocked" so no entity can move through them. The Exit points also behave like walls
until you obtain the key. then their "blocked" property is removed.

The entities have a interesting inheritance structure. The player inherits directly from the Entity class. Meanwhile, the ghosts inherit
from an intermediate class called Enemy, which could be used for future extensions with zombies and spiders. So far there are only ghosts.
The traps are adversaries too, but they don't move, so they inherit also directly from the entity. Also Treasure Chests and doors are entities.
That may be funny in the beginning, but it makes sense. Like ghosts and traps the player can also destroy chests and doors. But we gave them lots of health points.
There are also helper classes which helped us with parsing the map files "PropertiesFileParser", and creating tilesets with blocked tiles "TilesetUtils", and "MapUtils"
for testing collisions on maps.

For making the animations we also coded lots of supportive documents. Like an Enum which is called "Direction" which remembers the players last direction,
to choose the next appropriate "fighting" animation. There is a dedicated animation for killing the ghosts, they blow up into a dust cloud.

Last but not least the HUD is important.
As you can see we clearly separated the functions of the code documents and classes. And used inheritance where it made sense. You will notice some code duplications in parts
but all in all we tried to encapsulate much, like animation properties. speed, damage etc.


######################################
#### Beyond the Minimum ##############
######################################

The requirements beyond the minimum, is probably the sword fighting. We think it is very satisfying to go ghost hunting in the maze, and to demolish traps.
So don't forget to spam that "A" button when running through the maze.

For more detailed information please consult JavaDoc!





