package com.github.dolly0526.jessicarpc.api;

import com.github.dolly0526.jessicarpc.common.support.ServiceSpiSupport;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

/**
 * RPC框架对外提供的服务接口
 *
 * @author yusenyang
 * @create 2021/3/8 16:32
 */
public interface RpcAccessPoint extends Closeable {

    /**
     * 客户端获取远程服务的引用
     *
     * @param uri          远程服务地址
     * @param serviceClass 服务的接口类的Class
     * @param <T>          服务接口的类型
     * @return 远程服务引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 服务端注册服务的实现实例
     *
     * @param service      实现实例
     * @param serviceClass 服务的接口类的Class
     * @param <T>          服务接口的类型
     * @return 服务地址
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 获取注册中心的引用
     *
     * @param nameServiceUri 注册中心URI
     * @return 注册中心引用
     */
    default NameService getNameService(URI nameServiceUri) {
        Collection<NameService> nameServices = ServiceSpiSupport.loadAll(NameService.class);

        // 根据支持的协议查找对应的注册中心，找到后建立连接
        for (NameService nameService : nameServices) {
            if (nameService.supportedSchemes().contains(nameServiceUri.getScheme())) {
                nameService.connect(nameServiceUri);
                return nameService;
            }
        }
        return null;
    }

    /**
     * 服务端启动RPC框架，监听接口，开始提供远程服务。
     *
     * @return 服务实例，用于程序停止的时候安全关闭服务。
     */
    Closeable startServer() throws Exception;
}