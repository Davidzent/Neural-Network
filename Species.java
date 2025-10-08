import java.util.ArrayList;

public class Species {
    private ArrayList<Player> players = new ArrayList<Player>();
    private double bestFitness = 0;
    private Player champ;
    private double averageFitness = 0;
    private int staleness = 0;//how many generations the species has gone without an improvement
    private Brain rep;
  
    //--------------------------------------------
    //coefficients for testing compatibility 
    private double excessCoeff = 1;
    private double weightDiffCoeff = 0.5;
    private double compatibilityThreshold = 3;
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //empty constructor
  
    Species() {
    }
  
  
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
    //constructor which takes in the player which belongs to the species
    Species(Player p) {
      players.add(p); 
      //since it is the only one in the species it is by default the best
      bestFitness = p.fitness; 
      rep = p.brain.clone();
      champ = p.cloneForReplay();
    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
    //returns whether the parameter genome is in this species
    boolean sameSpecies(Brain g) {
      double compatibility;
      double excessAndDisjoint = getExcessDisjoint(g, rep);//get the number of excess and disjoint genes between this player and the current species rep
      double averageWeightDiff = averageWeightDiff(g, rep);//get the average weight difference between matching genes
  
  
      double largeBrainNormaliser = g.getConections().size() - 20;
      if (largeBrainNormaliser<1) {
        largeBrainNormaliser =1;
      }
  
      compatibility =  (excessCoeff* excessAndDisjoint/largeBrainNormaliser) + (weightDiffCoeff* averageWeightDiff);//compatablilty formula
      return (compatibilityThreshold > compatibility);
    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
    //add a player to the species
    void addToSpecies(Player p) {
      players.add(p);
    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
    //returns the number of excess and disjoint genes between the 2 input genomes
    //i.e. returns the number of genes which dont match
    double getExcessDisjoint(Brain brain1, Brain brain2) {
      double matching = 0.0;
      for (int i =0; i <brain1.getConections().size(); i++) {
        for (int j = 0; j < brain2.getConections().size(); j++) {
          if (brain1.getConections().get(i).getID() == brain2.getConections().get(j).getID()) {
            matching ++;
            break;
          }
        }
      }
      return (brain1.getConections().size() + brain2.getConections().size() - 2*(matching));//return no of excess and disjoint genes
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //returns the avereage weight difference between matching genes in the input genomes
    double averageWeightDiff(Brain brain1, Brain brain2) {
      if (brain1.getConections().size() == 0 || brain2.getConections().size() ==0) {
        return 0;
      }
  
  
      double matching = 0;
      double totalDiff= 0;
      for (int i =0; i <brain1.getConections().size(); i++) {
        for (int j = 0; j < brain2.getConections().size(); j++) {
          if (brain1.getConections().get(i).getID() == brain2.getConections().get(j).getID()) {
            matching ++;
            totalDiff += Math.abs(brain1.getConections().get(i).getWeight() - brain2.getConections().get(j).getWeight());
            break;
          }
        }
      }
      if (matching ==0) {//divide by 0 error
        return 100;
      }
      return totalDiff/matching;
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //sorts the species by fitness 
    void sortSpecies() {
  
      ArrayList<Player> temp = new ArrayList<Player>();
  
      //selection short 
      for (int i = 0; i < players.size(); i ++) {
        double max = 0;
        int maxIndex = 0;
        for (int j = 0; j< players.size(); j++) {
          if (players.get(j).fitness > max) {
            max = players.get(j).fitness;
            maxIndex = j;
          }
        }
        temp.add(players.get(maxIndex));
        players.remove(maxIndex);
        i--;
      }
  
      players = (ArrayList)temp.clone();
      if (players.size() == 0) {
        System.out.print("fucking"); 
        staleness = 200;
        return;
      }
      //if new best player
      if (players.get(0).fitness > bestFitness) {
        staleness = 0;
        bestFitness = players.get(0).fitness;
        rep = players.get(0).brain.clone();
        champ = players.get(0).cloneForReplay();
      } else {//if no new best player
        staleness ++;
      }
    }
  
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //simple stuff
    void setAverage() {
  
      double sum = 0;
      for (int i = 0; i < players.size(); i ++) {
        sum += players.get(i).fitness;
      }
      averageFitness = sum/players.size();
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  
    //gets baby from the players in this species
    Player giveMeBaby(ArrayList<ConnectionHistory> innovationHistory) {
      Player baby;
      if (Math.random() < 0.25) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
        baby =  selectPlayer().clone();
      } else {//75% of the time do crossover 
  
        //get 2 random(ish) parents 
        Player parent1 = selectPlayer();
        Player parent2 = selectPlayer();
  
        //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
        if (parent1.fitness < parent2.fitness) {
          baby =  parent2.crossover(parent1);
        } else {
          baby =  parent1.crossover(parent2);
        }
      }
      baby.brain.mutate(innovationHistory);//mutate that baby brain
      return baby;
    }
  
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //selects a player based on it fitness
    Player selectPlayer() {
      double fitnessSum = 0;
      for (int i =0; i<players.size(); i++) {
        fitnessSum += players.get(i).fitness;
      }
  
      double rand = Math.random()*fitnessSum;
      double runningSum = 0;
  
      for (int i = 0; i<players.size(); i++) {
        runningSum += players.get(i).fitness; 
        if (runningSum > rand) {
          return players.get(i);
        }
      }
      //unreachable code to make the parser happy
      return players.get(0);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //kills off bottom half of the species
    void cull() {
      if (players.size() > 2) {
        for (int i = players.size()/2; i<players.size(); i++) {
          players.remove(i); 
          i--;
        }
      }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //in order to protect unique players, the fitnesses of each player is divided by the number of players in the species that that player belongs to 
    void fitnessSharing() {
      for (int i = 0; i< players.size(); i++) {
        players.get(i).fitness/=players.size();
      }
    }


    public ArrayList<Player> getPlayers() {
      return players;
    }


    public void setPlayers(ArrayList<Player> players) {
      this.players = players;
    }


    public double getBestFitness() {
      return bestFitness;
    }


    public void setBestFitness(double bestFitness) {
      this.bestFitness = bestFitness;
    }


    public Player getChamp() {
      return champ;
    }


    public void setChamp(Player champ) {
      this.champ = champ;
    }


    public double getAverageFitness() {
      return averageFitness;
    }


    public void setAverageFitness(double averageFitness) {
      this.averageFitness = averageFitness;
    }


    public int getStaleness() {
      return staleness;
    }


    public void setStaleness(int staleness) {
      this.staleness = staleness;
    }


    public Brain getRep() {
      return rep;
    }


    public void setRep(Brain rep) {
      this.rep = rep;
    }


    public double getExcessCoeff() {
      return excessCoeff;
    }


    public void setExcessCoeff(double excessCoeff) {
      this.excessCoeff = excessCoeff;
    }


    public double getWeightDiffCoeff() {
      return weightDiffCoeff;
    }


    public void setWeightDiffCoeff(double weightDiffCoeff) {
      this.weightDiffCoeff = weightDiffCoeff;
    }


    public double getCompatibilityThreshold() {
      return compatibilityThreshold;
    }


    public void setCompatibilityThreshold(double compatibilityThreshold) {
      this.compatibilityThreshold = compatibilityThreshold;
    }

    
  }