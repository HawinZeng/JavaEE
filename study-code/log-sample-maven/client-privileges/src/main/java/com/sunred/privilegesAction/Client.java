package com.sunred.privilegesAction;

import java.io.FilePermission;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

/**
 *  一个 权限客户端， 并具有特权赋能作用
 */
public class Client {

    public void doCheck(){
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                check();
                return null;
            }
        });
    }

    private void check(){
        // 文件 aa 的读权限
        Permission perm = new FilePermission("/aa.txt","read");
        AccessController.checkPermission(perm); // 检查有权限则往下执行，否则抛异常
        System.out.println("TestService has permission");
    }

}
