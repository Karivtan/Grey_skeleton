package HomeMade.Tools;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import java.util.*;
import HomeMade.Tools.*;

public class Grey_Skeleton implements PlugIn {

	static int W, H;
	static double maxVal;
	static double max32;
	static ImageProcessor ip;
	static ImagePlus ipnew;
	static ImagePlus Skel;
	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		Skel (imp);
	}

	public void Skel(ImagePlus imp){
		IJ.run(imp, "8-bit", "");
		W=imp.getWidth();
		H=imp.getHeight();
		ipnew = new Duplicator().run(imp);
		ImageProcessor ip = ipnew.getProcessor();
		// this needs to be in the loop for going through multiple levels
		ImageStatistics is=imp.getStatistics();
		maxVal=0; //255 for light background images. 0 used for black background
		boolean passed =false;
		
		// here comes a while loop that checks if the picture was changed compare to the previous loop
		int morePasses=1;
		int count=0;
		IJ.log("starting");
		boolean proj255=false;
		while (morePasses!=0){
			// create an arraylist with removed pixel positions
			count++;
			ArrayList<pPos> removedPix= new ArrayList<pPos>();
			ArrayList<xyPos> rPix= new ArrayList<xyPos>();
			for (int i=0;i<W;i++){
				Check1:
				for (int j=0; j<H;j++){ // itterating through pixels
					//IJ.log(""+imp.getPixel(i,j)[0]);
					if (imp.getPixel(i,j)[0]==maxVal){ // if the pixels is background continue
						ip.putPixel(i,j,imp.getPixel(i,j)[0]);
						continue Check1;
					} else { // otherwise check neighbour values
						double Kval=0;
						double PixMax=0;
						int [] SV=new int [8];
						if (i==0 && j==0){ // left top
							Kval = ((double)(imp.getPixel(i,j+1)[0]+imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j+1)[0]))/3;
							PixMax=Math.max(imp.getPixel(i,j+1)[0],Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j+1)[0]));
							SV[0]=0;
							SV[1]=0;
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=getP(Kval,imp.getPixel(i+1,j+1)[0]);
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=0;
							SV[6]=0;
							SV[7]=0;
						} else if (i==0 && j==H-1){ //left bottom
							Kval = ((double)(imp.getPixel(i,j-1)[0]+imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j-1)[0]))/3;
							PixMax=Math.max(imp.getPixel(i,j-1)[0],Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j-1)[0]));
							SV[0]=getP(Kval,imp.getPixel(i,j-1)[0]);
							SV[1]=getP(Kval,imp.getPixel(i+1,j-1)[0]);
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=0;
							SV[4]=0;
							SV[5]=0;
							SV[6]=0;
							SV[7]=0;
						} else if (j==0 && i==W-1){ //right top
							Kval = ((double)(imp.getPixel(i,j+1)[0]+imp.getPixel(i-1,j+1)[0]+imp.getPixel(i-1,j)[0]))/3;
							PixMax=Math.max(imp.getPixel(i,j+1)[0],Math.max(imp.getPixel(i-1,j+1)[0],imp.getPixel(i-1,j)[0]));
							SV[0]=0;
							SV[1]=0;
							SV[2]=0;
							SV[3]=0;
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=getP(Kval,imp.getPixel(i-1,j+1)[0]);
							SV[6]=getP(Kval,imp.getPixel(i-1,j)[0]);
							SV[7]=0;
						} else if (j==H-1 && i==W-1){ //right bottom
							Kval = ((double)(imp.getPixel(i,j-1)[0]+imp.getPixel(i-1,j)[0]+imp.getPixel(i-1,j-1)[0]))/3;
							PixMax=Math.max(imp.getPixel(i+1,j)[0],Math.max(imp.getPixel(i+1,j+1)[0],imp.getPixel(i,j+1)[0]));
							SV[0]=0;
							SV[1]=0;
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=getP(Kval,imp.getPixel(i+1,j+1)[0]);
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=0;
							SV[6]=0;
							SV[7]=0;
						} else if (i==0){ //left
							Kval = ((double)(imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j+1)[0]+imp.getPixel(i,j+1)[0]+imp.getPixel(i,j-1)[0]+imp.getPixel(i+1,j-1)[0]))/5;
							double m1 = Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j+1)[0]);
							double m2 = Math.max(imp.getPixel(i,j+1)[0],imp.getPixel(i,j-1)[0]);
							PixMax=Math.max(imp.getPixel(i+1,j-1)[0],Math.max(m1,m2));
							SV[0]=getP(Kval,imp.getPixel(i,j-1)[0]);
							SV[1]=getP(Kval,imp.getPixel(i+1,j-1)[0]);
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=getP(Kval,imp.getPixel(i+1,j+1)[0]);
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=0;
							SV[6]=0;
							SV[7]=0;
						} else if (j==0){ //top
							Kval = ((double)(imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j+1)[0]+imp.getPixel(i,j+1)[0]+imp.getPixel(i-1,j+1)[0]+imp.getPixel(i-1,j)[0]))/5;
							double m1 = Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j+1)[0]);
							double m2 = Math.max(imp.getPixel(i,j+1)[0],imp.getPixel(i-1,j+1)[0]);
							PixMax=Math.max(imp.getPixel(i-1,j)[0],Math.max(m1,m2));
							SV[0]=0;
							SV[1]=0;
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=getP(Kval,imp.getPixel(i+1,j+1)[0]);
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=getP(Kval,imp.getPixel(i-1,j+1)[0]);
							SV[6]=getP(Kval,imp.getPixel(i-1,j)[0]);
							SV[7]=0;
						} else if (i==W-1){ //right
							Kval = ((double)(imp.getPixel(i-1,j)[0]+imp.getPixel(i-1,j+1)[0]+imp.getPixel(i,j+1)[0]+imp.getPixel(i,j-1)[0]+imp.getPixel(i-1,j-1)[0]))/5;
							double m1 = Math.max(imp.getPixel(i-1,j)[0],imp.getPixel(i-1,j+1)[0]);
							double m2 = Math.max(imp.getPixel(i,j+1)[0],imp.getPixel(i,j-1)[0]);
							PixMax=Math.max(imp.getPixel(i-1,j-1)[0],Math.max(m1,m2));
							SV[0]=getP(Kval,imp.getPixel(i,j-1)[0]);
							SV[1]=0;
							SV[2]=0;
							SV[3]=0;
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=getP(Kval,imp.getPixel(i-1,j+1)[0]);
							SV[6]=getP(Kval,imp.getPixel(i-1,j)[0]);
							SV[7]=getP(Kval,imp.getPixel(i-1,j-1)[0]);
						} else if (j==H-1){ // bottom
							Kval = ((double)(imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j-1)[0]+imp.getPixel(i,j-1)[0]+imp.getPixel(i-1,j-1)[0]+imp.getPixel(i-1,j)[0]))/5;
							double m1 = Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j-1)[0]);
							double m2 = Math.max(imp.getPixel(i,j-1)[0],imp.getPixel(i-1,j-1)[0]);
							PixMax=Math.max(imp.getPixel(i-1,j)[0],Math.max(m1,m2));
							SV[0]=getP(Kval,imp.getPixel(i,j-1)[0]);
							SV[1]=getP(Kval,imp.getPixel(i+1,j-1)[0]);
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=0;
							SV[4]=0;
							SV[5]=0;
							SV[6]=getP(Kval,imp.getPixel(i-1,j)[0]);
							SV[7]=getP(Kval,imp.getPixel(i-1,j-1)[0]);
						} else { // this is for all non border pixels
							Kval = ((double)(imp.getPixel(i+1,j)[0]+imp.getPixel(i+1,j+1)[0]+imp.getPixel(i,j+1)[0]+imp.getPixel(i-1,j+1)[0]+imp.getPixel(i-1,j)[0]+imp.getPixel(i-1,j-1)[0]+imp.getPixel(i,j-1)[0]+imp.getPixel(i+1,j-1)[0]))/8;
							double m1 = Math.max(imp.getPixel(i+1,j)[0],imp.getPixel(i+1,j+1)[0]);
							double m2 = Math.max(imp.getPixel(i,j+1)[0],imp.getPixel(i-1,j+1)[0]);
							double m3 = Math.max(imp.getPixel(i-1,j)[0],imp.getPixel(i-1,j-1)[0]);
							double m4 = Math.max(imp.getPixel(i,j-1)[0],imp.getPixel(i+1,j-1)[0]);
							double m5 =Math.max(m1,m2);
							double m6=Math.max(m3,m4);
							PixMax=Math.max(m5,m6);
							SV[0]=getP(Kval,imp.getPixel(i,j-1)[0]);
							SV[1]=getP(Kval,imp.getPixel(i+1,j-1)[0]);
							SV[2]=getP(Kval,imp.getPixel(i+1,j)[0]);
							SV[3]=getP(Kval,imp.getPixel(i+1,j+1)[0]);
							SV[4]=getP(Kval,imp.getPixel(i,j+1)[0]);
							SV[5]=getP(Kval,imp.getPixel(i-1,j+1)[0]);
							SV[6]=getP(Kval,imp.getPixel(i-1,j)[0]);
							SV[7]=getP(Kval,imp.getPixel(i-1,j-1)[0]);
						}
						if (Kval==maxVal){ // if there are only background pixels around continue to the next pixel
							continue Check1;
						}
						// itterate over SV to get 01 combos
						int PA=0;
						int BP=0;
						int BPnoC=0;
						int C1=0;
						int C2=0;
						for (int k=0;k<SV.length;k++){
							if (k<SV.length-1){
								if (SV[k+1]-SV[k]==1){
									PA++;
								}
							}
							if (SV[0]-SV[7]==1){
								PA++;
							}
							BP+=SV[k];
							BPnoC+=SV[k];
						}
	
						if (!passed){
							C1=SV[0]*SV[2]*SV[4];
							C2=SV[2]*SV[4]*SV[6];
						} else {
							C1=SV[0]*SV[2]*SV[6];
							C2=SV[0]*SV[4]*SV[6];
						}
						if (BP>=1 && BP<=6){
							//IJ.log("1");
						}
						if (BP>=2 && BP<=6 && PA==1 && C1==0 && C2==0){ // these pixels are added to the removal list
							// need to add a check to see how many neighbours have already been added
							removedPix.add(new pPos (i,j, (int)PixMax));
						}
					}
				}
			}

			if (passed){
				// here we remove the pixels from the image if it fullfills all criteria	
				morePasses=removedPix.size();
				IJ.log(""+morePasses +" pixels removed");
		
				for (int l=0;l<removedPix.size();l++){
					pPos remPos=removedPix.get(l);
					ip.putPixel(remPos.getX(), remPos.getY(), 0);
					//IJ.log("removing i:"+remPos.getX()+", "+remPos.getY());
				}

				

				
				
			}
			passed =!passed;
			imp.setProcessor(ip);
			// here we need to check if not too much was removed
		}
		
	}

	public int getP(double Kval, double pix){ // needed for setting thresholds
		if (pix<=Kval) return 0; 
		return 1;
	}



}

