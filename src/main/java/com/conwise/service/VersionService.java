package com.conwise.service;

public interface VersionService {
    boolean tryUpdateNodeVersion(int canvasId,String nodeId, Integer clientVersion);

    boolean tryUpdateEdgeVersion(int canvasId,String edgeId, Integer clientVersion);
    void  removeNodeVersion(int canvasId,String nodeID);
    void  removeEdgeVersion(int canvasId,String edgeId);
}
