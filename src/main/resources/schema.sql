create table IF NOT EXISTS film
(
    id           IDENTITY NOT NULL PRIMARY KEY,
    rating_id    LONG,
    name         varchar not null,
    description  text,
    duration     INTEGER,
    release_date date,
    constraint film_id
        primary key (id)
);

comment on table film is 'Таблица с фильмами';
comment on column film.id is 'Уникальный идентификатор фильма';
comment on column film.rating_id is 'Идентификатор возрастного рейтинга';
comment on column film.name is 'Название фильма';
comment on column film.description is 'Описание фильма';
comment on column film.duration is ' Длительность фильма в минутах';
comment on column film.release_date is 'Дата выпуска фильма';

create table IF NOT EXISTS rating
(
    id   INTEGER,
    name varchar,
    constraint rating_pk
        primary key (id)
);

comment on table rating is 'Возрастные рейтинги фильмов';
comment on column rating.id is 'Уникальный идентификатор возрастного рейтинга';
comment on column rating.name is 'Наименование рейтинга';

alter table FILM
    alter column ID BIGINT auto_increment;

alter table FILM
    add constraint if not exists FILM_RATING___FK
        foreign key (RATING_ID) references RATING
            on update cascade on delete set null;



create table IF NOT EXISTS FILMORATE_USER
(
    id        IDENTITY NOT NULL PRIMARY KEY,
    login     text,
    name      text,
    email     text,
    birthday date not null,
    constraint user_id
        primary key (id)
);

comment on table FILMORATE_USER is 'Пользователи фильмограма';

comment on column FILMORATE_USER.id is 'Идентификатор пользователя';
comment on column FILMORATE_USER.login is 'Логин пользователя';
comment on column FILMORATE_USER.name is 'Имя пользователя';
comment on column FILMORATE_USER.email is 'Электронная почта пользователя';
comment on column FILMORATE_USER.birthday is 'Дата рождения пользователя';

create table IF NOT EXISTS genre
(
    id   IDENTITY NOT NULL PRIMARY KEY,
    name varchar,
    constraint genre_pk
        primary key (id)
);

comment on table rating is 'Жанры фильмов';
comment on column rating.id is 'Уникальный идентификатор жанра';
comment on column rating.name is 'Наименование жанра';

create table IF NOT EXISTS film_genre
(
    genre_id LONG,
    film_id  LONG,
    constraint film_genre_pk
        unique (film_id, genre_id),
    constraint "film_genre_FILM_ID_fk"
        foreign key (film_id) references FILM,
    constraint "film_genre_GENRE_id_fk"
        foreign key (film_id) references GENRE
);

comment on table film_genre is 'Соответствие фильма к жанрам';

comment on column film_genre.genre_id is 'Идентификатор жанра';
comment on column film_genre.film_id is 'Идентификатор фильма';

create table if not exists friend
(
    initiator_id LONG,
    acceptor_id  LONG,
    constraint "friend_FILMORATE_USER_ID_fk"
        foreign key (initiator_id) references FILMORATE_USER,
    constraint "friend_FILMORATE_USER_ID_fk2"
        foreign key (acceptor_id) references FILMORATE_USER
);

comment on table friend is 'Таблица связей друзей';

comment on column friend.initiator_id is 'Друг, отправивший заявку';

comment on column friend.acceptor_id is 'Друг, принимающий заявку';

create table if not exists user_film_likes
(
    film_id LONG not null,
    user_id LONG not null,
    constraint "user_film_likes_FILMORATE_USER_ID_fk"
        foreign key (user_id) references FILMORATE_USER,
    constraint "user_film_likes_FILM_ID_fk"
        foreign key (film_id) references FILM
);

comment on table user_film_likes is 'Лайки, поставленные пользователями фильмам';

comment on column user_film_likes.film_id is 'Полайканный фильм';

