package com.conwise.service.impl;

import com.conwise.mapper.CanvasMapper;
import com.conwise.model.Edge;
import com.conwise.model.Node;
import com.conwise.service.VersionService;
import com.google.common.base.VerifyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VersionServiceImpl implements VersionService {
    private final CanvasMapper canvasMapper;
    // 版本缓存
    private final ConcurrentHashMap<String, VersionWrapper> versionMap = new ConcurrentHashMap<>();
    // 加载锁,防止多个线程同时加载同一资源版本号
    private final ConcurrentHashMap<String, CompletableFuture<VersionWrapper>> loadingMap = new ConcurrentHashMap<>();

    // 版本包装类,保存最近修改时间
    private static class VersionWrapper {
        private final AtomicInteger version;
        private long lastAccess;

        VersionWrapper(int initialVersion) {
            this.version = new AtomicInteger(initialVersion);
            this.lastAccess = System.currentTimeMillis();
        }

        int get() {
            return version.get();
        }

        long getLastAccess() {
            return lastAccess;
        }

        boolean compareAndSet(int expect, int update) {
            boolean success = version.compareAndSet(expect, update);
            if (success) {
                lastAccess = System.currentTimeMillis();
            }
            return success;
        }
    }

    public VersionServiceImpl(CanvasMapper canvasMapper) {
        this.canvasMapper = canvasMapper;
    }

    @Scheduled(initialDelay = 60_000, fixedRate = 30 * 60 * 1000)
    private void cleanStaleEntries() {
        long now = System.currentTimeMillis();
        // 超过30分钟没有更新的版本号
        long threshold = TimeUnit.MINUTES.toMillis(30);
        versionMap.entrySet().removeIf(entry ->
                now - entry.getValue().getLastAccess() > threshold
        );
    }

    @Override
    public boolean tryUpdateNodeVersion(int canvasId, String nodeId, Integer clientVersion) {
        // 获取版本号
        VersionWrapper versionWrapper = this.getNodeVersion(canvasId, nodeId);
        int latestVersion = versionWrapper.get();
        if (latestVersion != clientVersion) {
            return false;
        }
        return versionWrapper.compareAndSet(latestVersion, clientVersion + 1);
    }

    @Override
    public boolean tryUpdateEdgeVersion(int canvasId, String edgeId, Integer clientVersion) {
        // 获取版本号
        VersionWrapper versionWrapper = this.getEdgeVersion(canvasId, edgeId);
        int latestVersion = versionWrapper.get();
        if (latestVersion != clientVersion) {
            return false;
        }
        return versionWrapper.compareAndSet(latestVersion, clientVersion + 1);
    }

    @Override
    public void removeNodeVersion(int canvasId, String nodeID) {
        versionMap.remove(nodeID);
    }

    @Override
    public void removeEdgeVersion(int canvasId, String edgeId) {
        versionMap.remove(edgeId);
    }

    private VersionWrapper getNodeVersion(int canvasId, String nodeId) {
        // 1.尝试从缓冲直接获取版本号
        VersionWrapper versionWrapper = versionMap.get(nodeId);
        if (versionWrapper != null) {
            return versionWrapper;
        }
        System.out.println("查找版本号");
        // 2.从数据库获取该资源版本号
        CompletableFuture<VersionWrapper> loadingFuture = loadingMap.computeIfAbsent(nodeId,
                key -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // 2.1 等待加载结果存储map
                        Node node = canvasMapper.getNode(canvasId, nodeId);
                        Integer dbVersion = node.getVersion();
                        if (dbVersion == null) {
                            dbVersion = 0;
                        }
                        return new VersionWrapper(dbVersion);
                    } finally {
                        // 2.2 确保完成后移出加载状态
                        loadingMap.remove(key);
                    }
                }));
        try {
            // 3.等待加载结果
            VersionWrapper loadedVersionWrapper = loadingFuture.get();
            versionMap.put(nodeId, loadedVersionWrapper);
            return loadedVersionWrapper;
        } catch (ExecutionException | InterruptedException e) {
            loadingMap.remove(nodeId);
            throw new VerifyException("版本加载失败,节点id" + nodeId, e);
        }
    }

    private VersionWrapper getEdgeVersion(int canvasId, String edgeId) {
        // 1.尝试从缓冲直接获取版本号
        VersionWrapper versionWrapper = versionMap.get(edgeId);
        if (versionWrapper != null) {
            return versionWrapper;
        }
        System.out.println("查找版本号");
        // 2.从数据库获取该资源版本号
        CompletableFuture<VersionWrapper> loadingFuture = loadingMap.computeIfAbsent(edgeId,
                key -> CompletableFuture.supplyAsync(() -> {
                    try {
                        // 2.1 等待加载结果存储map
                        Edge edge = canvasMapper.getEdge(canvasId, edgeId);
                        Integer dbVersion = edge.getVersion();
                        if (dbVersion == null) {
                            dbVersion = 0;
                        }
                        return new VersionWrapper(dbVersion);
                    } finally {
                        // 2.2 确保完成后移出加载状态
                        loadingMap.remove(key);
                    }
                }));
        try {
            // 3.等待加载结果
            VersionWrapper loadedVersionWrapper = loadingFuture.get();
            versionMap.put(edgeId, loadedVersionWrapper);
            return loadedVersionWrapper;
        } catch (ExecutionException | InterruptedException e) {
            loadingMap.remove(edgeId);
            throw new VerifyException("版本加载失败,边id" + edgeId, e);
        }
    }
}
