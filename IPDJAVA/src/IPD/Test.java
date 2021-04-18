package IPD;

import java.util.ArrayList;

public class Test {
	public static void main( String[] args )
    {
		IPD ipd=new IPD();
		
		double timeStamp=21937;
		int s=1;
		int watermark_length=3;
		double precision=0.0001;
		
		ArrayList<Double> time=new ArrayList<Double>();
		time.add(667.154);time.add(667.154);time.add(667.157);time.add(667.158);
		time.add(677.617);time.add(677.619);time.add(677.624);time.add(677.625);
		time.add(680.095);time.add(680.961);time.add(680.926);time.add(680.964);
		time.add(680.964);time.add(682.885);time.add(682.887);
		
		ArrayList<Integer> watermark=new ArrayList<Integer>();
		watermark.add(1);watermark.add(0);watermark.add(1);
		
		ArrayList<Double> time_ans=new ArrayList<Double>();
		time_ans=ipd.embed(timeStamp, time, precision, watermark, s);
		System.out.println("time_ans:");
		for(int i=0;i<time_ans.size();i++)
			System.out.println(time_ans.get(i));
		
		double containPrecision=0.00001;
		if(ipd.containWatermark(timeStamp, time_ans.get(0), containPrecision))
		{
			ArrayList<Integer> watermark_ans=new ArrayList<Integer>();
			watermark_ans=ipd.extract(timeStamp, time_ans, watermark_length, s);
			System.out.println("watermark_ans:");
			for(int i=0;i<watermark_ans.size();i++)
				System.out.print(watermark_ans.get(i)+" ");
			System.out.print("\n");
		}
    }
}
