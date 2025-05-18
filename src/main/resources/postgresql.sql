create database conwise_db;
create table public.users
(
    id       serial
        primary key,
    username varchar(255) not null
        unique,
    email    varchar(255) not null
        unique,
    password varchar(255) not null
);

alter table public.users
    owner to conwise_dba;

create table public.canvases
(
    id            serial
        primary key,
    user_id       integer                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              not null,
    title         varchar(255)             default 'new Canvas'::character varying                                                                                                                                                                                                                                                                                                                                                                                                                                                     not null,
    description   text,
    thumbnail_url varchar(255),
    created_at    timestamp with time zone default CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                                                                                                                                                                                   not null,
    updated_at    timestamp with time zone default CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                                                                                                                                                                                   not null,
    nodes         jsonb                    default '[{"id": "node-1", "data": {"text": "这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。", "theme": "bg-linear-to-r from-green-300 via-emerald-300 to-teal-300"}, "type": "textNode", "position": {"x": -10.5, "y": -150.5}}, {"id": "node-2", "data": {"text": "这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。", "theme": "bg-linear-to-r from-purple-300 via-indigo-300 to-blue-300"}, "type": "textNode", "position": {"x": 300, "y": 60}}]'::jsonb not null,
    edges         jsonb                    default '[{"id": "edge-1", "data": {"label": "[关系]"}, "type": "curvedEdge", "source": "node-1", "target": "node-2", "animated": true, "sourceHandle": null, "targetHandle": null}]'::jsonb                                                                                                                                                                                                                                                                                                not null,
    settings      jsonb                    default '{}'::jsonb,
    user_name     varchar(50)              default ''::character varying                                                                                                                                                                                                                                                                                                                                                                                                                                                               not null
);

alter table public.canvases
    owner to postgres;

create table public.canvas_shares
(
    id         serial
        primary key,
    canvas_id  integer                                       not null,
    user_id    integer                                       not null,
    permission varchar(50) default 'view'::character varying not null,
    constraint canvas_shares_unique_canvas_user
        unique (canvas_id, user_id)
);

alter table public.canvas_shares
    owner to postgres;

