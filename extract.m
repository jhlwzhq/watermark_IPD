%clc,clear,close all
format long;
rand('seed',9);
%time=[0.0003 0.00032 0.00035 0.00037 0.00037 0.00038 0.00039 0.0004 0.00042 0.00045 0.00053 0.00055 0.00061 0.00063 0.00063 0.0008];
time=[1609814809.667154 1609814809.667154 1609814809.667157 1609814809.667159 ...
    1609814809.677618 1609814809.677619 1609814809.677624 1609814809.677625 ...
    1609814809.680095 1609814809.680961 1609814809.680962 1609814809.681924 ...
    1609814809.681924 1609814809.682885 1609814809.682887];
%offset=3;
offset=unidrnd(time_length-data_length,1,1)
%index=[2 1;1 2;2 1];
%**ˮӡ����δ֪ ʹ�ö�����ˮӡ��**
watermark_lengh=3;
index=[];
for i=1:watermark_lengh
    index=[index;randperm(2*s)];
end
index
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

watermark