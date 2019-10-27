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
    
    j = i+2 
    while j < len(individual)-1:
        
        temp_index = j-1-i
        val1 = individual[i] + individual[i+1]
        val1 = int(val1,2)
        val2 = individual[j] + individual[j+1]
        val2 = int(val2,2)
        temp_val = val2 - val1
        if temp_index != abs(temp_val) and val1 != val2:  #checa se as rainhas não estão na horizontal nem na diagonal
            fitness += 1   
        j+=2
        
         
    
    i+=2

sys.stdout.flush()
sys.stdout.write(str(fitness))