function watermark=extract(timeStamp,time)
format long;
rand('seed',getRandom(timeStamp));

s=1;
watermark_length=3;%ˮӡ����
%**ˮӡ����δ֪ ʹ�ö�����ˮӡ��**
time_length=length(time); %�����ݰ�����
data_length=watermark_length*4*s; %Ƕ��ˮӡ��Ҫ�õ������ݰ�����
offset=unidrnd(time_length-data_length,1,1)

index=[];
for i=1:watermark_length
    index=[index;randperm(2*s)];
end
watermark=[];

watermark_length=size(index,1); %index������Ϊˮӡ��Ϣ����
s=size(index,2)/2; %index����Ϊ2s
for i=1:watermark_length %�ⲿѭ�� ��ʱ���Ϊwatermark_length�� ��ȡ��i��ˮӡ
    time_i=time(offset+(i-1)*4*s+1:offset+i*4*s);%��i��ˮӡ�õ���ʱ�� 
    time_i_front=time_i(1:2*s); %ǰһ��
    time_i_rear=time_i(2*s+1:end); %��һ��
    ipd=time_i_front-time_i_rear; %�����ֵIPD
    
    rand_index=index(i,:); %ȡ��ˮӡǶ��ʱ����������±�
    ipd_a=ipd(rand_index(1:s)); %A�� 
    ipd_b=ipd(rand_index(s+1:end)); %B��
    avg_a=mean(ipd_a); %A��ƽ��ֵ
    avg_b=mean(ipd_b); %B��ƽ��ֵ
    ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
    
    if(ipd_diff<0)  watermark=[watermark 0];
    else watermark=[watermark 1];
    end
end

%watermark

end