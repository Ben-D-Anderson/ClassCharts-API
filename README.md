<h3 align="center">ClassCharts API</h3>
<br />

## About The Project
ClassCharts is an online behavior and homework management system used by schools.

This java project is an unofficial ClassCharts API wrapper that can be used to:
- Login and access ClassCharts
- View homeworks as parsed objects
- Mark homeworks as seen
- Tick homeworks off as "completed"

<br />

## How To Use
First, you should create a StudentCredentials object so that you can create a Student object and login to ClassCharts.

This can be achieved by calling the static factory method `StudentCredential#from(String, String)`.
```java
//The date of birth should be in the format "dd/MM/yyyy"
StudentCredentials studentCredentials = StudentCredentials.from("CODE", "DATE_OF_BIRTH");
```
<br />

Next, you can create a new Student object using the constructor `Student(StudentCredentials)`.

When the Student object is created, the student is logged in automatically and data that will be necessary for other methods is retrieved from ClassCharts.
```java
Student student = new Student(studentCredentials);
```
<br />

The Student instance can now be used to get the homework associated with that user.

Homework can be retrieved using the methods `getHomework()`, `getHomework(String, String)` and `getHomework(DisplayDate, String, String)` in the Student class.

Examples of how to use these methods are demonstrated below:
```java
List<Homework> homework = student.getHomework();
//The dates be in the format "dd/MM/yyyy"
List<Homework> homework2 = student.getHomework("01/01/2020", "20/11/2020");
List<Homework> homework3 = student.getHomework(DisplayDate.DUE, "01/01/2020", "20/11/2020");
List<Homework> homework3 = student.getHomework(DisplayDate.ISSUE, "01/01/2020", "20/11/2020");
```
<br />

Using the list of homeworks retrieved, you can do many things such as the following example: (which prints out the title of every homework in the default timeframe which hasn't been done)
```java
homework.stream().filter(hwk -> !(hwk.getStatus().isCompleted() || hwk.getStatus().isTicked())).map(Homework::getTitle).forEach(System.out::println);
```
<br />

Finally, the Student class contains methods to interact with homeworks.

You can tick a homework with the method `Student#tickHomework(Homework)`. This method effectively toggles the tick status of the homework therefore if it is already ticked then it will un-tick and vice versa.

Homeworks can also be marked as seen which happens normally when you just click on a homework on the ClassCharts web app. This is done with the method `Student#markHomeworkAsSeen(Homework)`.