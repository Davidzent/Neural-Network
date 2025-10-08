import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Map;

public class Brain {
    private final String[] InputNodeNames = {
        "Pixel (0,0)",
        "Pixel (0,1)",
        "Pixel (1,0)",
        "Pixel (1,1)",
        "BiasNode"
    };
    private final String[] OutputNodeNames = {
        "Solid White",
        "Solid Black",
        "Vertical First",
        "Vertical Second",
        "Diagonal top to bottom",
        "Diagonal bottom to top",
        "Horizontal top",
        "Horizontal bottom"
    };
    private final double WeightMutationRate = 0.8;
    private final double ConnectionMutationRate = 0.05;
    private final double NodeMutationRate = 0.01;
    private static int nextCon = 0;
    private Node biasNode;

    private ArrayList<Connection> connections = new ArrayList<Connection>();
    // private ArrayList<Node> nodes = new ArrayList<Node>();
    private int inputs; // Number of inputs in neural network
    private int outputs; // Number of outputs in neural network
    private int layers = 2; // current number of layers
    private int nextNode = 0; // the next position for a node
    private int biasNodePos; // the current position of a bias node

    private LinkedList network = new LinkedList();

    public Brain(int in, int out) {
        inputs = in;
        outputs = out;
        // create bias node
        biasNode = new Node(nextNode, 0);
        network.AddOnLayer(biasNode);
        biasNodePos = nextNode;
        nextNode++;

        // create inputs
        for (int i = 0; i < inputs; i++) {
            network.AddOnLayer(new Node(nextNode, 0));
            nextNode++;
        }
        
        // create outputs
        for (int i = 0; i < outputs; i++) {
            network.AddOnLayer(new Node(nextNode, 1));
            nextNode++;
        }
    }

    public Brain(int in, int out, boolean cross) {
        inputs = in;
        outputs = out;
    }

    // Sorts the network it is already done in the linked list
    // public void generateNetwork() {
    // connectNodes();
    // network = new LinkedList();
    // for (int i = 0; i < network.size(); i++) {
    // network.AddOnLayer(network.getByID(i));
    // }
    // System.out.println(network);
    // }

    public void connectNodes() {
        network.clearAllConnections();

        for (int i = 0; i < connections.size(); i++) {// for each connection
            connections.get(i).getStart().addConnection(connections.get(i));// add it to node
        }
    }

    public double[] feedForward(double[] inputValues) {
        // set the outputs of the input nodes
        // System.out.println(network);
        network.setInputsResponse(inputValues);

        network.engageAll();// for each node in the network engage it

        // the outputs are nodes[inputs] to nodes [inputs+outputs-1]
        double[] outs = network.getOutputsResponse(outputs);

        network.resetInputsSum();

        return outs;
    }

    public int getInnovationNumber(ArrayList<ConnectionHistory> innovationHistory, Node from, Node to) {
        boolean isNew = true;
        int connectionInnovationNumber = nextCon;
        for (int i = 0; i < innovationHistory.size(); i++) {// for each previous mutation
            if (innovationHistory.get(i).matches(this, from, to)) {// if match found
                isNew = false;// its not a new mutation
                connectionInnovationNumber = innovationHistory.get(i).innovationNumber; // set the innovation number as
                                                                                        // the innovation number of the
                                                                                        // match
                break;
            }
        }

        if (isNew) {// if the mutation is new then create an arrayList of integers representing the
                    // current state of the genome
            ArrayList<Integer> innoNumbers = new ArrayList<Integer>();
            for (int i = 0; i < connections.size(); i++) {// set the innovation numbers
                innoNumbers.add(connections.get(i).getID());
            }

            // then add this mutation to the innovationHistory
            innovationHistory
                    .add(new ConnectionHistory(from.getID(), to.getID(), connectionInnovationNumber, innoNumbers));
            nextCon++;
        }
        return connectionInnovationNumber;
    }

    public void addConnection(ArrayList<ConnectionHistory> innovationHistory) {
        // cannot add a connection to a fully connected network
        if (network.MaxConnections() == connections.size()) {
            System.out.println("connection failed");
            return;
        }

        // get random nodes
        int random1 = (int) (Math.random() * network.size());
        int random2 = (int) (Math.random() * network.size());
        Node randomNode1 = network.getByID(random1);
        Node randomNode2 = network.getByID(random2);

        while (checkNewConnection(randomNode1, randomNode2)) {// while the random nodes are no good
            // get new ones
            random1 = (int) (Math.random() * network.size());
            random2 = (int) (Math.random() * network.size());
            randomNode1 = network.getByID(random1);
            randomNode2 = network.getByID(random2);
        }

        if (randomNode1.getLayer() > randomNode2.getLayer()) {// swap if the first random node is after the second
            Node temp;
            temp = randomNode2;
            randomNode2 = randomNode1;
            randomNode1 = temp;
        }

        // get the innovation number of the connection
        // this will be a new number if no identical genome has mutated in the same way
        int connectionInnovationNumber = getInnovationNumber(innovationHistory, randomNode1, randomNode2);
        // add the connection with a random array

        connections.add(new Connection(randomNode1, randomNode2, (Math.random() * 2 - 1),
                connectionInnovationNumber));// changed this so if error here
        connectNodes();
    }

    public boolean checkNewConnection(Node r1, Node r2) {
        if (r1.getLayer() == r2.getLayer())
            return true; // if the nodes are in the same layer
        if (r1.isConnected(r2))
            return true; // if the nodes are already connected

        return false;
    }
    public boolean onlyBiasConnections(){
        for(int i=0;i<connections.size();i++){
            if(connections.get(i).getStart()!=biasNode)return false;
        }

        return true;
    }

    public void addNode(ArrayList<ConnectionHistory> innovationHistory) {
        // pick a random connection to create a node between
        if (connections.size() == 0||onlyBiasConnections()) {
            addConnection(innovationHistory);
            return;
        }

        int randomConnection = (int) (Math.random() * connections.size());
        int cnt = 0;

        while (connections.get(randomConnection).getStart() == biasNode && connections.size() != 1) {// dont
            // disconnect
            // bias

            randomConnection = (int) (Math.random() * connections.size());
        }

        Connection temp = connections.get(randomConnection);

        temp.setEnabled(false);// disable it

        int newNodeNo = nextNode;
        Node newNode = new Node(newNodeNo);
        int SLayer = temp.getStart().getLayer();
        int ELayer = temp.getEnd().getLayer();
        int NLayer = (ELayer - SLayer) / 2;
        newNode.setLayer(NLayer);
        if (NLayer == 0) {
            newNode.setLayer(ELayer);
            network.AddOnBetweenLayer(newNode);
            layers++;
        } else {

            network.AddOnLayer(newNode);
        }

        nextNode++;

        // add a new connection from the start of the disabled connection to the new
        // node with a weight of 1
        int connectionInno = getInnovationNumber(innovationHistory, temp.getStart(), newNode);
        connections.add(new Connection(temp.getStart(), newNode, 1, connectionInno));

        connectionInno = getInnovationNumber(innovationHistory, newNode, temp.getEnd());
        // add a new connection from the new node to the end of the disconnected
        // connection with a weight the same as the disabled
        connections.add(new Connection(newNode, temp.getEnd(), temp.getWeight(), connectionInno));
        // newNode.setLayer(temp.getStart().getLayer() + 1);

        connectionInno = getInnovationNumber(innovationHistory, biasNode, newNode);
        // connect the bias to the new node with a weight of 0
        connections.add(new Connection(biasNode, newNode, 0, connectionInno));

        connectNodes();
    }

    public void mutate(ArrayList<ConnectionHistory> innovationHistory) {
        if (connections.size() == 0) {
            addConnection(innovationHistory);
        }

        double rand = Math.random();
        if (rand < WeightMutationRate) { // 80% of the time mutate weights
            for (int i = 0; i < connections.size(); i++) {
                connections.get(i).mutateWeight();
            }
        }
        // 5% of the time add a new connection
        rand = Math.random();
        if (rand < ConnectionMutationRate) {
            addConnection(innovationHistory);
        }

        // 1% of the time add a node
        rand = Math.random();
        if (rand < NodeMutationRate) {
            addNode(innovationHistory);
        }
    }

    public Brain crossover(Brain parent2) {
        Brain child = new Brain(inputs, outputs, true);
        child.connections.clear();
        child.network = new LinkedList();
        child.layers = layers;
        child.nextNode = nextNode;
        child.biasNodePos = biasNodePos;

        ArrayList<Connection> childConnections = new ArrayList<Connection>();// list of connections to be inherrited
                                                                             // form
                                                                             // the parents
        ArrayList<Boolean> isEnabled = new ArrayList<Boolean>();
        // all inherrited connections
        for (int i = 0; i < connections.size(); i++) {
            boolean setEnabled = true;// is this node in the chlid going to be enabled

            int parent2gene = matchingGene(parent2, connections.get(i).getID());
            if (parent2gene != -1) {// if the connections match
                if (!connections.get(i).isEnabled() || !parent2.connections.get(parent2gene).isEnabled()) {// if either
                                                                                                           // of the
                                                                                                           // matching
                    // connections are disabled

                    if (Math.random() < 0.75) {// 75% of the time disabel the childs gene
                        setEnabled = false;
                    }
                }
                double rand = Math.random();
                // Decide who's connection it will get
                if (rand < 0.5) {
                    childConnections.add(connections.get(i));
                } else {
                    childConnections.add(parent2.connections.get(parent2gene));
                }
            } else {// disjoint or excess gene
                childConnections.add(connections.get(i));
                setEnabled = connections.get(i).isEnabled();
            }
            isEnabled.add(setEnabled);
        }

        // since all excess and disjoint connections are inherrited from the more fit
        // parent
        // (this brain) the childs structure is no different from this parent | with
        // exception of dormant connections being enabled but this wont effect nodes
        // so all the nodes can be inherrited from this parent
        child.network = network.clone();

        child.biasNode = child.network.getByID(biasNodePos);

        // clone all the connections so that they connect the childs new nodes
        for (int i = 0; i < childConnections.size(); i++) {
            child.connections.add(childConnections.get(i).clone(child.network.getByID(childConnections.get(i).getStart().getID()), child.network.getByID(childConnections.get(i).getEnd().getID())));
            child.connections.get(i).setEnabled(isEnabled.get(i));
        }

        child.connectNodes();
        return child;
    }

    public int matchingGene(Brain parent2, int innovationNumber) {
        for (int i = 0; i < parent2.connections.size(); i++) {
            if (parent2.connections.get(i).getID() == innovationNumber) {
                return i;
            }
        }
        return -1; // no matching gene found
    }

    public Brain clone() {
        Brain clone = new Brain(inputs, outputs, true);

        clone.network = network.clone();

        // copy all the connections so that they connect the clone new nodes

        for (int i = 0; i < connections.size(); i++) {// copy genes
            clone.connections.add(connections.get(i).clone(clone.network.getByID(connections.get(i).getStart().getID()), clone.network.getByID(connections.get(i).getEnd().getID())));
        }

        clone.layers = layers;
        clone.nextNode = nextNode;
        clone.biasNodePos = biasNodePos;
        clone.biasNode = clone.network.getByID(biasNodePos);

        clone.connectNodes();

        return clone;
    }

    // Getters and Seters
    public ArrayList<Connection> getConections() {
        return connections;
    }

    public void setConections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public int getInputs() {
        return inputs;
    }

    public void setInputs(int inputs) {
        this.inputs = inputs;
    }

    public int getOutputs() {
        return outputs;
    }

    public void setOutputs(int outputs) {
        this.outputs = outputs;
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }

    public int getNextNode() {
        return nextNode;
    }

    public void setNextNode(int nextNode) {
        this.nextNode = nextNode;
    }

    public int getbiasNodePos() {
        return biasNodePos;
    }

    public void setbiasNodePos(int biasNodePos) {
        this.biasNodePos = biasNodePos;
    }

    public LinkedList getNetwork() {
        return network;
    }

    public void setNetwork(LinkedList network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "Brain [WeightMutationRate=" + WeightMutationRate + ", ConnectionMutationRate=" + ConnectionMutationRate
                + ", NodeMutationRate=" + NodeMutationRate + ", \nconnections=" + connections
                + ", \ninputs=" + inputs + ", \noutputs=" + outputs + ", \nlayers=" + layers
                + ", \nnextNode=" + nextNode
                + ", \nbiasNodePos=" + biasNodePos
                + ", \nnetwork=" + network + "]";
    }

    public void show(int startX, int startY, int w, int h) {
        ArrayList<PVector> nodePoses = new ArrayList<PVector>();
        ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();

        // get the positions on the screen that each node is supposed to be in

        // split the nodes into layers
        Map<Integer, Integer> layerC = network.NodesInLayers();

        // for each layer add the position of the node on the screen to the node posses
        // arraylist
        int cnt = 0;
        for (int i = 0; layerC.containsKey(i); i++) {
            int x = startX + ((i) * w) / (layers - 1);
            for (int j = 0; j < layerC.get(i); j++) {// for the position in the layer
                int y = startY + ((j + 1) * h) / (layerC.get(i) + 1);
                nodePoses.add(new PVector(x, y));
                nodeNumbers.add(network.get(cnt).getID());
                cnt++;
                if (i == layers - 1) {
                    // println(i,j,x,y);

                }
            }
        }

        // draw connections
        Graphics2D g2D = (Graphics2D) App.frame.getGraphics();
        g2D.clearRect(600, 0, 1040, 750);
        g2D.drawLine(startX, startY, w, h);
        g2D.setStroke(new BasicStroke(0f));
        g2D.setColor(Color.black);
        // stroke(0);
        // strokeWeight(2);
        for (int i = 0; i < connections.size(); i++) {
            if (!connections.get(i).isEnabled())
                continue;
            PVector from;
            PVector to;
            from = nodePoses.get(nodeNumbers.indexOf(connections.get(i).getStart().getID()));
            to = nodePoses.get(nodeNumbers.indexOf(connections.get(i).getEnd().getID()));
            g2D.setStroke(new BasicStroke((float) Math.abs(connections.get(i).getWeight()*5.0f)));
            if (connections.get(i).getWeight() > 0) {
                g2D.setColor(Color.blue);
            } else {
                g2D.setColor(Color.red);
            }
            // strokeWeight(map(Math.abs((float) connections.get(i).weight), 0, 1, 0, 5));
            g2D.drawLine(from.x+10, from.y+10, to.x+10, to.y+10);
            // line(from.x, from.y, to.x, to.y);
        }

        // draw nodes last so they appear ontop of the connection lines
        g2D.setColor(Color.BLACK);
        cnt = 0;
        g2D.setStroke(new BasicStroke(1));
        // g2D.setFont(10);
        for (int i = 0; i < nodePoses.size(); i++) {
            // fill(255);
            // stroke(0);
            // strokeWeight(1);
            g2D.drawOval(nodePoses.get(i).x, nodePoses.get(i).y, 20, 20);
            // textSize(10);
            // fill(0);
            // textAlign(CENTER, CENTER);
            g2D.drawString("" + nodeNumbers.get(i), nodePoses.get(i).x, nodePoses.get(i).y);
            if(i<inputs+1){
                g2D.drawString(InputNodeNames[i], nodePoses.get(i).x-60, nodePoses.get(i).y+13);                
            }
            else if(i>=nodePoses.size()-OutputNodeNames.length){
                g2D.drawString(OutputNodeNames[cnt++], nodePoses.get(i).x+60, nodePoses.get(i).y+13);
            }
            // text(nodeNumbers.get(i), nodePoses.get(i).x, nodePoses.get(i).y);
        }
        cnt=0;
        System.out.print("");
    }

}