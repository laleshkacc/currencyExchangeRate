package com.currency.exchange.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import com.currency.exchange.configuration.FileStorageProperties;
import com.currency.exchange.utils.CurrencyExchangeRateLogConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final Path fileStorageLocation;

  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties) {
    this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
        .toAbsolutePath().normalize();
    createDirectoriesIfNotExists(fileStorageLocation);
  }

  private void createDirectoriesIfNotExists(Path path) {
    try {
      if (Files.notExists(path)) {
        Files.createDirectories(path);
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_SAVE_FILE_STORAGE, ex);
    }
  }

  /**
   * Guarda el archivo en el directorio de almacenamiento.
   *
   * @param file el archivo a guardar
   * @return el nombre del archivo guardado
   */
  public String storeFile(MultipartFile file) {
    String fileName = cleanFileName(file);
    Path targetLocation = fileStorageLocation.resolve(fileName);

    return Optional.of(file)
        .map(f -> saveFileToTargetLocation(f, targetLocation))
        .orElseThrow(() -> new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_FILE));
  }

  private String cleanFileName(MultipartFile file) {
    return Optional.ofNullable(file.getOriginalFilename())
        .map(StringUtils::cleanPath)
        .filter(name -> !name.contains(".."))
        .orElseThrow(() -> new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_NAME));
  }

  private String saveFileToTargetLocation(MultipartFile file, Path targetLocation) {
    try {
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return targetLocation.getFileName().toString();
    } catch (IOException ex) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_PROCESS, ex);
    }
  }

  /**
   * Carga un archivo como recurso.
   *
   * @param fileName el nombre del archivo a cargar
   * @return el recurso del archivo
   */
  public Resource loadFileAsResource(String fileName) {
    return Optional.of(fileName)
        .map(this::resolveFilePath)
        .map(this::loadResource)
        .orElseThrow(() -> new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_NOT_FOUND_FILE + fileName));
  }

  private Path resolveFilePath(String fileName) {
    return fileStorageLocation.resolve(fileName).normalize();
  }

  private Resource loadResource(Path filePath) {
    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_NOT_FOUND_FILE_ROUTE);
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_PROCESS, ex);
    }
  }
}
