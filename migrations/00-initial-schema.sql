CREATE TABLE chats (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_id BIGINT NOT NULL UNIQUE
);

CREATE TABLE links (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url TEXT NOT NULL UNIQUE,
    description TEXT,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_links (
    chat_id BIGINT REFERENCES chats(id) ON DELETE CASCADE,
    link_id BIGINT REFERENCES links(id) ON DELETE CASCADE,
    tags TEXT[],
    filters TEXT[],
    PRIMARY KEY (chat_id, link_id)
);

CREATE INDEX idx_chats_id ON chats(id);
CREATE INDEX idx_chats_chat_id ON chats(chat_id);

CREATE INDEX idx_links_id ON links(id);
CREATE INDEX idx_links_url ON links(url);

CREATE INDEX idx_chat_links_chat_id ON chat_links(chat_id);
CREATE INDEX idx_chat_links_link_id ON chat_links(link_id);

CREATE INDEX idx_chat_links_tags ON chat_links USING GIN (tags);
CREATE INDEX idx_chat_links_filters ON chat_links USING GIN (filters);
