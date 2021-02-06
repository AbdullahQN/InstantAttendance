# InstantAttendance
Design and implementation of attendance system using machine learning.


WEEK 0:
- [x] Android Project.
> The project members have decided to continue the project implementation on mobile to be codded on Java on Android Platform for the unmatched Documentation and Open Source Resources on any other platform and programming language.

- [x] Github setup for project synchronization between members.
> To get the advantages of version control systems and social distancing preventing a physical development meeting.

- [x] Login design and implementation connected to our project database.
> When launching the program (MainActivity class invoked), leading to check whether: 
> 1. There is an active logged in user: The system would greet the user with the home interface. 
> 2. There is no active logged in user: The system would greet the user with a login interface.
see more at: [Firebase Authentication](https://firebase.google.com/docs/auth) .

WEEK 1:
- [x] Getting user courses information from the database and display it in the Home interface.
> The first class to be invoked (MainActivity class) now acts as a loading screen performing different queries to the project database to get data about the user and sections, this is essential for upcoming functionalities.
see more at: [Get data with Cloud Firestore](https://firebase.google.com/docs/firestore/query-data/get-data) .

- [x] initial workflow skeleton between Interfaces.
> Deciding class hierarchies and objectives.
| Interface | Objective | Progress |
| :---: | :---: | :---: |
| MainActivity class | Loading user data | Ongoing |
| HomeActivity class | Users main interface with the system | Ongoing |

| CameraActivity class | Interface with device camera | Ongoing |
| PictureView class | Preview the taken picture | Ongoing |
| SignIn class | Authenticating user with the project database | Concluded |
| CourseAdapter class | Helper class to propagate Section card in HomeActivity class | Concluded |
| User class | Object-oriented class containing user data | Concluded |
| Section class | Object-oriented class containing sections data | Concluded |

WEEK 2:
- [x] Home interface GUI design and implementation.
- [x] Integrating Android CameraX library into the project.
- [x] Design and implementation of a Camera Preview Interface.

WEEK 3:

Todo:
- [ ] Home interface (The ability to view attendance history for a particular section).
- [ ] Taking attendance functionality.
- [ ] TensorFlowLight integration of MTCNN and Facenet in different scenarios:

| ML | Solution1 | Solution2 |
| --- | --- | --- |
| MTCNN | On device integration | Cloud integration |
| FaceNet | On device integration | On device integration |


