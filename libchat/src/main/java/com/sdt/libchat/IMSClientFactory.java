package com.sdt.libchat;

import com.sdt.libchat.core.ImsClient;
import com.sdt.libchat.core.NettyClient;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       IMSClientFactory.java</p>
 * <p>@PackageName:     com.freddy.im</p>
 * <b>
 * <p>@Description:     ims实例工厂方法</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/03/31 20:54</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class IMSClientFactory {

    public static ImsClient getIMSClient() {
        return NettyClient.getInstance();
    }
}
