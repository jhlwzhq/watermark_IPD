function watermark=extract(timeStamp,time)
format long;
rand('seed',getRandom(timeStamp));

s=1;
watermark_length=3;%水印长度
%**水印长度未知 使用定长的水印？**
time_length=length(time); %总数据包个数
data_length=watermark_length*4*s; %嵌入水印需要用到的数据包个数
offset=unidrnd(time_length-data_length,1,1)

index=[];
for i=1:watermark_length
    index=[index;randperm(2*s)];
end
watermark=[];

watermark_length=size(index,1); %index行数即为水印信息长度
s=size(index,2)/2; %index列数为2s
for i=1:watermark_length %外部循环 将时间分为watermark_length组 提取第i个水印
    time_i=time(offset+(i-1)*4*s+1:offset+i*4*s);%第i个水印用到的时间 
    time_i_front=time_i(1:2*s); %前一半
    time_i_rear=time_i(2*s+1:end); %后一半
    ipd=time_i_front-time_i_rear; %计算差值IPD
    
    rand_index=index(i,:); %取出水印嵌入时的随机分组下标
    ipd_a=ipd(rand_index(1:s)); %A组 
    ipd_b=ipd(rand_index(s+1:end)); %B组
    avg_a=mean(ipd_a); %A组平均值
    avg_b=mean(ipd_b); %B组平均值
    ipd_diff=avg_a-avg_b; %平均值差值
    
    if(ipd_diff<0)  watermark=[watermark 0];
    else watermark=[watermark 1];
    end
end

%watermark

end