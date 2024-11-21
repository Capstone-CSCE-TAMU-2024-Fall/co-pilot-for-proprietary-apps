# How to contribute

## Code Structure

**The overall codebase is split into 3 parts:**
- [com.codingassistant.codingassistantplugin](com.codingassistant.codingassistantplugin)
    - Main plugin
- [com.codingassistant.codingassistanttests](com.codingassistant.codingassistanttests)
    - For tests
- [com.codingassistant.codingAssistant](com.codingassistant.codingAssistant)
    - Feature for exporting the plugin


**The codingassistant plugin is split up into 3 main parts:**
- [TextCompletionService](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/TextCompletionService.java)
    - Handles communication between the backend, making requests and parsing responses into a usable format for the plugin
    - Calls TextRenderer with the parsed response to be displayed
    - Accept suggestion and insert code into file
    - Deny suggestion and dismiss displayed text
    - Singleton
- [TextCompletionListener](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/TextCompletionListener.java)
    - Detects when a valid keystroke is typed by the user
    - Triggers a code suggestion by calling TextCompletionService 
    - Attached to each TextEditor
- [TextRenderer](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/TextRenderer.java)
    - Graphically displays code suggestions in the current editor
    - Adjusts line spacing to make space for displayed code suggestions
    - Attached to the StyledText of each TextEditor

## Adding a new backend

1. Create a new package under src.com.codingassistant.codingassistant.connection
2. Create a new class in that package that extends the [BackendConnection](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/connection/backend/BackendConnection.java) abstract class
3. Implement the constructors and methods from the abstract class. The existing backend implementations ([Fauxpilot](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/connection/fauxpilot/FauxpilotConnection.java) and [Tabby](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/connection/tabby/TabbyConnection.java)) are great references.
4. Write tests to confirm that the requests and responses are being parsed correctly, adding them to a new class the [com.codingassistant.codingassistanttests.connection](/com.codingassistant.codingassistanttests/src/com/codingassistant/codingassistanttests/connection/) package.
5. Add the fully qualified name of the new class to backendOptions in [PluginPreferencePage](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/preferences/PluginPreferencePage.java#L42)

If your backend needs additional information from the editor, the method that handles making the request to the backend is located in [TextCompletionService](/com.codingassistant.codingassistantplugin/src/com/codingassistant/codingassistant/TextCompletionService.java#L153).

## TODO
- Fix suggestion not being inserted when pressing TAB after typing whitespace on an empty line 
- Handle cases for replacing code on the same line instead of only being able to insert code suggestions
- Feature project lacks a category for some reason
- Implement more advanced Tabby support using the Language Server Protocol (LSP)

If you're interested in contributing, any of these tasks would be greatly appreciated! You can create a new issue for discussing it if an issue for it does not already exist. Feel free to tackle any of the other open issues as well. 

## Pull Requests
You can make a draft pull request for any code that you're still working on.

When ready, request review from one of the team members ([Your Cheese](https://github.com/Your-Cheese) is likely to respond) and we can discuss if anything should be changed. After it is approved, it can be merged into the dev branch and later merged into main.


# Building & Development

## Project Environment
- Source Control
    - Git
- Development Environment
    - Eclipse PDE
- Language Version
    - Java 21

## Set Up
- Clone the repository
- Create a new branch off the dev branch
- Do your work
- Create PR back onto dev branch

## Installation

Install Eclipse PDE

Install Java >= 21

Clone this Github repository

## Execute Code

1. Open up the project in the Eclipse PDE
2. Click Run
3. A new Eclipse window will launch with the plugin installed

## Tests

1. Open up the [codingassistant](com.codingassistant.codingassistantplugin) plugin project in Eclipse
2. Navigate to [codingassistant.target](com.codingassistant.codingassistantplugin/codingassistant.target)
3. Click Start Target or Reload Target, which is in the upper right corner
4. Open the [codingassistanttests](com.codingassistant.codingassistanttests) plugin project
4. Navigate to a file to test and click Debug As -> JUnit Test or JUnit Plugin Test

## Deployment
1. Right click on the [codingassistant](com.codingassistant.codingAssistant) feature project and click Export...
2. Select Plug-in Development -> Deployable features and click Next
3. Select the Archive file option
4. Click Finish