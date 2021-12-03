package offer.q30;

import java.util.Stack;

class MinStack {

    Stack<Integer> base = new Stack<>();
    Stack<Integer> mins = new Stack<>();

    /**
     * initialize your data structure here.
     */
    public MinStack() {

    }

    public void push(int x) {
        base.push(x);
        if (mins.isEmpty() || mins.peek() > x) {
            mins.push(x);
        } else mins.push(mins.peek());
    }

    public void pop() {
        base.pop();
        mins.pop();
    }

    public int top() {
        return base.peek();
    }

    public int min() {
        return mins.peek();
    }
}

class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int l1 = nums1.length;
        int l2 = nums2.length;
        int t = l1 + l2;
        if (t % 2 != 0) {
            int midindex = (t - 1) / 2;
            if (midindex >= l1) {
                return nums2[midindex - l1];
            } else {
                return nums1[midindex];
            }
        } else {
            int right = t / 2;
            int left = (t / 2) - 1;
            if (left >= l1) {
                return Double.valueOf(nums2[left - l1] + nums2[right - l1]) / 2.0000;
            } else if (right >= l1) {
                return Double.valueOf(nums1[left] + nums2[right - l1]) / 2.0000;
            } else {
                return Double.valueOf(nums1[left] + nums1[right]) / 2.0000;
            }
        }
    }

    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}