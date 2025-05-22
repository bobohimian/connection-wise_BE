create sequence thumbnail_sequence;

alter sequence thumbnail_sequence owner to root;

create sequence canvases_id_seq1
    as integer;

alter sequence canvases_id_seq1 owner to root;

create table users
(
    id       serial
        primary key,
    username varchar(255) not null
        unique,
    email    varchar(255) not null
        unique,
    password varchar(255) not null
);

alter table users
    owner to root;

create table canvas_shares
(
    id         serial
        primary key,
    canvas_id  integer                                       not null,
    user_id    integer                                       not null,
    permission varchar(50) default 'view'::character varying not null,
    constraint canvas_shares_unique_canvas_user
        unique (canvas_id, user_id)
);

alter table canvas_shares
    owner to root;

create table canvases
(
    id                  integer                  default nextval('canvases_id_seq1'::regclass)                                                                                                                                                                                                                                                                                                                                                                                                                                               not null
        constraint canvases_pkey1
            primary key,
    user_id             integer                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              not null,
    title               varchar(255)             default 'new Canvas'::character varying                                                                                                                                                                                                                                                                                                                                                                                                                                                     not null,
    description         text,
    created_at          timestamp with time zone default CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                                                                                                                                                                                   not null,
    updated_at          timestamp with time zone default CURRENT_TIMESTAMP                                                                                                                                                                                                                                                                                                                                                                                                                                                                   not null,
    nodes               jsonb                    default '[{"id": "node-1", "data": {"text": "这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。", "theme": "bg-linear-to-r from-green-300 via-emerald-300 to-teal-300"}, "type": "textNode", "position": {"x": -10.5, "y": -150.5}}, {"id": "node-2", "data": {"text": "这是一个示例节点，你可以在其中编辑文本，拖拽节点和连接不同的节点。", "theme": "bg-linear-to-r from-purple-300 via-indigo-300 to-blue-300"}, "type": "textNode", "position": {"x": 300, "y": 60}}]'::jsonb not null,
    edges               jsonb                    default '[{"id": "edge-1", "data": {"label": "[关系]"}, "type": "curvedEdge", "source": "node-1", "target": "node-2", "animated": true, "sourceHandle": null, "targetHandle": null}]'::jsonb                                                                                                                                                                                                                                                                                                not null,
    settings            jsonb                    default '{}'::jsonb,
    user_name           varchar(50)              default ''::character varying                                                                                                                                                                                                                                                                                                                                                                                                                                                               not null,
    thumbnail_file_name varchar(100) generated always as ((('thumbnail_canvas_'::text || id) || '.webp'::text)) stored
);

alter table canvases
    owner to root;

alter sequence canvases_id_seq1 owned by canvases.id;

