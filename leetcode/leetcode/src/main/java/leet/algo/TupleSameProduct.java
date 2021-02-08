package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TupleSameProduct {
    private static Logger logger =  LogManager.getLogger(TupleSameProduct.class);

    public int tupleSameProduct(int[] nums) {
        int result=0;
        Arrays.sort(nums);
        Set<Integer> numSet = new HashSet<>();
        numSet.addAll(Arrays.stream(nums).boxed().collect(Collectors.toList()));

//        for(int n1=0;n1<nums.length-3;n1++){//n1,n2,n3,n4
//            for(int n2=n1+1;n2<nums.length-2;n2++){
//                for(int n3=n2+1;n3<nums.length-1;n3++){
//                    Integer num2 = nums[n2];
//                    Integer num3 = nums[n3];
//                    Integer num1 = nums[n1];
//                    if((num2 * num3)% num1 ==0){
//                        int target=num2*num3/num1;
//                        if(num1!=target&&num2!=target&&num3!=target&&numSet.contains(target)){
//                            result++;
//                            //System.out.println(num1+","+num2+","+num3+","+target);
//                        }
//                    }
//                }
//            }
//        }


        for(int n1=0;n1<nums.length-3;n1++){//n1,n2,n3,n4
            for(int n4=n1+3;n4<nums.length;n4++){
                for(int n2=n1+1;n2<n4-1;n2++){
                    Integer num2 = nums[n2];
                    Integer num4 = nums[n4];
                    Integer num1 = nums[n1];
                    if((num1 * num4)% num2 ==0){
                        int target=num1*num4/num2;
                        if(target>num2 && target<num4 && numSet.contains(target)){
                            result++;
                            //logger.info(num1+","+num2+","+target+","+num4);
                        }
                    }
                }
            }
        }

        return result*8;
    }

    public static void main(String[] args){
        TupleSameProduct tupleSameProduct = new TupleSameProduct();
        int ret;

        ret = tupleSameProduct.tupleSameProduct(new int[]{2,3,4,6});
        logger.info(ret);

        ret = tupleSameProduct.tupleSameProduct(new int[]{2,3,4,6,8,12});
        logger.info(ret);
    }
}
