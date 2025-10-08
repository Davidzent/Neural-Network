import java.util.ArrayList;


public class Node {
    private int ID;
    private double inSum=0;
    private double outValue=0;
    private ArrayList<Connection> outConnections = new ArrayList<Connection>();
    private int layer = 0;

    Node(){
    }

    Node(int no) {
        ID = no;
    }

    Node(int no,int layer) {
        ID = no;
        this.layer = layer;
    }

    Node(Node node){
        ID = node.ID;
        layer = node.layer;
    }

    public void addConnection(Node node,double weight, int id){
        outConnections.add(new Connection(this,node, weight,id));
    }

    public void addConnection(Connection c){
        outConnections.add(c);
    }

    public void clearConnection(){
        outConnections.clear();
    }

    public void engage(){
        if(layer!=0){
            outValue = sigmoid(inSum);
        }

        for (int i = 0; i< outConnections.size(); i++) {
            if (outConnections.get(i).isEnabled()) {
              outConnections.get(i).getEnd().addInSum(
                 outConnections.get(i).getWeight() * outValue
              );//add the weighted output to the sum of the inputs of whatever node this node is connected to
            }
          }
    }

    public double sigmoid(double x) {
        double y = 1 / (1 + Math.pow((double)Math.E, -4.9*x));
        return y;
    }

    public boolean isConnected(Node node){
        // if(node.layer==layer)return false;
        
        if(node.layer < layer){
            for(int i=0;i<node.outConnections.size();i++){
                if(node.outConnections.get(i).getEnd() == this){
                    return true;
                }
            }
        }else{
            for(int i=0;i<outConnections.size();i++){
                if(outConnections.get(i).getEnd() == node){
                    return true;
                }
            }
        }

        return false;
    }

    public Node clone() {
        Node clone = new Node(ID);
        clone.layer = layer;
        return clone;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getInSum() {
        return inSum;
    }

    public void setInSum(double inSum) {
        this.inSum = inSum;
    }

    public void addInSum(double inSum) {
        this.inSum += inSum;
    }

    public double getOutValue() {
        return outValue;
    }

    public void setOutValue(double outValue) {
        this.outValue = outValue;
    }

    public ArrayList<Connection> getOutConnections() {
        return outConnections;
    }

    public void setOutConnections(ArrayList<Connection> outConnections) {
        this.outConnections = outConnections;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    public void incrementLayer(){
        this.layer++;
    }

    @Override
    public String toString() {
        return "Node [ID=" + ID + ", inSum=" + inSum + ", outValue=" + outValue + ", outConnections=" + outConnections
                + ", layer=" + layer + "]";
    }

}

  
