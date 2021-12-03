package offer.q3;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class Solution {

    public int lengthOfLongestSubstring(String s) {
        Tree root = new Tree();
        Set<Tree> curr = new HashSet<>();
        char[] chars = s.toCharArray();
        for (char c : chars) {
            curr.forEach(node -> node.findchild(c));
            curr.add(root.findchild(c));
        }
//        root.print();
        String s1 = JSONObject.toJSONString(root, true);
        System.out.println(s1);
        return 0;
    }

    public static void main(String[] args) {
        new Solution().lengthOfLongestSubstring("abc");
    }
}
