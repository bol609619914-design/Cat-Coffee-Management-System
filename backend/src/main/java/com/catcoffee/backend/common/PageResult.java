package com.catcoffee.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "当前页", example = "1")
    private Long current;
    @Schema(description = "每页条数", example = "10")
    private Long size;
    @Schema(description = "总记录数", example = "100")
    private Long total;
    @Schema(description = "当前页数据")
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
