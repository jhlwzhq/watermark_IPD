package IPD;

import java.util.ArrayList;

public class Test {
	public static void main( String[] args )
    {
		IPD ipd=new IPD();
		
		long timeStamp=21937;
		int s=1;
		int watermark_length=3;
		long precision=1;
		
		ArrayList<Long> time=new ArrayList<Long>();
		time.add((long)667154);time.add((long)667154);time.add((long)667157);time.add((long)667158);
		time.add((long)677617);time.add((long)677619);time.add((long)677624);time.add((long)677625);
		time.add((long)680095);time.add((long)680961);time.add((long)680926);time.add((long)680964);
		time.add((long)680964);time.add((long)682885);time.add((long)682887);
		
		ArrayList<Integer> watermark=new ArrayList<Integer>();
		watermark.add(1);watermark.add(1);watermark.add(1);
		
		ArrayList<Long> time_ans=new ArrayList<Long>();
		time_ans=ipd.embed(timeStamp, time, precision, watermark, s);
		System.out.println("time_ans:");
		for(int i=0;i<time_ans.size();i++)
			System.out.println(time_ans.get(i));
		
		long containPrecision=1;
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
