%����timeStamp�ĵ�λ�Ǻ��룬��Java��ȡ����ϵͳʱ�䴫��matlab�����ٷ�����ñ�������
%timeStamp����ȱʡ��Ϊ���Է��㣬����ȱʡ�����Ϊ148��
%���ֵ�ķ�Χ��0-9999��
%���ܣ�����ʱ�������α�������Ϊ��ʹ�ö�̨������������ͬһ��������������ô��䣬����ʱ�����ɷ���
function r=getRandom(timeStamp)
if nargin==0
    timeStamp=61234;
    timeStamp=timeStamp*112345123;
end
r=mod(timeStamp/1000,10000000)/60
r=mod(floor(r*37/3),10000)
end