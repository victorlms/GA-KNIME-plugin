import sys
# This algorithm evaluate a configuration of queens disposal at a chess board so they don't attack themselves
# Made by Victor Lima
sys.stdout.flush()

individual = sys.argv[1]

# INDIVIDUAL CONVERTED TO IT'S DECIMAL VALUE
num = int(int(individual, 2))

# LOWER BOUND
inf = 0

# HIGHER BOUND
sup = 6 

# SIZE OF THE CROMOSSOME
k = len(individual)

# CONVERTION TO ADEQUATE IT'S VALUE WITHIN THE RANGE
real = inf + ((sup - inf)/(pow(2, k)-1) * num)


# FUNCTION TO BE DETERMINED
fitness = (pow (real, 2) - 6 * real + 5) * (-1)
# fitness = (pow(real, 3) - pow((6 * real), 2) + 4 * real + 12)

print(real)
sys.stdout.flush()
sys.stdout.write(str(fitness))
