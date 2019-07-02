# Genetic Algorithm KNIME Plugin

## This project was handed down to me as a suggestion from my college professors to be my Final Year Project.

The main idea is to create a new node to be included into [KNIME platform](https://www.knime.com/) 
that will implement a generic Genetic Algorithm based on users settings.

**That's how it must be structured</br>**
![workflow](https://github.com/victorlms/GA-KNIME-plugin/blob/master/images/new_workflow.png)</br>

**This is the settings windows</br>**
![settings](https://github.com/victorlms/GA-KNIME-plugin/blob/master/images/new_settings.png)</br>

**This is the output, where it specify the best individual fitness from each population as it's average fitness</br>**
![output](https://github.com/victorlms/GA-KNIME-plugin/blob/master/images/new_output.png)</br>

**And this is the graphic output, where it should specify the best individual</br>**
***We can see that the evaluation function was poorly implemented, but it was just for testing***
![output](https://github.com/victorlms/GA-KNIME-plugin/blob/master/images/new_graphic.png)</br>


The user will be able to set main settings which are:
- Number of cromossomes of the individuals
- Number of individuals in the population
- Number of generations of the population
- Crossover rate
- Mutation rate
- If elitism will be implemented or not
- The path to the evaluation function implemented in Python

Beyond that, since each problem has it's own solution and users may deal with it in their own perspectives, it will be needed to 
use a file reader to a Python script that will evaluate each individual fitness using previous settings.

## Instructions for the evaluation function
- Since KNIME API is built upon Java, the way I found to execute user evaluation function
was to reference a Python script that need to follow some rules:
- Use the following import command ***import sys***
- Sometimes it was noticed a bug in the output, to bypass it use: ***sys.stdout.flush()***
- The input is the individual that is going to be evaluated
- The individual will come in a string format, since it's inputed by the OS **USE*****sys.arg[1]*** **TO REFERENCE IT**
- The output **MUST BE IN A STRING FORMAT USING:** ***sys.stdout.write(individual_fitness_as_string)***
- Following that, you can assign ***sys.argv[1]*** to a variable ***i.e. individual = sys.argv[1]*** and manipulate it as you wish
