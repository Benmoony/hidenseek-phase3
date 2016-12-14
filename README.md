Hide N Seek - Phase3
================

# Overview
The Hide N Seek application (app) runs on devices with Android versions 19 (KitKat) and up.
Hide N Seek initial development was in the Spring of 2014. The original idea was
Aaron Campbell's and with the help of Barbara Greenlee they created the base foundation
of the game. The app received additional updates by Todd Taylor, Eric Sergio, Tao Hong in
the Fall of 2014.  This third round was worked by Deborah Engelmeyer, Benjamin Miller,
Zin Maung, and Debra Ward in the Fall of 2016.

This mobile game adds a new twist to the traditional game of Hide and Seek.  In the
traditional game, a Seeker counts down from 10 while the other players (Hiders) hide nearby.
When the Seeker finishes the countdown, he calls out “Ready or not, here I come!” to notify
the Hiders the seek part of the game has begun.
When the hiders are located, they are tagged out, and the game restarts.

Because the mobile app is part of the game, the Seeker gets a Hot or Cold clue for the
location of the hiders, and the hiders all know when the game is over.  The game can also
cover a larger area because the players do not have to hear the Seeker.

The game makes use of GPS technology and Google maps to show the location of players when
one is found.  The game also notifies the players when the game ends.

Hide N Seek also provides a Sandbox mode where all the players are shown on a map.
This may be used to keep track of members of a group in a shopping center or park.

# Audience
* School students: Great game for junior high and other students. The supervisor mode would
enable a designated adult to monitor the game and the locations of the students.
* Recreationists: Campers and other outdoor recreationists may find hide-n-seek to be a
fun activity. Additionally, groups of hikers and such could use the sandbox gameplay to
keep track of all members
* Tourists and shoppers: When touring and shopping in a group, the sandbox gameplay would
 allow users to keep track of the rest of the group. Standard gameplay would provide an interesting twist, combining the fun of browsing shops with the fun of traditional hide and seek.

# Use Cases
##Case 1 - Start a Game
1. The user starts the app and taps “Host Match”.
1. The user is then presented with a screen to enter the Match name, type, password, and the player’s (Seeker’s) name.
1. The user is shown a screen with the Match name and fields to enter hide time and search time.  The screen also shows the list of players that have joined the match.
1. The user selects “Begin Match” to start play.
1. A screen loads with the “Hide” countdown timer.
1. The “Hide” timer ends and the “Seek” timer begins.
1. The Seeker is shown a list of Hiders with an indication of how close they are to the him/her.  Blue being far away and Red being very close.

##Case 2 - Join a Game
1. The user starts the app and selects “Join Match”
1. The user is then shown a list of available matches to join.  User selects desired match and enters password.
1. User sees list of players in match until it begins.
1. When the game begins, a map is shown with the Hider’s location and a countdown timer for the Seek portion of the game.

## Case 3 - Seeker finds a Hider
The game is in the “Seek” portion and the Seeker has located a Hider.
1. The Seeker selects the Hider in the player list and marks the Hider as found.
1. The Hider gets a confirmation prompt and verifies the find.
1. The Hiders name is removed from the list or moved to the bottom and marked as found.

## Use Case 4 - Hider When another Hider is Found
Another Hider has been discovered by the Seeker.  The Seeker has marked the Hider as found, and the Hider has acknowledged the find.
1. This Hider is shown the location of the Seeker and other Hiders on the map for 10 seconds.
1. Then the map returns to showing just the location of the player.

## Use Case 5 - Seeker End of Game
Seeker has found the Hiders or “Seek” timer has expired.
1. Seeker score is displayed, or perhaps list of found players.
1. Screen returns to “Create” or “Join” selection screen.

## Use Case 6 - Hider End of Game
Hider is skilled at hiding (or lucky), and has not been discovered before the “Seek” timer has expired.
1. Hider is notified by a sound that the timer has expired and awarded points for not being discovered.
1. Screen returns to “Create” or “Join” selection screen.

## Use Case 7 - Group Lead Starts Sandbox
User is a teacher that wishes to keep track of a group of students on a field trip to the zoo.
1. The user starts the app and selects “Host Match”.
1. The user is then presented with a screen to enter the Match name, type (Sandbox), password, and the player’s (Seeker’s) name.
1. The user is shown a screen with the Match name.  The screen also shows the list of players that have joined the match.
1. The user selects “Begin Match” to start.
1. The user is shown a map with the location of all the players.  Each player on the map has an icon or similar marking to distinguish them.  Tapping on the player icon can show information about the player.
1. When the players need to reconvene, the user selects “End Match”.

## Case 8 - Join a Sandbox
1. The user starts the app and selects “Join Match”
1. The user is then shown a list of available matches to join.  User selects desired match and enters password.
1. User sees list of players in match until it begins.
1. When the sandbox begins, a map is shown with all the Player’s locations.
1. At the end of the match, the user is notified with a sound and a notification to return to the rendezvous area.

# Enhancements
The complete list of proposed changes to the app can be found in the Issues list, and the label Feature was added to planned changes.  The following paragraphs summarize the changes completed, partially completed, and planned for the app.

## Completed
* Update the Google Maps interface, and position the map with the player in the center
* Remove a player from a match at the API when the player leaves
* Update the player’s location at the API
* Create separate layouts for Seekers and Hiders in the Hide N Seek mode, where the Seeker sees a list of hiders, and the Hiders see a map
* Notify the user that internet connection is required rather than crash

## Partially Completed
* Show both Seeker and Hiders countdown timers for hiding and seeking
* Show Seeker cold and hot indications for each hider

## Planned
* Show Seeker and found hider positions to remaining hiders for a period of time, probably the hide time
* Sort the list of hiders shown to the Seeker by found/not found and closeness
* Use a floating action button to join or start a match
* Allow the game organizer to send notifications to potential players to join the game, the player would then get a notification and could join the game from the notification
* Show the location of other players on the map in Sandbox mode
