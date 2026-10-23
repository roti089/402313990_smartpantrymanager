Smart Pantry Manager
Make more of what you already have.
About the Project
Smart Pantry Manager is an Android app written using Java to help users manage their kitchens on a daily basis. It provides the opportunity to monitor ingredients that one possesses and find recipes that can be cooked without any guessing about existing ingredients.
The basic concept of the app is very simple – a recipe must be proposed to the user only if he/she has all necessary products for its creation. Thus, the app takes into account all requirements of the recipe (list of ingredients and their amounts).
The recipes database includes home-made dishes, economically viable recipes and simple international dishes.
What Makes It Useful?
An organized pantry
Users can create pantry entries, alter amounts, change descriptions, and delete the ingredients that are no longer required. An ingredient may be added with an associated category, unit and expiry date.
Recommendations based on available ingredients
Recipe matching process makes comparisons between ingredients available in users' pantry and those required by recipes. A recipe would appear in the recommended list only when all the required ingredients are available in required quantities and units.
For instance, having 100 g of chicken would not make a recipe that needs 200 g of chicken qualified for inclusion in recommendations. Similarly, absence of an ingredient disqualifies the recipe.
Recipe having detailed instructions
A recipe may consist of name, category, estimated preparation time, complexity, ingredients and preparation method. The users may have access to the recipe list even without using their pantry contents.
Preferences and persistence
The application has a settings section to store available preferences and the pantry. SQLite database offers persistence facility that helps keep the records in users' pantries even after closing the application.

Built With
Technology	Purpose
Java	Application logic and functionality
Android Studio	Development environment
XML	Screen layouts and interface design
SQLite	Local database storage
RecyclerView	Displaying pantry items and recipes
Android Intents	Moving between application screens
Git and GitHub	Version control and project hosting

Organization of the Application
The application divides all responsibilities related to data handling, screen behavior and recipe analysis into separate classes.
• MainActivity – Main class representing the pantry interface.
• PantryIngredient – Class which represents an ingredient and stores all related information.
• SmartPantryStorage – Class managing all operations with the SQLite database and pantry.
• PantryInventoryDisplay – Displays items from the pantry and allows interaction with the items.
• IngredientEditorActivity – Ingredient creation and editing.
• RecipeCatalog – Recipe catalog creation and accessing.
• RecipeMatcher – Matching the availability and quantity of ingredients.
• RecipeSuggestionsActivity – Displays recipes which passed the matching check.
• RecipeSuggestionAdapter – Interface displaying the recipe suggestions.
• Recipe and RecipeIngredient – Classes representing recipe and their ingredients respectively.
• RecipeDetailActivity – Displays details about the selected recipe.
• PantryPreferencesActivity – Application preferences management.
Database
SQLite database contains three main tables.
pantry_ingredients
Contains user ingredients which include ingredient name, quantity, units, category and expiration date.
recipes
Contains all the information about recipes such as name, category, description, preparation time, difficulty and instructions.
recipe_ingredients
Contains all the individual ingredients that go into making the recipes, along with how much is needed in quantity.
Breaking down the information about the recipes from the ingredients enables the storage of different requirements for one recipe.
The Matching Recipe Rule
The first rule of Smart Pantry Manager states that a suggested recipe has to be feasible with respect to the pantry ingredients recorded in the application.
Matching algorithm
1.	Reading the ingredients stored in the pantry.
2.	Loading the recipes and their ingredients.
3.	Matching ingredient names by means of text normalisation.
4.	Converting compatible units of measure, when supported.
5.	Checking that the amount available is enough to cover the requirement.
6.	Returning recipes that passed all previous steps.
If a recipe fails in meeting any requirement, then it will not be included in the suggested recipe set. If there is no match at all, the application has to notify the user that he needs more ingredients or bigger amounts.
Project Information
Prerequisites
•	Android Studio
•	A suitable JDK installation for the project
•	The required Android SDK for the project
•	An Android emulator or a suitable physical Android device
Running the Project
1.	Download the project from GitHub.
2.	Open the project folder in Android Studio.
3.	Let Gradle synchronise itself.
4.	Install any missing SDK components.
5.	Specify an emulator or an Android device.
6.	Run the application.
SQLite is used as the local database in this application and does not need an external database server to work.
Verification Checklist
The following tests can be helpful for verifying the application:
•	Adding, editing, and deleting a pantry ingredient.
•	Trying to close and re-open the application and ensuring that data is saved.
•	Adding all ingredients that are needed for making a recipe and ensuring that it appears.
•	Removing one of the required ingredients and ensuring that the recipe does not appear anymore.
•	Reducing the amount of some ingredient below the recipe requirement and ensuring that it is not included in the recipe list.
•	Trying different supported unit conversions and incompatible units.
•	Browsing the recipe catalog and using category filters.
•	Viewing a recipe and looking at its list of ingredients and instructions for cooking.
•	Trying to open a recipe with an empty pantry and reading the message that appears.
Existing Scope and Improvements in Future
Automatic replacement of ingredients, validation of food quality, and diet related information cannot be found in current scope. These features have to be developed separately.
Improvements in future may contain alerts about expired ingredients, shopping list for missing ingredients, dietary filters, extra recipes for different areas, etc.
Project Information
Application: Smart Pantry Manager
Platform: Android
Language: Java
Database: SQLite
Project Type: Mobile Application Development
Created as an academic assignment to demonstrate skills in Android application development, database design, CRUD operations, interface design and application logic.
Licence and Third Party Sources
The third party sources such as libraries, icons, images and other material used in this project are still bound by their respective licences. The source code repository should state licences applicable to external sources included.
