# rundeck-openstack-plugin
[![Build Status](https://img.shields.io/travis/alkersan/rundeck-openstack-plugin/master.svg?style=flat-square)](https://travis-ci.org/alkersan/rundeck-openstack-plugin)

Rundeck Plugin   | &nbsp;
---------------- | ----
Service type:    | `ResourceModelSource`
Rundeck version: | 2.5+
Version:         | 1.0.0
Description:     | Obtains nodes from OpenStack compute service

### Setup
Download [plugin](https://github.com/alkersan/rundeck-openstack-plugin/releases) jar and put it to `$RDECK_BASE/libext`

### Usage
On project configuration tab add new `Resource Model Source` and select type `OpenStack`. You'll prompted to enter several mandatory parameters, specific to your `OpenStack` setup:

Parameter           | Description
------------------- | -----------
 `Id`               | Used to distinguish node sources of same type in one Rundeck project
 `Tenant`           | Name of a Tenant (aka Project) in `OpenStack`
 `Username`         | An `OpenStack` user who can do queries to `OpenStack Compute` service
 `Password`         | Password (or API Key) of previously mentioned user
 `Endpoint`         | URL of `OpenStack Keystone` service. Can be found in `Horizon > Access & Security > API Access` tab
 `Refresh interval` | How often the background fetcher should refresh server list (default 30s)
