public class Connection {
    final double FullWeightMutationRate = 0.1;
    final double WeightMutationRange = 0.02;
    private Node start;
    private Node end;
    private double weight;
    private int ID;
    private boolean enabled = true;

    public Connection(Node s, Node e, double w, int ID) {
        start = s;
        end = e;
        weight = w;
        this.ID = ID;
    }

    void mutateWeight() {
        double rand = Math.random();
        if (rand < FullWeightMutationRate) {// 10% of the time completely change the weight
            weight = Math.random()*2-1;
        } else {// otherwise slightly change it
            weight += ((Math.random()*2)-1)*WeightMutationRange;
            // keep weight between bounds
            if (weight > 1) {
                weight = 1;
            }
            else if (weight < -1) {
                weight = -1;

            }
        }
    }

    public Connection clone(Node start, Node end) {
        return new Connection(start, end, weight, ID);
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Connection [start=" + start + ", end=" + end + ", weight=" + weight + ", ID=" + ID + "]";
    }

}
