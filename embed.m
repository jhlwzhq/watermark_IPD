format long;
rand('seed',9);
watermark=[0 1 1]; %��ҪǶ��Ķ���������ˮӡ��Ϣ
%time=[0.0003 0.00032 0.00035 0.00037 0.00037 0.00038 0.00039 0.00040 0.00042 0.00045 0.00053 0.00055 0.00061 0.00063 0.00063 0.00080];%���ݰ�����ʱ��
time=[1609814809.667154 1609814809.667154 1609814809.667157 1609814809.667158 ...
    1609814809.677617 1609814809.677619 1609814809.677624 1609814809.677625 ...
    1609814809.680095 1609814809.680961 1609814809.680962 1609814809.680964 ...
    1609814809.680964 1609814809.682885 1609814809.682887]

time_length=length(time); %�����ݰ�����
watermark_length=length(watermark); %��Ҫ���ӵ�ˮӡ����
s=1; %����ȷ�����ݳ��� 
% **s��ȡֵ���ȷ�����ţ�**
data_length=watermark_length*4*s; %Ƕ��ˮӡ��Ҫ�õ������ݰ�����
offset=unidrnd(time_length-data_length,1,1); %���������ȷ����ʼ��

time_i_front=[];
time_i_rear=[];
rand_index=[];
time_ans=time(1:offset);%��¼������ķ���ʱ��
index_ans=[];%��¼AB�������

for i=1:watermark_length %�ⲿѭ�� ��ʱ���Ϊwatermark_length�� Ƕ���i��ˮӡ
    disp("-----------------------------")
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
        disp(["��Ƕ���ˮӡΪ0,��ǰ��ֵΪ" ipd_diff])
        if(ipd_diff<0) continue; end
        disp("����������,�������ݷ���ʱ��")
        while(ipd_diff>=0) %���������� �������ݷ���ʱ��
            time_i_rear(rand_index(1:s))=time_i_rear(rand_index(1:s))+0.000001; %�ӳ�A��ڶ������ݰ�����ʱ��
            time_i_front(rand_index(s+1:end))=time_i_front(rand_index(s+1:end))+0.000001; %�ӳ�B���һ�����ݰ�����ʱ��
    %**���ȷ���ӳٶ೤ʱ��Ϊ���Ž⣿�����籾����ʱ����йأ����հٷֱ��ӳ٣�**
            
            ipd=time_i_front-time_i_rear; %�����ֵIPD
            ipd_a=ipd(rand_index(1:s)); %A�� 
            ipd_b=ipd(rand_index(s+1:end)); %B��
            avg_a=mean(ipd_a); %A��ƽ��ֵ
            avg_b=mean(ipd_b); %B��ƽ��ֵ
            ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
            disp(["����Ϊ" ipd_diff])
        end

    else %��Ƕ��ˮӡΪ1
        disp(["��Ƕ���ˮӡΪ1,��ǰ��ֵΪ" ipd_diff])
        if(ipd_diff>=0) continue; end
        disp("����������,�������ݷ���ʱ��")
        while(ipd_diff<0) %���������� �������ݷ���ʱ��
            time_i_front(rand_index(1:s))=time_i_front(rand_index(1:s))+0.000001; %�ӳ�A���һ�����ݰ�����ʱ��
            time_i_rear(rand_index(s+1:end))=time_i_rear(rand_index(s+1:end))+0.000001; %�ӳ�B��ڶ������ݰ�����ʱ��
            
            ipd=time_i_front-time_i_rear; %�����ֵIPD
            ipd_a=ipd(rand_index(1:s)); %A�� 
            ipd_b=ipd(rand_index(s+1:end)); %B��
            avg_a=mean(ipd_a); %A��ƽ��ֵ
            avg_b=mean(ipd_b); %B��ƽ��ֵ
            ipd_diff=avg_a-avg_b; %ƽ��ֵ��ֵ
            disp(["����Ϊ" ipd_diff])
        end
    end
end
time_ans=[time_ans time_i_front time_i_rear time(offset+watermark_length*4*s+1:end)]; %���·���ʱ��
index_ans=[index_ans;rand_index]; %���·����¼

time_ans
index_ans
offset