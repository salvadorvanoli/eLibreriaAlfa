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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private PasswordEncoder passwordEncoder;

    private Random random = new Random();

    @Value("${data.init.enabled:true}")
    private boolean dataInitEnabled;

    @PostConstruct
    @Transactional
    public void init() {
        if (!dataInitEnabled) {
            log.info("Carga de datos desactivada por configuración.");
            return;
        }

        // Crear usuarios
        crearUsuarios();

        // Crear categorías
        List<Categoria> categorias = crearCategorias();

        // Crear productos
        crearProductos(categorias);

    }

    private void crearUsuarios() {

        // Administrador
        Usuario admin = new Usuario();
        admin.setEmail("admin@libreria.com");
        admin.setContrasenia(passwordEncoder.encode("admin123"));
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setTelefono("091234567");
        admin.setRol(Rol.ADMINISTRADOR);
        usuarioRepository.save(admin);

        // Cliente 1
        Usuario cliente1 = new Usuario();
        cliente1.setEmail("cliente1@correo.com");
        cliente1.setContrasenia(passwordEncoder.encode("cliente123"));
        cliente1.setNombre("Juan");
        cliente1.setApellido("Pérez");
        cliente1.setTelefono("092345678");
        cliente1.setRol(Rol.CLIENTE);
        usuarioRepository.save(cliente1);

        // Cliente 2
        Usuario cliente2 = new Usuario();
        cliente2.setEmail("cliente2@correo.com");
        cliente2.setContrasenia(passwordEncoder.encode("cliente123"));
        cliente2.setNombre("Sofía");
        cliente2.setApellido("González");
        cliente2.setTelefono("093456789");
        cliente2.setRol(Rol.CLIENTE);
        usuarioRepository.save(cliente2);

        // Crear encargues en estado EN_CREACION para los usuarios
        for (Usuario usuario : Arrays.asList(admin, cliente1, cliente2)) {
            Encargue encargue = new Encargue();
            encargue.setUsuario(usuario);
            encargue.setEstado(Encargue_Estado.EN_CREACION);
            encargue.setProductos(new ArrayList<>());
            encargue.setTotal(0f);
            encargue.setFecha(null);
            encargueRepository.save(encargue);
        }
    }

    private List<Categoria> crearCategorias() {

        // Crear árbol de categorías
        Categoria libros = new Categoria();
        libros.setNombre("Libros");
        libros.setPadre(null);
        categoriaRepository.save(libros);

        Categoria literatura = new Categoria();
        literatura.setNombre("Literatura");
        literatura.setPadre(libros);
        categoriaRepository.save(literatura);

        Categoria novela = new Categoria();
        novela.setNombre("Novela");
        novela.setPadre(literatura);
        categoriaRepository.save(novela);

        Categoria cuento = new Categoria();
        cuento.setNombre("Cuento");
        cuento.setPadre(literatura);
        categoriaRepository.save(cuento);

        Categoria infantil = new Categoria();
        infantil.setNombre("Infantil");
        infantil.setPadre(libros);
        categoriaRepository.save(infantil);

        Categoria utiles = new Categoria();
        utiles.setNombre("Útiles Escolares");
        utiles.setPadre(null);
        categoriaRepository.save(utiles);

        Categoria escritura = new Categoria();
        escritura.setNombre("Escritura");
        escritura.setPadre(utiles);
        categoriaRepository.save(escritura);

        Categoria papel = new Categoria();
        papel.setNombre("Papel");
        papel.setPadre(utiles);
        categoriaRepository.save(papel);

        Categoria oficina = new Categoria();
        oficina.setNombre("Material de Oficina");
        oficina.setPadre(null);
        categoriaRepository.save(oficina);

        Categoria electronicos = new Categoria();
        electronicos.setNombre("Electrónicos");
        electronicos.setPadre(null);
        categoriaRepository.save(electronicos);

        Categoria computo = new Categoria();
        computo.setNombre("Computación");
        computo.setPadre(electronicos);
        categoriaRepository.save(computo);

        Categoria audio = new Categoria();
        audio.setNombre("Audio");
        audio.setPadre(electronicos);
        categoriaRepository.save(audio);

        Categoria juegos = new Categoria();
        juegos.setNombre("Juegos Educativos");
        juegos.setPadre(null);
        categoriaRepository.save(juegos);

        Categoria mesa = new Categoria();
        mesa.setNombre("Juegos de Mesa");
        mesa.setPadre(juegos);
        categoriaRepository.save(mesa);

        Categoria didacticos = new Categoria();
        didacticos.setNombre("Didácticos");
        didacticos.setPadre(juegos);
        categoriaRepository.save(didacticos);

        // Devolver todas las categorías creadas
        return Arrays.asList(
                libros, literatura, novela, cuento, infantil,
                utiles, escritura, papel,
                oficina,
                electronicos, computo, audio,
                juegos, mesa, didacticos
        );
    }

    private void crearProductos(List<Categoria> categorias) {

        // Buscar categorías por nombre para asignar productos
        Categoria novela = categorias.stream().filter(c -> c.getNombre().equals("Novela")).findFirst().orElse(null);
        Categoria cuento = categorias.stream().filter(c -> c.getNombre().equals("Cuento")).findFirst().orElse(null);
        Categoria infantil = categorias.stream().filter(c -> c.getNombre().equals("Infantil")).findFirst().orElse(null);
        Categoria escritura = categorias.stream().filter(c -> c.getNombre().equals("Escritura")).findFirst().orElse(null);
        Categoria papel = categorias.stream().filter(c -> c.getNombre().equals("Papel")).findFirst().orElse(null);
        Categoria computo = categorias.stream().filter(c -> c.getNombre().equals("Computación")).findFirst().orElse(null);
        Categoria audio = categorias.stream().filter(c -> c.getNombre().equals("Audio")).findFirst().orElse(null);
        Categoria mesa = categorias.stream().filter(c -> c.getNombre().equals("Juegos de Mesa")).findFirst().orElse(null);
        Categoria didacticos = categorias.stream().filter(c -> c.getNombre().equals("Didácticos")).findFirst().orElse(null);
        Categoria oficina = categorias.stream().filter(c -> c.getNombre().equals("Material de Oficina")).findFirst().orElse(null);

        // Productos variados
        Producto p1 = new Producto();
        p1.setNombre("Cien años de soledad");
        p1.setDescripcion("Novela clásica de Gabriel García Márquez");
        p1.setPrecio(25.99f);
        p1.setCategorias(Arrays.asList(novela));
        productoRepository.save(p1);

        Producto p2 = new Producto();
        p2.setNombre("El principito");
        p2.setDescripcion("Obra de Antoine de Saint-Exupéry");
        p2.setPrecio(15.50f);
        p2.setCategorias(Arrays.asList(cuento, infantil));
        productoRepository.save(p2);

        Producto p3 = new Producto();
        p3.setNombre("Harry Potter y la piedra filosofal");
        p3.setDescripcion("Primer libro de la saga Harry Potter");
        p3.setPrecio(29.99f);
        p3.setCategorias(Arrays.asList(novela, infantil));
        productoRepository.save(p3);

        Producto p4 = new Producto();
        p4.setNombre("Set de lápices de colores");
        p4.setDescripcion("Caja de 24 lápices de colores");
        p4.setPrecio(7.99f);
        p4.setCategorias(Arrays.asList(escritura));
        productoRepository.save(p4);

        Producto p5 = new Producto();
        p5.setNombre("Cuaderno universitario");
        p5.setDescripcion("Cuaderno de tapa dura con 100 hojas");
        p5.setPrecio(3.99f);
        p5.setCategorias(Arrays.asList(papel));
        productoRepository.save(p5);

        Producto p6 = new Producto();
        p6.setNombre("Notebook Lenovo 14''");
        p6.setDescripcion("Notebook para estudiantes y oficina");
        p6.setPrecio(499.99f);
        p6.setCategorias(Arrays.asList(computo, oficina));
        productoRepository.save(p6);

        Producto p7 = new Producto();
        p7.setNombre("Auriculares inalámbricos JBL");
        p7.setDescripcion("Auriculares bluetooth con micrófono");
        p7.setPrecio(59.99f);
        p7.setCategorias(Arrays.asList(audio));
        productoRepository.save(p7);

        Producto p8 = new Producto();
        p8.setNombre("Juego de mesa: Catan");
        p8.setDescripcion("Juego de estrategia para toda la familia");
        p8.setPrecio(44.99f);
        p8.setCategorias(Arrays.asList(mesa));
        productoRepository.save(p8);

        Producto p9 = new Producto();
        p9.setNombre("Puzzle educativo de animales");
        p9.setDescripcion("Puzzle didáctico para niños pequeños");
        p9.setPrecio(12.50f);
        p9.setCategorias(Arrays.asList(didacticos, infantil));
        productoRepository.save(p9);

        Producto p10 = new Producto();
        p10.setNombre("Silla ergonómica de oficina");
        p10.setDescripcion("Silla con soporte lumbar y altura regulable");
        p10.setPrecio(129.99f);
        p10.setCategorias(Arrays.asList(oficina));
        productoRepository.save(p10);

        // Más productos variados
        Producto p11 = new Producto();
        p11.setNombre("Resaltadores pastel");
        p11.setDescripcion("Set de 6 resaltadores colores pastel");
        p11.setPrecio(5.99f);
        p11.setCategorias(Arrays.asList(escritura));
        productoRepository.save(p11);

        Producto p12 = new Producto();
        p12.setNombre("Agenda 2025");
        p12.setDescripcion("Agenda anual con calendario y stickers");
        p12.setPrecio(8.99f);
        p12.setCategorias(Arrays.asList(papel, oficina));
        productoRepository.save(p12);

        Producto p13 = new Producto();
        p13.setNombre("Tablet Samsung Galaxy Tab A7");
        p13.setDescripcion("Tablet para estudio y entretenimiento");
        p13.setPrecio(199.99f);
        p13.setCategorias(Arrays.asList(computo, infantil));
        productoRepository.save(p13);

        Producto p14 = new Producto();
        p14.setNombre("Juego didáctico de matemáticas");
        p14.setDescripcion("Juego para aprender sumas y restas");
        p14.setPrecio(14.99f);
        p14.setCategorias(Arrays.asList(didacticos));
        productoRepository.save(p14);

        Producto p15 = new Producto();
        p15.setNombre("Bolígrafos gel azul");
        p15.setDescripcion("Pack de 10 bolígrafos de gel azul");
        p15.setPrecio(4.50f);
        p15.setCategorias(Arrays.asList(escritura));
        productoRepository.save(p15);
    }
}
