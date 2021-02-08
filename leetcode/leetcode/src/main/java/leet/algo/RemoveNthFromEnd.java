package leet.algo;

import algo.util.IOUtil;
import algo.util.ListNode;
import algo.util.ListNodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class RemoveNthFromEnd {
    private static Logger logger =  LogManager.getLogger(RemoveNthFromEnd.class);

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode a = head;
        ListNode b = head;
        ListNode c = head;//a->b->------>c
        for (int i=0; i<n; i++){
            c = c.next;
        }
        logger.info(String.format("a:%d, b:%d, c:%d", a!=null?a.val:-1, b!=null?b.val:-1, c!=null?c.val:-1));
        while (c!=null){
            c= c.next;
            if (b!=head) {
                a = a.next;
            }
            b= b.next;
            logger.info(String.format("a:%d, b:%d, c:%d", a!=null?a.val:-1, b!=null?b.val:-1, c!=null?c.val:-1));
        }
        if (a==b){
            if (b.next!=null){
                head = b.next;
            }else {
                return null;
            }
        }else {
            a.next = b.next;
        }
        return head;
    }

    public static void main(String[] args){
        RemoveNthFromEnd removeNthFromEnd = new RemoveNthFromEnd();
        ListNode ret;
        String strRet;
        int n;
        ListNode head;

        head = ListNodeUtil.getLN("[1,2,3,4,5]");
        n = 2;
        ret = removeNthFromEnd.removeNthFromEnd(head, n);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("1,2,3,5,"));


        head = ListNodeUtil.getLN("[1,2]");
        n = 1;
        ret = removeNthFromEnd.removeNthFromEnd(head, n);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("1,"));

        head = ListNodeUtil.getLN("[1]");
        n = 1;
        ret = removeNthFromEnd.removeNthFromEnd(head, n);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals(""));

        head = ListNodeUtil.getLN("[1,2]");
        n = 2;
        ret = removeNthFromEnd.removeNthFromEnd(head, n);
        strRet = ListNodeUtil.toString(ret);
        logger.info(strRet);
        assertTrue(strRet.equals("2,"));

    }
}
