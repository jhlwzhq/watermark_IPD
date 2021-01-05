%参数timeStamp的单位是毫秒，由Java获取本地系统时间传入matlab包，再发起调用本函数。
%timeStamp可以缺省，为调试方便，设置缺省，输出为148。
%输出值的范围是0-9999。
%功能：根据时间戳产生伪随机数。为了使得多台服务器可以有同一个随机数，而不用传输，采用时间生成法。
function r=getRandom(timeStamp)
if nargin==0
    timeStamp=61234;
    timeStamp=timeStamp*112345123;
end
r=mod(timeStamp/1000,10000000)/60
r=mod(floor(r*37/3),10000)
end