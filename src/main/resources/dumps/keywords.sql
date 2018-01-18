--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.5.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = face_control, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: s_cameras_keywords; Type: TABLE; Schema: face_control; Owner: postgres
--

CREATE TABLE s_cameras_keywords (
    label character varying(50),
    value character varying(50),
    id numeric NOT NULL,
    icon character varying(20),
    stamp character varying(30)
);


ALTER TABLE s_cameras_keywords OWNER TO postgres;

--
-- Name: TABLE s_cameras_keywords; Type: COMMENT; Schema: face_control; Owner: postgres
--

COMMENT ON TABLE s_cameras_keywords IS 'ключевые слова для поиска контроля лиц';


--
-- Name: s_cameras_keywords_id_seq; Type: SEQUENCE; Schema: face_control; Owner: postgres
--

CREATE SEQUENCE s_cameras_keywords_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE s_cameras_keywords_id_seq OWNER TO postgres;

--
-- Name: s_cameras_keywords_id_seq; Type: SEQUENCE OWNED BY; Schema: face_control; Owner: postgres
--

ALTER SEQUENCE s_cameras_keywords_id_seq OWNED BY s_cameras_keywords.id;


--
-- Name: id; Type: DEFAULT; Schema: face_control; Owner: postgres
--

ALTER TABLE ONLY s_cameras_keywords ALTER COLUMN id SET DEFAULT nextval('s_cameras_keywords_id_seq'::regclass);


--
-- Data for Name: s_cameras_keywords; Type: TABLE DATA; Schema: face_control; Owner: postgres
--

COPY s_cameras_keywords (label, value, id, icon, stamp) FROM stdin;
		28053	\N	\N
stdpr-office-fl2-cam3	stdpr-office-fl2-cam3	28054	\N	\N
без группы	без группы	28055	\N	\N
stdpr-office-cam4	stdpr-office-cam4	28056	\N	\N
stdpr-office-fl2-cam1	stdpr-office-fl2-cam1	28057	\N	\N
входовая камера	входовая камера	28058	\N	\N
г.Москва ул.Трифоновская д.2	г.Москва ул.Трифоновская д.2	28059	\N	\N
Кр. Площадь	Кр. Площадь	28060	\N	\N
камера в самом дульнем углу офиса	камера в самом дульнем углу офиса	28061	\N	\N
Cam 0-1-копия	Cam 0-1-копия	28062	\N	\N
CD3-5	CD3-5	28063	\N	\N
ул. Ташкентская	ул. Ташкентская	28064	\N	\N
угловая камера	угловая камера	28065	\N	\N
stdpr-office-cam-12-копия	stdpr-office-cam-12-копия	28066	\N	\N
DE3-8	DE3-8	28067	\N	\N
Самаркандский б-р	Самаркандский б-р	28068	\N	\N
камера около лестницы на втором этаже	камера около лестницы на втором этаже	28069	\N	\N
A2-2	A2-2	28070	\N	\N
A2-0	A2-0	28071	\N	\N
окололестничная камера	окололестничная камера	28072	\N	\N
Cam 2-2	Cam 2-2	28073	\N	\N
Cam 2-0	Cam 2-0	28074	\N	\N
Cam 0-1	Cam 0-1	28075	\N	\N
ZEL-876	ZEL-876	28076	\N	\N
Cam 3-8	Cam 3-8	28077	\N	\N
Cam 3-5	Cam 3-5	28078	\N	\N
stdpr-office-cam-12	stdpr-office-cam-12	28079	\N	\N
ул. Тургенева	ул. Тургенева	28080	\N	\N
\.


--
-- Name: s_cameras_keywords_id_seq; Type: SEQUENCE SET; Schema: face_control; Owner: postgres
--

SELECT pg_catalog.setval('s_cameras_keywords_id_seq', 28080, true);


--
-- Name: s_cameras_keywords_id_pk; Type: CONSTRAINT; Schema: face_control; Owner: postgres
--

ALTER TABLE ONLY s_cameras_keywords
    ADD CONSTRAINT s_cameras_keywords_id_pk PRIMARY KEY (id);


--
-- Name: s_cameras_keywords_id_uindex; Type: INDEX; Schema: face_control; Owner: postgres
--

CREATE UNIQUE INDEX s_cameras_keywords_id_uindex ON s_cameras_keywords USING btree (id);


--
-- Name: s_cameras_keywords_label_uindex; Type: INDEX; Schema: face_control; Owner: postgres
--

CREATE UNIQUE INDEX s_cameras_keywords_label_uindex ON s_cameras_keywords USING btree (label);


--
-- Name: s_cameras_keywords_value_uindex; Type: INDEX; Schema: face_control; Owner: postgres
--

CREATE UNIQUE INDEX s_cameras_keywords_value_uindex ON s_cameras_keywords USING btree (value);


--
-- PostgreSQL database dump complete
--

