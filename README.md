# Genetic Algorithm KNIME Plugin

## This project was handed down to me as a suggestion from my college professors to be my Final Year Project.

The main idea is to create a new node to be included into [KNIME platform](https://www.knime.com/) 
that will implement a generic Genetic Algorithm based on users settings.

**That's how it must be structured</br>**
![workflow](https://github.com/victorlms/GA-KNIME-plugin/blob/Development/images/workflow.png)</br>

**This is the settings windows</br>**
![settings](https://github.com/victorlms/GA-KNIME-plugin/blob/Development/images/settings.png)</br>

**And this is the output, where it should specify the best individual</br>**
![output](https://github.com/victorlms/GA-KNIME-plugin/blob/Development/images/output.png)</br>


The user will be able to set main settings which are:
- Number of cromossomes of the individuals
- Number of individuals in the population
- Number of generations of the population
- Crossover rate
- Mutation rate
- If elitism will be implemented or not

Beyond that, since each problem has it's own solution and users may deal with it in their own perspectives, it will be needed to 
use a file reader to a Python script that will evaluate each individual fitness using previous settings.

### I'll keep updating this repo and make a tutorial later once it's done.

**Built with:**
- [Eclipse IDE for RCP and RAP](https://www.eclipse.org/downloads/packages/release/2018-12/r/eclipse-ide-rcp-and-rap-developers)
- [KNIME SDK](https://github.com/KNIME/knime-sdk-setup/)
