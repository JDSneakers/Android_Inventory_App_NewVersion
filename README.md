# Android Inventory App New Version

## Describe the Artifact
The artifact I chose for the software design and engineering part is an Android inventory management app. It requires the user to create a login that allows them to access the inventory app. Once logged in the user has multiple categories in which they can store various inventory items. The app allows the user to create, edit, and delete categories and items once inside each category. I initially created it in my CS-360 course in October of 2022.
## Justify the Inclusion
The reason I chose this artifact to include in my ePortfolio is it demonstrates three skills, my ability to develop an Android app, proficiency in Java as well as object-oriented programming, and my ability to code using XML. Through these three key skills, I also can demonstrate my ability to handle both frontend and backend development. I made many improvements to my original design to improve not only the overall functionality but to improve the overall aesthetic as well. As for aesthetics, I changed the category grid layout to have a span of three columns instead of two to increase the overall real estate available to display categories on a single page without the need for the user to have to scroll through categories while still keeping the logic to allow for the user to scroll through them if needed. I changed the background to have a more minimalistic look and added the background to all of the app layouts instead of just some of them to keep a cohesive design and feel for the app. I made text labels bold to increase visibility and make it easier to read. I changed the color palette used for the cards used to display the categories to better match the colors used in the app. I made a small adjustment to the overall layout bringing it up toward the top of the screen so that it looked more centered toward the top than centered on the whole screen. I also changed the logo to a more professional-looking design instead of the previous design which had a clip art look to it. As for the functionality, I created a separate registration page that the user can only access once when first setting up the app to prevent unauthorized access to the app as was possible with the previous design. I added key listeners to both the LoginActivity and NewUserActivity to prevent the user from creating a new line in the EditText fields when the user presses enter on the keyboard. I added checks to the SaveButtonClick to prevent the user from adding items without filling in all of the text fields. I added toasts to present to the user after they decide rather or not to approve SMS permissions displaying to them their choice in case they are unsure or accidentally choose the wrong choice. I also cleaned up some of the code that needed improvements such as in the CategoryActivity file line 65, where I opted to simplify an if-else statement by instead negating the original if statement parameters so that I could move the logic from the else statement into the if statement and removed the else statement altogether. I also removed functions and imports that were unused relating to the toggle visibility function that is no longer needed and an unused action bar import and function in the ItemEditActivity.
## Justify the Inclusion
The biggest challenge I faced initially was getting reacquainted with the many aspects of the app, all of the files, and how they interact but after spending a bit of time working with it and reviewing some documentation I was up to full speed and able to pick up momentum to enhance the app. The biggest takeaway I learned from creating and improving the Andriod app was the importance of regular practice using the skills I have learned in my software and engineering degree makes a huge difference in my ability to pick up not only where I left off on an older project but how this will be key in my future career endeavors as I will face code that I did not develop and will have to catch up to speed with code I have not worked on before.

## Navigating the App
When first opening the app for the first time the user will be asked to create a user login. For security, this will be the only time the user will be able to create a user login on their device (store your login somewhere safe!). From there the user will be taken to the login screen where they can log in. Once logged in there are already three default categories for inventory items to be stored in. For ease of management with large inventory databases there is an option menu in the top right-hand corner that allows the user to filter categories either alphabetically, or in ascending or descending order based on the time of the category's creation. Once inside one of the categories, the user can add, edit, delete, or cycle through inventory items. 

## Register User Screen
![RegisterUser](https://github.com/JDSneakers/Android_Inventory_App_NewVersion/assets/79832547/3769dc2b-dcad-4716-a545-c735b85f75d5)
## Login Screen 
![LoginScreen](https://github.com/JDSneakers/Android_Inventory_App_NewVersion/assets/79832547/0b60e272-d255-4426-95fe-660394c0daa2)
## Category Screen 
![CategoryScreen](https://github.com/JDSneakers/Android_Inventory_App_NewVersion/assets/79832547/9b36ba58-f02e-4c11-9f3a-45e24bce19b1)
## Item Screen Without Items
![ItemScreen](https://github.com/JDSneakers/Android_Inventory_App_NewVersion/assets/79832547/2527d457-ef93-43d4-b710-69b374dbf4e4)
## Item Screen With Items
![ItemScreenWithItem](https://github.com/JDSneakers/Android_Inventory_App_NewVersion/assets/79832547/e8e0ee52-862c-4aba-a151-5116943d66af)

## Course Outcomes Met Through the Enhancments of the Android Inventory Management App
In completing the coursework for my software and engineering enhancement I was able to develop and showcase the strengths I have built in the computer science program by meeting all five of the expected course outcomes. I employed strategies for building collaborative environments that enable diverse audiences to support organizational decision-making in the field of computer science by completing enhancements such as making the app more user-friendly and visually appealing, by transforming the category grid layout to a three-column span from the original two and applying a minimalistic design philosophy to the app's background across all layouts. This ensured a coherent design language that is inclusive to a diverse audience. I designed, developed, and delivered professional-quality oral, written, and visual communications that were coherent, technically sound, and appropriately adapted to specific audiences and contexts by optimizing user feedback mechanisms, and incorporating toasts to clarify user decisions regarding SMS permissions, ensuring they were aware of their choices. I designed and evaluated computing solutions that solved a given problem using algorithmic principles and computer science practices and standards appropriate to their solution while managing the trade-offs involved in design choices by incorporating key listeners into both the LoginActivity and NewUserActivity. This hinders the creation of a new line in EditText fields when users press enter, improving the user interface and preventing unwanted input demonstrating my ability to discuss experiences and best practices in designing and evaluating computing solutions. I demonstrated an ability to use well-founded and innovative techniques, skills, and tools in computing practices to implement computer solutions that deliver value and accomplish industry-specific goals by simplifying an if-else statement in the CategoryActivity file and removing redundant functions and imports, which were no longer required, streamlining the code and enhancing its efficiency and demonstrating my ability to create industry-standard software design. I developed a security mindset that anticipated adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources by designing a one-time access registration page. This significant enhancement mitigates potential unauthorized access, which was a vulnerability in the previous design, and greatly enhances the app's security features.