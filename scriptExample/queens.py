import sys
#This algorithm evaluate a configuration of queens disposal at a chess board so they don't attack themselves
#Made by Victor Lima
sys.stdout.flush()

individual = sys.argv[1]

fitness = 0
i = 0
j = 0
temp_index = 0
temp_val = 0

while i < len(individual):
    j = i
    while j < len(individual):

        temp_index = j-i
        temp_val = int(individual[j]) - int(individual[i])

        if temp_index != temp_val and i != j:  #checa se as rainhas não estão na horizontal nem na diagonal
            fitness += 1   
        j+=1 

    i+=1

sys.stdout.flush()
sys.stdout.write(str(fitness))
