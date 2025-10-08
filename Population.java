import java.util.ArrayList;

public class Population {
    ArrayList<Player> pop = new ArrayList<Player>();
    Player bestPlayer;//the best ever player 
    int cntAlive;
    int bestScore =0;//the score of the best ever player
    int currentBestScore = 0;
    int currentBestScorePos=0;
    int currentBestCorrect=0;
    int gen;
    double passingGrade = 0.4;
    ArrayList<ConnectionHistory> innovationHistory = new ArrayList<ConnectionHistory>();
    ArrayList<Player> genPlayers = new ArrayList<Player>();
    ArrayList<Species> species = new ArrayList<Species>();
  
    boolean massExtinctionEvent = false;
    boolean newStage = false;
    int populationLife = 0;
  
  
  
  
    //------------------------------------------------------------------------------------------------------------------------------------------
    //constructor
    Population(int size) {
  
      for (int i =0; i<size; i++) {
        pop.add(new Player());
        // pop.get(i).brain.generateNetwork();
        pop.get(i).brain.mutate(innovationHistory);
      }
      cntAlive=size;
    }
    
    //------------------------------------------------------------------------------------------------------------------------------------------
    //update all the players which are alive
    void updateAlive(Square ans,int squareType,boolean showNothing) {
      populationLife ++;
      boolean first=true;
      boolean died=false;
      for (int i = 0; i< pop.size(); i++) {
        if (!pop.get(i).dead) {
          pop.get(i).look(ans);//get inputs for brain 
          pop.get(i).think();//use outputs from neural network
          died=pop.get(i).update(squareType,passingGrade,populationLife);//move the player according to the outputs from the neural network
          if(died)cntAlive--;
          if(pop.get(i).score>currentBestScore||currentBestScorePos==i){
            currentBestScore=pop.get(i).score;
            currentBestScorePos=i;
            
          }
          if(pop.get(i).correct>currentBestCorrect){
            currentBestCorrect=pop.get(i).correct;
          }
          
          if (!showNothing&&first&&(i==currentBestScore||pop.get(i).score>=currentBestScore)) {
            first=false;
            pop.get(i).show();
            App.drawBrain(pop.get(i));
          }
        }
      }
      if(populationLife>50&&populationLife%10==0){
        passingGrade+=0.01;
      }
      
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //returns true if all the players are dead      sad
    boolean done() {
      for (int i = 0; i< pop.size(); i++) {
        if (!pop.get(i).dead) {
          return false;
        }
      }
      passingGrade=0.4;
      cntAlive=pop.size();
      return true;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //sets the best player globally and for this gen
    void setBestPlayer() {
      Player tempBest =  species.get(0).getPlayers().get(0);
      tempBest.gen = gen;
  
  
      //if best this gen is better than the global best score then set the global best as the best this gen
  
      if (tempBest.score > bestScore) {
        genPlayers.add(tempBest.cloneForReplay());
        System.out.println("old best: " + bestScore);
        System.out.println("new best: " + tempBest.score);
        bestScore = tempBest.score;
        bestPlayer = tempBest.cloneForReplay();
      }
    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------------
    //this function is called when all the players in the population are dead and a new generation needs to be made
    void naturalSelection() {
      
      speciate();//seperate the population into species 
      calculateFitness();//calculate the fitness of each player
      sortSpecies();//sort the species to be ranked in fitness order, best first
      // if (massExtinctionEvent) { 
      //   massExtinction();
      //   massExtinctionEvent = false;
      // }
      cullSpecies();//kill off the bottom half of each species
      setBestPlayer();//save the best player of this gen
      killStaleSpecies();//remove species which haven't improved in the last 15(ish) generations
      killBadSpecies();//kill species which are so bad that they cant reproduce
  
  
      System.out.println("generation "+ gen+ " Number of mutations "+ innovationHistory.size()+ " species: " + species.size()+ " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
  
  
      double averageSum = getAvgFitnessSum();
      ArrayList<Player> children = new ArrayList<Player>();//the next generation
      System.out.println("Species:");               
      for (int j = 0; j < species.size(); j++) {//for each species
  
        System.out.println("best unadjusted fitness: "+ species.get(j).getBestFitness());
        for (int i = 0; i < species.get(j).getPlayers().size(); i++) {
          System.out.printf("player " + i, "fitness: " +  species.get(j).getPlayers().get(i).fitness, "score " + species.get(j).getPlayers().get(i).score, ' ');
        }
        System.out.println();
        children.add(species.get(j).getChamp().clone());//add champion without any mutation
  
        
        int NoOfChildren = (int) Math.floor(species.get(j).getAverageFitness()/averageSum * pop.size()) -1;//the number of children this species is allowed, note -1 is because the champ is already added
        
        // System.out.println("hello "+NoOfChildren);      
        for (int i = 0; i< NoOfChildren; i++) {//get the calculated amount of children from this species
          children.add(species.get(j).giveMeBaby(innovationHistory));
        }
        // System.out.println("hello2");
      }
      // System.out.println("hello3");
      while (children.size() < pop.size()) {//if not enough babies (due to flooring the number of children to get a whole int) 
        children.add(species.get(0).giveMeBaby(innovationHistory));//get babies from the best species
      }
      pop.clear();
      pop = children; //set the children as the current population
      gen+=1;
      // for (int i = 0; i< pop.size(); i++) {//generate networks for each of the children
      //   pop.get(i).brain.connectNodes();
      // }
      
      populationLife = 0;
      currentBestScore=0;
      currentBestCorrect=0;

    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------
    //seperate population into species based on how similar they are to the leaders of each species in the previous gen
    void speciate() {
      for (Species s : species) {//empty species
        s.getPlayers().clear();
      }
      for (int i = 0; i< pop.size(); i++) {//for each player
        boolean speciesFound = false;
        for (Species s : species) {//for each species
          if (s.sameSpecies(pop.get(i).brain)) {//if the player is similar enough to be considered in the same species
            s.addToSpecies(pop.get(i));//add it to the species
            speciesFound = true;
            break;
          }
        }
        if (!speciesFound) {//if no species was similar enough then add a new species with this as its champion
          species.add(new Species(pop.get(i)));
        }
      }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //calculates the fitness of all of the players 
    void calculateFitness() {
      for (int i =1; i<pop.size(); i++) {
        pop.get(i).calculateFitness();
      }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //sorts the players within a species and the species by their fitnesses
    void sortSpecies() {
      //sort the players within a species
      for (Species s : species) {
        s.sortSpecies();
      }
  
      //sort the species by the fitness of its best player
      //using selection sort like a loser
      ArrayList<Species> temp = new ArrayList<Species>();
      for (int i = 0; i < species.size(); i ++) {
        double max = 0;
        int maxIndex = 0;
        for (int j = 0; j< species.size(); j++) {
          if (species.get(j).getBestFitness() > max) {
            max = species.get(j).getBestFitness();
            maxIndex = j;
          }
        }
        temp.add(species.get(maxIndex));
        species.remove(maxIndex);
        i--;
      }
      species = (ArrayList)temp.clone();
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //kills all species which haven't improved in 15 generations
    void killStaleSpecies() {
      for (int i = 2; i< species.size(); i++) {
        if (species.get(i).getStaleness() >= 15) {
          species.remove(i);
          i--;
        }
      }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //if a species sucks so much that it wont even be allocated 1 child for the next generation then kill it now
    void killBadSpecies() {
      double averageSum = getAvgFitnessSum();
  
      for (int i = 1; i< species.size(); i++) {
        if (species.get(i).getAverageFitness()/averageSum * pop.size() < 1) {//if wont be given a single child 
          species.remove(i);//sad
          i--;
        }
      }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    //returns the sum of each species average fitness
    double getAvgFitnessSum() {
      double averageSum = 0;
      for (Species s : species) {
        averageSum += s.getAverageFitness();
      }
      return averageSum;
    }
  
    //------------------------------------------------------------------------------------------------------------------------------------------
    //kill the bottom half of each species
    void cullSpecies() {
      for (Species s : species) {
        s.cull(); //kill bottom half
        s.fitnessSharing();//also while we're at it lets do fitness sharing
        s.setAverage();//reset averages because they will have changed
      }
    }
  
  
    void massExtinction() {
      for (int i =5; i< species.size(); i++) {
        species.remove(i);//sad
        i--;
      }
    }
  }