function time_ans=embed(timeStamp,time,watermark)
format long;
rand('seed',getRandom(timeStamp));

time_length=length(time); %总数据包个数
watermark_length=length(watermark); %需要添加的水印长度
s=1; %用于确定数据长度 
% **s的取值如何确定最优？**
data_length=watermark_length*4*s; %嵌入水印需要用到的数据包个数
offset=unidrnd(time_length-data_length,1,1); %产生随机数确定起始点

time_i_front=[];
time_i_rear=[];
rand_index=[];
time_ans=time(1:offset);%记录调整后的发送时间
index_ans=[];%记录AB分组情况

for i=1:watermark_length %外部循环 将时间分为watermark_length组 嵌入第i个水印
    disp("-----------------------------")
    time_ans=[time_ans time_i_front time_i_rear];%更新发送时间
    index_ans=[index_ans;rand_index];%更新分组记录
    
    time_i=time(offset+(i-1)*4*s+1:offset+i*4*s);%嵌入第i个水印用到的时间
    time_i_front=time_i(1:2*s); %前一半
    time_i_rear=time_i(2*s+1:end); %后一半
    ipd=time_i_front-time_i_rear; %计算差值IPD
    
    rand_index=randperm(2*s); %生成打乱的下标排序，用于分A,B组
    ipd_a=ipd(rand_index(1:s)); %A组 
    ipd_b=ipd(rand_index(s+1:end)); %B组
    avg_a=mean(ipd_a); %A组平均值
    avg_b=mean(ipd_b); %B组平均值
    ipd_diff=avg_a-avg_b; %平均值差值
    
    if(watermark(i)==0) %需嵌入的水印为0
        disp(["需嵌入的水印为0,当前差值为" ipd_diff])
        if(ipd_diff<0) continue; end
        disp("不满足条件,调整数据发送时间")
        while(ipd_diff>=0) %不满足条件 调整数据发送时间
            time_i_rear(rand_index(1:s))=time_i_rear(rand_index(1:s))+0.000001; %延迟A组第二个数据包发送时间
            time_i_front(rand_index(s+1:end))=time_i_front(rand_index(s+1:end))+0.000001; %延迟B组第一个数据包发送时间
    %**如何确定延迟多长时间为最优解？与网络本身的时间差有关，按照百分比延迟？**
            
            ipd=time_i_front-time_i_rear; %计算差值IPD
            ipd_a=ipd(rand_index(1:s)); %A组 
            ipd_b=ipd(rand_index(s+1:end)); %B组
            avg_a=mean(ipd_a); %A组平均值
            avg_b=mean(ipd_b); %B组平均值
            ipd_diff=avg_a-avg_b; %平均值差值
            disp(["调整为" ipd_diff])
        end

    else %需嵌入水印为1
        disp(["需嵌入的水印为1,当前差值为" ipd_diff])
        if(ipd_diff>=0) continue; end
        disp("不满足条件,调整数据发送时间")
        while(ipd_diff<0) %不满足条件 调整数据发送时间
            time_i_front(rand_index(1:s))=time_i_front(rand_index(1:s))+0.000001; %延迟A组第一个数据包发送时间
            time_i_rear(rand_index(s+1:end))=time_i_rear(rand_index(s+1:end))+0.000001; %延迟B组第二个数据包发送时间
            
            ipd=time_i_front-time_i_rear; %计算差值IPD
            ipd_a=ipd(rand_index(1:s)); %A组 
            ipd_b=ipd(rand_index(s+1:end)); %B组
            avg_a=mean(ipd_a); %A组平均值
            avg_b=mean(ipd_b); %B组平均值
            ipd_diff=avg_a-avg_b; %平均值差值
            disp(["调整为" ipd_diff])
        end
    end
end
time_ans=[time_ans time_i_front time_i_rear time(offset+watermark_length*4*s+1:end)]; %更新发送时间
index_ans=[index_ans;rand_index]; %更新分组记录

%time_ans
%index_ans
%offset

end
