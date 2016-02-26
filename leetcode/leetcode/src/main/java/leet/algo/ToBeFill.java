package leet.algo;


class ToBeFill{
	int x;
	int y;
	char v;
	
	ToBeFill(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public static boolean isFit(char[][] board, ToBeFill tbf){
		board[tbf.x][tbf.y]=tbf.v;
		int sum=0;
		int unfill=0;
		for (int i=0;i<9; i++){
			if (i!=tbf.x && board[i][tbf.y]==board[tbf.x][tbf.y]){
				board[tbf.x][tbf.y]='.';
				return false;
			}
			if (board[i][tbf.y]!='.'){
				sum +=(board[i][tbf.y]-'0');
			}else{
				unfill++;
			}
			
		}
		if (unfill>0 && sum >= 45 || unfill==0 && sum!=45){
			board[tbf.x][tbf.y]='.';
			return false;
		}
		
		sum =0;
		unfill=0;
		for (int j=0; j<9; j++){
			if (j!=tbf.y && board[tbf.x][j]==board[tbf.x][tbf.y]){
				board[tbf.x][tbf.y]='.';
				return false;
			}
			if (board[tbf.x][j]!='.'){
				sum += (board[tbf.x][j]-'0');
			}else{
				unfill++;
			}			
		}
		if (unfill>0 && sum >= 45 || unfill==0 && sum!=45){
			board[tbf.x][tbf.y]='.';
			return false;
		}
		
		sum =0;
		unfill=0;
		int a = tbf.x/3;
		int b = tbf.y/3;
		for (int i=0; i<3;i++){
			for (int j=0; j<3; j++){
				if (3*a+i!=tbf.x && 3*b+j!=tbf.y && board[3*a+i][3*b+j]==board[tbf.x][tbf.y]){
					board[tbf.x][tbf.y]='.';
					return false;
				}
				if (board[3*a+i][3*b+j]!='.'){
					sum += (board[3*a+i][3*b+j]-'0');
				}else{
					unfill++;
				}
			}
		}
		if (unfill>0 && sum >= 45 || unfill==0 && sum!=45){
			board[tbf.x][tbf.y]='.';
			return false;
		}
		
		board[tbf.x][tbf.y]='.';
		return true;
	}
}