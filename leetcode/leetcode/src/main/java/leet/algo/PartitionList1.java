package leet.algo;

import algo.util.ListNode;
import algo.util.ListNodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class PartitionList1 {
    private static Logger logger =  LogManager.getLogger(PartitionList1.class);
    public ListNode partition(ListNode head, int x) {
        if (head == null) return null;
        ListNode smallHead, bigHead, smallTail, bigTail;
        smallHead = bigHead = smallTail = bigTail = null;
        while (head != null){
            if (head.val < x){
                if (smallHead == null){
                    smallHead = head;
                    smallTail = head;
                }else{
                    smallTail.next = head;
                    smallTail = head;
                }
            }else{//>=x
                if (bigHead == null){
                    bigHead = head;
                    bigTail = head;
                }else{
                    bigTail.next = head;
                    bigTail = head;
                }
            }
            head = head.next;
        }
        if (bigTail!=null) {
            bigTail.next = null;
        }
        if (smallTail!=null){
            if (bigHead!=null) {
                smallTail.next = bigHead;
            }else{
                smallTail.next = null;
            }
            return smallHead;
        }else{
            return bigHead;
        }

    }

    public static void main(String[] args){
        PartitionList1 pl = new PartitionList1();
        ListNode head, ret;
        int x;
        String strRet;

        head = ListNodeUtil.getLN("1,4,3,2,5,2");
        x = 3;
        ret = pl.partition(head, x);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("1,2,2,4,3,5,"));

        head = ListNodeUtil.getLN("");
        x = 0;
        ret = pl.partition(head, x);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals(""));

        head = ListNodeUtil.getLN("1");
        x = 2;
        ret = pl.partition(head, x);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("1,"));

    }

}
