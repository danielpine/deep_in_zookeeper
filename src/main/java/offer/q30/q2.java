package offer.q30;

import java.math.BigDecimal;

/**
 * Definition for singly-linked list.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    public int getVal() {
        return val;
    }

    public ListNode getNext() {
        return next;
    }
}
class Solution1 {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        String n1 = String.valueOf(l1.val);
        while ((l1 = l1.next) != null) {
            n1= String.valueOf(l1.val).concat(n1);
        }
        String n2 = String.valueOf(l2.val);
        while ((l2 = l2.next) != null) {
            n2=String.valueOf(l2.val).concat(n2);
        }

        String[] split = new BigDecimal(n1).add(new BigDecimal(n2)).toPlainString().split("");
        ListNode node = new ListNode(Integer.parseInt(split[split.length - 1]));
        ListNode head = node;

        for (int i = split.length - 2; i >= 0; i--) {
            node.next = new ListNode(Integer.parseInt(split[i]));
            node = node.next;
        }
        return head;
    }
}
