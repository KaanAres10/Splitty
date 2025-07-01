# Splitty
## Description
Splitty is an application which purpose is to facilitate financial organization in a group of people. This is basically achieved by creating an event and adding participants to it. Every participant is able to create an expense and add participants that owe him money. Also every participant can send invite code for the event, mark debts settled, see all expenses in the event etc. Every user is able to set up its own application by choosing between some common languages and choosing server.
##  Setup
1. Clone the repository
> run `git clone https://github.com/Aduomas/splitty/` in the app directory

2. Build the app
> run `./gradlew build` in the app directory

3. Run the server
> run `./gradlew bootRun` in the app directory

4. Run the client (no need to setup openjfx, since javafx dependency is included in gradle.build in the client)
> run `./gradlew run` in the app directory

## Config

- The application uses `user.json` file to store the data for the current user. If you want ot create a new user just delete the file and start the app again.
- `config.properties` file contains language and current server, if current server IP is not reachable, the app will not launch.
- `h2-database` files contain the database - delete it before launching a new server if you want to reset database.
- Starting the server will provide a code in the terminal, which can be used as a password for logging in the application as an admin. This can be done through a settings button, located on the top-right on the start page.
- Recommended way to launch multiple clients:
> clone the repo twice in different folders, and launch seperately.
- Recommended way to launch multiple servers:
> clone the repo twice in different folders, and launch servers with different ports (defined in servers/resources/application.properties - server.port=#)

## Keyboard shortcuts
- `arrows` for moving in the app
- `enter` for performing button press on selected button (the button can be accessed by navigating with the arrows)
- `escape` for going back in pages, and for exiting the program in start screen page.
- `backspace` for going back in some pages.

## Long-polling and websockets
- Example for long-polling usage can be observed by running two clients on the same server. Go with both on the ``event overview`` of the same event. On one of them try to ``add a valid new participant``. The ``event overview`` on the other client should be automatically updated. Can be seen in the list of all participants.
- Example of websockets usage can be observed by running two separate clients on the same server. Enter the same event on both and on one of them go to ``settle debts page``, with the other go to ``add a valid expense``. In the debts page the data will be updated immediately. Another example of websockets usage can be found on the ``event overview`` page. Just add a new expense on one and it will automatically show up on the other client too.

## Multi-modal visualization
- StartScreen page contains Icons which can be differentiated using Colors, Image and Tooltip. Hover on the element in the startscreen page to also see the tooltip (on hover) it provides (text) and also for Statistic Page, you can learn the percentages of expense tag by staying on the desired slice of the PieChart.

## Changing the language/serverIP
- Open the program, press the settings icon in the top right corner.
- Write your username (it is saved when you press the button to go back to start screen page)
- Change server IP (it is only changed if the client successfully connects to it upon **Connect** button press). A new user in that new server is created. You can also go back to previous server IP.
- Changing server IP manually -> config.properties (server ip without protocol and last dash) for example - `localhost:8080`.
- Language can be changed in the settings page.
- Language can also be changed in the config (config.properties), change the preffered language to 'en', 'fr', 'nl', 'de'.
