# QuizManager
This is the Project for Java Fundamentals lecture on EPITA 2018 Fundamentals Period


# Setting Up
There are some preconditions to be followed to run the program without problems

### Config file
Config file is a file includes program configuration parameters. The content of the file must include 14 required lines of settings to setup program correctly. Values of these settings can be changed to customize the program

### Database
Program uses H2 database to store and fetch data. It doesn't creates any tables on itself. So database should be set up before running the program. SQL commands on file DatabaseCreateTableCommands.txt can be used to create required tables

# Running
- The config file should be given as an argument to the program
- Database should be running before program starts executing
- Program can be run on Java 8 and higher versions
- Main or MainGUI can be selected to run the program as console application or with a graphical user interface
