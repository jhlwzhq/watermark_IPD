%���ܣ�ʵ��ˮӡת��
%���룺��ǰʱ���timeStamp�����ݰ�ʱ���time����Ҫ��ӵ�ˮӡwatermark�����ݳ���s��4sȷ��һλˮӡ��
%��������ڵ�һ�����ݰ��ĸ����ݰ�ʱ��time_ans,0~1�������ͷhead
function [time_ans,head]=Embed(timeStamp,time,watermark,s)
format long;
rand('seed',getRandom(timeStamp));

head=rand(1,1); %����0~1����������Ϊ����ͷ

time=time(2:end);
time_length=length(time); %�����ݰ�����(��ȥ���ݰ�ͷ)
watermark_length=length(watermark); %��Ҫ��ӵ�ˮӡ����
data_length=watermark_length*4*s; %Ƕ��ˮӡ��Ҫ�õ������ݰ�����
offset=unidrnd(time_length-data_length,1,1); %���������ȷ����ʼ��

time_i_front=[];
time_i_rear=[];
rand_index=[];
time_ans=time(1:offset);%��¼������ķ���ʱ��
index_ans=[];%��¼AB�������

for i=1:watermark_length %�ⲿѭ�� ��ʱ���Ϊwatermark_length�� Ƕ���i��ˮӡ
    %disp("-----------------------------");
    time_ans=[time_ans time_i_front time_i_rear];%���·���ʱ��
    index_ans=[index_ans;rand_index];%���·����¼
    
    time_i=time(offset+(i-1)*4*s+1:offset+i*4*s);%Ƕ���i��ˮӡ�õ���ʱ��
    time_i_front=time_i(1:2*s); %ǰһ��
    time_i_rear=time_i(2*s+1:end); %��һ��
    ipd=time_i_front-time_i_rear; %�����ֵIPD
    
    rand_index=randperm(2*s); %���ɴ��ҵ��±��������ڷ�A,B��
    ipd_a=ipd(rand_index(1:s)); %A�� 
    ipd_b=ipd(rand_index(s+1:end)); %B��
    avg_a=mean(ipd_a); %A��ƽ��ֵ
    avg_b=mean(ipd_b); %B��ƽ��ֵ
    ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
    
    if(watermark(i)==0) %��Ƕ���ˮӡΪ0
     %   disp(["��Ƕ���ˮӡΪ0,��ǰ��ֵΪ" ipd_diff]);
        if(ipd_diff<0) continue; end
      %  disp("����������,�������ݷ���ʱ��");
        while(ipd_diff>=0) %���������� �������ݷ���ʱ��
            time_i_rear(rand_index(1:s))=time_i_rear(rand_index(1:s))+0.000001; %�ӳ�A��ڶ������ݰ�����ʱ��
            time_i_front(rand_index(s+1:end))=time_i_front(rand_index(s+1:end))+0.000001; %�ӳ�B���һ�����ݰ�����ʱ��
    %**���ȷ���ӳٶ೤ʱ��Ϊ���Ž⣿�����籾���ʱ����йأ����հٷֱ��ӳ٣�**
            
            ipd=time_i_front-time_i_rear; %�����ֵIPD
            ipd_a=ipd(rand_index(1:s)); %A�� 
            ipd_b=ipd(rand_index(s+1:end)); %B��
            avg_a=mean(ipd_a); %A��ƽ��ֵ
            avg_b=mean(ipd_b); %B��ƽ��ֵ
            ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
       %     disp(["����Ϊ" ipd_diff]);
        end

    else %��Ƕ��ˮӡΪ1
        %disp(["��Ƕ���ˮӡΪ1,��ǰ��ֵΪ" ipd_diff]);
        if(ipd_diff>=0) continue; end
        %disp("����������,�������ݷ���ʱ��");
        while(ipd_diff<0) %���������� �������ݷ���ʱ��
            time_i_front(rand_index(1:s))=time_i_front(rand_index(1:s))+0.000001; %�ӳ�A���һ�����ݰ�����ʱ��
            time_i_rear(rand_index(s+1:end))=time_i_rear(rand_index(s+1:end))+0.000001; %�ӳ�B��ڶ������ݰ�����ʱ��
            
            ipd=time_i_front-time_i_rear; %�����ֵIPD
            ipd_a=ipd(rand_index(1:s)); %A�� 
            ipd_b=ipd(rand_index(s+1:end)); %B��
            avg_a=mean(ipd_a); %A��ƽ��ֵ
            avg_b=mean(ipd_b); %B��ƽ��ֵ
            ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
         %   disp(["����Ϊ" ipd_diff]);
        end
    end
end
time_ans=[time_ans time_i_front time_i_rear time(offset+watermark_length*4*s+1:end)]; %���·���ʱ��
index_ans=[index_ans;rand_index]; %���·����¼

for i=2:length(time)
    time_ans(i)=time_ans(i)-time_ans(1);
end
time_ans(1)=0;

%time_ans
%index_ans
%offset

end
