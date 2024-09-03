package com.currency.exchange.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Maneja IllegalArgumentException
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  // Maneja MultipartException (por ejemplo, si el archivo no se sube correctamente)
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<String> handleMultipartException(MultipartException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al subir el archivo. Asegúrate de que el archivo sea válido.");
  }

  // Maneja IOException y devuelve 500
  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleIOException(IOException ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo Excel.");
  }

  // Maneja cualquier otra excepción no controlada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
  }

}
