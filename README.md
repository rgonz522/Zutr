Original App Design Project - README Template
===

# Zutr

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)

## Overview
### Description
A direct link between a student's questions to an answer. Students can publish questions they have and a tutor would answer for a price. As well as having the ability to book tutoring appointments through chat, call, video chat, and in person{situation permitting}.

### App Evaluation
- **Category:**
    - Education
- **Mobile:**
    - UI that grants an instant response in comparison to forums, not dependent on textbooks questions like competitors. Ability to see nearby in person tutors. Ability to offer price per question or per session or per time.
- **Story:**
    - Speaking from a former high schooler and current college student, one has experiences more often than not doubts about an assignment, project, or assesment that is not in the textbook, in one's notes, nor even found on google. In this day and age, answers must be immediate and not left alone till the test. From a tutor's perspective, sometimes it is easier to answer and explain a single question for quick change than to drive,sit down, and physically elaborate on subjects. Tutors can be current faculty, retired experts, or even other students that have passed said courses. There will be rating for the student and the tutors, so as to maintain quality of education. As well as reporting abilities of any violation of the conduct policy.
    
- **Market:**
    - Currently the market is somewhat flooded with education apps, but nothing immediate. Everything else lacks the ability to format to an individual's question instantly.
- **Habit:**
    - As a colllege student, I have seen other students develop habits to education apps, passwords being shared across campus, because students need an answer so desperately. This immediate solutions will have users leaving their googling days behind them.
- **Scope:**
    - The skeleton of this app is feasible within the scope of the internships. A user/student version of a search is possible.
    - Another app design is needed for the tutor to see requests in their area as well as online question requests.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Account SetUp(Student and Tutor)
    - Required: Username, Real Name, Email
    - Subjects, Classes
        - For tutors: tutoring address for in person sessions
* Student Homepage with previously asked questions, tutors based on their subject, common questions asked based on their subject, tutors/questions on discount
* Tutor and student ratings and reviewing after sessions.
* Question/Session soliciting page to create a question or schedule a meeting [call,chat,video,or in person]
* Tutor homepage with posed questions, sessions solicited by students [filter by subject] 
* Chat, Video, and Voice calls works only when the session has been deemed 'paid for'.

* Google Maps SDK in person tutors by address.

**Optional Nice-to-have Stories**

* Chat system works
* Screen Share with white board.org or w.e
* Having a shared whiteboard interface
* Payment set up for tutors and students.
* Payment works using Square SDK
* A information page, using some education news api that gives help on 
* Also have a page of general information
    - College Application, Studying, Remote Class advise etc.
* Zutr has calendar availability, if you like a certain one you can book them
* Zutr can offer classes and students sign up for a 1:1 or many:1 like a tutoring session, but not.
* Open ended skills: arts, cinema (video editing), dance.


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Page
* Info Page
* Get A Zutr Page
* Account Page

**Student Flow Navigation** (Screen to Screen)

* HomePage
   * Maps nearby tutors
   * Previous questions posed
* Info Page (an extra story)
   * Articles
   * Covid annoucements/ General Updates
* Get a Zutr Page
    * Start by asking if they want a single question or a session
    * Click Question >Subject>Set Price > Write Question
    * Session> Choose What Kind> Choose Time/Date> Choose Tutor
    * "Your Zutr is on His way!"
* Account Page
    * individual information, 
    * payment details


**Tutor Tab Navigation** (Tab to Screen)

* Home Page
* Info Page
* Be a Zutr Page
* Account Page

**Tutor Flow Navigation** (Screen to Screen)

* Home Page
    * Questions being solicited
    * Previous questions answered
* Info Page
    *  Articles
   * Covid annoucements/ General Updates
* Be a Zutr Page
    * Requested sessions
* Account Page
    * individual information

## Wireframes



![](https://raw.githubusercontent.com/rgonz522/Zutr/master/New%20Folder%20With%20Items/Android%20-%201.png)

![](https://github.com/rgonz522/Zutr/blob/master/New%20Folder%20With%20Items/Android%20-%202.png)


![](https://github.com/rgonz522/Zutr/blob/master/New%20Folder%20With%20Items/Android%20-%208.png)

![](https://github.com/rgonz522/Zutr/blob/master/New%20Folder%20With%20Items/Android%20-%206.png)

![](https://github.com/rgonz522/Zutr/blob/master/New%20Folder%20With%20Items/Android%20-%207.png)

![](https://github.com/rgonz522/Zutr/blob/master/New%20Folder%20With%20Items/Android%20-%205.png)




### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
- Session (text,video, call sessions)/ Single Qestion
    - Time started
    - Time ended
    - Hourly wage
        -  [If Single Question,  wage will be the agreed value]
    - student asking
    - tutor
    - subject/class
- Student
    - Name
    - Email
    - Payment?
    - Preferred tutoring Address: optional
- Tutor
    - Name
    - Email
    - Payment?
    - Preferred tutoring Address: optional






### Networking
- [Add list of network requests by screen ]
    - Google Maps SDK, Square SDK, Latex for a possible math keyboard.
