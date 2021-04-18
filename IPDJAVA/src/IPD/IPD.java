package IPD;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class IPD {

/**********************************************
 * @功能
 * 生成平均值
 */
	public double getAvg(ArrayList<Double> a)
	{
		double sum=0;
		for(int i=0;i<a.size();i++)
			sum=sum+a.get(i);
		return sum/a.size();
	}
	
/**********************************************
 * @功能
 * 根据时间戳产生伪随机数。为了使得多台服务器可以有
 * 同一个随机数，而不用传输，采用时间生成法。
 * @param 
 * timeStamp:系统时间戳
 * @return 
 * r:生成的伪随机数，范围为0-9999
 */
	public int getRandom(double timeStamp)
	{
		double r=timeStamp/1000%10000000/60;
		r=Math.floor(r*37/3)%10000;
		return (int) (r);
	}
	
/**********************************************
 * @功能
 * 生成1到n的不重复随机数序列
 * @param 
 * n：生成随机数的范围
 * @return 
 * ans：生成结果
 */
	public ArrayList<Integer> randPerm(int n,double timeStamp)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));
		ArrayList<Integer> ans=new ArrayList<Integer>();
		ArrayList<Integer> num=new ArrayList<Integer>();
		for(int i=0;i<n;i++)
			num.add(i);
		while(num.size()!=0)
		{
			int index=Math.abs(r.nextInt())%num.size();
			ans.add(num.get(index));
			num.remove(index);
		}
		return ans;
	}
	
/**********************************************
 * @功能
 * 实现水印转换
 * @param 
 * timeStamp:系统时间戳
 * time：数据包时间戳
 * watermark：需要添加的水印
 * s：数据长度（4s确定一位水印）
 * @return 
 * r:
 */
	public ArrayList<Double> embed(double timeStamp,ArrayList<Double> time,double precision,ArrayList<Integer> watermark,int s)
	{
		Scanner scanner = new Scanner(System.in);
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		
		ArrayList<Double> time_ans=new ArrayList<Double>();
		time_ans.add(r.nextDouble());//产生0~1间的随机数作为数据头
		
		int time_length=time.size()-1;//减去数据包头的数据包个数
		int data_length=watermark.size()*4*s;//嵌入水印需要用到的数据包个数
		int offset=1;
		if(time_length-data_length!=0)
			offset=r.nextInt()%(time_length-data_length+1)+1;//产生随机数确定起始点
		////System.out.println("offset="+offset);
		
		for(int i=1;i<offset;i++)
			time_ans.add(time.get(i));  //记录未改变时间
		
		ArrayList<Double> time_i_front=new ArrayList<Double>();
		ArrayList<Double> time_i_rear=new ArrayList<Double>();
		ArrayList<Integer> rand_index=new ArrayList<Integer>(); //随机分组
		ArrayList<Double> ipd_a=new ArrayList<Double>();
		ArrayList<Double> ipd_b=new ArrayList<Double>();
		double avg_a,avg_b,ipd_diff=0.0;
		
		for(int i=1;i<=watermark.size();i++)
		{
			//System.out.println("for watermark "+i);
			time_i_front.clear();
			time_i_rear.clear();
			rand_index.clear();
			////System.out.println("clear");
			
			////System.out.println("time_i_front:");
			for(int j=offset+(i-1)*4*s;j<offset+(i-1)*4*s+2*s;j++) {
				////System.out.print("j:"+j);
				time_i_front.add(time.get(j));
				////System.out.print(time.get(j)+" ");
			}
			////System.out.print("\n");
			////System.out.println("time_i_rear:");
			for(int j=offset+(i-1)*4*s+2*s;j<offset+i*4*s;j++) {
				time_i_rear.add(time.get(j));
				////System.out.print(time.get(j)+" ");
			}
			//System.out.print("\n");
			ArrayList<Double> ipd=new ArrayList<Double>(); 
			//System.out.println("ipd:");
			for(int j=0;j<time_i_front.size();j++) {
				ipd.add(time_i_front.get(j)-time_i_rear.get(j)); //计算差值
				//System.out.print(time_i_front.get(j)+"-"+time_i_rear.get(j)+"="+(time_i_front.get(j)-time_i_rear.get(j))+" ");
			}
			//System.out.print("\n");
			
			rand_index=randPerm(2*s,timeStamp);  //生成打乱下标，用于分AB组
			ipd_a.clear();
			ipd_b.clear();
			for(int j=0;j<s;j++)
				ipd_a.add(ipd.get(rand_index.get(j)));
			for(int j=s;j<2*s;j++)
				ipd_b.add(ipd.get(rand_index.get(j))); //分AB组
			avg_a=getAvg(ipd_a);
			avg_b=getAvg(ipd_b);
			ipd_diff=avg_a-avg_b; //获取平均值差值
			//System.out.println("获取平均差值");
			
			if(watermark.get(i-1)==0) //需嵌入的水印为0
			{	
				if(ipd_diff<0) 
				{
					//System.out.println("ipd_diff["+i+"]:"+ipd_diff);
					time_ans.addAll(time_i_front);
					time_ans.addAll(time_i_rear);  //更新发送时间
					continue;
				}
				while(ipd_diff>=0)  //不满足条件 调整数据发送时间
				{
					//System.out.println("不满足条件 调整数据发送时间");
					for(int j=0;j<s;j++)
						time_i_rear.set(rand_index.get(j), time_i_rear.get(rand_index.get(j))+precision);  //延迟A组第二个数据包发送时间
					////System.out.println("point1");
					for(int j=s;j<2*s;j++)
						time_i_front.set(rand_index.get(j), time_i_front.get(rand_index.get(j))+precision);  //延迟B组第一个数据包发送时间
					////System.out.println("point2");
					//System.out.println("ipd:");
					for(int j=0;j<time_i_front.size();j++) {
						ipd.set(j,time_i_front.get(j)-time_i_rear.get(j)); //计算差值
						//System.out.print(time_i_front.get(j)+"-"+time_i_rear.get(j)+"="+(time_i_front.get(j)-time_i_rear.get(j))+" ");
					}
					//System.out.print("\n");

					for(int j=0;j<s;j++) {
						ipd_a.set(j,ipd.get(rand_index.get(j)));
						//System.out.println("ipd_a["+j+"]="+ipd.get(j));
					}
					////System.out.println("point3");
					////System.out.println("ipd_b_size"+ipd_b.size());
					for(int j=s;j<2*s;j++) {
						////System.out.println("rand_index["+j+"]="+rand_index.get(j));
						ipd_b.set(j-s,ipd.get(rand_index.get(j))); //分AB组
						//System.out.println("ipd_b["+(j-s)+"]="+ipd.get(j-s));
						////System.out.println("point");
					}
					////System.out.println("point4");
					avg_a=getAvg(ipd_a);
					avg_b=getAvg(ipd_b);
					ipd_diff=avg_a-avg_b; //获取平均值差值
					//System.out.println("ipd_diff="+ipd_diff);
					//scanner.next();
				}
				//System.out.println("调整结束，ipd_diff="+ipd_diff);
			}
			
			else //需嵌入水印为1
			{
				if(ipd_diff>=0) 
				{
					//System.out.println("ipd_diff["+i+"]:"+ipd_diff);
					time_ans.addAll(time_i_front);
					time_ans.addAll(time_i_rear);  //更新发送时间
					continue;
				}
				while(ipd_diff<0)  //不满足条件 调整数据发送时间
				{
					//System.out.println("不满足条件 调整数据发送时间");
					//System.out.println("time_i_front:");
					for(int j=0;j<s;j++) {
						time_i_front.set(rand_index.get(j), time_i_front.get(rand_index.get(j))+precision);  //延迟B组第一个数据包发送时间
						//System.out.print(time_i_front.get(rand_index.get(j))+" ");
					}
					//System.out.print("\n");
					////System.out.println("point5");
					//System.out.println("time_i_rear:");
					for(int j=s;j<2*s;j++) {
						time_i_rear.set(rand_index.get(j), time_i_rear.get(rand_index.get(j))+precision);  //延迟A组第二个数据包发送时间
						//System.out.print(time_i_front.get(rand_index.get(j))+" ");
					}
					//System.out.print("\n");
					////System.out.println("point6");
					//System.out.println("ipd:");
					for(int j=0;j<time_i_front.size();j++) {
						ipd.set(j,time_i_front.get(j)-time_i_rear.get(j)); //计算差值
						//System.out.print(time_i_front.get(j)+"-"+time_i_rear.get(j)+"="+(time_i_front.get(j)-time_i_rear.get(j))+" ");
					}
					//System.out.print("\n");
					
					for(int j=0;j<s;j++) {
						ipd_a.set(j,ipd.get(rand_index.get(j)));
						//System.out.println("ipd_a["+j+"]="+ipd_a.get(j));
					}
					////System.out.println("point7");
					for(int j=s;j<2*s;j++) {
						ipd_b.set(j-s,ipd.get(rand_index.get(j))); //分AB组
						//System.out.println("ipd_b["+(j-s)+"]="+ipd_b.get(j-s));
					}
					////System.out.println("point8");
					avg_a=getAvg(ipd_a);
					avg_b=getAvg(ipd_b);
					ipd_diff=avg_a-avg_b; //获取平均值差值
					//System.out.println("ipd_diff="+ipd_diff);
					//scanner.next();
				}
				//System.out.println("调整结束，ipd_diff="+ipd_diff);
			}
			
			//System.out.println("ipd_diff["+i+"]:"+ipd_diff);
			time_ans.addAll(time_i_front);
			time_ans.addAll(time_i_rear);  //更新发送时间
			
		}
		
		for(int i=offset+watermark.size()*4*s;i<time.size();i++)
			time_ans.add(time.get(i));  //添加结尾未改变时间
		
		/*System.out.println("time_ans:");
		for(int j=0;j<time_ans.size();j++)
			System.out.println(time_ans.get(j)+" ");
		System.out.println("\n");*/
		
		for(int i=2;i<time_ans.size();i++)
			time_ans.set(i, time_ans.get(i)-time_ans.get(1));  //计算数据包间时间间隔
		time_ans.set(1,0.0);
		
		return time_ans;
	}
	
	
	
/**********************************************
 * @功能
 * 实现水印解码
 * @param 
 * timeStamp:系统时间戳
 * time：数据包时间戳(含head)
 * watermark_length：水印长度
 * s：数据长度（4s确定一位水印）
 * @return 
 * r:
 */
	public ArrayList<Integer> extract(double timeStamp,ArrayList<Double> time,int watermark_length,int s)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		
		ArrayList<Integer> watermark=new ArrayList<Integer>();
		double head=r.nextDouble();//产生0~1间的随机数作为数据头
		
		int time_length=time.size()-1;  //除去数据包头的数据包个数
		int data_length=watermark_length*4*s;
		int offset=1;
		if(time_length-data_length!=0)
			offset=r.nextInt()%(time_length-data_length+1)+1;//产生随机数确定起始点
		
		ArrayList<Integer> rand_index=new ArrayList<Integer>();
		rand_index=randPerm(2*s,timeStamp);  //生成打乱下标，用于分AB组
		
		ArrayList<Double> time_i=new ArrayList<Double>();
		ArrayList<Double> time_i_front=new ArrayList<Double>();
		ArrayList<Double> time_i_rear=new ArrayList<Double>();
		ArrayList<Double> ipd=new ArrayList<Double>();
		ArrayList<Double> ipd_a=new ArrayList<Double>();
		ArrayList<Double> ipd_b=new ArrayList<Double>();
		double avg_a,avg_b,ipd_diff=0.0;
		for(int i=1;i<=watermark_length;i++)
		{
			time_i_front.clear();
			time_i_rear.clear();
			ipd.clear();
			ipd_a.clear();
			ipd_b.clear();
			
			for(int j=offset+(i-1)*4*s;j<offset+(i-1)*4*s+2*s;j++) 
				time_i_front.add(time.get(j));
			for(int j=offset+(i-1)*4*s+2*s;j<offset+i*4*s;j++)
				time_i_rear.add(time.get(j));
			for(int j=0;j<time_i_front.size();j++) 
				ipd.add(time_i_front.get(j)-time_i_rear.get(j)); //计算差值
			
			for(int j=0;j<s;j++)
				ipd_a.add(ipd.get(rand_index.get(j)));
			for(int j=s;j<2*s;j++)
				ipd_b.add(ipd.get(rand_index.get(j))); //分AB组
			avg_a=getAvg(ipd_a);
			avg_b=getAvg(ipd_b);
			ipd_diff=avg_a-avg_b; //获取平均值差值
			//System.out.println("ipd_diff["+i+"]:"+ipd_diff);
			
			if(ipd_diff<0)
				watermark.add(0);
			else
				watermark.add(1);
		}
		
		return watermark;
	}
	
/**********************************************
 * @功能
 * 判断数据包中是否含有水印
 * @param 
 * timeStamp:系统时间戳
 * head：水印头
 * precision:判断精度
 * @return 
 * ans:判断数据包中是否含有水印，不含水印返回0，含有水印返回1
 */
	public boolean containWatermark(double timeStamp,double head,double precision)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		double stampHead=r.nextDouble();//产生0~1间的随机数作为数据头
		if(Math.abs(head-stampHead)<precision)
			return true;
		return false;
	}

}
