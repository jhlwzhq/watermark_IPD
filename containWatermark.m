%���ܣ��ж����ݰ����Ƿ���ˮӡ
%���룺��ǰʱ���timeStamp������ͷhead
%������ж����ݰ����Ƿ���ˮӡ������ˮӡ����0������ˮӡ����1
function ans=containWatermark(timeStamp,head)


rand('seed',getRandom(timeStamp));
stamp_head=rand(1,1);%����ʱ�����������ͷ
if(abs(head-stamp_head)<0.0000001)
    ans=1;
else
    ans=0;
end
end
