# skan

Skan is a simple terminal [kanban
board](https://en.wikipedia.org/wiki/Kanban_board) with minimal configuration.
It has a focus of minimal configuration, a small core feature-set, and easy
on-boarding.

![skan overview](https://i.imgur.com/VzWRjN4.png)

## Overview

As stated up above, the focus of skan is intentionally minimal. The swim lanes
aren't configurable, meaning you can't add more or change them. While this may
change in the future, for now it's intentional to simply focus on what you're
working on and what you're not. This started as a project just to help myself
keep track of various things that I was working on in various contexts of my
life, meaning it's catered to my own workflow.

To understand skan, you only need a couple concepts:

1. Your main view (board) holds items either _In Progress_ or in _TODO_ status.
2. Each context has a separate board.
3. There are only a few screens/views
    1. Your main board view
    2. Your new item view
    3. Your context operation view
    4. Your edit context view

## Getting Started

To get started with skan, you'll need to download the artifacts from the latest
release found [here](https://github.com/ckipp01/skan/releases). Currently only
Linux and Mac are supported. Windows is supported, but you'll need to build from
source locally.

### MacOS

1. Download `skan-x86_64-apple-darwin.tar.gz` from [releases](https://github.com/ckipp01/skan/releases).
2. Unzip via `tar -xf skan-x86_64-apple-darwin.tar.gz`
3. Move to your bin (or somewhere on your `$PATH`) `mv
   skan-x86_64-apple-darwin/skan ~/bin/skan`
4. Now you're ready to use `skan`.

Note that when on mac you'll probably get a warning about:

> "skan" can’t be opened because Apple cannot check it for malicious software...

In order to get around this you'll need to run the following:

```
xattr -d com.apple.quarantine <path-to-skan>
```

### Linux

1. Download `skan-x86_64-linux.tar.gz` from [releases](https://github.com/ckipp01/skan/releases).
2. Unzip via `tar -xf skan-x86_64-linux.tar.gz`
3. Move to your bin (or somewhere on your `$PATH`) `mv
   skan-x86_64-linux/skan ~/bin/skan`
4. Now you're ready to use `skan`.

## Usage

On the bottom of every screen you'll see a small help menu with the existing
bindings. These will always be your reference if you're stuck. If you don't see
this, you may have `skan` in too small of a space.

### Creating a new item

To create a new item you'll want to enter the new item screen by using `n` when
in your board view. This will allow you to give the item a `title`, a
`description`, and a `priority`.

![new-item](https://i.imgur.com/C18J9Qi.png)

### Creating/editing contexts

Contexts exist to separate your tasks into different categories. You can rename
a context, add a new one, or delete one. You can choose which of these
operations you'd like to make in the context view. You can get to this view by
using `c` from your main board view. This view will show you your existing
contexts, your current context, and a choice between context actions that you
can choose:

![context-view](https://i.imgur.com/cwbQEsX.png)

### Configuration

`skan` follows the various practices for each OS to determine where it should
put the config and data files it needs:

- the [XDG base
  directory](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html)
  and the [XDG user
  directory](https://www.freedesktop.org/wiki/Software/xdg-user-dirs/)
  specifications on Linux.
- the [Known Folder
  API](https://learn.microsoft.com/en-us/windows/win32/shell/knownfolderid?redirectedfrom=MSDN)
  on Windows
- the [Standard
  Directories](https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/FileSystemProgrammingGuide/FileSystemOverview/FileSystemOverview.html#//apple_ref/doc/uid/TP40010672-CH2-SW6)
  guidelines on macOS

So in your config dir `skan` will look for a `config.json` file. The current
configurations values and their defaults are below:

```json
{
    "dataDir": "specific to your OS, see above for the default data dir",
    "zoneId": "GMT+2"
}
```
