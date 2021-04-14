%功能：判断数据包中是否含有水印
%输入：当前时间戳timeStamp，数据头head
%输出：判断数据包中是否含有水印，不含水印返回0，含有水印返回1
function ans=containWatermark(timeStamp,head)
rand('seed',getRandom(timeStamp));
stamp_head=rand(1,1);%根据时间戳产生数据头
if(head==stamp_head)
    ans=1;
else
    ans=0;
end
end
