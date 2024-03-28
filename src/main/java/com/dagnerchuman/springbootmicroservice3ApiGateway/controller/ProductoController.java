package com.dagnerchuman.springbootmicroservice3ApiGateway.controller;

import com.dagnerchuman.springbootmicroservice3ApiGateway.request.ProductoServiceRequest;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gateway/producto")
@CrossOrigin(origins = "http://api-gateway:5200") // Esto permite solicitudes desde http://localhost:5200
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoServiceRequest productoServiceRequest;

    @PostMapping
    public ResponseEntity<?> saveProducto(@RequestBody Object producto)
    {
        return new ResponseEntity<>(productoServiceRequest.saveProducto(producto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<?> deleteProducto(@PathVariable("productoId") Long productoId)
    {
        productoServiceRequest.deleteProducto(productoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getAllProductos()
    {
        return ResponseEntity.ok(productoServiceRequest.getAllProductos());
    }



    @GetMapping("/{productoId}")
    public ResponseEntity<?> getProductoById(@PathVariable Long productoId) {
        try {
            Object producto = productoServiceRequest.getProductoById(productoId);
            return ResponseEntity.ok(producto);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Nuevo endpoint para eliminar todos los productos
    @DeleteMapping("/eliminar-todos")
    public ResponseEntity<?> deleteAllProductos() {
        productoServiceRequest.deleteAllProductos();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Nuevo endpoint para obtener productos según la posición y la cantidad
    @GetMapping("/siguientes")
    public ResponseEntity<?> getSiguientesProductos(
            @RequestParam("posicion") int posicion,
            @RequestParam("cantidad") int cantidad
    ) {
        try {
            Object productos = productoServiceRequest.getSiguientesProductos(posicion, cantidad);
            return ResponseEntity.ok(productos);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("{productoId}")
    public ResponseEntity<?> actualizarProducto(@PathVariable("productoId") Long productoId, @RequestBody Object nuevoProducto) {
        try {
            Object productoActualizado = productoServiceRequest.actualizarProducto(productoId, nuevoProducto);
            return ResponseEntity.ok(productoActualizado);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

        @GetMapping("/pornegocio/{negocioId}")
        public ResponseEntity<?> getProductosPorNegocio(@PathVariable Long negocioId) {
            try {
                List<Object> productos = productoServiceRequest.getProductosPorNegocio(negocioId);
                return ResponseEntity.ok(productos);
            } catch (FeignException.NotFound e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

    // Nuevo endpoint para obtener productos por categoría
    @GetMapping("/porcategoria/{categoriaId}")
    public ResponseEntity<?> getProductosPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<Object> productos = productoServiceRequest.getProductosPorCategoria(categoriaId);
            return ResponseEntity.ok(productos);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/v1/qrcode/{productId}")
    public ResponseEntity<?> generateQRCode(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "350") int width,
            @RequestParam(defaultValue = "350") int height,
            HttpServletResponse response
    ) {
        try {
            // Obtener el código QR como arreglo de bytes desde el servicio
            byte[] qrCodeImage = productoServiceRequest.generateQRCode(productId, width, height);

            // Configurar la respuesta HTTP
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=qrcode.png");

            // Escribir la imagen en el flujo de salida
            outputStream.write(qrCodeImage);
            outputStream.flush();
            outputStream.close();

            return ResponseEntity.ok().build();
        } catch (FeignException e) {
            log.error("Error al intentar generar el código QR", e);
            return ResponseEntity.status(e.status()).body("Error al intentar generar el código QR");
        } catch (Exception e) {
            log.error("Error inesperado al intentar generar el código QR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al intentar generar el código QR");
        }
    }
}
