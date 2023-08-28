# Cloudhub

A high available, scalable distributed file system.

## Requirements

- Java 17

You can clone this project and build it with Maven: `mvn clean package`.

## Getting Started

After you have built the project or already have packaged files, 
you can run the `meta-server` and `file-server` with the following steps:

1. First you should unpack the packaged file with like `tar -zxvf starter.tar.gz`;
2. Start `meta-server` with `sbin/start-meta-server.sh`;
3. Start `file-server` with `sbin/start-file-server.sh`. (No order required)

> Note: Before you start servers, you should also modify the environment variables 
> in the scripts if you haven't set the `JAVA_HOME` environment variable.

You can run `sbin/start-meta-server.sh -h` or `sbin/start-file-server.sh -h`
to get more information about the arguments.

### Modify Configuration

You can modify the configuration in `resource/cloudhub.conf` before you pack the project or
start it locally for testing.

After you have packed the project, you can modify the configuration in `conf/cloudhub.conf`.

> Note: For the configuration files loading order: 
> 
> Specified path by `--config` argument > `conf/cloudhub.conf` > 
> `cloudhub.conf` file in current directory >
> `resource/cloudhub.conf`.

## API Usage

Run the `mvn clean install` command to install the project to your local Maven repository.

Then you can add the dependency to your project:

```xml
<dependencies>
    <dependency>
        <groupId>tech.rollw.cloudhub</groupId>
        <artifactId>cloudhub-client</artifactId>
        <version>0.1.3</version>
    </dependency>
</dependencies>
```

## Architecture

Cloudhub File System (CFS) is designed as a scalable distributed file system
with read-only and limited write operations.

CFS aims to achieve BASE, sacrificing some consistency to ensure high availability of the system.

File operations after upload are limited to read and delete,
so it has the advantage of storing static and long-term unchanged data.

### Server Architecture

CFS is designed as a master-slave architecture. 
A complete CFS deployment includes a metadata server (`meta-server`) cluster
and a file server (`file-server`) cluster.

The metadata server implements the management of the file server cluster,
the management of the replicas, and the allocation of requests.
It is allowed to set up a backup metadata servers to achieve a certain degree of high availability.

The file server implements the storage of files in blocks.

### File Storage Architecture

For file storage, CFS uses a key-value mapping method. 
This makes it possible to quickly map files to specific file servers.

CFS is designed for file storage, and all files are stored as they are,
so the compression process for files needs to be completed before uploading.

Files are divided into a large number of blocks when stored,
and the blocks are stored in different containers.
Blocks are the basic unit of communication and synchronization between file servers.

Containers are designed as data structures that can scale in size according 
to the number of blocks (currently not implemented, but the corresponding 
interface is reserved).
Each container will create a corresponding index, and the file location 
can be quickly addressed through the index.

Files are distributed by hash values, which can disperse files well.
Usually, there will be no performance degradation due to files gathering in
the same hot spot area.

### Data Structure

CFS is designed to support the storage of large files.

By organizing countless `block`s into `container`s,
and then organizing them into `container group`s,
a `container group` can accommodate a large number of files.

The support for large files depends on the disk size of the current server,
and the support for larger files has not been completed.


### Availability

CFS is designed to achieve the goal of being able to use the service partially in 
the event of a failure, and to maintain the basic data storage and retrieval functions.

When it is confirmed that one of the `file-server`s has failed (usually data corruption),
the `meta-server` will try to allocate the data of other replicas for repair.

#### Heartbeat

Every `file-server` periodically sends heartbeat information to `meta-server`.
When no heartbeat is received from the `file-server` for a period of time,
it is considered that the server is down and marked as dead,
and no requests will be sent or forwarded to them.

The `file-server` crash may cause the number of replicas of some files to decrease,
so the `meta-server` needs to confirm which file replicas are lost and start 
the synchronization process if necessary.

After the `file-server` is marked as dead, the time delay for starting the 
synchronization process is long (usually more than 10 minutes).
This is to prevent a large number of replication requests from being 
sent due to temporary loss caused by server status fluctuations or network
status fluctuations, causing a network storm.

#### Data Integrity

Data corruption during transmission is highly possible, such as disk failure or network failure.

To ensure that the data is always complete when it is obtained,
the `meta-server` always saves the hash value of the file for verification after uploading.

### Consistency

CFS always maintains a soft state, there is a data delay in the 
synchronization of replicas between different file servers.

This is because in the CFS architecture design, the file is actually in a 
read-only state in the system, and the container is the basic unit of 
synchronization when synchronizing replicas.

After the container is synchronized, the file can remain in the available 
state in this replica, regardless of whether the container changes later,
so there is no need to keep the consistent state at all times.

## Contributing

You can contribute to this project by submitting issues or pull requests.

For major changes, please open an issue first to
discuss what you would like to change.

## License

```text
Cloudhub - A high available, scalable distributed file system.
Copyright (C) 2022 Cloudhub

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
