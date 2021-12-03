package offer.q3;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Tree {
    Integer label;
    @JSONField(serialize = false)
    Tree parent;
    @JSONField
    Map<Integer, Tree> next = new HashMap<>();


    public Tree(char label, Tree parent) {
        this.label = (int) label;
        this.parent = parent;
    }

    public Tree() {
    }

    public Tree findchild(char c) {
        if (!next.containsKey(c)) {
            next.put((int) c, new Tree(c, this));
        }
        return next.get((int) c);
    }

//    public void print() {
//        System.out.println(next.keySet().stream().map(c -> (char) c.intValue()).collect(Collectors.toList()));
//        next.values().forEach(Tree::print);
//    }
}
