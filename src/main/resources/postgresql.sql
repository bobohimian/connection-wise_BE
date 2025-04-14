--  创建一个数据库和角色
CREATE ROLE conwise_dba WITH LOGIN PASSWORD '123456';
CREATE DATABASE conwise_db WITH OWNER conwise_dba;
GRANT ALL PRIVILEGES ON DATABASE conwise_db TO conwise_dba;



CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE canvases (
                          id SERIAL PRIMARY KEY,
                          userid INTEGER NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          thumbnail_url VARCHAR(255),
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          nodes JSONB DEFAULT '[]',
                          edges JSONB DEFAULT '[]',
                          settings JSONB DEFAULT '{}'
);
-- 假设已有一个用户，ID为1
-- 首先创建一个新的画布
INSERT INTO canvases (
    userid,
    title,
    description,
    nodes,
    edges
) VALUES (
             1, -- 用户ID
             'Sample Flow Chart', -- 画布标题
             'A demonstration of different node types and connections', -- 画布描述
             -- 节点数据
             '[
               {
                 "id": "node-1",
                 "type": "textNode",
                 "position": { "x": 250, "y": 50 },
                 "data": { "text": "This is a sample note created with ReactFlow. You can drag nodes around and connect them." }
               },
               {
                 "id": "node-2",
                 "type": "todoNode",
                 "position": { "x": 200, "y": 300 },
                 "data": {
                   "items": [
                     { "id": "1", "text": "Create custom nodes", "completed": true },
                     { "id": "2", "text": "Implement drag and drop", "completed": true },
                     { "id": "3", "text": "Add connection lines", "completed": false },
                     { "id": "4", "text": "Style with Tailwind CSS", "completed": false }
                   ]
                 }
               },
               {
                 "id": "node-3",
                 "type": "codeNode",
                 "position": { "x": 500, "y": 350 },
                 "data": {
                   "background": "#48BB78",
                   "language": "javascript",
                   "code": "function greeting() {\n  return ''Hello, ReactFlow!'';\n}"
                 }
               }
             ]'::jsonb,
             -- 边数据
             '[
               {
                 "id": "edge-1-2",
                 "source": "node-1",
                 "target": "node-2",
                 "type": "curvedEdge",
                 "animated": true
               },
               {
                 "id": "edge-1-3",
                 "source": "node-1",
                 "target": "node-3",
                 "type": "polylineEdge"
               },
               {
                 "id": "edge-2-3",
                 "source": "node-2",
                 "target": "node-3",
                 "type": "straightEdge"
               }
             ]'::jsonb
         );