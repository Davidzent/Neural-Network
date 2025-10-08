import java.util.HashMap;
import java.util.Map;

public class LinkedList{
    private Node data;
    private LinkedList next;
    private int size=0;
     
    LinkedList() {
    }

    LinkedList(Node node) {
        data = node;
        size++;
    }

    public void AddOnLayer(Node n){
        LinkedList temp=this;
        while(!checkAdd(n,temp)){
            temp=temp.next;   
        }
        size++;
    }

    public void AddOnBetweenLayer(Node n){
        LinkedList temp=this;
        boolean after=false;
        int CLayer=0;
        while(!checkAdd(n,temp)){
            temp=temp.next;
            CLayer=temp.data.getLayer();   
        }
        while(temp!=null){
            if(after){
                temp.data.incrementLayer();
            }
            if(temp.data==n)after=true;
            temp=temp.next;   
        }
        size++;
    }

    private boolean checkAdd(Node node,LinkedList at){
        //insert at current position
        if(at.data==null){      
            at.data = node;
            return true;
        }
        //insert at current position then move everything one forward
        else if(at.data.getLayer()>=node.getLayer()){        
            LinkedList temp = new LinkedList(at.data);
            temp.next=at.next;
            at.data=node;
            at.next=temp;
            return true;
        }
        //insert on the next position
        else if(at.next==null||node.getLayer()<at.next.data.getLayer()){
            LinkedList nw = new LinkedList(node);
            nw.setNext(at.next);
            at.setNext(nw);
            return true;
        }
        return false;
    }

    public void clearAllConnections(){
        if(data==null)return;
        LinkedList temp=this;
        while(temp!=null){
            temp.data.clearConnection();
            temp=temp.next;   
        }
    }

    public void setInputsResponse(double[] inputValues){
        LinkedList temp=this;
        int i=0;
        while(temp.data.getLayer()==0&&inputValues.length>i){
            temp.data.setOutValue(inputValues[i++]);
            temp=temp.next;
        }
        if(temp.data.getLayer()==0&&inputValues.length<=i){
            temp.data.setOutValue(1);
        }
    }

    public double[] getOutputsResponse(int n){
        if(data==null)return null;

        LinkedList temp=this;
        LinkedList layerS=this;
        double[] outs = new double[n];
        int i=0;
        while(temp!=null){
            if(temp.data.getLayer()>layerS.data.getLayer()){
                layerS=temp;
            }
            temp=temp.next;
        }

        while(layerS!=null){
            outs[i++]=layerS.data.getOutValue();
            layerS = layerS.next;
        }

        return outs;
    }

    public Map <Integer, Integer> NodesInLayers(){
        Map <Integer, Integer> layerC = new HashMap<Integer, Integer>();
        LinkedList temp=this;
        int cnt=0;

        while(temp!=null){
            cnt=layerC.getOrDefault(temp.data.getLayer(), 0);
            layerC.put(temp.data.getLayer(), ++cnt);
            temp=temp.next;
        }
        return layerC;
    }

    public int MaxConnections(){
        if(data==null) return 0;        
        Map <Integer, Integer> layerC = NodesInLayers();
        int maxConnections=0;
        int frontNodes=0;

        for(int i=0;layerC.containsKey(i);i++){
            frontNodes=0;
            for(int j=i+1;layerC.containsKey(j);j++){
                frontNodes += layerC.get(j);
            }
            maxConnections+=layerC.get(i)*frontNodes;
        }

        return maxConnections;

    }

    

    public void resetInputsSum(){
        if(data==null)return;
        LinkedList temp=this;
        while(temp!=null){
            temp.data.setInSum(0);
            temp=temp.next;
        }
    }

    public Node get(int index){
        LinkedList temp=this;
        int i=0;
        while(temp!=null&&i++!=index){
            temp=temp.next;   
        }
        if(temp!=null)return temp.data;
        return null;
    }

    public Node getByID(int ID){
        LinkedList temp=this;
        while(temp!=null&&temp.data.getID()!=ID){
            temp=temp.next;   
        }
        if(temp!=null)return temp.data;
        return null;
    }

    public void engageAll(){
        if(data==null)return;

        LinkedList temp=this;
        while(temp!=null){
            temp.data.engage();
            temp=temp.next;
        }
    }

    
    public LinkedList clone(){
        LinkedList clone = new LinkedList();
        if(data==null)return clone;
        
        LinkedList temp=this;
        LinkedList temp2=clone;
        clone.data=temp.data.clone();

        while(temp.next!=null){
            temp2.next=new LinkedList(temp.next.data.clone());
            temp2=temp2.next;    
            temp=temp.next;
        }
        clone.size=size;
        
        return clone;

    }

    public int size(){
        return size;
    }

    public LinkedList getNext() {
        return next;
    }

    public void setNext(LinkedList next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "LinkedList [data=" + data + ", \nnext=" + next + "]";
    }
    
}
