DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id       SERIAL       PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    role     VARCHAR(20)  NOT NULL CHECK (role IN ('AUTHOR', 'EDITOR', 'ADMIN'))
);

CREATE TABLE articles (
    id         SERIAL       PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    category   VARCHAR(50)  NOT NULL,
    status     VARCHAR(20)  NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id  INTEGER      NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_articles_author   ON articles(author_id);
CREATE INDEX idx_articles_category ON articles(category);
CREATE INDEX idx_articles_status   ON articles(status);

INSERT INTO users (username, email, role) VALUES
    ('ivan_petrov',   'ivan@mail.ru',   'AUTHOR'),
    ('olga_smirnova', 'olga@mail.ru',   'AUTHOR'),
    ('petr_ivanov',   'petr@mail.ru',   'EDITOR'),
    ('anna_k',        'anna@mail.ru',   'AUTHOR'),
    ('sergey_m',      'sergey@mail.ru', 'ADMIN');

INSERT INTO articles (title, content, category, status, author_id) VALUES
    ('Новости технологий',       'Полный текст...',  'Технологии', 'PUBLISHED', 1),
    ('Обзор нового смартфона',   'Подробный обзор...','Технологии','DRAFT',     1),
    ('Выборы в парламент',       'Результаты...',    'Политика',   'PUBLISHED', 2),
    ('Экономический прогноз',    'Аналитики...',     'Экономика',  'PUBLISHED', 2),
    ('Спортивный матч',          'Разгромная...',    'Спорт',      'ARCHIVED',  3),
    ('Премьера нового фильма',   'Режиссёр...',      'Культура',   'PUBLISHED', 4),
    ('Курс валют на неделю',     'Доллар...',        'Экономика',  'DRAFT',     4),
    ('Погода на неделю',         'Синоптики...',     'Общество',   'PUBLISHED', 5),
    ('Научное открытие',         'Учёные...',        'Наука',      'PUBLISHED', 1),
    ('Интервью с режиссёром',    'Беседа...',        'Культура',   'DRAFT',     3),
    ('Рынок акций',              'Индекс...',        'Экономика',  'PUBLISHED', 2);