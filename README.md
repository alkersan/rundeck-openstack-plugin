# rundeck-openstack-plugin

[![Join the chat at https://gitter.im/alkersan/rundeck-openstack-plugin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/alkersan/rundeck-openstack-plugin)&nbsp;
[![Build Status](https://img.shields.io/travis/alkersan/rundeck-openstack-plugin/master.svg?style=flat-square)](https://travis-ci.org/alkersan/rundeck-openstack-plugin)

Rundeck Plugin   | &nbsp;
---------------- | ----
Service type:    | `ResourceModelSource`
Rundeck version: | 2.5+
Version:         | 1.1.0
Description:     | Obtains nodes from OpenStack compute service

### Setup
Download plugin jar from GitHub [releases](https://github.com/alkersan/rundeck-openstack-plugin/releases) and put it to `$RDECK_BASE/libext`

### Usage
On project configuration tab add new `Resource Model Source` and select type `OpenStack`. You'll prompted to enter several mandatory parameters, specific to your `OpenStack` setup:

Parameter           | Description
------------------- | -----------
 `Id`               | Used to distinguish node sources of same type in one Rundeck project
 `Tenant`           | Name of a Tenant (aka Project) in `OpenStack`
 `Username`         | An `OpenStack` user who can do queries to `OpenStack Compute` service
 `Password`         | Password (or API Key) of previously mentioned user
 `Endpoint`         | URL of `OpenStack Keystone` service. Can be found in `Horizon > Access & Security > API Access` tab
 `Refresh interval` | How often the background fetcher should refresh server list (default `30s`)
 `Tags separator`   | A character used to split instance meta-attribute named `tags` (if present) into Rundeck's tags (default `,`)<br>Technically it's a regex, so it's possible to set multiple separators like <code>,&#124;;&#124;##</code>

