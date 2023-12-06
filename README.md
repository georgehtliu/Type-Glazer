# cs346

**Project Name:** Type Glazer

**Name:** Cindy Gu, George Liu, Ray Hao, Mark Liu

**Links**:
- [Project Proposal](https://git.uwaterloo.ca/r25hao/cs346/-/wikis/Project-Proposal)

- [Release Notes](https://git.uwaterloo.ca/r25hao/cs346/-/tree/main/releases?ref_type=heads)

- Scaffolded using https://git.uwaterloo.ca/cs346/public/sample-code/-/tree/master/project-template?ref_type=heads

**Meeting Minutes**: 

**Requirements:**

_**Problem Description**_

While typing is a crucial aspect of various professional and academic activities, there are limited avenues for targeted improvement as individuals often lack structured opportunities to practice and enhance their typing skills. In addition, the absence of a motivating factor contributes to the lack of consistent practice. Traditional methods, such as typing exercises, may feel monotonous and fail to sustain individuals' interest and commitment over time. 

The application we created - Type Glazer, addresses this issue by providing a platform for users to improve their typing skills in an engaging manner. Through gamification, the app not only offers a means of regular practice but also instils a sense of achievement and progress, fostering motivation to sustain continuous improvement in typing skills.

_**Users**_

Type Glazer caters to the following user groups:

Students - Students often have academic requirements that involve extensive typing, from research papers to online exams. They are looking to improve their typing speed and accuracy for academic and future professional endeavours.

Professionals - Professionals in office settings often deal with a high volume of digital communication and documentation. This segment can optimize their workflow by improving typing speed, reducing errors, and enhancing overall efficiency.

Seniors - Seniors seek to stay connected with the modern world. They want to enhance their digital literacy and maintain mental agility through an engaging typing activity.

Self-Improvement Enthusiasts - Individuals dedicated to personal development and skills enhancement want to continue sharpening their typing abilities. This group of users enjoy integrating learning into their leisure time in a low-pressure and fun environment. 


_**Major Features**_

**User Authentication**
- Sign-in - Users are required to input their unique and secure credentials to sign into their account and access past race information.
- Sign up - New users can create an account by providing the necessary information - username and password.
- Log out - Users can easily log out of their accounts, terminating the active session


**Challenge Difficulty Levels** - Users can select a typing challenge from a range of difficulty levels: Easy, Medium, and Hard. Each difficulty level presents users with texts of varying complexities.

**Real-Time Text Feedback and Progress Tracker**
- Interactive Text Input - Users actively type the given sentence into the provided text box. The challenge is completed only when the entire sentence is typed correctly.
- Correctness Highlighting - As users type, the system instantly highlights correct entries in green and incorrect entries in red in the sentence being followed. The colour-coded markers provide users with immediate feedback on their accuracy, aiding in on-the-fly correction of errors.
- Progress Tracker - A progress tracker visually displays the completion status of the sentence, allowing users to track their advancement in real time.
- Words Per Minute (WPM) Counter - A WPM counter calculates and displays the user's typing speed, updating with each typed character.

**Performance Analytics**
- Challenge Statistics Table - A detailed data table provides users with a comprehensive overview of their performance after each typing session. The table holds the race number, the date of each typing challenge, and the corresponding WPM.
- Progress Graph - A dynamic line graph shows the user’s progress over time, highlighting the correlation between race number and WPM. This graph provides users with a clear representation of their evolving typing skills.


**Head-to-Head Challenge**
- Challenge Invitation - Users can initiate a head-to-head challenge by selecting a friend's username from a dropdown menu populated with valid users. The selected friend receives an invitation to participate in a challenge with the same race text.
- My Challenges Page - The 'My Challenges' page displays a list of incoming challenges, which users can choose to accept or decline.
- Results Display - After both users complete the challenge, the 'Head-to-Head’ page dynamically updates to provide a comprehensive overview of the race, including each user's achieved WPM and the outcome of the competition (Win, Lose, Tie).

**Architecture:**

_**Architectural Diagram**_


_**Design Decisions**_

**Frontend:**

We chose to use Jetbrains Compose for the frontend due to its several benefits such as:

- Kotlin Integration: Compose UI is built using Kotlin, thus, we had a seamless integration with our existing Kotlin codebase
- Reactive UI: Our Compose components can automatically update in response to its underlying data. This simplifies the development and drastically reduces the codebase size.
- Architecture: Compose has a component-based architecture which allows us to build highly reusable UI components. Compose follows the declarative UI design pattern (as opposed to imperative UI frameworks). This means we can simply specify what the UI should look like based on the current state of the application.

**Backend:**

We chose to use Ktor because of its several benefits:

- Kotlin Integration: Like compose, Ktor is built with Kotlin, thus, we had a seamless integration with our Kotlin codebase and other Kotlin-based technologies and libraries.
- DSL: Since Ktor uses a Domain Specific Language which makes it easy to define routes and handle HTTP requests.
- Lightweight: Since Ktor is lightweight and module, when developing, we only include the specific components we need. This drastically simplifies the code and reduces the overall codebase size.

We chose to Dockerize our sever and push it to Docker Hub because:
- Dockerizing the server means all users will have a consistent and reproducible environment. This means, if it works on our local machine, it is likely it will work in production.
- Docker containers encapsulate all the dependencies and configurations needed. This helps streamline the process of multiple developers working on the project and the same time and makes the deployed version more reliable.
- Docker Hub allows us to have a centralized location where we can store and view all our Docker images. This simplifies the distribution process by allowing easy versioning and rollbacks.

**Database:**

We chose to use Digital Ocean’s managed PostgreSQL clusters for our database due to several reasons:

- Digital Ocean’s cluster means we have multiple instances of PostgreSQL distributed across multiple servers. This leads to high availability and minimizes the risk of downtime. Additionally, these servers implement reliable replication and failover mechanisms to ensure our database is accessible.
- Digital Ocean’s developer interface allows us to easily scale our Digital Ocean cluster to adapt to fluctuating data and user loads. This means if our app suddenly gains large amounts of traction, we will not have any problems handling the new user data.
- Digital Ocean provides various monitoring tools and metrics to help us gain a deeper understanding of our users and the database’s performance.

_**Reflections on Practices**_

Our team followed the following development practices:

- Code Reviews - Code reviews were crucial in fostering knowledge sharing and ensuring bugs were caught early in the development process. Team members didn’t get to work directly on certain components got to learn about it from reviewing merge requests.
- Pair Programming - Pair programming was used extensively throughout the development process. We followed the model of having a driver who writes the code, and the navigator, who reviews each line of code as it's written for complex tasks like database setup. This worked especially well for our team as it facilitated immediate problem-solving and faster resolution.
- Iterative Development - In iterative development, we broke down features into smaller components (e.g., frontend and backend tasks) to enable concurrent work, preventing dependencies from stalling progress. Thorough testing at the task level also reduced bugs and ensured high-quality outcomes.
- Version Control -  We adhered to version control best practices on GitLab, employing branches and providing detailed commit messages. This practice was effective in managing code changes. A notable instance of its effectiveness was when accidental changes were made to the code for our progress graph. By using version control, we were able to revert to a previous commit, avoiding any complications.


Future areas of improvement:
- Refactoring - One of the key areas of improvement is refactoring our code to improve readability, maintainability, and performance. There’s some redundancy in our existing code and some complex structures that could be simplified to ensure our code is easier to extend and troubleshoot. 
- Feature Enhancements - Unfortunately, we were unable to deliver a key feature: the synchronous typing race. This feature was intended to provide users with a real-time and competitive experience, allowing participants to observe each other's progress as they type. The primary setback was our team's limited familiarity with managing concurrency and ensuring thread safety. Consequently, our initial approach encountered resistance, leading to difficulties in promptly getting sockets to function as intended. As a result, we had to pivot to our contingency plan: asynchronous challenges. Given more time, we would like to delve deeper into this technical challenge.
