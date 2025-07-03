--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-01 16:10:53

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
-- SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 25943)
-- Name: categorias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorias (
    id bigint NOT NULL,
    padre_id bigint,
    nombre character varying(255) NOT NULL
);


ALTER TABLE public.categorias OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 25946)
-- Name: categorias_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorias_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorias_seq OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 25947)
-- Name: comentarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comentarios (
    fecha_creacion timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    publicacion_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    texto character varying(255) NOT NULL,
    titulo character varying(255) NOT NULL
);


ALTER TABLE public.comentarios OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 25952)
-- Name: comentarios_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.comentarios_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.comentarios_seq OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 25953)
-- Name: encargues; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.encargues (
    fecha date,
    total real NOT NULL,
    id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    estado character varying(255) NOT NULL,
    CONSTRAINT encargues_estado_check CHECK (((estado)::text = ANY (ARRAY[('EN_CREACION'::character varying)::text, ('PENDIENTE'::character varying)::text, ('CANCELADO'::character varying)::text, ('ENTREGADO'::character varying)::text, ('COMPLETADO'::character varying)::text])))
);


ALTER TABLE public.encargues OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 25957)
-- Name: encargues_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.encargues_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.encargues_seq OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 25958)
-- Name: impresiones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.impresiones (
    color boolean NOT NULL,
    doble_cara boolean,
    id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    comentario_adicional character varying(200),
    estado character varying(255),
    formato character varying(255),
    nombre_archivo character varying(255),
    orientacion character varying(255),
    tipo_papel character varying(255)
);


ALTER TABLE public.impresiones OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 25963)
-- Name: impresiones_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.impresiones_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.impresiones_seq OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 25964)
-- Name: producto_categoria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.producto_categoria (
    categoria_id bigint NOT NULL,
    producto_id bigint NOT NULL
);


ALTER TABLE public.producto_categoria OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 25967)
-- Name: producto_encargue; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.producto_encargue (
    cantidad integer NOT NULL,
    encargue_id bigint NOT NULL,
    id bigint NOT NULL,
    producto_id bigint NOT NULL
);


ALTER TABLE public.producto_encargue OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 25970)
-- Name: producto_encargue_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.producto_encargue_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.producto_encargue_seq OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 25971)
-- Name: productos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.productos (
    habilitado boolean NOT NULL,
    precio real NOT NULL,
    id bigint NOT NULL,
    descripcion character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    imagenes character varying(255)[]
);


ALTER TABLE public.productos OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 25976)
-- Name: productos_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.productos_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.productos_seq OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 25977)
-- Name: publicaciones; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.publicaciones (
    fecha_creacion timestamp(6) without time zone NOT NULL,
    id bigint NOT NULL,
    contenido character varying(255) NOT NULL,
    imagen character varying(255),
    titulo character varying(255) NOT NULL
);


ALTER TABLE public.publicaciones OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 25982)
-- Name: publicaciones_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.publicaciones_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.publicaciones_seq OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 25983)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    apellido character varying(255),
    contrasenia character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    rol character varying(255) NOT NULL,
    telefono character varying(255),
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY (ARRAY[('ADMINISTRADOR'::character varying)::text, ('EMPLEADO'::character varying)::text, ('CLIENTE'::character varying)::text])))
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 25989)
-- Name: usuarios_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_seq OWNER TO postgres;

--
-- TOC entry 4912 (class 0 OID 25943)
-- Dependencies: 217
-- Data for Name: categorias; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.categorias VALUES (1, NULL, 'Libros');
INSERT INTO public.categorias VALUES (2, 1, 'Literatura');
INSERT INTO public.categorias VALUES (3, 2, 'Novela');
INSERT INTO public.categorias VALUES (4, 2, 'Cuento');
INSERT INTO public.categorias VALUES (5, 1, 'Infantil');
INSERT INTO public.categorias VALUES (6, NULL, 'Útiles Escolares');
INSERT INTO public.categorias VALUES (7, 6, 'Escritura');
INSERT INTO public.categorias VALUES (8, 6, 'Papel');
INSERT INTO public.categorias VALUES (9, NULL, 'Material de Oficina');
INSERT INTO public.categorias VALUES (10, NULL, 'Electrónicos');
INSERT INTO public.categorias VALUES (11, 10, 'Computación');
INSERT INTO public.categorias VALUES (12, 10, 'Audio');
INSERT INTO public.categorias VALUES (13, NULL, 'Juegos Educativos');
INSERT INTO public.categorias VALUES (14, 13, 'Juegos de Mesa');
INSERT INTO public.categorias VALUES (15, 13, 'Didácticos');


--
-- TOC entry 4914 (class 0 OID 25947)
-- Dependencies: 219
-- Data for Name: comentarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.comentarios VALUES ('2025-06-29 21:42:12.778', 1, 2, 2, '¿De qué marca son los marcadores?', 'Duda sobre marca');
INSERT INTO public.comentarios VALUES ('2025-06-29 21:42:41.569', 2, 1, 2, '¿Hasta que día es válida la oferta?', 'Duda fecha');
INSERT INTO public.comentarios VALUES ('2025-06-29 21:44:49.551', 3, 1, 3, 'Si compro más de 3 libros, hay más descuento??', 'Sobre los descuentos');


--
-- TOC entry 4916 (class 0 OID 25953)
-- Dependencies: 221
-- Data for Name: encargues; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.encargues VALUES (NULL, 0, 1, 1, 'EN_CREACION');
INSERT INTO public.encargues VALUES (NULL, 0, 4, 4, 'EN_CREACION');
INSERT INTO public.encargues VALUES ('2025-07-03', 199.99, 2, 2, 'COMPLETADO');
INSERT INTO public.encargues VALUES ('2025-07-03', 13.49, 3, 3, 'ENTREGADO');
INSERT INTO public.encargues VALUES ('2025-07-07', 57.469997, 5, 2, 'COMPLETADO');
INSERT INTO public.encargues VALUES ('2025-07-15', 74.98, 6, 3, 'ENTREGADO');
INSERT INTO public.encargues VALUES ('2025-07-02', 374.98, 7, 2, 'CANCELADO');
INSERT INTO public.encargues VALUES ('2025-07-10', 56.97, 9, 2, 'PENDIENTE');
INSERT INTO public.encargues VALUES ('2025-07-02', 25.99, 8, 3, 'PENDIENTE');
INSERT INTO public.encargues VALUES (NULL, 0, 10, 5, 'EN_CREACION');


--
-- TOC entry 4918 (class 0 OID 25958)
-- Dependencies: 223
-- Data for Name: impresiones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.impresiones VALUES (true, false, 2, 2, 'Cuidado con los bordes!!!', 'Pendiente', 'A4', '7f008633-17ea-441e-90d4-504e54627946_poster.jpg', 'vertical', 'Papel común');
INSERT INTO public.impresiones VALUES (true, false, 1, 2, '', 'En proceso', 'A4', '8990efd4-cbb5-4010-a57d-c22848f02f50_poster3.jpeg', 'vertical', 'Papel común');
INSERT INTO public.impresiones VALUES (false, false, 4, 3, '', 'Completado', 'A4', '03e66448-30bd-4e81-a0ee-0ac5b7c2d76b_canto1Illiada.pdf', 'vertical', 'Papel común');
INSERT INTO public.impresiones VALUES (false, false, 3, 3, '', 'Entregado', 'A4', '6e9439eb-486b-4325-9ad5-c940f541e1de_ci.jpg', 'vertical', 'Papel común');
INSERT INTO public.impresiones VALUES (true, false, 5, 3, '', 'Cancelado', 'A4', 'cbb45b0a-7256-4915-85bb-35e408ad566a_manual.pdf', 'vertical', 'Papel común');


--
-- TOC entry 4920 (class 0 OID 25964)
-- Dependencies: 225
-- Data for Name: producto_categoria; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.producto_categoria VALUES (3, 1);
INSERT INTO public.producto_categoria VALUES (4, 2);
INSERT INTO public.producto_categoria VALUES (5, 2);
INSERT INTO public.producto_categoria VALUES (3, 3);
INSERT INTO public.producto_categoria VALUES (5, 3);
INSERT INTO public.producto_categoria VALUES (7, 4);
INSERT INTO public.producto_categoria VALUES (8, 5);
INSERT INTO public.producto_categoria VALUES (11, 6);
INSERT INTO public.producto_categoria VALUES (9, 6);
INSERT INTO public.producto_categoria VALUES (12, 7);
INSERT INTO public.producto_categoria VALUES (14, 8);
INSERT INTO public.producto_categoria VALUES (15, 9);
INSERT INTO public.producto_categoria VALUES (5, 9);
INSERT INTO public.producto_categoria VALUES (9, 10);
INSERT INTO public.producto_categoria VALUES (7, 11);
INSERT INTO public.producto_categoria VALUES (8, 12);
INSERT INTO public.producto_categoria VALUES (9, 12);
INSERT INTO public.producto_categoria VALUES (11, 13);
INSERT INTO public.producto_categoria VALUES (5, 13);
INSERT INTO public.producto_categoria VALUES (15, 14);
INSERT INTO public.producto_categoria VALUES (7, 15);


--
-- TOC entry 4921 (class 0 OID 25967)
-- Dependencies: 226
-- Data for Name: producto_encargue; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.producto_encargue VALUES (1, 2, 1, 13);
INSERT INTO public.producto_encargue VALUES (1, 3, 2, 15);
INSERT INTO public.producto_encargue VALUES (1, 3, 3, 12);
INSERT INTO public.producto_encargue VALUES (1, 5, 4, 1);
INSERT INTO public.producto_encargue VALUES (1, 5, 5, 2);
INSERT INTO public.producto_encargue VALUES (2, 5, 6, 4);
INSERT INTO public.producto_encargue VALUES (1, 6, 7, 14);
INSERT INTO public.producto_encargue VALUES (1, 6, 8, 7);
INSERT INTO public.producto_encargue VALUES (1, 7, 9, 13);
INSERT INTO public.producto_encargue VALUES (1, 7, 10, 10);
INSERT INTO public.producto_encargue VALUES (10, 7, 11, 15);
INSERT INTO public.producto_encargue VALUES (2, 9, 12, 11);
INSERT INTO public.producto_encargue VALUES (1, 9, 13, 8);
INSERT INTO public.producto_encargue VALUES (1, 8, 14, 1);


--
-- TOC entry 4923 (class 0 OID 25971)
-- Dependencies: 228
-- Data for Name: productos; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.productos VALUES (true, 199.99, 13, 'Tablet para estudio y entretenimiento', 'Tablet Samsung Galaxy Tab A7', '{productos/c2e48f0a-b8d4-4286-a431-9564840caef5_tablet.jpeg}');
INSERT INTO public.productos VALUES (true, 8.99, 12, 'Agenda anual con calendario y stickers', 'Agenda 2025', '{productos/06d2b017-31c5-4f7d-91eb-b5a6d3fa72eb_agenda.webp}');
INSERT INTO public.productos VALUES (true, 4.5, 15, 'Pack de 10 bolígrafos de gel azul', 'Bolígrafos gel azul', '{productos/8110e717-9319-4e5b-ac06-9461cbc5be2b_boligrafo.webp}');
INSERT INTO public.productos VALUES (true, 14.99, 14, 'Juego para aprender sumas y restas', 'Juego didáctico de matemáticas', '{productos/1d9befda-8978-46b2-bfe8-84fbda74e118_juegoMate.webp,productos/b286b81f-8383-491f-b114-50c1b2b83914_juegoMate2.webp}');
INSERT INTO public.productos VALUES (true, 25.99, 1, 'Novela clásica de Gabriel García Márquez', 'Cien años de soledad', '{productos/2a4cbaf3-8e39-4a3e-bc60-d4fae3ce1d48_cienA_os.webp}');
INSERT INTO public.productos VALUES (true, 15.5, 2, 'Obra de Antoine de Saint-Exupéry', 'El principito', '{productos/f6eb9aa1-78a7-4a9e-b8bc-6f2ac649b82f_principito.jpg}');
INSERT INTO public.productos VALUES (true, 29.99, 3, 'Primer libro de la saga Harry Potter', 'Harry Potter y la piedra filosofal', '{productos/5060b284-5670-411d-b86e-df45f35cfc02_harryPotter.webp}');
INSERT INTO public.productos VALUES (true, 7.99, 4, 'Caja de 24 lápices de colores', 'Set de lápices de colores', '{productos/ce709593-57c0-47a0-9dec-f7b2b3250dcd_lapicesColores.jpeg}');
INSERT INTO public.productos VALUES (true, 3.99, 5, 'Cuaderno de tapa dura con 100 hojas', 'Cuaderno universitario', '{productos/1ba31455-0114-41c7-9d2a-c5e129e38006_cuaderno.webp,productos/4ad3c574-c919-4ea4-a999-c1755e5dc3ab_cuaderno2.webp}');
INSERT INTO public.productos VALUES (true, 59.99, 7, 'Auriculares bluetooth con micrófono', 'Auriculares inalámbricos JBL', '{productos/c7e1cbd4-3bf1-4b27-9b2a-3b523f6ac82e_auriculares.jpg}');
INSERT INTO public.productos VALUES (true, 499.99, 6, 'Notebook para estudiantes y oficina', 'Notebook Lenovo 14''''', '{productos/5c0ef255-88b4-4bee-9948-4ef5a733720d_lenovo.jpg}');
INSERT INTO public.productos VALUES (true, 44.99, 8, 'Juego de estrategia para toda la familia', 'Juego de mesa: Catan', '{productos/83f31d37-42ce-4274-af07-91252b76bd5c_catan.webp}');
INSERT INTO public.productos VALUES (true, 12.5, 9, 'Puzzle didáctico para niños pequeños', 'Puzzle educativo de animales', '{productos/88f8758a-d723-4f8e-9fb5-8f11a2862d83_puzzle.webp,productos/f0b6a2d1-a9b7-47e3-ae16-121e64be6b9f_puzzle2.jpeg}');
INSERT INTO public.productos VALUES (true, 129.99, 10, 'Silla con soporte lumbar y altura regulable', 'Silla ergonómica de oficina', '{productos/9e1d319d-7bfd-4500-a189-63b714b7df6f_silla.webp}');
INSERT INTO public.productos VALUES (true, 5.99, 11, 'Set de 6 resaltadores colores pastel', 'Resaltadores pastel', '{productos/45fdc680-104c-46c0-b9d7-c5eea290d6f9_resaltadores.webp,productos/8390ca4f-e6d8-481b-b075-34e9916dd29c_resaltadores2.webp,productos/96a7ecec-40cf-4be7-8add-ecd066c196e4_resaltadores3.webp}');


--
-- TOC entry 4925 (class 0 OID 25977)
-- Dependencies: 230
-- Data for Name: publicaciones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.publicaciones VALUES ('2025-06-29 18:32:11.778318', 1, 'Comprando tres libros, el último tiene 50% de descuento. Todos los libros participan, no te lo pierdas!!', 'publicaciones/6544d407-fb97-4bcb-99a9-0c083f5babd8_libros.jpg', 'Oferta de libros!!');
INSERT INTO public.publicaciones VALUES ('2025-06-29 18:33:37.42839', 2, 'Hay nuevos lápices, marcadores, gomas de borrar y mucho más!', 'publicaciones/b9681f84-d3be-4bbe-ae26-22bdad7897a6_utiless.jpg', 'Nuevos útiles');


--
-- TOC entry 4927 (class 0 OID 25983)
-- Dependencies: 232
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.usuarios VALUES (1, 'Sistema', '$2a$10$Wut.5yrHjsrWVRiDRqDcNeyCwQ/yR.xJ.tP5747zlYK6yWD72YZgq', 'admin@libreria.com', 'Administrador', 'ADMINISTRADOR', '091234567');
INSERT INTO public.usuarios VALUES (2, 'Pérez', '$2a$10$itu5FSirraGBqo5tUNJkNu6cYSF3HYbjKyGHFx4m4Yprr/YBNO4um', 'cliente1@correo.com', 'Juan', 'CLIENTE', '092345678');
INSERT INTO public.usuarios VALUES (3, 'González', '$2a$10$FCUtLcWBcu6fm5vvUpEMj.2ZNx5UWyxxEDYgU911bHD8HhmH3c/aa', 'cliente2@correo.com', 'Sofía', 'CLIENTE', '093456789');
INSERT INTO public.usuarios VALUES (4, 'Rodríguez', '$2a$10$DgZE/9g.br5iTLG800ycmuYbaYQX.zrCv4yDTQ43iIc1jz3R8M5Ki', 'empleado1@libreria.com', 'Laura ', 'EMPLEADO', '098989987');
INSERT INTO public.usuarios VALUES (5, 'Díaz', '$2a$10$So3/4uaawwjGNmqHsoZmMOY3vmBGnT.IkDlyRMoggK8shWKPlANBa', 'cliente3@correo.com', 'Marcos', 'CLIENTE', '098934556');


--
-- TOC entry 4934 (class 0 OID 0)
-- Dependencies: 218
-- Name: categorias_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorias_seq', 51, true);


--
-- TOC entry 4935 (class 0 OID 0)
-- Dependencies: 220
-- Name: comentarios_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.comentarios_seq', 51, true);


--
-- TOC entry 4936 (class 0 OID 0)
-- Dependencies: 222
-- Name: encargues_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.encargues_seq', 51, true);


--
-- TOC entry 4937 (class 0 OID 0)
-- Dependencies: 224
-- Name: impresiones_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.impresiones_seq', 51, true);


--
-- TOC entry 4938 (class 0 OID 0)
-- Dependencies: 227
-- Name: producto_encargue_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.producto_encargue_seq', 51, true);


--
-- TOC entry 4939 (class 0 OID 0)
-- Dependencies: 229
-- Name: productos_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.productos_seq', 51, true);


--
-- TOC entry 4940 (class 0 OID 0)
-- Dependencies: 231
-- Name: publicaciones_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.publicaciones_seq', 51, true);


--
-- TOC entry 4941 (class 0 OID 0)
-- Dependencies: 233
-- Name: usuarios_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_seq', 51, true);


--
-- TOC entry 4737 (class 2606 OID 25991)
-- Name: categorias categorias_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_nombre_key UNIQUE (nombre);


--
-- TOC entry 4739 (class 2606 OID 25993)
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- TOC entry 4741 (class 2606 OID 25995)
-- Name: comentarios comentarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT comentarios_pkey PRIMARY KEY (id);


--
-- TOC entry 4743 (class 2606 OID 25997)
-- Name: encargues encargues_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.encargues
    ADD CONSTRAINT encargues_pkey PRIMARY KEY (id);


--
-- TOC entry 4745 (class 2606 OID 25999)
-- Name: impresiones impresiones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.impresiones
    ADD CONSTRAINT impresiones_pkey PRIMARY KEY (id);


--
-- TOC entry 4747 (class 2606 OID 26001)
-- Name: producto_encargue producto_encargue_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto_encargue
    ADD CONSTRAINT producto_encargue_pkey PRIMARY KEY (id);


--
-- TOC entry 4749 (class 2606 OID 26003)
-- Name: productos productos_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_nombre_key UNIQUE (nombre);


--
-- TOC entry 4751 (class 2606 OID 26005)
-- Name: productos productos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.productos
    ADD CONSTRAINT productos_pkey PRIMARY KEY (id);


--
-- TOC entry 4753 (class 2606 OID 26007)
-- Name: publicaciones publicaciones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.publicaciones
    ADD CONSTRAINT publicaciones_pkey PRIMARY KEY (id);


--
-- TOC entry 4755 (class 2606 OID 26009)
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- TOC entry 4757 (class 2606 OID 26011)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 4765 (class 2606 OID 26012)
-- Name: producto_encargue fk3si64whag12rrvpfaia262ip3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto_encargue
    ADD CONSTRAINT fk3si64whag12rrvpfaia262ip3 FOREIGN KEY (producto_id) REFERENCES public.productos(id);


--
-- TOC entry 4759 (class 2606 OID 26017)
-- Name: comentarios fk69vtiv6pfa3utlb34dbhnsphg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT fk69vtiv6pfa3utlb34dbhnsphg FOREIGN KEY (publicacion_id) REFERENCES public.publicaciones(id) ON DELETE CASCADE;


--
-- TOC entry 4758 (class 2606 OID 26022)
-- Name: categorias fk8p1hav5nyrk70vqo30lr7k74p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT fk8p1hav5nyrk70vqo30lr7k74p FOREIGN KEY (padre_id) REFERENCES public.categorias(id);


--
-- TOC entry 4761 (class 2606 OID 26027)
-- Name: encargues fkcg152vmt314hjogguurdjo3mu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.encargues
    ADD CONSTRAINT fkcg152vmt314hjogguurdjo3mu FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- TOC entry 4763 (class 2606 OID 26032)
-- Name: producto_categoria fkck76h1dqwbw3rp8gkxkxytqe6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto_categoria
    ADD CONSTRAINT fkck76h1dqwbw3rp8gkxkxytqe6 FOREIGN KEY (categoria_id) REFERENCES public.categorias(id);


--
-- TOC entry 4760 (class 2606 OID 26037)
-- Name: comentarios fkdts62yj83qe3k748cgcjvm48r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT fkdts62yj83qe3k748cgcjvm48r FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- TOC entry 4764 (class 2606 OID 26042)
-- Name: producto_categoria fkfahqc7k27mgnlrr5q6oylure7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto_categoria
    ADD CONSTRAINT fkfahqc7k27mgnlrr5q6oylure7 FOREIGN KEY (producto_id) REFERENCES public.productos(id);


--
-- TOC entry 4766 (class 2606 OID 26047)
-- Name: producto_encargue fkg1co3tnvxmxkx948qqn6f4w0k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto_encargue
    ADD CONSTRAINT fkg1co3tnvxmxkx948qqn6f4w0k FOREIGN KEY (encargue_id) REFERENCES public.encargues(id);


--
-- TOC entry 4762 (class 2606 OID 26052)
-- Name: impresiones fki4qvi2mfx9gr8s2nxqdb1xjf3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.impresiones
    ADD CONSTRAINT fki4qvi2mfx9gr8s2nxqdb1xjf3 FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


-- Completed on 2025-07-01 16:10:53

--
-- PostgreSQL database dump complete
--

