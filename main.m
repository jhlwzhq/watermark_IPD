time=[1609814809.667154 1609814809.667154 1609814809.667157 1609814809.667158 ...
    1609814809.677617 1609814809.677619 1609814809.677624 1609814809.677625 ...
    1609814809.680095 1609814809.680961 1609814809.680962 1609814809.680964 ...
    1609814809.680964 1609814809.682885 1609814809.682887];

watermark=[1 0 1]; %需要嵌入的二进制向量水印信息
timeStamp=1457476;
[embed_time,head]=embed(timeStamp,time,0.0001,watermark,1)
if(containWatermark(timeStamp,head))
    extract_watermark=extract(timeStamp,embed_time,3,1)
end