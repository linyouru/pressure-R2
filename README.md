# 压测服务执行器


## 部署
1.运行docker目录下的build.sh脚本，生成镜像
2.将镜像推送到ops.zlgcloud.com的docker仓库
```
docker tag zws-r2-pressure-execute:latest ops.zlgcloud.com/library/zws-r2-pressure-execute:master
docker push ops.zlgcloud.com/library/zws-r2-pressure-execute:master
```
在待部署的服务器拉取镜像:  
`docker pull ops.zlgcloud.com/library/zws-r2-pressure-execute:master`  
(若未登录docker请先登录docker login -u zycloud -p ZhiyuanCloud1234! ops.zlgcloud.com)  

将外部配置文件放到
`/data/pressure2/execute/config`

生成容器:
```
docker run -p 9320:9320 --name pressure_execute -v /data/pressure2/execute/logs:/home/pressure2execute/logs/pressureR2_execute -v /data/pressure2/execute/config:/home/pressure2execute/config -itd ops.zlgcloud.com/library/zws-r2-pressure-execute:master
```

## 给mqtt服务器添加压测节点虚拟网卡的路由
`sudo route add -net 192.168.10.0/24 gw 192.168.24.95`
压测节点虚拟网卡：192.168.10.x
