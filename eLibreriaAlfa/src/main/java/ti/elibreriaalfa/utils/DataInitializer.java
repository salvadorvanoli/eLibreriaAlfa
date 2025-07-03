package ti.elibreriaalfa.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ti.elibreriaalfa.business.entities.*;
import ti.elibreriaalfa.business.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("!prod")
@Slf4j
public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EncargueRepository encargueRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ImpresionRepository impresionRepository;

    @Autowired
    private Producto_EncargueRepository productoEncargueRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${data.init.enabled:true}")
    private boolean dataInitEnabled;

    @PostConstruct
    @Transactional
    public void init() {
        if (!dataInitEnabled) {
            log.info("Carga de datos desactivada por configuración.");
            return;
        }

        try {
            // Crear usuarios
            List<Usuario> usuarios = crearUsuarios();

            // Crear categorías
            List<Categoria> categorias = crearCategorias();

            // Crear productos
            List<Producto> productos = crearProductos(categorias);

            // Crear encargues
            List<Encargue> encargues = crearEncargues(usuarios);

            // Crear producto_encargues
            crearProductoEncargues(productos, encargues);

            // Crear publicaciones
            List<Publicacion> publicaciones = crearPublicaciones();

            // Crear comentarios
            crearComentarios(publicaciones, usuarios);

            // Crear impresiones
            crearImpresiones(usuarios);
        } catch (Exception e) {
            log.error("Error al inicializar los datos: {}", e.getMessage());
        }
    }

    private List<Usuario> crearUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        // Usuario ID 1 - Administrador
        Usuario admin = new Usuario();
        admin.setId(1L);
        admin.setApellido("Sistema");
        admin.setContrasenia("$2a$10$Wut.5yrHjsrWVRiDRqDcNeyCwQ/yR.xJ.tP5747zlYK6yWD72YZgq");
        admin.setEmail("admin@libreria.com");
        admin.setNombre("Administrador");
        admin.setRol(Rol.ADMINISTRADOR);
        admin.setTelefono("091234567");
        usuarios.add(usuarioRepository.save(admin));

        // Usuario ID 2 - Cliente 1
        Usuario cliente1 = new Usuario();
        cliente1.setId(2L);
        cliente1.setApellido("Pérez");
        cliente1.setContrasenia("$2a$10$itu5FSirraGBqo5tUNJkNu6cYSF3HYbjKyGHFx4m4Yprr/YBNO4um");
        cliente1.setEmail("cliente1@correo.com");
        cliente1.setNombre("Juan");
        cliente1.setRol(Rol.CLIENTE);
        cliente1.setTelefono("092345678");
        usuarios.add(usuarioRepository.save(cliente1));

        // Usuario ID 3 - Cliente 2
        Usuario cliente2 = new Usuario();
        cliente2.setId(3L);
        cliente2.setApellido("González");
        cliente2.setContrasenia("$2a$10$FCUtLcWBcu6fm5vvUpEMj.2ZNx5UWyxxEDYgU911bHD8HhmH3c/aa");
        cliente2.setEmail("cliente2@correo.com");
        cliente2.setNombre("Sofía");
        cliente2.setRol(Rol.CLIENTE);
        cliente2.setTelefono("093456789");
        usuarios.add(usuarioRepository.save(cliente2));

        // Usuario ID 4 - Empleado
        Usuario empleado = new Usuario();
        empleado.setId(4L);
        empleado.setApellido("Rodríguez");
        empleado.setContrasenia("$2a$10$DgZE/9g.br5iTLG800ycmuYbaYQX.zrCv4yDTQ43iIc1jz3R8M5Ki");
        empleado.setEmail("empleado1@libreria.com");
        empleado.setNombre("Laura ");
        empleado.setRol(Rol.EMPLEADO);
        empleado.setTelefono("098989987");
        usuarios.add(usuarioRepository.save(empleado));

        // Usuario ID 5 - Cliente 3
        Usuario cliente3 = new Usuario();
        cliente3.setId(5L);
        cliente3.setApellido("Díaz");
        cliente3.setContrasenia("$2a$10$So3/4uaawwjGNmqHsoZmMOY3vmBGnT.IkDlyRMoggK8shWKPlANBa");
        cliente3.setEmail("cliente3@correo.com");
        cliente3.setNombre("Marcos");
        cliente3.setRol(Rol.CLIENTE);
        cliente3.setTelefono("098934556");
        usuarios.add(usuarioRepository.save(cliente3));

        return usuarios;
    }

    private List<Categoria> crearCategorias() {
        List<Categoria> categorias = new ArrayList<>();

        // Crear categorías con IDs específicos según schema.sql
        Categoria libros = new Categoria();
        libros.setId(1L);
        libros.setNombre("Libros");
        libros.setPadre(null);
        categorias.add(categoriaRepository.save(libros));

        Categoria literatura = new Categoria();
        literatura.setId(2L);
        literatura.setNombre("Literatura");
        literatura.setPadre(libros);
        categorias.add(categoriaRepository.save(literatura));

        Categoria novela = new Categoria();
        novela.setId(3L);
        novela.setNombre("Novela");
        novela.setPadre(literatura);
        categorias.add(categoriaRepository.save(novela));

        Categoria cuento = new Categoria();
        cuento.setId(4L);
        cuento.setNombre("Cuento");
        cuento.setPadre(literatura);
        categorias.add(categoriaRepository.save(cuento));

        Categoria infantil = new Categoria();
        infantil.setId(5L);
        infantil.setNombre("Infantil");
        infantil.setPadre(libros);
        categorias.add(categoriaRepository.save(infantil));

        Categoria utiles = new Categoria();
        utiles.setId(6L);
        utiles.setNombre("Útiles Escolares");
        utiles.setPadre(null);
        categorias.add(categoriaRepository.save(utiles));

        Categoria escritura = new Categoria();
        escritura.setId(7L);
        escritura.setNombre("Escritura");
        escritura.setPadre(utiles);
        categorias.add(categoriaRepository.save(escritura));

        Categoria papel = new Categoria();
        papel.setId(8L);
        papel.setNombre("Papel");
        papel.setPadre(utiles);
        categorias.add(categoriaRepository.save(papel));

        Categoria oficina = new Categoria();
        oficina.setId(9L);
        oficina.setNombre("Material de Oficina");
        oficina.setPadre(null);
        categorias.add(categoriaRepository.save(oficina));

        Categoria electronicos = new Categoria();
        electronicos.setId(10L);
        electronicos.setNombre("Electrónicos");
        electronicos.setPadre(null);
        categorias.add(categoriaRepository.save(electronicos));

        Categoria computo = new Categoria();
        computo.setId(11L);
        computo.setNombre("Computación");
        computo.setPadre(electronicos);
        categorias.add(categoriaRepository.save(computo));

        Categoria audio = new Categoria();
        audio.setId(12L);
        audio.setNombre("Audio");
        audio.setPadre(electronicos);
        categorias.add(categoriaRepository.save(audio));

        Categoria juegos = new Categoria();
        juegos.setId(13L);
        juegos.setNombre("Juegos Educativos");
        juegos.setPadre(null);
        categorias.add(categoriaRepository.save(juegos));

        Categoria mesa = new Categoria();
        mesa.setId(14L);
        mesa.setNombre("Juegos de Mesa");
        mesa.setPadre(juegos);
        categorias.add(categoriaRepository.save(mesa));

        Categoria didacticos = new Categoria();
        didacticos.setId(15L);
        didacticos.setNombre("Didácticos");
        didacticos.setPadre(juegos);
        categorias.add(categoriaRepository.save(didacticos));

        return categorias;
    }

    private List<Producto> crearProductos(List<Categoria> categorias) {
        List<Producto> productos = new ArrayList<>();

        // Buscar categorías por ID
        Categoria novela = categorias.stream().filter(c -> c.getId().equals(3L)).findFirst().orElse(null);
        Categoria cuento = categorias.stream().filter(c -> c.getId().equals(4L)).findFirst().orElse(null);
        Categoria infantil = categorias.stream().filter(c -> c.getId().equals(5L)).findFirst().orElse(null);
        Categoria escritura = categorias.stream().filter(c -> c.getId().equals(7L)).findFirst().orElse(null);
        Categoria papel = categorias.stream().filter(c -> c.getId().equals(8L)).findFirst().orElse(null);
        Categoria oficina = categorias.stream().filter(c -> c.getId().equals(9L)).findFirst().orElse(null);
        Categoria computo = categorias.stream().filter(c -> c.getId().equals(11L)).findFirst().orElse(null);
        Categoria audio = categorias.stream().filter(c -> c.getId().equals(12L)).findFirst().orElse(null);
        Categoria mesa = categorias.stream().filter(c -> c.getId().equals(14L)).findFirst().orElse(null);
        Categoria didacticos = categorias.stream().filter(c -> c.getId().equals(15L)).findFirst().orElse(null);

        // Producto ID 1 - Cien años de soledad
        Producto p1 = new Producto();
        p1.setId(1L);
        p1.setHabilitado(true);
        p1.setPrecio(25.99f);
        p1.setDescripcion("Novela clásica de Gabriel García Márquez");
        p1.setNombre("Cien años de soledad");
        p1.setImagenes(new String[]{"productos/2a4cbaf3-8e39-4a3e-bc60-d4fae3ce1d48_cienA_os.webp"});
        p1.setCategorias(Arrays.asList(novela));
        productos.add(productoRepository.save(p1));

        // Producto ID 2 - El principito
        Producto p2 = new Producto();
        p2.setId(2L);
        p2.setHabilitado(true);
        p2.setPrecio(15.5f);
        p2.setDescripcion("Obra de Antoine de Saint-Exupéry");
        p2.setNombre("El principito");
        p2.setImagenes(new String[]{"productos/f6eb9aa1-78a7-4a9e-b8bc-6f2ac649b82f_principito.jpg"});
        p2.setCategorias(Arrays.asList(cuento, infantil));
        productos.add(productoRepository.save(p2));

        // Producto ID 3 - Harry Potter
        Producto p3 = new Producto();
        p3.setId(3L);
        p3.setHabilitado(true);
        p3.setPrecio(29.99f);
        p3.setDescripcion("Primer libro de la saga Harry Potter");
        p3.setNombre("Harry Potter y la piedra filosofal");
        p3.setImagenes(new String[]{"productos/5060b284-5670-411d-b86e-df45f35cfc02_harryPotter.webp"});
        p3.setCategorias(Arrays.asList(novela, infantil));
        productos.add(productoRepository.save(p3));

        // Producto ID 4 - Set de lápices
        Producto p4 = new Producto();
        p4.setId(4L);
        p4.setHabilitado(true);
        p4.setPrecio(7.99f);
        p4.setDescripcion("Caja de 24 lápices de colores");
        p4.setNombre("Set de lápices de colores");
        p4.setImagenes(new String[]{"productos/ce709593-57c0-47a0-9dec-f7b2b3250dcd_lapicesColores.jpeg"});
        p4.setCategorias(Arrays.asList(escritura));
        productos.add(productoRepository.save(p4));

        // Producto ID 5 - Cuaderno universitario
        Producto p5 = new Producto();
        p5.setId(5L);
        p5.setHabilitado(true);
        p5.setPrecio(3.99f);
        p5.setDescripcion("Cuaderno de tapa dura con 100 hojas");
        p5.setNombre("Cuaderno universitario");
        p5.setImagenes(new String[]{"productos/1ba31455-0114-41c7-9d2a-c5e129e38006_cuaderno.webp", "productos/4ad3c574-c919-4ea4-a999-c1755e5dc3ab_cuaderno2.webp"});
        p5.setCategorias(Arrays.asList(papel));
        productos.add(productoRepository.save(p5));

        // Producto ID 6 - Notebook Lenovo
        Producto p6 = new Producto();
        p6.setId(6L);
        p6.setHabilitado(true);
        p6.setPrecio(499.99f);
        p6.setDescripcion("Notebook para estudiantes y oficina");
        p6.setNombre("Notebook Lenovo 14''");
        p6.setImagenes(new String[]{"productos/5c0ef255-88b4-4bee-9948-4ef5a733720d_lenovo.jpg"});
        p6.setCategorias(Arrays.asList(computo, oficina));
        productos.add(productoRepository.save(p6));

        // Producto ID 7 - Auriculares JBL
        Producto p7 = new Producto();
        p7.setId(7L);
        p7.setHabilitado(true);
        p7.setPrecio(59.99f);
        p7.setDescripcion("Auriculares bluetooth con micrófono");
        p7.setNombre("Auriculares inalámbricos JBL");
        p7.setImagenes(new String[]{"productos/c7e1cbd4-3bf1-4b27-9b2a-3b523f6ac82e_auriculares.jpg"});
        p7.setCategorias(Arrays.asList(audio));
        productos.add(productoRepository.save(p7));

        // Producto ID 8 - Juego Catan
        Producto p8 = new Producto();
        p8.setId(8L);
        p8.setHabilitado(true);
        p8.setPrecio(44.99f);
        p8.setDescripcion("Juego de estrategia para toda la familia");
        p8.setNombre("Juego de mesa: Catan");
        p8.setImagenes(new String[]{"productos/83f31d37-42ce-4274-af07-91252b76bd5c_catan.webp"});
        p8.setCategorias(Arrays.asList(mesa));
        productos.add(productoRepository.save(p8));

        // Producto ID 9 - Puzzle educativo
        Producto p9 = new Producto();
        p9.setId(9L);
        p9.setHabilitado(true);
        p9.setPrecio(12.5f);
        p9.setDescripcion("Puzzle didáctico para niños pequeños");
        p9.setNombre("Puzzle educativo de animales");
        p9.setImagenes(new String[]{"productos/88f8758a-d723-4f8e-9fb5-8f11a2862d83_puzzle.webp", "productos/f0b6a2d1-a9b7-47e3-ae16-121e64be6b9f_puzzle2.jpeg"});
        p9.setCategorias(Arrays.asList(didacticos, infantil));
        productos.add(productoRepository.save(p9));

        // Producto ID 10 - Silla ergonómica
        Producto p10 = new Producto();
        p10.setId(10L);
        p10.setHabilitado(true);
        p10.setPrecio(129.99f);
        p10.setDescripcion("Silla con soporte lumbar y altura regulable");
        p10.setNombre("Silla ergonómica de oficina");
        p10.setImagenes(new String[]{"productos/9e1d319d-7bfd-4500-a189-63b714b7df6f_silla.webp"});
        p10.setCategorias(Arrays.asList(oficina));
        productos.add(productoRepository.save(p10));

        // Producto ID 11 - Resaltadores pastel
        Producto p11 = new Producto();
        p11.setId(11L);
        p11.setHabilitado(true);
        p11.setPrecio(5.99f);
        p11.setDescripcion("Set de 6 resaltadores colores pastel");
        p11.setNombre("Resaltadores pastel");
        p11.setImagenes(new String[]{"productos/45fdc680-104c-46c0-b9d7-c5eea290d6f9_resaltadores.webp", "productos/8390ca4f-e6d8-481b-b075-34e9916dd29c_resaltadores2.webp", "productos/96a7ecec-40cf-4be7-8add-ecd066c196e4_resaltadores3.webp"});
        p11.setCategorias(Arrays.asList(escritura));
        productos.add(productoRepository.save(p11));

        // Producto ID 12 - Agenda 2025
        Producto p12 = new Producto();
        p12.setId(12L);
        p12.setHabilitado(true);
        p12.setPrecio(8.99f);
        p12.setDescripcion("Agenda anual con calendario y stickers");
        p12.setNombre("Agenda 2025");
        p12.setImagenes(new String[]{"productos/06d2b017-31c5-4f7d-91eb-b5a6d3fa72eb_agenda.webp"});
        p12.setCategorias(Arrays.asList(papel, oficina));
        productos.add(productoRepository.save(p12));

        // Producto ID 13 - Tablet Samsung
        Producto p13 = new Producto();
        p13.setId(13L);
        p13.setHabilitado(true);
        p13.setPrecio(199.99f);
        p13.setDescripcion("Tablet para estudio y entretenimiento");
        p13.setNombre("Tablet Samsung Galaxy Tab A7");
        p13.setImagenes(new String[]{"productos/c2e48f0a-b8d4-4286-a431-9564840caef5_tablet.jpeg"});
        p13.setCategorias(Arrays.asList(computo, infantil));
        productos.add(productoRepository.save(p13));

        // Producto ID 14 - Juego didáctico matemáticas
        Producto p14 = new Producto();
        p14.setId(14L);
        p14.setHabilitado(true);
        p14.setPrecio(14.99f);
        p14.setDescripcion("Juego para aprender sumas y restas");
        p14.setNombre("Juego didáctico de matemáticas");
        p14.setImagenes(new String[]{"productos/1d9befda-8978-46b2-bfe8-84fbda74e118_juegoMate.webp", "productos/b286b81f-8383-491f-b114-50c1b2b83914_juegoMate2.webp"});
        p14.setCategorias(Arrays.asList(didacticos));
        productos.add(productoRepository.save(p14));

        // Producto ID 15 - Bolígrafos gel
        Producto p15 = new Producto();
        p15.setId(15L);
        p15.setHabilitado(true);
        p15.setPrecio(4.5f);
        p15.setDescripcion("Pack de 10 bolígrafos de gel azul");
        p15.setNombre("Bolígrafos gel azul");
        p15.setImagenes(new String[]{"productos/8110e717-9319-4e5b-ac06-9461cbc5be2b_boligrafo.webp"});
        p15.setCategorias(Arrays.asList(escritura));
        productos.add(productoRepository.save(p15));

        return productos;
    }

    private List<Encargue> crearEncargues(List<Usuario> usuarios) {
        List<Encargue> encargues = new ArrayList<>();

        // Buscar usuarios por ID
        Usuario usuario1 = usuarios.stream().filter(u -> u.getId().equals(1L)).findFirst().orElse(null);
        Usuario usuario2 = usuarios.stream().filter(u -> u.getId().equals(2L)).findFirst().orElse(null);
        Usuario usuario3 = usuarios.stream().filter(u -> u.getId().equals(3L)).findFirst().orElse(null);
        Usuario usuario4 = usuarios.stream().filter(u -> u.getId().equals(4L)).findFirst().orElse(null);
        Usuario usuario5 = usuarios.stream().filter(u -> u.getId().equals(5L)).findFirst().orElse(null);

        // Encargue ID 1 - EN_CREACION
        Encargue e1 = new Encargue();
        e1.setId(1L);
        e1.setFecha(null);
        e1.setTotal(0f);
        e1.setUsuario(usuario1);
        e1.setEstado(Encargue_Estado.EN_CREACION);
        e1.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e1));

        // Encargue ID 2 - COMPLETADO
        Encargue e2 = new Encargue();
        e2.setId(2L);
        e2.setFecha(LocalDate.of(2025, 7, 3));
        e2.setTotal(199.99f);
        e2.setUsuario(usuario2);
        e2.setEstado(Encargue_Estado.COMPLETADO);
        e2.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e2));

        // Encargue ID 3 - ENTREGADO
        Encargue e3 = new Encargue();
        e3.setId(3L);
        e3.setFecha(LocalDate.of(2025, 7, 3));
        e3.setTotal(13.49f);
        e3.setUsuario(usuario3);
        e3.setEstado(Encargue_Estado.ENTREGADO);
        e3.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e3));

        // Encargue ID 4 - EN_CREACION
        Encargue e4 = new Encargue();
        e4.setId(4L);
        e4.setFecha(null);
        e4.setTotal(0f);
        e4.setUsuario(usuario4);
        e4.setEstado(Encargue_Estado.EN_CREACION);
        e4.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e4));

        // Encargue ID 5 - COMPLETADO
        Encargue e5 = new Encargue();
        e5.setId(5L);
        e5.setFecha(LocalDate.of(2025, 7, 7));
        e5.setTotal(57.469997f);
        e5.setUsuario(usuario2);
        e5.setEstado(Encargue_Estado.COMPLETADO);
        e5.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e5));

        // Encargue ID 6 - ENTREGADO
        Encargue e6 = new Encargue();
        e6.setId(6L);
        e6.setFecha(LocalDate.of(2025, 7, 15));
        e6.setTotal(74.98f);
        e6.setUsuario(usuario3);
        e6.setEstado(Encargue_Estado.ENTREGADO);
        e6.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e6));

        // Encargue ID 7 - CANCELADO
        Encargue e7 = new Encargue();
        e7.setId(7L);
        e7.setFecha(LocalDate.of(2025, 7, 2));
        e7.setTotal(374.98f);
        e7.setUsuario(usuario2);
        e7.setEstado(Encargue_Estado.CANCELADO);
        e7.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e7));

        // Encargue ID 8 - PENDIENTE
        Encargue e8 = new Encargue();
        e8.setId(8L);
        e8.setFecha(LocalDate.of(2025, 7, 2));
        e8.setTotal(25.99f);
        e8.setUsuario(usuario3);
        e8.setEstado(Encargue_Estado.PENDIENTE);
        e8.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e8));

        // Encargue ID 9 - PENDIENTE
        Encargue e9 = new Encargue();
        e9.setId(9L);
        e9.setFecha(LocalDate.of(2025, 7, 10));
        e9.setTotal(56.97f);
        e9.setUsuario(usuario2);
        e9.setEstado(Encargue_Estado.PENDIENTE);
        e9.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e9));

        // Encargue ID 10 - EN_CREACION
        Encargue e10 = new Encargue();
        e10.setId(10L);
        e10.setFecha(null);
        e10.setTotal(0f);
        e10.setUsuario(usuario5);
        e10.setEstado(Encargue_Estado.EN_CREACION);
        e10.setProductos(new ArrayList<>());
        encargues.add(encargueRepository.save(e10));

        return encargues;
    }

    private void crearProductoEncargues(List<Producto> productos, List<Encargue> encargues) {
        // Crear las relaciones producto_encargue según schema.sql
        
        // ID 1: cantidad=1, encargue_id=2, producto_id=13
        Producto_Encargue pe1 = new Producto_Encargue();
        pe1.setId(1L);
        pe1.setCantidad(1);
        pe1.setEncargue(encargues.stream().filter(e -> e.getId().equals(2L)).findFirst().orElse(null));
        pe1.setProducto(productos.stream().filter(p -> p.getId().equals(13L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe1);

        // ID 2: cantidad=1, encargue_id=3, producto_id=15
        Producto_Encargue pe2 = new Producto_Encargue();
        pe2.setId(2L);
        pe2.setCantidad(1);
        pe2.setEncargue(encargues.stream().filter(e -> e.getId().equals(3L)).findFirst().orElse(null));
        pe2.setProducto(productos.stream().filter(p -> p.getId().equals(15L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe2);

        // ID 3: cantidad=1, encargue_id=3, producto_id=12
        Producto_Encargue pe3 = new Producto_Encargue();
        pe3.setId(3L);
        pe3.setCantidad(1);
        pe3.setEncargue(encargues.stream().filter(e -> e.getId().equals(3L)).findFirst().orElse(null));
        pe3.setProducto(productos.stream().filter(p -> p.getId().equals(12L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe3);

        // ID 4: cantidad=1, encargue_id=5, producto_id=1
        Producto_Encargue pe4 = new Producto_Encargue();
        pe4.setId(4L);
        pe4.setCantidad(1);
        pe4.setEncargue(encargues.stream().filter(e -> e.getId().equals(5L)).findFirst().orElse(null));
        pe4.setProducto(productos.stream().filter(p -> p.getId().equals(1L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe4);

        // ID 5: cantidad=1, encargue_id=5, producto_id=2
        Producto_Encargue pe5 = new Producto_Encargue();
        pe5.setId(5L);
        pe5.setCantidad(1);
        pe5.setEncargue(encargues.stream().filter(e -> e.getId().equals(5L)).findFirst().orElse(null));
        pe5.setProducto(productos.stream().filter(p -> p.getId().equals(2L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe5);

        // ID 6: cantidad=2, encargue_id=5, producto_id=4
        Producto_Encargue pe6 = new Producto_Encargue();
        pe6.setId(6L);
        pe6.setCantidad(2);
        pe6.setEncargue(encargues.stream().filter(e -> e.getId().equals(5L)).findFirst().orElse(null));
        pe6.setProducto(productos.stream().filter(p -> p.getId().equals(4L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe6);

        // ID 7: cantidad=1, encargue_id=6, producto_id=14
        Producto_Encargue pe7 = new Producto_Encargue();
        pe7.setId(7L);
        pe7.setCantidad(1);
        pe7.setEncargue(encargues.stream().filter(e -> e.getId().equals(6L)).findFirst().orElse(null));
        pe7.setProducto(productos.stream().filter(p -> p.getId().equals(14L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe7);

        // ID 8: cantidad=1, encargue_id=6, producto_id=7
        Producto_Encargue pe8 = new Producto_Encargue();
        pe8.setId(8L);
        pe8.setCantidad(1);
        pe8.setEncargue(encargues.stream().filter(e -> e.getId().equals(6L)).findFirst().orElse(null));
        pe8.setProducto(productos.stream().filter(p -> p.getId().equals(7L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe8);

        // ID 9: cantidad=1, encargue_id=7, producto_id=13
        Producto_Encargue pe9 = new Producto_Encargue();
        pe9.setId(9L);
        pe9.setCantidad(1);
        pe9.setEncargue(encargues.stream().filter(e -> e.getId().equals(7L)).findFirst().orElse(null));
        pe9.setProducto(productos.stream().filter(p -> p.getId().equals(13L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe9);

        // ID 10: cantidad=1, encargue_id=7, producto_id=10
        Producto_Encargue pe10 = new Producto_Encargue();
        pe10.setId(10L);
        pe10.setCantidad(1);
        pe10.setEncargue(encargues.stream().filter(e -> e.getId().equals(7L)).findFirst().orElse(null));
        pe10.setProducto(productos.stream().filter(p -> p.getId().equals(10L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe10);

        // ID 11: cantidad=10, encargue_id=7, producto_id=15
        Producto_Encargue pe11 = new Producto_Encargue();
        pe11.setId(11L);
        pe11.setCantidad(10);
        pe11.setEncargue(encargues.stream().filter(e -> e.getId().equals(7L)).findFirst().orElse(null));
        pe11.setProducto(productos.stream().filter(p -> p.getId().equals(15L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe11);

        // ID 12: cantidad=2, encargue_id=9, producto_id=11
        Producto_Encargue pe12 = new Producto_Encargue();
        pe12.setId(12L);
        pe12.setCantidad(2);
        pe12.setEncargue(encargues.stream().filter(e -> e.getId().equals(9L)).findFirst().orElse(null));
        pe12.setProducto(productos.stream().filter(p -> p.getId().equals(11L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe12);

        // ID 13: cantidad=1, encargue_id=9, producto_id=8
        Producto_Encargue pe13 = new Producto_Encargue();
        pe13.setId(13L);
        pe13.setCantidad(1);
        pe13.setEncargue(encargues.stream().filter(e -> e.getId().equals(9L)).findFirst().orElse(null));
        pe13.setProducto(productos.stream().filter(p -> p.getId().equals(8L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe13);

        // ID 14: cantidad=1, encargue_id=8, producto_id=1
        Producto_Encargue pe14 = new Producto_Encargue();
        pe14.setId(14L);
        pe14.setCantidad(1);
        pe14.setEncargue(encargues.stream().filter(e -> e.getId().equals(8L)).findFirst().orElse(null));
        pe14.setProducto(productos.stream().filter(p -> p.getId().equals(1L)).findFirst().orElse(null));
        productoEncargueRepository.save(pe14);
    }

    private List<Publicacion> crearPublicaciones() {
        List<Publicacion> publicaciones = new ArrayList<>();

        // Publicación ID 1
        Publicacion pub1 = new Publicacion();
        pub1.setId(1L);
        pub1.setFechaCreacion(LocalDateTime.of(2025, 6, 29, 18, 32, 11, 778318000));
        pub1.setContenido("Comprando tres libros, el último tiene 50% de descuento. Todos los libros participan, no te lo pierdas!!");
        pub1.setImagen("publicaciones/6544d407-fb97-4bcb-99a9-0c083f5babd8_libros.jpg");
        pub1.setTitulo("Oferta de libros!!");
        publicaciones.add(publicacionRepository.save(pub1));

        // Publicación ID 2
        Publicacion pub2 = new Publicacion();
        pub2.setId(2L);
        pub2.setFechaCreacion(LocalDateTime.of(2025, 6, 29, 18, 33, 37, 428390000));
        pub2.setContenido("Hay nuevos lápices, marcadores, gomas de borrar y mucho más!");
        pub2.setImagen("publicaciones/b9681f84-d3be-4bbe-ae26-22bdad7897a6_utiless.jpg");
        pub2.setTitulo("Nuevos útiles");
        publicaciones.add(publicacionRepository.save(pub2));

        return publicaciones;
    }

    private void crearComentarios(List<Publicacion> publicaciones, List<Usuario> usuarios) {
        // Buscar publicaciones y usuarios por ID
        Publicacion pub1 = publicaciones.stream().filter(p -> p.getId().equals(1L)).findFirst().orElse(null);
        Publicacion pub2 = publicaciones.stream().filter(p -> p.getId().equals(2L)).findFirst().orElse(null);
        Usuario usuario2 = usuarios.stream().filter(u -> u.getId().equals(2L)).findFirst().orElse(null);
        Usuario usuario3 = usuarios.stream().filter(u -> u.getId().equals(3L)).findFirst().orElse(null);

        // Comentario ID 1
        Comentario com1 = new Comentario();
        com1.setId(1L);
        com1.setFechaCreacion(LocalDateTime.of(2025, 6, 29, 21, 42, 12, 778000000));
        com1.setPublicacion(pub2);
        com1.setUsuario(usuario2);
        com1.setTexto("¿De qué marca son los marcadores?");
        com1.setTitulo("Duda sobre marca");
        comentarioRepository.save(com1);

        // Comentario ID 2
        Comentario com2 = new Comentario();
        com2.setId(2L);
        com2.setFechaCreacion(LocalDateTime.of(2025, 6, 29, 21, 42, 41, 569000000));
        com2.setPublicacion(pub1);
        com2.setUsuario(usuario2);
        com2.setTexto("¿Hasta que día es válida la oferta?");
        com2.setTitulo("Duda fecha");
        comentarioRepository.save(com2);

        // Comentario ID 3
        Comentario com3 = new Comentario();
        com3.setId(3L);
        com3.setFechaCreacion(LocalDateTime.of(2025, 6, 29, 21, 44, 49, 551000000));
        com3.setPublicacion(pub1);
        com3.setUsuario(usuario3);
        com3.setTexto("Si compro más de 3 libros, hay más descuento??");
        com3.setTitulo("Sobre los descuentos");
        comentarioRepository.save(com3);
    }

    private void crearImpresiones(List<Usuario> usuarios) {
        // Buscar usuarios por ID
        Usuario usuario2 = usuarios.stream().filter(u -> u.getId().equals(2L)).findFirst().orElse(null);
        Usuario usuario3 = usuarios.stream().filter(u -> u.getId().equals(3L)).findFirst().orElse(null);

        // Impresión ID 1
        Impresion imp1 = new Impresion();
        imp1.setId(1L);
        imp1.setColor(true);
        imp1.setDobleCara(false);
        imp1.setUsuario(usuario2);
        imp1.setComentarioAdicional("");
        imp1.setEstado("En proceso");
        imp1.setFormato("A4");
        imp1.setNombreArchivo("8990efd4-cbb5-4010-a57d-c22848f02f50_poster3.jpeg");
        imp1.setOrientacion("vertical");
        imp1.setTipoPapel("Papel común");
        impresionRepository.save(imp1);

        // Impresión ID 2
        Impresion imp2 = new Impresion();
        imp2.setId(2L);
        imp2.setColor(true);
        imp2.setDobleCara(false);
        imp2.setUsuario(usuario2);
        imp2.setComentarioAdicional("Cuidado con los bordes!!!");
        imp2.setEstado("Pendiente");
        imp2.setFormato("A4");
        imp2.setNombreArchivo("7f008633-17ea-441e-90d4-504e54627946_poster.jpg");
        imp2.setOrientacion("vertical");
        imp2.setTipoPapel("Papel común");
        impresionRepository.save(imp2);

        // Impresión ID 3
        Impresion imp3 = new Impresion();
        imp3.setId(3L);
        imp3.setColor(false);
        imp3.setDobleCara(false);
        imp3.setUsuario(usuario3);
        imp3.setComentarioAdicional("");
        imp3.setEstado("Entregado");
        imp3.setFormato("A4");
        imp3.setNombreArchivo("6e9439eb-486b-4325-9ad5-c940f541e1de_ci.jpg");
        imp3.setOrientacion("vertical");
        imp3.setTipoPapel("Papel común");
        impresionRepository.save(imp3);

        // Impresión ID 4
        Impresion imp4 = new Impresion();
        imp4.setId(4L);
        imp4.setColor(false);
        imp4.setDobleCara(false);
        imp4.setUsuario(usuario3);
        imp4.setComentarioAdicional("");
        imp4.setEstado("Completado");
        imp4.setFormato("A4");
        imp4.setNombreArchivo("03e66448-30bd-4e81-a0ee-0ac5b7c2d76b_canto1Illiada.pdf");
        imp4.setOrientacion("vertical");
        imp4.setTipoPapel("Papel común");
        impresionRepository.save(imp4);

        // Impresión ID 5
        Impresion imp5 = new Impresion();
        imp5.setId(5L);
        imp5.setColor(true);
        imp5.setDobleCara(false);
        imp5.setUsuario(usuario3);
        imp5.setComentarioAdicional("");
        imp5.setEstado("Cancelado");
        imp5.setFormato("A4");
        imp5.setNombreArchivo("cbb45b0a-7256-4915-85bb-35e408ad566a_manual.pdf");
        imp5.setOrientacion("vertical");
        imp5.setTipoPapel("Papel común");
        impresionRepository.save(imp5);
    }
}
