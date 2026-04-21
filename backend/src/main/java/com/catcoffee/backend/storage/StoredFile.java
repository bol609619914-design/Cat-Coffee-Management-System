package com.catcoffee.backend.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {

    private String fileName;
    private String url;
    private Long size;
}
