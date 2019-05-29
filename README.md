# folders
We have a folder structure, represented by simple Java objects (like: TreeItem { public string name; public List<TreeItem> children; } ). The names of folders don't contain special characters. We receive a String list containing the folders that are readable by the user (given as full paths, eg '/var/lib/jenkins'), and another list containing the writable folders for the user (you can assume that if a folder is in writable list, it's also included in the readable list).

Give an algorithm that returns a tree that contains all writable folders that can be reached via at least readable folder from the root. The tree cannot contain folders that are not readable by the user (not even if it has writable subfolders). Folders that are not writable must be included if they have writable subfolders, but if it has no writable descendant, it has to be excluded, because it would just mislead the user (trying to select a writable target folder for example).

You can assume that the folder structure fits in memory.
