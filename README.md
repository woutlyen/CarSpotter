# Java application assignment: *CarSpotter*
Made by:
* Wout Lyen
* Jonathan Valgaeren
## Inspiration
The original idea for the application was a form of wikipedia, but all about cars. However, we have changed to a more user-interactive application. The main goal of the app to share sightings or 'spots' of cars with others. We have retained the wiki side of the app, where the user is able to look up information about the spotted cars. We also added an event feature for even more user-interaction.
## Functions
* Home
    Upon opening the app the user will find themselves on the home page. Here they can browse through the latest spots, wikis and events. At the bottem of the app they can find a navigation bar, where they can decide to jump to the spotter or to events.
* Spotter
    Here the user is able to look up any car they'd like and, if it is not available, add it. Once a model is selected, information of the specific car model will be presented. At the bottem of this page, there is a 'Spots' button. When clicked upon, they can scroll through all the times the car has been spotted with information on time and location. From this list there are two possible choices:
    - At the bottem of the 'Spots' page the user can select to add a spot, which will open a menu to upload an image of the spotted car and a button to save the location of the user.
    - When a certain 'spot' is selected, a heatmap will open of all the spots. The map will also be focused on the selected 'spot', indicated with an extra marker.
* Events
    The right tab of the navigation bar takes the user to a new window where they can scroll through upcoming events. They have the option to look for specific events using a search bar or filter based on how soon the events will take place (within a week, month or year). At the bottem of this page, the user can again decide to upload their own event.
    When a specific even is selected, information on the type of event, fee and time can be found above a description of the event itself.

## Possible expandability
* User functionality: login and password, enlist for events, user information in list of spots
* Add location to the events.

## Important note
Incase there might be any issues during testing, you might have to add the google API key to local.properties. (MAPS_API_KEY=AIzaSyBL5W5Y_tALAcsXnJOnK45CAhuOp4kIUio)
