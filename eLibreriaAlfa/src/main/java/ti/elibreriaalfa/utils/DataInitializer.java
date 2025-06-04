package ti.elibreriaalfa.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ti.elibreriaalfa.business.entities.*;
import ti.elibreriaalfa.business.repositories.*;

import java.math.BigDecimal;
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

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Inicializando datos de prueba...");

        // Crear usuarios
        crearUsuarios();

        // Crear categorías
        List<Categoria> categorias = crearCategorias();

        // Crear productos
        crearProductos(categorias);

        log.info("Datos de prueba inicializados correctamente");
    }

    private void crearUsuarios() {
        log.info("Creando usuarios...");

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
        log.info("Creando categorías...");

        List<String> nombresCategorias = Arrays.asList(
                "Libros de Literatura",
                "Útiles Escolares",
                "Material de Oficina",
                "Juegos Educativos",
                "Electrónicos"
        );

        List<Categoria> categorias = new ArrayList<>();

        for (String nombre : nombresCategorias) {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setPadre(null);
            categorias.add(categoriaRepository.save(categoria));
        }

        return categorias;
    }

    private void crearProductos(List<Categoria> categorias) {
        log.info("Creando productos...");

        String[][] productosInfo = {
                {"Cien años de soledad", "Novela clásica de Gabriel García Márquez", "25.99"},
                {"El principito", "Obra de Antoine de Saint-Exupéry", "15.50"},
                {"Cuaderno universitario", "Cuaderno de tapa dura con 100 hojas", "3.99"},
                {"Bloc de notas", "Block tamaño A5 con hojas rayadas", "2.50"},
                {"Lapiceras de colores", "Set de 12 lapiceras de gel de colores", "8.75"},
                {"Carpeta archivadora", "Carpeta A4 con anillos", "6.25"},
                {"Calculadora científica", "Calculadora con funciones avanzadas", "18.50"},
                {"Post-it", "Pack de 5 blocks de notas adhesivas", "4.99"},
                {"Juego de mesa educativo", "Para aprender matemáticas jugando", "22.50"},
                {"Rompecabezas mapa mundial", "Puzzle de 1000 piezas", "19.99"},
                {"Juego de memoria", "Clásico juego para ejercitar la memoria", "12.75"},
                {"Tablet educativa", "Tablet con apps educativas para niños", "89.99"},
                {"Audífonos", "Audífonos con cable y micrófono", "34.50"},
                {"Cargador portátil", "Batería externa 10000mAh", "45.99"},
                {"Lápices", "Paquete de 12 lápices HB", "3.25"},
                {"Borrador", "Borrador de goma blanco", "0.99"},
                {"Diccionario", "Diccionario español completo", "28.75"},
                {"Mochila escolar", "Mochila resistente con varios compartimentos", "39.99"},
                {"Estuche", "Estuche con cierre para guardar útiles", "7.50"},
                {"Kit de geometría", "Set completo con regla, transportador y compás", "5.99"}
        };

        for (int i = 0; i < productosInfo.length; i++) {
            Producto producto = new Producto();
            producto.setNombre(productosInfo[i][0]);
            producto.setDescripcion(productosInfo[i][1]);
            producto.setPrecio(Float.parseFloat(productosInfo[i][2]));

            // Asignar categoría(s) - cada producto puede estar en 1 o 2 categorías
            int numCategorias = random.nextInt(2) + 1; // 1 o 2 categorías
            List<Categoria> categoriasProducto = new ArrayList<>();

            for (int j = 0; j < numCategorias; j++) {
                int categoriaIndex = random.nextInt(categorias.size());
                Categoria categoria = categorias.get(categoriaIndex);
                if (!categoriasProducto.contains(categoria)) {
                    categoriasProducto.add(categoria);
                }
            }

            producto.setCategorias(categoriasProducto);
            productoRepository.save(producto);
        }
    }
}
