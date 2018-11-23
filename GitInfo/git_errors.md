# Git Error Summary

**issue-1:**

```
Please enter a commit message to explain why this merge is necessary,
especially if it merges an updated upstream into a topic branch.

Lines starting with '#' will be ignored, and an empty message aborts the commit.
```

> 这是由于在合并分支时，git发现一个端上传了指定文件内容，而在分布式的另一个端发现要删除指定的文件。这时，Git就疯了，然后就报上面合并错误信息。代表放弃当前本地commit内容。
>
> 方法一： 
> 1、直接关闭当前窗口，再重新打开新的窗口； 
> 2、接着就可以继续操作；但个人觉得这个方法不是办法中的办法，迫不得已，太麻烦了。（个人不建议）
>
> 方法二： 
> 1、当出现上述情况，先进入编辑状态，按i键，然后按键盘左上角的“Ese”退出键； 
>
> 2、输入“：wq”，注意是英文输入状态下的冒号，然后按下“Enter”键即可。

