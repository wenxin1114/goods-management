package com.wenxin.sm.controller;


import com.mybatisflex.core.query.QueryWrapper;
import com.wenxin.sm.entity.Category;
import com.wenxin.sm.entity.Result;
import com.wenxin.sm.mapper.CategoryMapper;
import com.wenxin.sm.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wenxin.sm.entity.table.CategoryTableDef.CATEGORY;


@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryMapper categoryMapper;
    private final QueryWrapper qwAsSelectName = QueryWrapper.create().select(CATEGORY.NAME).from(CATEGORY);

    public CategoryController(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody Category category) {
        if (StringUtils.isNullOrEmpty(category.getName())) {
            return Result.error("分类名称不得为空");
        }
        categoryMapper.insert(category);
        List<String> categories = categoryMapper.selectListByQueryAs(qwAsSelectName, String.class);
        return Result.success("添加成功", categories);
    }

    @PostMapping("/delete")
    public Result<Object> delete(@RequestBody Category category) {
        if (StringUtils.isNullOrEmpty(category.getName())) {
            return Result.error("分类名称不得为空");
        }
        categoryMapper.deleteByQuery(QueryWrapper.create().from(CATEGORY).where(CATEGORY.NAME.eq(category.getName())));
        List<String> categories = categoryMapper.selectListByQueryAs(qwAsSelectName, String.class);
        return Result.success("删除成功", categories);
    }

    @GetMapping("/list")
    public Result<Object> list() {
        List<String> categories = categoryMapper.selectListByQueryAs(qwAsSelectName, String.class);
        return Result.success("查询成功", categories);
    }
}
