package com.currency.exchange.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class FileMetadata {

  @Id
  private String id;

  private String fileName;

  private String fileExtension;

  private LocalDateTime uploadDate;

  private long fileSize;

}
