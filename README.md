# GoblinExchange

![https://img.shields.io/badge/jdk-1.8-yellowgreen?style=flat-square&logo=java](https://img.shields.io/badge/jdk-1.8-yellowgreen?style=flat-square&logo=java)
![https://img.shields.io/badge/maven-3.5.0-success?style=flat-square&logo=apache-maven](https://img.shields.io/badge/maven-3.5.0-success?style=flat-square&logo=apache-maven)
![https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java](https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java)
![https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java](https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java)
![https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java](https://img.shields.io/badge/JDK-1.8-yellowgreen?style=flat-square&logo=java)

一个基于跳表实现的在内存中撮合的快速撮合引擎，能够在基础的硬件设备上实现每秒中处理10W订单。

# 单交易对撮合
- [x] 单线程 10W 次下单能够在 1s 内完成撮合
- [ ] 多线程 10W 次下单能够在 1s 内完成撮合
- [ ] 忽略相同订单编号的挂单
- [x] 支持交易深度
- [x] 支持交易流水
- [ ] 支持不同时间粒度的K线数据
- [ ] 支持灵活的费率调整
- [ ] 支持市价交易
- [ ] 支持杠杆、合约交易已经爆仓机制
- [x] 任意状态下撤销订单
- [ ] 容器化方式嵌入 SpringBoot 中