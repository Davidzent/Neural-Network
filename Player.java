public class Player{
    double fitness;
    Brain brain;
    boolean replay = false;
    int combo = 0;
    
    double unadjustedFitness;
    int lifespan = 0;//how long the player lived for fitness
    int bestScore =0;//stores the score achieved used for replay
    boolean dead=false;
    int guess=0;
    int score=0;
    
    int correct=0;
    int gen = 0;
  
    int genomeInputs = 4;
    int genomeOutputs = 8;
    
    double[] vision = new double[genomeInputs];//t he input array fed into the neuralNet 
    double[] decision = new double[genomeOutputs]; //the out put of the NN 
    double[] decisionHistory = new double[genomeOutputs];
    
    Player(){
      brain = new Brain(genomeInputs, genomeOutputs);
    }
    
    void show(){
      Square s = App.types(guess,3,0);
      s.show(1);


    }

    
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //fot Genetic algorithm
    void calculateFitness() {
      fitness = score*score;
    }
    
    boolean update(int ans,double pass,int attempt){
      if(ans==guess){
        combo++;
        score+=combo*1;
        correct++;
      }else{
        if(attempt>=50&&score<attempt*pass||(attempt>500&&correct<attempt*pass)) dead=true;
        if(decisionHistory[ans] >= 10)score-=(int)decisionHistory[ans]/10;
        decisionHistory[ans]++;
        combo=0;
      }
      
      return dead;
    }
    
    void look(Square square){
      int cnt=0;
      for(int i=0;i<square.length;i++){
        for(int j=0;j<square.length;j++){
          vision[cnt++]=square.pixels[i][j].val*2-1;
          //println(vision[cnt-1],cnt-1);
        }
      }
      
    }
    
    void think(){
      double max = 0;
      int maxIndex = 0;
      //get the output of the neural network
      decision = brain.feedForward(vision);
  
      for (int i = 0; i < decision.length; i++) {
        //println(decision[i],i);
        if (decision[i] > max) {
          max = decision[i];
          maxIndex = i;
        }
      }
      guess=maxIndex;
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    Player crossover(Player parent2) {
      Player child = new Player();
      child.brain = brain.crossover(parent2.brain);
    //   child.brain.generateNetwork();
      return child;
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------------------------  
    //returns a clone of this player with the same brian
    
    protected Player clone() {
      Player clone = new Player();
      clone.brain = brain.clone();
      clone.fitness = fitness;
    //   clone.brain.generateNetwork(); 
      clone.gen = gen;
      clone.bestScore = score;
      return clone;
    }
  
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //since there is some randomness in games sometimes when we want to replay the game we need to remove that randomness
    //this fuction does that
  
    Player cloneForReplay() {
      Player clone = new Player();
      clone.brain = brain.clone();
      clone.fitness = fitness;
    //   clone.brain.generateNetwork();
      clone.gen = gen;
      clone.bestScore = score;
      clone.replay = true;
  
      return clone;
    }
  
  }