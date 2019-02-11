/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Node;
import java.util.ArrayList;
import java.util.List;

public class JJTOgnlParserState {
    private List<Node> nodes = new ArrayList<Node>();
    private List<Integer> marks = new ArrayList<Integer>();
    private int numNodesOnStack = 0;
    private int currentMark = 0;
    private boolean nodeCreated;

    public boolean nodeCreated() {
        return this.nodeCreated;
    }

    public void reset() {
        this.nodes.clear();
        this.marks.clear();
        this.numNodesOnStack = 0;
        this.currentMark = 0;
    }

    public Node rootNode() {
        return this.nodes.get(0);
    }

    public void pushNode(Node node) {
        this.nodes.add(node);
        ++this.numNodesOnStack;
    }

    public Node popNode() {
        if (--this.numNodesOnStack < this.currentMark) {
            this.currentMark = this.marks.remove(this.marks.size() - 1);
        }
        return this.nodes.remove(this.nodes.size() - 1);
    }

    public Node peekNode() {
        return this.nodes.get(this.nodes.size() - 1);
    }

    public int nodeArity() {
        return this.numNodesOnStack - this.currentMark;
    }

    public void clearNodeScope(Node unused) {
        while (this.numNodesOnStack > this.currentMark) {
            this.popNode();
        }
        this.currentMark = this.marks.remove(this.marks.size() - 1);
    }

    public void openNodeScope(Node node) {
        this.marks.add(this.currentMark);
        this.currentMark = this.numNodesOnStack;
        node.jjtOpen();
    }

    public void closeNodeScope(Node node, int num) {
        this.currentMark = this.marks.remove(this.marks.size() - 1);
        while (num-- > 0) {
            Node poppedNode = this.popNode();
            poppedNode.jjtSetParent(node);
            node.jjtAddChild(poppedNode, num);
        }
        node.jjtClose();
        this.pushNode(node);
        this.nodeCreated = true;
    }

    public void closeNodeScope(Node node, boolean condition) {
        if (condition) {
            int arity = this.nodeArity();
            this.currentMark = this.marks.remove(this.marks.size() - 1);
            while (arity-- > 0) {
                Node poppedNode = this.popNode();
                poppedNode.jjtSetParent(node);
                node.jjtAddChild(poppedNode, arity);
            }
            node.jjtClose();
            this.pushNode(node);
            this.nodeCreated = true;
        } else {
            this.currentMark = this.marks.remove(this.marks.size() - 1);
            this.nodeCreated = false;
        }
    }
}

