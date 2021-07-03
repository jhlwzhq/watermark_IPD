package IPD;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class IPD {

/**********************************************
 * @功能
 * 生成平均值
 */
	public long getAvg(ArrayList<Long> a)
	{
		long sum=0;
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
	public int getRandom(long timeStamp)
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
	public ArrayList<Integer> randPerm(int n,long timeStamp)
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
	public ArrayList<Long> embed(long timeStamp,ArrayList<Long> time,long precision,ArrayList<Integer> watermark,int s)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		
		ArrayList<Long> time_ans=new ArrayList<Long>();
		time_ans.add(Math.abs(r.nextLong()));//产生0~1间的随机数作为数据头
		
		int time_length=time.size()-1;//减去数据包头的数据包个数
		int data_length=watermark.size()*4*s;//嵌入水印需要用到的数据包个数
		int offset=1;
		if(time_length-data_length!=0)
			offset=r.nextInt()%(time_length-data_length+1)+1;//产生随机数确定起始点
		
		for(int i=1;i<offset;i++)
			time_ans.add(time.get(i));  //记录未改变时间
		
		ArrayList<Long> time_i_front=new ArrayList<Long>();
		ArrayList<Long> time_i_rear=new ArrayList<Long>();
		ArrayList<Integer> rand_index=new ArrayList<Integer>(); //随机分组
		ArrayList<Long> ipd_a=new ArrayList<Long>();
		ArrayList<Long> ipd_b=new ArrayList<Long>();
		long avg_a,avg_b,ipd_diff=0;
		
		for(int i=1;i<=watermark.size();i++)
		{
			time_i_front.clear();
			time_i_rear.clear();
			rand_index.clear();
			
			for(int j=offset+(i-1)*4*s;j<offset+(i-1)*4*s+2*s;j++) {
				time_i_front.add(time.get(j));
			}
			for(int j=offset+(i-1)*4*s+2*s;j<offset+i*4*s;j++) {
				time_i_rear.add(time.get(j));
			}
			ArrayList<Long> ipd=new ArrayList<Long>(); 
			for(int j=0;j<time_i_front.size();j++) {
				ipd.add(time_i_rear.get(j)-time_i_front.get(j)); //计算差值
			}
			
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
			
			if(watermark.get(i-1)==0) //需嵌入的水印为0
			{	
				if(ipd_diff<0) 
				{
					time_ans.addAll(time_i_front);
					time_ans.addAll(time_i_rear);  //更新发送时间
					continue;
				}
				while(ipd_diff>=0)  //不满足条件 调整数据发送时间
				{
					for(int j=0;j<s;j++) {
						int index=rand_index.get(j);
						time_i_front.set(index, time_i_front.get(index)+precision);  //延迟A组第一个数据包发送时间
					}
					for(int j=s;j<2*s;j++) {
						int index=rand_index.get(j);
						time_i_rear.set(index, time_i_rear.get(index)+precision);  //延迟B组第二个数据包发送时间
					}
					for(int j=0;j<time_i_front.size();j++) {
						ipd.set(j,time_i_rear.get(j)-time_i_front.get(j)); //计算差值
					}

					for(int j=0;j<s;j++) {
						ipd_a.set(j,ipd.get(rand_index.get(j)));
					}
					for(int j=s;j<2*s;j++) {
						ipd_b.set(j-s,ipd.get(rand_index.get(j))); //分AB组
					}
					avg_a=getAvg(ipd_a);
					avg_b=getAvg(ipd_b);
					ipd_diff=avg_a-avg_b; //获取平均值差值
				}
			}
			
			else //需嵌入水印为1
			{
				if(ipd_diff>=0) 
				{
					time_ans.addAll(time_i_front);
					time_ans.addAll(time_i_rear);  //更新发送时间
					continue;
				}
				while(ipd_diff<0)  //不满足条件 调整数据发送时间
				{
					for(int j=0;j<s;j++) {
						int index=rand_index.get(j);
						time_i_rear.set(index, time_i_rear.get(index)+precision);  //延迟A组第二个数据包发送时间
					}
					for(int j=s;j<2*s;j++) {
						int index=rand_index.get(j);
						time_i_front.set(index, time_i_front.get(index)+precision);  //延迟B组第一个数据包发送时间
					}
					for(int j=0;j<time_i_front.size();j++) {
						ipd.set(j,time_i_rear.get(j)-time_i_front.get(j)); //计算差值
					}
					
					for(int j=0;j<s;j++) {
						ipd_a.set(j,ipd.get(rand_index.get(j)));
					}
					for(int j=s;j<2*s;j++) {
						ipd_b.set(j-s,ipd.get(rand_index.get(j))); //分AB组
					}
					avg_a=getAvg(ipd_a);
					avg_b=getAvg(ipd_b);
					ipd_diff=avg_a-avg_b; //获取平均值差值
				}
			}
			
			time_ans.addAll(time_i_front);
			time_ans.addAll(time_i_rear);  //更新发送时间
			
		}
		
		for(int i=offset+watermark.size()*4*s;i<time.size();i++)
			time_ans.add(time.get(i));  //添加结尾未改变时间
		
		for(int j=0;j<time_ans.size();j++)
			System.out.println(time_ans.get(j)+" ");
		
		for(int i=2;i<time_ans.size();i++)
			time_ans.set(i, time_ans.get(i)-time_ans.get(1));  //计算数据包间时间间隔
		time_ans.set(1,(long)0);
		
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
	public ArrayList<Integer> extract(long timeStamp,ArrayList<Long> time,int watermark_length,int s)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		
		ArrayList<Integer> watermark=new ArrayList<Integer>();
		long head=Math.abs(r.nextLong());//产生0~1间的随机数作为数据头
		
		int time_length=time.size()-1;  //除去数据包头的数据包个数
		int data_length=watermark_length*4*s;
		int offset=1;
		if(time_length-data_length!=0)
			offset=r.nextInt()%(time_length-data_length+1)+1;//产生随机数确定起始点
		
		ArrayList<Integer> rand_index=new ArrayList<Integer>();
		rand_index=randPerm(2*s,timeStamp);  //生成打乱下标，用于分AB组
		
		ArrayList<Long> time_i=new ArrayList<Long>();
		ArrayList<Long> time_i_front=new ArrayList<Long>();
		ArrayList<Long> time_i_rear=new ArrayList<Long>();
		ArrayList<Long> ipd=new ArrayList<Long>();
		ArrayList<Long> ipd_a=new ArrayList<Long>();
		ArrayList<Long> ipd_b=new ArrayList<Long>();
		long avg_a,avg_b,ipd_diff=0;
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
				ipd.add(time_i_rear.get(j)-time_i_front.get(j)); //计算差值
			
			for(int j=0;j<s;j++)
				ipd_a.add(ipd.get(rand_index.get(j)));
			for(int j=s;j<2*s;j++)
				ipd_b.add(ipd.get(rand_index.get(j))); //分AB组
			avg_a=getAvg(ipd_a);
			avg_b=getAvg(ipd_b);
			ipd_diff=avg_a-avg_b; //获取平均值差值
			
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
	public boolean containWatermark(long timeStamp,long head,long precision)
	{
		java.util.Random r=new java.util.Random(getRandom(timeStamp));//伪随机数作为种子
		long stampHead=Math.abs(r.nextLong());//产生0~1间的随机数作为数据头
		if(Math.abs(head-stampHead)<precision)
			return true;
		return false;
	}

}
