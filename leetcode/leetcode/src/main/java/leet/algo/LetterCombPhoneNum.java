package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;

//Given a digit string, return all possible letter combinations that the number could represent.

public class LetterCombPhoneNum {

	class CharPos{
		char c;
		int pos; //current idx from 0
		int max; //max number

		CharPos(char c, int pos, int max){
			this.c = c;
			this.pos = pos;
			this.max = max;
		}

		//return false for carry
		boolean inc(){
			if (pos<max-1){
				pos++;
				return true;
			}else{
				pos=0;
				return false;
			}
		}

		public String toString(){
			return "digit:" + c + ", curPos:" + pos + ", max:" + max;
		}
	}

	class CharListPos{
		ArrayList<CharPos> curPos = new ArrayList<CharPos>();

		void add(CharPos cp){
			curPos.add(cp);
		}

		//return false means reached the biggest
		boolean inc(){
			for (int i=curPos.size()-1; i>=0; i--){
				CharPos cp = curPos.get(i);
				if (cp.inc()){
					return true;
				}
			}
			return false;
		}

		public String toString(){
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<curPos.size(); i++){
				CharPos cp = curPos.get(i);
				sb.append(cp.toString() + "\n");
			}
			return sb.toString();
		}

		String getCurString(HashMap<Character, char[]> mappingTable){
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<curPos.size(); i++){
				CharPos cp = curPos.get(i);
				sb.append(mappingTable.get(cp.c)[cp.pos]);
			}
			return sb.toString();
		}
	}

	public static HashMap<Character, char[]> table = new HashMap<Character, char[]>();
	
	static{
		//table.put('1', new char[]{});
		table.put('2', new char[]{'a','b','c'});
		table.put('3', new char[]{'d','e','f'});
		table.put('4', new char[]{'g','h','i'});
		table.put('5', new char[]{'j','k','l'});
		table.put('6', new char[]{'m','n','o'});
		table.put('7', new char[]{'p','q','r','s'});
		table.put('8', new char[]{'t','u','v'});
		table.put('9', new char[]{'w','x','y', 'z'});
		table.put('0', new char[]{' '});
	}
	
	public ArrayList<String> letterCombinations(String digits) {
		ArrayList<String> result = new ArrayList<String>();
		CharListPos clp = new CharListPos();
        char[] chars = digits.toCharArray();
        for (int i=0; i<chars.length; i++){
        	char c = chars[i];
        	char[] ca = table.get(c);
        	if (ca!=null){
        		clp.add(new CharPos(c,0, ca.length));
        	}
        }
        result.add(clp.getCurString(table));
        while (clp.inc()){
        	result.add(clp.getCurString(table));	
        }
        return result;
    }
}
