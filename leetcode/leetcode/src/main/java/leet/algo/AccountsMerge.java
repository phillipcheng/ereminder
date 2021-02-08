package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AccountsMerge {

    private static Logger logger =  LogManager.getLogger(AccountsMerge.class);

    Map<String, List<Account>> emailAccountMap = new HashMap<>();//email to account
    Map<Integer, MergedAccount> mergedAccountMap = new HashMap<>();//account idx to merged account
    Set<MergedAccount> mergedAccountSet = new HashSet<>();

    class MergedAccount{
        String name;
        TreeSet<Integer> accountIdxList = new TreeSet<>();
        TreeSet<String> emails = new TreeSet<>();

        public MergedAccount(Account account){
            this.name = account.name;
            accountIdxList.add(account.idx);
            emails.addAll(account.emails);
        }

        public MergedAccount(){}

        public String toString(){
            return "account idx:" + accountIdxList + ", emails:" + emails;
        }
    }

    class Account{
        int idx;
        String name;
        TreeSet<String> emails;

        public Account(List<String> info, int idx){
            this.idx = idx;
            name = info.get(0);
            emails = new TreeSet<>();
            for (int i=1; i<info.size(); i++){
                emails.add(info.get(i));
            }
        }
        public String toString(){
            return String.format("idx:%d, name:%s, emails:%s", idx, name, emails);
        }
    }

    MergedAccount mergeAccount(MergedAccount thisMergedAccount, MergedAccount thatMergedAccount){

        MergedAccount ret = new MergedAccount();
        ret.name = thisMergedAccount.name;
        ret.accountIdxList.addAll(thisMergedAccount.accountIdxList);
        ret.accountIdxList.addAll(thatMergedAccount.accountIdxList);
        ret.emails.addAll(thisMergedAccount.emails);
        ret.emails.addAll(thatMergedAccount.emails);

        for (Integer i: thisMergedAccount.accountIdxList){
            mergedAccountMap.remove(i);
            mergedAccountSet.remove(thisMergedAccount);
        }
        for (Integer i: thatMergedAccount.accountIdxList){
            mergedAccountMap.remove(i);
            mergedAccountSet.remove(thatMergedAccount);
        }
        for (Integer i: ret.accountIdxList){
            mergedAccountMap.put(i, ret);
        }
        mergedAccountSet.add(ret);
        //logger.info(String.format("this:%s, that:%s, merged:%s", thisMergedAccount, thatMergedAccount, ret));
        return ret;
    }

    void mergeAccounts(List<Account> accounts){
        //logger.info("try to merge: " + accounts);
        Account account = accounts.get(0);
        MergedAccount thisMergedAccount = mergedAccountMap.get(account.idx);
        if (thisMergedAccount == null){
            thisMergedAccount = new MergedAccount(account);
            //1st account has no merged account
            mergedAccountSet.add(thisMergedAccount);
            mergedAccountMap.put(account.idx, thisMergedAccount);
        }

        for (int i=1; i<accounts.size(); i++){
            Account thatAccount = accounts.get(i);
            MergedAccount thatMergedAccount = mergedAccountMap.get(thatAccount.idx);
            if (thatMergedAccount == null){
                thatMergedAccount = new MergedAccount(thatAccount);
            }
            if (thisMergedAccount!=thatMergedAccount) {
                thisMergedAccount = mergeAccount(thisMergedAccount, thatMergedAccount);
            }
        }
    }

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        for (int i=0; i<accounts.size(); i++){
            List<String> info = accounts.get(i);
            Account account = new Account(info, i);
            for (String email: account.emails){
                List<Account> alist;
                if (!emailAccountMap.containsKey(email)){
                    alist = new ArrayList<>();
                    emailAccountMap.put(email, alist);
                }else{
                    alist = emailAccountMap.get(email);
                }
                alist.add(account);
            }
        }
        for (String email: emailAccountMap.keySet()){
            List<Account> accounts1 = emailAccountMap.get(email);
            mergeAccounts(accounts1);
            //logger.info("merged account:" + mergedAccountMap);

        }
        Iterator<MergedAccount> it = mergedAccountSet.iterator();
        List<List<String>> ret = new ArrayList<>();
        while (it.hasNext()){
            MergedAccount mergedAccount =it.next();
            List<String> list = new ArrayList<>();
            list.add(mergedAccount.name);
            list.addAll(mergedAccount.emails);
            ret.add(list);
        }
        return ret;
    }

    public static void main(String[] args){
        AccountsMerge accountsMerge = new AccountsMerge();
        List<List<String>> accounts;
        List<List<String>> ret;
        accounts = IOUtil.getStringListList(" [[John, johnsmith@mail.com, john00@mail.com],[John, johnnybravo@mail.com],[John, johnsmith@mail.com, john_newyork@mail.com],[Mary, mary@mail.com]]");
        logger.info(accounts);
        ret = accountsMerge.accountsMerge(accounts);//[[John, 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com'],  [John, johnnybravo@mail.com], [Mary, mary@mail.com]]
        logger.info(ret);


    }
}
