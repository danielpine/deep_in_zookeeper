package offer.q09;

import java.util.Stack;

class CQueue {
    Stack<Integer> base=new Stack<>();
    Stack<Integer> func=new Stack<>();
    public CQueue() {

    }

    public void appendTail(int value) {
        base.push(value);
    }

    public int deleteHead() {
        if(base.empty()){return -1;}
        return base.remove(0);
    }
}

/**
 * Your CQueue object will be instantiated and called as such:
 * CQueue obj = new CQueue();
 * obj.appendTail(value);
 * int param_2 = obj.deleteHead();
 */
