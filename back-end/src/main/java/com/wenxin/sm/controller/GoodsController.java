package com.wenxin.sm.controller;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenxin.sm.entity.Goods;
import com.wenxin.sm.entity.PageRequest;
import com.wenxin.sm.entity.Result;
import com.wenxin.sm.mapper.GoodsMapper;
import com.wenxin.sm.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.wenxin.sm.entity.table.GoodsTableDef.GOODS;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    private final GoodsMapper goodsMapper;

    public GoodsController(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }


    @GetMapping("/detail/{id}")
    public Result<Object> getInfo(@PathVariable String id) {
        if (!StringUtils.isNumeric(id)) {
            return Result.error("意外参数");
        }
        Goods goods = goodsMapper.selectOneById(id);
        if (goods == null) {
            return Result.error("没有找到该商品数据");
        }
        return Result.success("查询成功", goods);
    }

    /**
     * 新增商品
     * @param goods 商品表
     * @return 成功
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Goods goods) {
        if (goods == null) {
            return Result.error("非空");
        }
        goodsMapper.insertWithPk(goods);
        return Result.success("添加成功");
    }

    /**
     * 更新商品
     * @param goods 商品表
     * @return 成功
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody Goods goods) {
        if (goods == null) {
            return Result.error("更新数据不得为空");
        }
        goodsMapper.update(goods);
        return Result.success("更新成功");
    }

    /**
     * 查询商品表
     * @param pageRequest 页码 每页条数 升降排序
     * @return 商品信息
     */
    @GetMapping("/page")
    public Result<Page<Goods>> paginate(PageRequest pageRequest) {
        try {
            Page<Goods> goodsPage = goodsMapper.paginate(pageRequest.getPageNumber(), pageRequest.getPageSize(), QueryWrapper.create().from(GOODS));
            return Result.success("查询成功",goodsPage);
        } catch (Exception e) {
            return Result.Exception();
        }
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody ArrayList<Integer> ids){
        try {
            if (ids == null) {
                return Result.error("商品id不得为空");
            }
            goodsMapper.deleteBatchByIds(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.Exception();
        }
    }

    /**
     * 商品搜索
     * @param text 商品名字
     * @param category 商品分类
     * @return 商品列表
     */
    @GetMapping("/search")
    public Result<Object> search(String text, String category) {
        System.out.println(text);
        System.out.println(category);
        if (StringUtils.isNullOrEmpty(text) && StringUtils.isNullOrEmpty(category)) {
            return Result.error("参数缺失");
        }
        QueryWrapper where = QueryWrapper.create()
                .from(GOODS)
                .where(GOODS.NAME.like(text))
                .and(GOODS.CATEGORY.eq(category));
        List<Goods> goodsList = goodsMapper.selectListByQuery(where);
        return Result.success("查询成功", goodsList);
    }
}
