package HomeMade.Tools;
import ij.*;

public class pPos {
	
		int xp;
		int yp;
		int max;
		
		pPos(int x, int y, int max){
			xp=x;
			yp=y;
			this.max=max;
		}

		public int getX(){
			return xp;
		}

		public int getY(){
			return yp;
		}

		public int getM(){
			return max;
		}

	}
