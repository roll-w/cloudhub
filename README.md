# Cloudhub

可靠高可用的分布式文件系统。

## 运行说明

运行步骤：

1. 运行`meta-server`；
2. 运行`file-server`；

**一定要保证`meta-server`最先运行**。

### 配置修改

修改位于`resource/`文件夹下的`cloudhub.conf`文件中的配置。

> 打包后位于`conf/`文件夹下。

### 部署说明

打包后解压相应的`starter.tar.gz`压缩文件，运行位于`sbin`文件夹下的对应启动脚本，
运行前注意修改配置以及脚本中的环境变量。

## 文档指引

- [`file-server`工作流程说明](cloudhub-file-server/README.md)
- [`meta-server`工作流程说明](cloudhub-meta-server/README.md)
- [`client`（对象存储服务实现）主要功能说明](cloudhub-client/README.md)

## 文件系统架构设计

Cloudhub文件系统（Cloudhub File System, CFS）是支撑Cloudhub的核心部分，
其被设计成一个仅限读及有限写操作的、可伸缩的分布式文件系统。

CFS以做到BASE为目标，牺牲一部分一致性以保证系统的高可用。
> BASE: Basically Available, Soft State, Eventual Consistency

对文件的操作被设计为了读（GET）、上传（PUT）和删除（DELETE）的有限操作，因此对于静态且较长时期不变的数据保存具有优势。

### 服务器架构

CFS具有主从架构，完整的CFS部署中包含一个元数据服务器（`meta-server`）以及文件服务器集群（`file-server`）。

元数据服务器实现对文件服务器集群的管理、对副本的管理以及对请求的分配。单个的元数据服务器简化了系统架构。

文件服务器实现对文件的分块存储。

### 文件存储设计

在对文件的存储上，CFS采用键值对映射的方式。使得文件能够快速映射到特定文件服务器上。

CFS是针对文件的存储，对于所有的文件都按原样存储，因此对文件的压缩过程需要在上传之前完成。

在对文件存储时，会被划分为大量的块，分块存储在不同的容器中。
块是文件服务器之间交流、同步的基本单位。

容器被设计为了可以根据块数的不同，伸缩大小的数据结构（当前还未实现，但预留了相应接口）。
每个容器会建立相应索引，通过索引能够快速寻址到文件所在位置。

文件通过哈希值完成分布映射，能够较好的分散文件。通常不会出现文件聚集在同一热点区域的情况而导致性能降低。

### 数据结构

CFS在设计上支持存储较大文件。

通过将无数块（`block`）组织成容器（`container`），再组织成为容器组（`container group`），
一个容器组可容纳大量文件。

对于超大文件的支持取决于当前服务器的磁盘大小，对超大文件的分块支持尚未完成。

### 通信

所有的CFS通信都通过TCP/IP协议实现。具体而言，是通过RPC远程过程调用实现的。

### 可用性

CFS的其中一个目标就是做到出现故障也能部分的使用服务，维持基本的数据存储获取功能。

在确认其中一个`file-server`发生故障（通常是数据损坏）时，
`meta-server`会尝试调配其他副本的数据进行修复。

#### 心跳检测

每个`file-server`周期性的向`meta-server`发送心跳信息。
当经过一段时间接收不到来自该`file-server`的心跳时，视为服务器宕机并标记为dead，
将不会再向它们发送或转发任何请求。`file-server`宕机可能导致某些文件副本数变少，
因此`meta-server`需要确认哪些文件副本丢失，并在必要时启动同步过程。

在`file-server`标记为dead后，启动同步过程的时间延迟较长（一般超过10分钟）。
这是为防止因服务器状态波动或网络状态波动而导致的暂时性丢失，而引起大量的复制请求被发送，
造成网络风暴。

#### 数据完整性

获取的数据在传输过程中损坏的可能性是极大的，例如磁盘故障、网络故障都有可能导致数据发生损坏。

为了保证数据在获取时始终是完整的，在上传之后，`meta-server`始终会保存文件的哈希值用于校验。

### 一致性

CFS始终维持在软状态，即不同的文件服务器的副本同步存在数据延时。

这是由于CFS架构设计中，文件在系统中实际是只读的状态，且在同步副本时以容器为同步的基本单位。

当容器同步之后，文件在此副本中可保持可用状态不变，无论之后的容器是否发生变化，
因此无需时刻保持一致状态。

## 性能测试

简要介绍对此系统的本地读写性能测试。

### 测试环境

三台配置Java运行环境的Linux虚拟机，机器相关配置如下：

- 系统: 均为Ubuntu系统下测试，版本号：20.04.5 LTS
- 硬盘：挂载一块60G大小的SSD；
- CPU：Intel 10代CPU；
- 彼此间网络延迟：小于2ms。

### 说明

- 大文件：指文件大小大于1GB的文件；
- 中等大小文件：文件大小大于64MB但小于1GB的文件。
- 小文件：小于64MB的文件。


完整测试结果位于`/tests` 目录下。


## 开源许可

```text
Cloudhub - A distributed file system.
Copyright (C) 2022 HUEL Team.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
```
