import sys
# This algorithm evaluate a configuration of queens disposal at a chess board so they don't attack themselves
# Made by Victor Lima
sys.stdout.flush()

individual = sys.argv[1]

travelPrices = [['A','B',10],['A','C',5],['A','D',7],['A','E',12],
                ['B','A',12],['B','C',3],['B','D',2],['B','E',8],
                ['C','A',4],['C','B',4],['C','D',7],['C','E',11],
                ['D','A',4],['D','B',10],['D','C',7],['D','E',2],
                ['E','A',6],['E','B',6],['E','C',11],['E','D',2]]

fitness = int(0)

if (individual[0] == 'A'):
    fitness -= 3
if (individual[0] == 'B'):
    fitness -= 4
if (individual[0] == 'C'):
    fitness -= 3
if (individual[0] == 'D'):
    fitness -= 6
if (individual[0] == 'E'):
    fitness -= 2

for x in range(len(individual)):
    if(x == len(individual)-1):
        break
    for y in range(len(travelPrices)):
        if travelPrices[y][0] == individual[x] and travelPrices[y][1] == individual[x+1]:
            fitness -= travelPrices[y][2]



if (individual[len(individual)-1] == 'A'):
    fitness -= 3
if (individual[len(individual)-1] == 'B'):
    fitness -= 4
if (individual[len(individual)-1] == 'C'):
    fitness -= 3
if (individual[len(individual)-1] == 'D'):
    fitness -= 6
if (individual[len(individual)-1] == 'E'):
    fitness -= 2

# FUNCTION TO BE DETERMINED
# print(fitness)
sys.stdout.flush()
sys.stdout.write(str(fitness))
